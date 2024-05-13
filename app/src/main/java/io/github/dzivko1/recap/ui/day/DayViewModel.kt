package io.github.dzivko1.recap.ui.day

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.dzivko1.recap.data.record.RecordRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DayViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val recordRepository: RecordRepository,
) : ViewModel() {

  private val args = DayArgs(savedStateHandle)

  val date = args.date
  val shouldStartRecordOnOpen = args.startRecord

  var uiState by mutableStateOf(DayUiState())
    private set

  private var recordMoveJob: Job? = null
  private var loaded = false

  fun loadData() {
    if (loaded) return
    viewModelScope.launch {
      recordRepository.getDayRecordsFlow(date).collect { records ->
        uiState = uiState.copy(
          isLoading = false,
          records = records
        )
      }
    }
    loaded = true
  }

  override fun onCleared() {
    super.onCleared()
    recordMoveJob?.cancel()
    if (recordMoveJob != null) {
      recordRepository.setRecordsOrder(uiState.records)
    }
  }

  fun saveRecord(id: String?, text: String) {
    viewModelScope.launch {
      recordRepository.saveRecord(id, date, text)
    }
  }

  fun deleteRecord(id: String) {
    viewModelScope.launch {
      recordRepository.deleteRecord(id)
      recordRepository.setRecordsOrder(uiState.records)
    }
  }

  fun moveRecord(from: Int, to: Int) {
    if (from !in uiState.records.indices || to !in uiState.records.indices) return

    uiState = uiState.copy(
      records = uiState.records.toMutableList().apply {
        add(to, removeAt(from))
      }
    )

    recordMoveJob?.cancel()
    recordMoveJob = viewModelScope.launch {
      delay(4000)
      recordRepository.setRecordsOrder(uiState.records)
      recordMoveJob = null
    }
  }
}