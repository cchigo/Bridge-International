package com.bridge.androidtechnicaltest.domain

import androidx.lifecycle.asFlow
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class PendingWorkerUseCase @Inject constructor(
    private val workManager: WorkManager
) : StatusScheduler {

    fun startPendingSyncWorker(): Flow<PendingStatus> {
        val request = OneTimeWorkRequestBuilder<PendingWorker>()
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                10, TimeUnit.SECONDS
            )
            .addTag("pending_worker_sync")
            .build()

        val workId = request.id
        workManager.enqueue(request)

        return workManager.getWorkInfoByIdLiveData(workId)
            .asFlow()
            .mapLatest { workInfo ->
                when (workInfo.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        PendingStatus.SUCCESS
                    }
                    WorkInfo.State.FAILED -> {
                        PendingStatus.FAILED
                    }
                    WorkInfo.State.RUNNING -> {
                        PendingStatus.STARTED
                    }
                    WorkInfo.State.ENQUEUED -> {
                        PendingStatus.STARTED
                    }
                    else -> {
                        PendingStatus.STARTED
                    }
                }
            }

    }

    override fun scheduleWorker() {}
}


enum class PendingStatus{
    FAILED, SUCCESS, STARTED, EMPTY
}

sealed class WorkerResult<out T>{
    class Success<out T>(val data: T) :WorkerResult<T>()
    class Error(val error: String) :WorkerResult<Nothing>()

}

interface StatusScheduler{
    fun scheduleWorker()
}





