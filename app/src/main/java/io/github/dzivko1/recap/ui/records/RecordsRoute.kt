package io.github.dzivko1.recap.ui.records

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.dzivko1.recap.ui.common.component.PageLoadingIndicator
import io.github.dzivko1.recap.ui.records.composable.RecordsScreen
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
object RecordsRoute

fun NavGraphBuilder.recordsRoute(
  onDaySelect: (LocalDate, startRecord: Boolean) -> Unit,
) {
  composable<RecordsRoute> {
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
      onRequestMoreRecords = viewModel::loadMoreRecords,
      onFilterToggle = viewModel::toggleTagFilter,
      onFiltersConfirm = viewModel::applyTagFilters
    )
  }
}