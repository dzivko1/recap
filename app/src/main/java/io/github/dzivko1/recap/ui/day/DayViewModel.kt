package io.github.dzivko1.recap.ui.day

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.dzivko1.recap.data.record.RecordRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DayViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val recordRepository: RecordRepository,
) : ViewModel() {

  private val args = DayArgs(savedStateHandle)

  val date = args.date

  val records = recordRepository.getDayRecordsFlow(date)
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  fun saveRecord(id: String?, text: String) {
    viewModelScope.launch {
      recordRepository.saveRecord(id, date, text)
    }
  }

  fun deleteRecord(id: String) {
    viewModelScope.launch {
      recordRepository.deleteRecord(id)
    }
  }
}