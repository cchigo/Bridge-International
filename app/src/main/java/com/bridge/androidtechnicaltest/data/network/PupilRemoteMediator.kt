package com.bridge.androidtechnicaltest.data.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.bridge.androidtechnicaltest.data.database.AppDatabase
import com.bridge.androidtechnicaltest.data.models.local.EntityModelMapper
import com.bridge.androidtechnicaltest.data.models.local.PupilEntity
import com.bridge.androidtechnicaltest.data.models.remote.PupilDTOMapper
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PupilRemoteMediator(
    private val pupilDB: AppDatabase,
    private val pupilApi: PupilApi,

    private val entityMapper: EntityModelMapper,
    private val dtoMapper: PupilDTOMapper
): RemoteMediator<Int, PupilEntity>() {



    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PupilEntity>
    ): MediatorResult {
        return try {
            val loadKey = when(loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if(lastItem == null) {
                        1
                    } else {
                        (lastItem.id / state.config.pageSize) + 1
                    }
                }
            }

            val pupilsRemote = pupilApi.getPupils(
                page = loadKey,
            )

            pupilDB.withTransaction {
                val pupilEntities = dtoMapper.convertDtoListToEntityList(pupilsRemote.items)
                if(loadType == LoadType.REFRESH) {
                  //  pupilDB.pupilDao().deleteAllPupils()
                }


                pupilDB.pupilDao().upsertAll(pupilEntities)
            }
            MediatorResult.Success(
                endOfPaginationReached = pupilsRemote.pageNumber == pupilsRemote.totalPages
            )
        } catch(e: IOException) {
            MediatorResult.Error(e)
        } catch(e: HttpException) {
            MediatorResult.Error(e)
        }
    }


}