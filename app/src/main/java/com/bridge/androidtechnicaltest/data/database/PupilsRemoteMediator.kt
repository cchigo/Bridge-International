package com.bridge.androidtechnicaltest.data.database

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.bridge.androidtechnicaltest.data.model.pupil.local.EntityModelMapper
import com.bridge.androidtechnicaltest.data.model.pupil.local.PupilEntity
import com.bridge.androidtechnicaltest.data.model.pupil.local.RemoteKeys
import com.bridge.androidtechnicaltest.data.model.pupil.remote.PupilDTOMapper
import com.bridge.androidtechnicaltest.data.network.PupilApi

@OptIn(ExperimentalPagingApi::class)
class PupilsRemoteMediator(
    private val db: AppDatabase,
    private val api: PupilApi,
    private val entityMapper: EntityModelMapper,
    private val dtoMapper: PupilDTOMapper
) : RemoteMediator<Int, PupilEntity>() {



    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PupilEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull() ?: return MediatorResult.Success(true)
                val remoteKeys = lastItem.pupilId?.let { db.remoteKeysDao().remoteKeysPupilId(it) }
                remoteKeys?.currentPage?.plus(1) ?: return MediatorResult.Success(true)
            }
        }

        try {
            val response = api.getPupils(page = page)
            val pupils = response.items.orEmpty()
            val currentPage = response.pageNumber ?: page
            val totalPages = response.totalPages ?: currentPage
            val endReached = currentPage >= totalPages

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeysDao().clearRemoteKeys()
                   // db.pupilDao().deleteAllPupils()
                }

                val remoteKeys = pupils.map {
                    RemoteKeys(pupilId = it.pupilId ?: 0, currentPage = currentPage)
                }

                db.remoteKeysDao().insertAll(remoteKeys)

                val mappedPupils = entityMapper
                    .toList(dtoMapper.fromList(pupils))
                    .map { it.copy(isSynced = true) }

                db.pupilDao().insertPupils(mappedPupils)

            }

            return MediatorResult.Success(endOfPaginationReached = endReached)

        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }



}

