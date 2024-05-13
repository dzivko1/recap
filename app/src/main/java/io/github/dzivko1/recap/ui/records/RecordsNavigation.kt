package io.github.dzivko1.recap.ui.records

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.dzivko1.recap.ui.common.component.PageLoadingIndicator
import java.time.LocalDate

const val RECORDS_ROUTE = "records"

fun NavGraphBuilder.recordsScreen(
  onDaySelect: (LocalDate, startRecord: Boolean) -> Unit,
) {
  composable(RECORDS_ROUTE) {
    val viewModel = hiltViewModel<RecordsViewModel>()
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
      viewModel.loadData()
    }

    if (uiState.records == null) {
      PageLoadingIndicator()
    }

    RecordsScreen(
      uiState = uiState,
      onDaySelect = onDaySelect,
      onRequestMoreRecords = viewModel::loadMoreRecords
    )
  }
}