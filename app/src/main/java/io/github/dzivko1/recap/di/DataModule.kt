package io.github.dzivko1.recap.di

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.dzivko1.recap.data.record.OfflineFirstRecordRepository
import io.github.dzivko1.recap.data.record.RecordRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

  @Binds
  abstract fun bindRecordRepository(impl: OfflineFirstRecordRepository): RecordRepository

  companion object {
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    fun provideNotificationManagerCompat(
      @ApplicationContext context: Context,
    ): NotificationManagerCompat = NotificationManagerCompat.from(context)

    @Provides
    fun provideWorkManager(
      @ApplicationContext context: Context,
    ): WorkManager = WorkManager.getInstance(context)
  }
}