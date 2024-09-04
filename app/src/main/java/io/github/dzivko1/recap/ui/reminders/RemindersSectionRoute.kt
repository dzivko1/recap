package io.github.dzivko1.recap.ui.reminders

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
object RemindersSectionRoute

fun NavGraphBuilder.remindersSection(navController: NavHostController) {
  navigation<RemindersSectionRoute>(startDestination = RemindersRoute) {
    remindersRoute()
  }
}