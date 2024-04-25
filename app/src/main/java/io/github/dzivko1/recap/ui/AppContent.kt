package io.github.dzivko1.recap.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import io.github.dzivko1.recap.ui.common.component.input.AppNavBar
import io.github.dzivko1.recap.ui.theme.RecapTheme

@Composable
fun AppContent() {
  val navController = rememberNavController()

  RecapTheme {
    Scaffold(
      bottomBar = {
        AppNavBar(navController)
      }
    ) { contentPadding ->
      AppNavHost(
        modifier = Modifier.padding(contentPadding),
        navController = navController
      )
    }
  }
}