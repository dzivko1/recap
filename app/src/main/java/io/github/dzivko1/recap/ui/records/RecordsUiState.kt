package io.github.dzivko1.recap.ui.records

import io.github.dzivko1.recap.model.Record

data class RecordsUiState(
  val areRecordsLoading: Boolean = false,
  val recordLoadingError: Exception? = null,
  val records: List<Record>? = null,
  val tagFilters: List<TagFilter> = emptyList(),
)

data class TagFilter(
  val name: String,
  val isSelected: Boolean,
)