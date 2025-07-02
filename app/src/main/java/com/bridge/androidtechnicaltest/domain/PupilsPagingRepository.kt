package com.bridge.androidtechnicaltest.domain

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bridge.androidtechnicaltest.data.database.AppDatabase
import com.bridge.androidtechnicaltest.data.database.PupilsRemoteMediator
import com.bridge.androidtechnicaltest.data.model.pupil.local.EntityModelMapper
import com.bridge.androidtechnicaltest.data.model.pupil.local.PupilEntity
import com.bridge.androidtechnicaltest.data.model.pupil.remote.PupilDTOMapper
import com.bridge.androidtechnicaltest.data.network.PupilApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class PupilsPagingRepository @Inject constructor(
    private val db: AppDatabase,
    private val api: PupilApi,
    private val entityMapper: EntityModelMapper,
    private val dtoMapper: PupilDTOMapper

) {
    @OptIn(ExperimentalPagingApi::class)
    fun getPagedPupils(): Flow<PagingData<PupilEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = PupilsRemoteMediator(db, api, entityMapper = entityMapper, dtoMapper = dtoMapper),
            pagingSourceFactory = { db.pupilDao().getPagedPupils() }
        ).flow
    }
}
