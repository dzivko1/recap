package io.github.dzivko1.recap.ui.day

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.github.dzivko1.recap.ui.common.component.PageLoadingIndicator
import java.time.LocalDate

const val DAY_ROUTE = "day"
private const val DATE_ARG = "date"
private const val START_RECORD_ARG = "startRecord"

class DayArgs(
  val date: LocalDate,
  val startRecord: Boolean,
) {
  constructor(savedStateHandle: SavedStateHandle) :
    this(
      date = LocalDate.ofEpochDay(checkNotNull(savedStateHandle[DATE_ARG])),
      startRecord = checkNotNull(savedStateHandle[START_RECORD_ARG])
    )
}

fun NavController.navigateToDay(date: LocalDate, startRecord: Boolean) {
  navigate("$DAY_ROUTE/${date.toEpochDay()}?$START_RECORD_ARG=$startRecord")
}

fun NavGraphBuilder.dayScreen() {
  composable(
    route = "$DAY_ROUTE/{$DATE_ARG}?$START_RECORD_ARG={$START_RECORD_ARG}",
    arguments = listOf(
      navArgument(DATE_ARG) { type = NavType.LongType },
      navArgument(START_RECORD_ARG) { type = NavType.BoolType; defaultValue = false }
    )
  ) {
    val viewModel = hiltViewModel<DayViewModel>()
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
      viewModel.loadData()
    }

    if (uiState.isLoading) {
      PageLoadingIndicator()
    }

    DayScreen(
      date = viewModel.date,
      records = uiState.records,
      startRecordOnOpen = viewModel.shouldStartRecordOnOpen,
      onSaveRecord = viewModel::saveRecord,
      onDeleteRecord = viewModel::deleteRecord,
      onMoveRecord = viewModel::moveRecord
    )
  }
}