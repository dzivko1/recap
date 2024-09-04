package io.github.dzivko1.recap.ui.reminders

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object RemindersRoute

fun NavGraphBuilder.remindersRoute() {
  composable<RemindersRoute> {

  }
}