package com.gpcasiapac.storesystems.core.sync_queue.domain.coordinator

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.gpcasiapac.storesystems.core.sync_queue.api.SyncHandler
import com.gpcasiapac.storesystems.core.sync_queue.api.exceptions.PermanentFailureException
import com.gpcasiapac.storesystems.core.sync_queue.api.exceptions.RetryAfterException
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTask
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTaskAttemptError
import com.gpcasiapac.storesystems.core.sync_queue.api.model.TaskStatus
import com.gpcasiapac.storesystems.core.sync_queue.domain.registry.SyncHandlerRegistry
import com.gpcasiapac.storesystems.core.sync_queue.domain.repository.SyncRepository
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalTime::class)
class SyncWorker(
    appContext: Context,
    params: WorkerParameters,
    handlers: List<SyncHandler>,
    private val repo: SyncRepository,
) : CoroutineWorker(appContext, params) {

    private val registry = SyncHandlerRegistry(handlers)

    // If any retryable failure occurred during this run, we ask WorkManager to retry with backoff
    private var shouldRetryLater: Boolean = false
    private var minDelayMs: Long? = null

    override suspend fun doWork(): Result = runCatching {
        drainOnce(batch = 8)
        // Schedule a precise delayed run if handlers suggested a delay
        minDelayMs?.let { delay ->
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("sync_pump")
                .build()
            WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                "sync_pump",
                ExistingWorkPolicy.KEEP,
                request
            )
        }
        // If we encountered any retryable failures (attempt < maxAttempts), request a retry with backoff
        if (shouldRetryLater && minDelayMs == null) Result.retry() else Result.success()
    }.getOrElse { Result.retry() }

    private suspend fun drainOnce(batch: Int) {
        var processed = 0
        while (processed < batch) {
            val task = repo.getNextPendingTask() ?: break
            val handler = registry.get(task.taskType)
            if (handler == null) {
                repo.updateTaskStatus(
                    taskId = task.id,
                    status = TaskStatus.REQUIRES_ACTION,
                    lastErrorMessage = "No handler for ${task.taskType}"
                )
                processed++; continue
            }

            val attemptNumber = repo.startAttempt(task.id) ?: run {
                // Someone else took it or max attempts reached; skip
                processed++
                continue
            }

            val r = handler.handle(task)
            if (r.isSuccess) {
                repo.updateTaskStatus(task.id, TaskStatus.COMPLETED, lastErrorMessage = null)
            } else {
                handleFailure(task, r.exceptionOrNull(), attemptNumber)
            }
            processed++
        }
    }

    private suspend fun handleFailure(task: SyncTask, e: Throwable?, attempt: Int) {
        val now = Clock.System.now()
        val maxAttempts = task.maxAttempts
        when (e) {
            is PermanentFailureException -> {
                repo.updateTaskStatus(
                    taskId = task.id, status = TaskStatus.REQUIRES_ACTION,
                    lastErrorMessage = e.message ?: e.code
                )
            }

            is RetryAfterException -> {
                if (attempt >= maxAttempts) {
                    repo.updateTaskStatus(
                        taskId = task.id, status = TaskStatus.REQUIRES_ACTION,
                        lastErrorMessage = "retry limit reached"
                    )
                } else {
                    // Mark as retryable; schedule next run with a precise delay if provided
                    shouldRetryLater = true
                    val hint = e.delayMs
                    if (hint > 0) {
                        minDelayMs = minDelayMs?.let { kotlin.math.min(it, hint) } ?: hint
                    }
                    repo.updateTaskStatus(
                        task.id, TaskStatus.FAILED,
                        e.reason ?: "retry"
                    )
                }
            }

            else -> {
                if (attempt >= maxAttempts) {
                    repo.updateTaskStatus(
                        taskId = task.id, status = TaskStatus.REQUIRES_ACTION,
                        lastErrorMessage = e?.message ?: "retry limit reached"
                    )
                } else {
                    // Generic failure: mark to retry via WorkManager backoff
                    shouldRetryLater = true
                    repo.updateTaskStatus(
                        taskId = task.id, status = TaskStatus.FAILED,
                        lastErrorMessage = e?.message ?: "retry"
                    )
                }
            }
        }
    }
}