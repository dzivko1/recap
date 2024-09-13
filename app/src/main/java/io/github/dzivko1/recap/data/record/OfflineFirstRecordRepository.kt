package io.github.dzivko1.recap.data.record

import io.github.dzivko1.recap.model.Record
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineFirstRecordRepository @Inject constructor(
  private val firebaseDataSource: FirebaseDataSource,
) : RecordRepository {

  private val _recordsFlow: MutableStateFlow<List<Record>?> = MutableStateFlow(null)
  private val recordsFlow: StateFlow<List<Record>?> = _recordsFlow.asStateFlow()

  private val _tagFiltersFlow: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
  private val tagFiltersFlow: StateFlow<List<String>> = _tagFiltersFlow.asStateFlow()

  override fun getRecordsFlow(): Flow<List<Record>?> {
    return recordsFlow.onSubscription {
      if (recordsFlow.value.isNullOrEmpty()) {
        runCatching {
          loadMoreRecords(count = RecordRepository.INITIAL_RECORD_LOAD_COUNT)
        }
      }
    }
  }

  override suspend fun loadMoreRecords(count: Int) {
    val loadedRecords = firebaseDataSource.getRecords(
      startAfter = recordsFlow.value?.lastOrNull(),
      count = count,
      tagFilters = tagFiltersFlow.value
    )
    _recordsFlow.value = _recordsFlow.value?.plus(loadedRecords) ?: loadedRecords
  }

  override fun getDayRecordsFlow(date: LocalDate): Flow<List<Record>> {
    return firebaseDataSource.getDayRecordsFlow(
      date = date,
      tagFilters = tagFiltersFlow.value
    )
  }

  override suspend fun saveRecord(id: String?, date: LocalDate, text: String) {
    val records = recordsFlow.value ?: run {
      Timber.e("Could not save record. No records were loaded previously.")
      return
    }
    if (id == null) {
      val record = firebaseDataSource.createRecord(date, text, extractTags(text))
      if (record.passesTagFilters(tagFiltersFlow.value)) {
        _recordsFlow.value = (records + record)
          .sortedWith(
            compareByDescending(Record::date).thenBy(Record::index)
          )
      }
    } else {
      val oldRecord = records.find { it.id == id }
        ?: run {
          Timber.e("Could not save existing record. Unable to find record with ID $id")
          return
        }
      val newRecord = oldRecord.copy(date = date, text = text, tags = extractTags(text))
      firebaseDataSource.editRecord(id, date, text, newRecord.tags, oldRecord.tags)
      if (newRecord.passesTagFilters(tagFiltersFlow.value)) {
        _recordsFlow.value = records.map {
          if (it.id == id) newRecord else it
        }
      } else {
        _recordsFlow.value = records - oldRecord
        reloadIfTagsDepleted()
      }
    }
  }

  override suspend fun deleteRecord(id: String) {
    val records = recordsFlow.value ?: run {
      Timber.e("Could not delete record. No records were loaded previously.")
      return
    }
    val record = records.find { it.id == id } ?: run {
      Timber.e("Could not delete record. Unable to find record with ID $id")
      return
    }
    firebaseDataSource.deleteRecord(id, extractTags(record.text))
    _recordsFlow.value = records - record
    reloadIfTagsDepleted()
  }

  override fun setRecordsOrder(records: List<Record>) {
    val allRecords = recordsFlow.value ?: run {
      Timber.e("Could not set records order. No records were loaded previously.")
      return
    }
    firebaseDataSource.setRecordsOrderAsync(records)
    _recordsFlow.value = allRecords.map { existingRecord ->
      val newIndex = records.indexOf(existingRecord)
      if (newIndex != -1) existingRecord.copy(index = newIndex) else existingRecord
    }.sortedWith(
      compareByDescending(Record::date).thenBy(Record::index)
    )
  }

  override fun getTagsFlow(): Flow<List<String>> {
    return firebaseDataSource.getTagsFlow()
  }

  override fun getActiveTagFiltersFlow() = tagFiltersFlow

  override suspend fun setActiveTagFilters(filters: List<String>) {
    if (filters == _tagFiltersFlow.value) return
    _tagFiltersFlow.value = filters
    _recordsFlow.value = null
    loadMoreRecords(count = RecordRepository.INITIAL_RECORD_LOAD_COUNT)
  }

  private fun extractTags(text: String): List<String> {
    return text.split(Regex("\\s+"))
      .filter { it.startsWith("#") && it.length > 1 }
      .map { it.substring(1) }
  }

  /**
   * While tag filters are active, if deleting or editing records leaves the view without any
   * records, this reloads the records without any tag filters.
   */
  private suspend fun reloadIfTagsDepleted() {
    if (recordsFlow.value?.isEmpty() == true) {
      _tagFiltersFlow.value = emptyList()
      loadMoreRecords(RecordRepository.INITIAL_RECORD_LOAD_COUNT)
    }
  }
}