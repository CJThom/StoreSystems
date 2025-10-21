package com.gpcasiapac.storesystems.app.collect.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.gpcasiapac.storesystems.app.collect.sync.SyncWorker
import com.gpcasiapac.storesystems.core.sync_queue.domain.SyncHandler
import com.gpcasiapac.storesystems.core.sync_queue.domain.repository.SyncRepository
import org.koin.core.context.GlobalContext

class AppWorkerFactory : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        params: WorkerParameters
    ): ListenableWorker? = when (workerClassName) {
        SyncWorker::class.qualifiedName -> {
            val koin = GlobalContext.get()
            val handlers: List<SyncHandler> = koin.getAll()
            val repo: SyncRepository = koin.get()
            SyncWorker(appContext, params, handlers, repo)
        }
        else -> null
    }
}
