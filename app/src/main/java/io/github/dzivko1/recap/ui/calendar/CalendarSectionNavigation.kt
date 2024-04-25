package io.github.dzivko1.recap.ui.calendar

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation

const val CALENDAR_SECTION_ROUTE = "calendar-section"

fun NavGraphBuilder.calendarSection(navController: NavHostController) {
  navigation(route = CALENDAR_SECTION_ROUTE, startDestination = CALENDAR_ROUTE) {
    calendarScreen()
  }
}