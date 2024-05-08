package io.github.dzivko1.recap.ui.records

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.dzivko1.recap.ui.common.component.PageLoadingIndicator
import java.time.LocalDate

const val RECORDS_ROUTE = "records"

fun NavGraphBuilder.recordsScreen(
  onDaySelect: (LocalDate) -> Unit,
) {
  composable(RECORDS_ROUTE) {
    val viewModel = hiltViewModel<RecordsViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.records == null) {
      PageLoadingIndicator()
    }

    RecordsScreen(
      records = uiState.records ?: emptyList(),
      onDaySelect = onDaySelect
    )
  }
}