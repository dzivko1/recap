package io.github.dzivko1.recap.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class RecapNudgeWorker @AssistedInject constructor(
  @Assisted context: Context,
  @Assisted workerParameters: WorkerParameters,
  private val notificationHandler: NotificationHandler,
) : Worker(context, workerParameters) {

  override fun doWork(): Result {
    Timber.i("Recap nudge triggered.")
    notificationHandler.sendRecapNudge()
    return Result.success()
  }
}