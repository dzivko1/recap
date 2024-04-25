package io.github.dzivko1.recap.ui.records

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation

const val RECORDS_SECTION_ROUTE = "records-section"

fun NavGraphBuilder.recordsSection(navController: NavHostController) {
  navigation(route = RECORDS_SECTION_ROUTE, startDestination = RECORDS_ROUTE) {
    recordsScreen()
  }
}