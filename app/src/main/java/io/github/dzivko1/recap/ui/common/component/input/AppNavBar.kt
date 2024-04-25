package io.github.dzivko1.recap.ui.common.component.input

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import io.github.dzivko1.recap.ui.TopLevelDestination
import io.github.dzivko1.recap.ui.common.asString

@Composable
fun AppNavBar(
  navController: NavHostController,
) {
  val backStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = backStackEntry?.destination

  NavigationBar {
    TopLevelDestination.entries.forEach { destination ->
      NavigationBarItem(
        icon = { Icon(destination.icon, contentDescription = null) },
        label = { Text(destination.label.asString()) },
        selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true,
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
