package io.github.dzivko1.recap.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
  @Provides
  fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}