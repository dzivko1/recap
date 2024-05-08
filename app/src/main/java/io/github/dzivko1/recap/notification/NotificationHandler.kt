package io.github.dzivko1.recap.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.dzivko1.recap.MainActivity
import io.github.dzivko1.recap.R
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NotificationHandler @Inject constructor(
  private val notificationManager: NotificationManagerCompat,
  private val workManager: WorkManager,
  @ApplicationContext private val context: Context,
) {
  fun createRecapNudgeChannel() {
    notificationManager.createNotificationChannel(
      NotificationChannelCompat.Builder(
        RECAP_NUDGE_CHANNEL_ID,
        NotificationManagerCompat.IMPORTANCE_DEFAULT
      )
        .setName(context.getString(R.string.recap_nudge_channel_name))
        .setDescription(context.getString(R.string.recap_nudge_channel_description))
        .build()
    )
  }

  fun sendRecapNudge() {
    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    val notification = NotificationCompat.Builder(context, RECAP_NUDGE_CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_recap_logo)
      .setContentTitle(context.getString(R.string.recap_nudge_notification_title))
      .setContentText(context.getString(R.string.recap_nudge_notification_text))
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .build()

    if (ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      Timber.i("Sending Recap nudge notification.")
      notificationManager.notify(RECAP_NUDGE_NOTIFICATION_ID, notification)
    } else {
      Timber.w("No notification permission granted for Recap nudge.")
    }
  }

  fun scheduleRecapNudges() {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, 21)
      set(Calendar.MINUTE, 0)
      if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
    }
    val initialDelay = target.timeInMillis - now.timeInMillis

    val workRequest = PeriodicWorkRequestBuilder<RecapNudgeWorker>(
      repeatInterval = 1,
      repeatIntervalTimeUnit = TimeUnit.DAYS,
      flexTimeInterval = RECAP_NUDGE_FLEX_TIME_INTERVAL_MILLIS,
      flexTimeIntervalUnit = TimeUnit.MILLISECONDS
    )
      .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
      .build()

    workManager.enqueueUniquePeriodicWork(
      RECAP_NUDGE_WORK_NAME,
      ExistingPeriodicWorkPolicy.UPDATE,
      workRequest
    )

    Timber.i("Scheduled periodic Recap nudge notification with initial delay of $initialDelay ms (${initialDelay / 60000} min).")
  }

  companion object {
    private const val RECAP_NUDGE_CHANNEL_ID = "recap-nudge"
    private const val RECAP_NUDGE_WORK_NAME = "recap-nudge"
    private const val RECAP_NUDGE_NOTIFICATION_ID = 1
    private val RECAP_NUDGE_FLEX_TIME_INTERVAL_MILLIS =
      (30 * 60 * 1000L).coerceAtLeast(PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS)
  }
}