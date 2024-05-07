package io.github.dzivko1.recap

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.github.dzivko1.recap.data.record.RecordRepository
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

  @Inject
  lateinit var recordRepository: RecordRepository

  override fun onCreate() {
    super.onCreate()

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }

//    GlobalScope.launch {
//      migrateData(this@App, recordRepository)
//    }
  }
}