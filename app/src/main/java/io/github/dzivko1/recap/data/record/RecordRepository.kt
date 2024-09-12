package io.github.dzivko1.recap.data.record

import io.github.dzivko1.recap.model.Record
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface RecordRepository {

  fun getRecordsFlow(): Flow<List<Record>>

  suspend fun loadMoreRecords(count: Int = RECORD_PAGE_SIZE)

  fun getDayRecordsFlow(date: LocalDate): Flow<List<Record>>

  suspend fun saveRecord(
    id: String? = null,
    date: LocalDate,
    text: String,
  )

  suspend fun deleteRecord(id: String)

  fun setRecordsOrder(records: List<Record>)

  suspend fun migrate()

  companion object {
    const val INITIAL_RECORD_LOAD_COUNT = 40
    const val RECORD_PAGE_SIZE = 30
  }
}