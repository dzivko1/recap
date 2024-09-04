package io.github.dzivko1.recap.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import io.github.dzivko1.recap.ui.calendar.calendarSection
import io.github.dzivko1.recap.ui.records.RecordsSectionRoute
import io.github.dzivko1.recap.ui.records.recordsSection
import io.github.dzivko1.recap.ui.reminders.remindersSection

@Composable
fun AppNavHost(
  modifier: Modifier = Modifier,
  navController: NavHostController = rememberNavController(),
) {
  NavHost(
    navController = navController,
    startDestination = RecordsSectionRoute,
    modifier = modifier
  ) {
    recordsSection(navController)
    calendarSection(navController)
    remindersSection(navController)
  }
}