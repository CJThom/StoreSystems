package com.gpcasiapac.storesystems.app.collect.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gpcasiapac.storesystems.core.sync_queue.domain.SyncHandler
import com.gpcasiapac.storesystems.core.sync_queue.domain.exceptions.PermanentFailureException
import com.gpcasiapac.storesystems.core.sync_queue.domain.exceptions.RetryAfterException
import com.gpcasiapac.storesystems.core.sync_queue.domain.model.SyncTaskAttemptError
import com.gpcasiapac.storesystems.core.sync_queue.domain.model.TaskStatus
import com.gpcasiapac.storesystems.core.sync_queue.domain.registry.SyncHandlerRegistry
import com.gpcasiapac.storesystems.core.sync_queue.domain.repository.SyncRepository
import kotlin.time.Clock
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
                        timestamp = Clock.System.now(),
                        errorMessage = "No handler for ${task.taskType}"
                    )
                )
                processed++; continue
            }

            repo.updateTaskStatus(task.id, TaskStatus.IN_PROGRESS)
            repo.incrementTaskAttempt(task.id)

            when (val r = handler.handle(task)) {
                is Result.Success -> repo.updateTaskStatus(task.id, TaskStatus.COMPLETED)
                is Result.Failure -> handleFailure(task.id, task.noOfAttempts + 1, r.exceptionOrNull())
            }
            processed++
        }
    }

    private suspend fun handleFailure(taskId: String, attempt: Int, e: Throwable?) {
        val now = Clock.System.now()
        when (e) {
            is PermanentFailureException -> {
                repo.updateTaskStatus(
                    taskId, TaskStatus.REQUIRES_ACTION,
                    SyncTaskAttemptError(attempt, now, e.message ?: e.code)
                )
            }
            is RetryAfterException -> {
                if (attempt >= 3) { // use entity's maxAttempts in future if exposed by repo
                    repo.updateTaskStatus(
                        taskId, TaskStatus.REQUIRES_ACTION,
                        SyncTaskAttemptError(attempt, now, e.reason ?: "retry limit reached")
                    )
                } else {
                    repo.updateTaskStatus(
                        taskId, TaskStatus.FAILED,
                        SyncTaskAttemptError(attempt, now, e.reason ?: "retry")
                    )
                }
            }
            else -> {
                if (attempt >= 3) {
                    repo.updateTaskStatus(
                        taskId, TaskStatus.REQUIRES_ACTION,
                        SyncTaskAttemptError(attempt, now, e?.message ?: "retry limit reached")
                    )
                } else {
                    repo.updateTaskStatus(
                        taskId, TaskStatus.FAILED,
                        SyncTaskAttemptError(attempt, now, e?.message ?: "retry")
                    )
                }
            }
        }
    }
}
