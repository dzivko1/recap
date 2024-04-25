package io.github.dzivko1.recap.ui.reminders

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation

const val REMINDERS_SECTION_ROUTE = "reminders-section"

fun NavGraphBuilder.remindersSection(navController: NavHostController) {
  navigation(route = REMINDERS_SECTION_ROUTE, startDestination = REMINDERS_ROUTE) {
    remindersScreen()
  }
}