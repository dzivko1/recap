package io.github.dzivko1.recap.ui.records

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import java.time.LocalDate

const val RECORDS_ROUTE = "records"

fun NavGraphBuilder.recordsScreen(
  onDaySelect: (LocalDate) -> Unit,
) {
  composable(RECORDS_ROUTE) {
    val viewModel = hiltViewModel<RecordsViewModel>()
    val records by viewModel.recordsFlow.collectAsState()

    RecordsScreen(
      records = records,
      onDaySelect = onDaySelect
    )
  }
}