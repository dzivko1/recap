package io.github.dzivko1.recap.ui.day

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.dzivko1.recap.ui.common.component.PageLoadingIndicator
import io.github.dzivko1.recap.ui.day.composable.DayScreen
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class DayRoute(
  val epochDay: Long,
  val startRecord: Boolean = false,
) {
  constructor(date: LocalDate, startRecord: Boolean) : this(date.toEpochDay(), startRecord)

  val date: LocalDate
    get() = LocalDate.ofEpochDay(epochDay)
}

fun NavGraphBuilder.dayRoute() {
  composable<DayRoute> {
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
      uiState = uiState,
      startRecordOnOpen = viewModel.shouldStartRecordOnOpen,
      onSaveRecord = viewModel::saveRecord,
      onDeleteRecord = viewModel::deleteRecord,
      onMoveRecord = viewModel::moveRecord
    )
  }
}