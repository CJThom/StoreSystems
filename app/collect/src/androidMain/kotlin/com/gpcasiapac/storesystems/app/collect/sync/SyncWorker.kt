package com.gpcasiapac.storesystems.app.collect.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gpcasiapac.storesystems.core.sync_queue.api.SyncHandler
import com.gpcasiapac.storesystems.core.sync_queue.api.exceptions.PermanentFailureException
import com.gpcasiapac.storesystems.core.sync_queue.api.exceptions.RetryAfterException
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTask
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTaskAttemptError
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskStatus
import com.gpcasiapac.storesystems.core.sync_queue.domain.registry.SyncHandlerRegistry
import com.gpcasiapac.storesystems.core.sync_queue.domain.repository.SyncRepository
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SyncWorker(
    appContext: Context,
    params: WorkerParameters,
    handlers: List<SyncHandler>,
    private val repo: SyncRepository,
) : CoroutineWorker(appContext, params) {

    private val registry = SyncHandlerRegistry(handlers)

    override suspend fun doWork(): Result = runCatching {
        drainOnce(batch = 50)
    }.fold(onSuccess = { Result.success() }, onFailure = { Result.retry() })

    private suspend fun drainOnce(batch: Int) {
        var processed = 0
        while (processed < batch) {
            val task = repo.getNextPendingTask() ?: break
            val handler = registry.get(task.taskType)
            if (handler == null) {
                repo.updateTaskStatus(
                    taskId = task.id,
                    status = TaskStatus.REQUIRES_ACTION,
                    errorAttempt = SyncTaskAttemptError(
                        attemptNumber = task.noOfAttempts + 1,
                        timestamp = kotlin.time.Clock.System.now(),
                        errorMessage = "No handler for ${task.taskType}"
                    )
                )
                processed++; continue
            }

            repo.updateTaskStatus(task.id, TaskStatus.IN_PROGRESS)
            repo.incrementTaskAttempt(task.id)

            val r = handler.handle(task)
            if (r.isSuccess) {
                repo.updateTaskStatus(task.id, TaskStatus.COMPLETED)
            } else {
                handleFailure(task, r.exceptionOrNull())
            }
            processed++
        }
    }

    private suspend fun handleFailure(task: SyncTask, e: Throwable?) {
        val now = kotlin.time.Clock.System.now()
        val attempt = task.noOfAttempts + 1
        val maxAttempts = task.maxAttempts
        when (e) {
            is PermanentFailureException -> {
                repo.updateTaskStatus(
                    task.id, TaskStatus.REQUIRES_ACTION,
                    SyncTaskAttemptError(attempt, now, e.message ?: e.code)
                )
            }
            is RetryAfterException -> {
                if (attempt >= maxAttempts) {
                    repo.updateTaskStatus(
                        task.id, TaskStatus.REQUIRES_ACTION,
                        SyncTaskAttemptError(attempt, now, e.reason ?: "retry limit reached")
                    )
                } else {
                    repo.updateTaskStatus(
                        task.id, TaskStatus.FAILED,
                        SyncTaskAttemptError(attempt, now, e.reason ?: "retry")
                    )
                }
            }
            else -> {
                if (attempt >= maxAttempts) {
                    repo.updateTaskStatus(
                        task.id, TaskStatus.REQUIRES_ACTION,
                        SyncTaskAttemptError(attempt, now, e?.message ?: "retry limit reached")
                    )
                } else {
                    repo.updateTaskStatus(
                        task.id, TaskStatus.FAILED,
                        SyncTaskAttemptError(attempt, now, e?.message ?: "retry")
                    )
                }
            }
        }
    }
}
