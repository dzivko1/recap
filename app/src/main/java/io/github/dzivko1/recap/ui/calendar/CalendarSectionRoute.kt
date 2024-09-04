package io.github.dzivko1.recap.ui.calendar

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
object CalendarSectionRoute

fun NavGraphBuilder.calendarSection(navController: NavHostController) {
  navigation<CalendarSectionRoute>(startDestination = CalendarRoute) {
    calendarRoute()
  }
}