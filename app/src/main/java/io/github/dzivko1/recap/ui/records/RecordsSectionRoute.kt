package io.github.dzivko1.recap.ui.records

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import io.github.dzivko1.recap.ui.day.DayRoute
import io.github.dzivko1.recap.ui.day.dayRoute
import kotlinx.serialization.Serializable

@Serializable
object RecordsSectionRoute

fun NavGraphBuilder.recordsSection(navController: NavHostController) {
  navigation<RecordsSectionRoute>(startDestination = RecordsRoute) {
    recordsRoute(
      onDaySelect = { date, startRecord -> navController.navigate(DayRoute(date, startRecord)) }
    )

    dayRoute()
  }
}