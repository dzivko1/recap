package io.github.dzivko1.recap.ui.day

import io.github.dzivko1.recap.model.Record

data class DayUiState(
  val isLoading: Boolean = true,
  val records: List<Record> = emptyList(),
  val availableTags: List<String> = emptyList(),
)