package com.bridge.androidtechnicaltest.domain

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.room.Dao
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bridge.androidtechnicaltest.common.BaseResponse
import com.bridge.androidtechnicaltest.common.ErrorApiResponse
import com.bridge.androidtechnicaltest.common.NetworkChecker
import com.bridge.androidtechnicaltest.data.database.PupilDao
import com.bridge.androidtechnicaltest.data.database.PupilLocalDataSource
import com.bridge.androidtechnicaltest.data.models.local.EntityModelMapper
import com.bridge.androidtechnicaltest.data.models.local.PupilEntity
import com.bridge.androidtechnicaltest.data.models.remote.PupilDTOMapper
import com.bridge.androidtechnicaltest.data.network.PupilApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import timber.log.Timber
import kotlin.random.Random


@HiltWorker
class PendingWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val wParams: WorkerParameters,
     private val repo: PupilsRepository,
    private val db: PupilLocalDataSource,
    private val networkChecker: NetworkChecker,
    private val dtoMapper: PupilDTOMapper,
    private val localMapper: EntityModelMapper
) : CoroutineWorker(context, wParams) {


    override suspend fun doWork(): Result {
        return try {
            if (!networkChecker.isConnected()) return Result.failure()

             val  createList = db.getUnsyncedPupils().first()

            createList.forEach { entity ->
                val pupil = localMapper.from(entity)
                val remoteDto = dtoMapper.to(pupil)

                when (repo.createPupil(remoteDto)) {
                    is BaseResponse.Success -> {

                        Timber.d("PW_Mock creating: ${pupil.name}")
                        db.delete(entity)
                    }
                    is BaseResponse.Error -> {
                        return if (runAttemptCount < 2) Result.retry() else Result.failure()
                    }
                    else ->{}
                }
            }

            val deleteList = db.getDeleteList().first()

            deleteList.forEach { entity ->
                val pupil = localMapper.from(entity)
                pupil.pupilId?.let { pupilId ->
                    when (repo.deletePupil(pupilId)) {
                        is BaseResponse.Success -> {

                            Timber.d("PW_Mock delrting: ${pupil.name}")
                            db.delete(entity)
                        }
                        is BaseResponse.Error -> {
                            return if (runAttemptCount < 2) Result.retry() else Result.failure()
                        }
                        else ->{}

                    }
                }
            }
            if (createList.isEmpty() && deleteList.isEmpty()) {
                Timber.d("PW_Mock: No pupils to process. Returning success.")
                return Result.success()
            }

            Result.success()
        } catch (e: Exception) {
            Timber.e("Worker error: $e")
            Result.failure()
        }
    }




}
