package io.github.dzivko1.recap.ui.day

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import java.time.LocalDate

const val DAY_ROUTE = "day"
private const val DAY_DATE_ARG = "date"

fun NavController.navigateToDay(date: LocalDate) {
  navigate("$DAY_ROUTE/${date.toEpochDay()}")
}

fun NavGraphBuilder.dayScreen() {
  composable(route = "$DAY_ROUTE/{$DAY_DATE_ARG}") {

  }
}