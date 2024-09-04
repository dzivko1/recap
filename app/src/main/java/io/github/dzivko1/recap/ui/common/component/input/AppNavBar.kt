package io.github.dzivko1.recap.ui.common.component.input

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import io.github.dzivko1.recap.R
import io.github.dzivko1.recap.ui.calendar.CalendarSectionRoute
import io.github.dzivko1.recap.ui.common.TextResource
import io.github.dzivko1.recap.ui.common.asString
import io.github.dzivko1.recap.ui.records.RecordsSectionRoute
import io.github.dzivko1.recap.ui.reminders.RemindersSectionRoute

@Composable
fun AppNavBar(
  navController: NavHostController,
) {
  val backStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = backStackEntry?.destination

  NavigationBar {
    TopLevelDestination.entries.forEach { destination ->
      val routeQualifier = destination.route::class.qualifiedName ?: ""

      NavigationBarItem(
        icon = { Icon(destination.icon, contentDescription = null) },
        label = { Text(destination.label.asString()) },
        selected = currentDestination?.hierarchy?.any {
          it.route?.contains(routeQualifier) == true
        } == true,
        onClick = {
          navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
              saveState = true
            }
            launchSingleTop = true
            restoreState = true
          }
        }
      )
    }
  }
}

private enum class TopLevelDestination(
  val route: Any,
  val label: TextResource,
  val icon: ImageVector,
) {
  Records(
    route = RecordsSectionRoute,
    label = TextResource.fromStringRes(R.string.records_section_label),
    icon = Icons.Default.History
  ),
  Calendar(
    route = CalendarSectionRoute,
    label = TextResource.fromStringRes(R.string.calendar_section_label),
    icon = Icons.Default.CalendarMonth
  ),
  Reminders(
    route = RemindersSectionRoute,
    label = TextResource.fromStringRes(R.string.reminders_section_label),
    icon = Icons.Default.Alarm
  )
}