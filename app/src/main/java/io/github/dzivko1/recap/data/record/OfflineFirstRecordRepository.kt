package io.github.dzivko1.recap.data.record

import io.github.dzivko1.recap.model.Record
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineFirstRecordRepository @Inject constructor(
  private val firebaseDataSource: FirebaseDataSource,
) : RecordRepository {

  private val _recordsFlow: MutableStateFlow<List<Record>> = MutableStateFlow(emptyList())
  private val recordsFlow: StateFlow<List<Record>> = _recordsFlow.asStateFlow()

  override fun getRecordsFlow(): Flow<List<Record>> {
    return recordsFlow.onSubscription {
      if (recordsFlow.value.isEmpty()) {
        runCatching {
          loadMoreRecords(count = RecordRepository.INITIAL_RECORD_LOAD_COUNT)
        }
      }
    }
  }

  override suspend fun loadMoreRecords(count: Int) {
    _recordsFlow.value += firebaseDataSource.getRecords(
      startAfter = _recordsFlow.value.lastOrNull(),
      count = count
    )
  }

  override fun getDayRecordsFlow(date: LocalDate): Flow<List<Record>> {
    return firebaseDataSource.getDayRecordsFlow(date)
  }

  override suspend fun saveRecord(id: String?, date: LocalDate, text: String) {
    if (id == null) {
      val record = firebaseDataSource.createRecord(date, text, extractTags(text))
      _recordsFlow.value = (_recordsFlow.value + record)
        .sortedWith(compareByDescending(Record::date).thenBy(Record::index))
    } else {
      val oldRecord = _recordsFlow.value.first { it.id == id }
      firebaseDataSource.editRecord(id, date, text, extractTags(text), extractTags(oldRecord.text))
      _recordsFlow.value = _recordsFlow.value.map {
        if (it.id == id) it.copy(date = date, text = text) else it
      }
    }
  }

  override suspend fun deleteRecord(id: String) {
    val record = _recordsFlow.value.find { it.id == id } ?: return
    firebaseDataSource.deleteRecord(id, extractTags(record.text))
    _recordsFlow.value -= record
  }

  override fun setRecordsOrder(records: List<Record>) {
    firebaseDataSource.setRecordsOrderAsync(records)
    _recordsFlow.value = _recordsFlow.value.map { existingRecord ->
      val newIndex = records.indexOf(existingRecord)
      if (newIndex != -1) existingRecord.copy(index = newIndex) else existingRecord
    }.sortedWith(compareByDescending(Record::date).thenBy(Record::index))
  }

  private fun extractTags(text: String): List<String> {
    return text.split(Regex("\\s+"))
      .filter { it.startsWith("#") }
      .map { it.substring(1) }
  }
}