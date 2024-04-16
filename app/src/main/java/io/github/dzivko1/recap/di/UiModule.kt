package io.github.dzivko1.recap.di

import com.firebase.ui.auth.AuthUI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UiModule {

  @Provides
  fun provideAuthUi(): AuthUI = AuthUI.getInstance()
}