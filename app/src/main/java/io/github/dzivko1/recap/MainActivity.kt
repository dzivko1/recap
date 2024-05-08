package io.github.dzivko1.recap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.dzivko1.recap.notification.NotificationHandler
import io.github.dzivko1.recap.ui.AppContent
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @Inject
  lateinit var notificationHandler: NotificationHandler

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    notificationHandler.createRecapNudgeChannel()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
      ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.POST_NOTIFICATIONS
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
    }

    notificationHandler.scheduleRecapNudges()

    setContent {
      AppContent()
    }
  }
}
