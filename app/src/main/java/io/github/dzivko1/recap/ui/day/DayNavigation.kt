package io.github.dzivko1.recap.ui.day

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import java.time.LocalDate

const val DAY_ROUTE = "day"
private const val DAY_DATE_ARG = "date"

class DayArgs(val date: LocalDate) {
  constructor(savedStateHandle: SavedStateHandle) :
    this(
      LocalDate.ofEpochDay(
        (checkNotNull(savedStateHandle[DAY_DATE_ARG]) as String).toLong()
      )
    )
}

fun NavController.navigateToDay(date: LocalDate) {
  navigate("$DAY_ROUTE/${date.toEpochDay()}")
}

fun NavGraphBuilder.dayScreen() {
  composable(route = "$DAY_ROUTE/{$DAY_DATE_ARG}") {
    val viewModel = hiltViewModel<DayViewModel>()
    val records by viewModel.records.collectAsState()

    DayScreen(
      date = viewModel.date,
      records = records,
      onSaveRecord = viewModel::saveRecord,
      onDeleteRecord = viewModel::deleteRecord
    )
  }
}