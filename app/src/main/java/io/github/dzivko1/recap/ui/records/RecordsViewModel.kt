package io.github.dzivko1.recap.ui.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.dzivko1.recap.data.record.RecordRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RecordsViewModel @Inject constructor(
  private val recordRepository: RecordRepository,
) : ViewModel() {

  val uiState = recordRepository.getRecordsFlow()
    .map { records -> RecordsUiState(records) }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = RecordsUiState()
    )
}