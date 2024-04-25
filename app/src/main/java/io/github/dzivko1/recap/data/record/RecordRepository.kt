package io.github.dzivko1.recap.data.record

import io.github.dzivko1.recap.model.Record
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface RecordRepository {
  fun getRecordsFlow(): Flow<List<Record>>

  suspend fun createRecord(
    date: LocalDate,
    text: String,
  )
}