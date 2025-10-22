package com.gpcasiapac.storesystems.core.sync_queue.api.model

enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    RETRYING,
    REQUIRES_ACTION
}
