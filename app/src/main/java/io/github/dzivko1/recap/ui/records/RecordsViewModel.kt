package io.github.dzivko1.recap.ui.records

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.dzivko1.recap.data.record.RecordRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordsViewModel @Inject constructor(
  private val recordRepository: RecordRepository,
) : ViewModel() {

  var uiState by mutableStateOf(RecordsUiState())
    private set

  private var loaded = false

  fun loadData() {
    if (loaded) return
    viewModelScope.launch {
      recordRepository.getRecordsFlow().collect {
        uiState = uiState.copy(records = it)
      }
    }
    viewModelScope.launch {
      recordRepository.getTagsFlow().collect { newTags ->
        val currentTags = uiState.tagFilters
        uiState = uiState.copy(
          tagFilters = newTags.map { tag ->
            TagFilter(
              name = tag,
              isSelected = currentTags.any { it.name == tag && it.isSelected }
            )
          }
        )
      }
    }
    loaded = true
  }

  fun loadMoreRecords() {
    uiState = uiState.copy(
      areRecordsLoading = true,
      recordLoadingError = null
    )

    viewModelScope.launch {
      try {
        recordRepository.loadMoreRecords()
      } catch (e: Exception) {
        uiState = uiState.copy(recordLoadingError = e)
      } finally {
        uiState = uiState.copy(areRecordsLoading = false)
      }
    }
  }

  fun toggleTagFilter(filter: TagFilter) {
    uiState = uiState.copy(
      tagFilters = uiState.tagFilters.map {
        if (it.name == filter.name) {
          it.copy(isSelected = !it.isSelected)
        } else {
          it
        }
      }
    )
  }

  fun applyTagFilters() {
    viewModelScope.launch {
      recordRepository.setActiveTagFilters(
        uiState.tagFilters
          .filter { it.isSelected }
          .map { it.name }
      )
    }
  }
}