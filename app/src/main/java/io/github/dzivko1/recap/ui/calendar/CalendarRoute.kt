package io.github.dzivko1.recap.ui.calendar

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object CalendarRoute

fun NavGraphBuilder.calendarRoute() {
  composable<CalendarRoute> {

  }
}