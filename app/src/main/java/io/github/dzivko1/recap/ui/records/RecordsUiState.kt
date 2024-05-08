package io.github.dzivko1.recap.ui.records

import io.github.dzivko1.recap.model.Record

data class RecordsUiState(
  val records: List<Record>? = null,
)