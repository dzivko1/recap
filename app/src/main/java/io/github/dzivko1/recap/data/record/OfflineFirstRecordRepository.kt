package io.github.dzivko1.recap.data.record

import io.github.dzivko1.recap.model.Record
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class OfflineFirstRecordRepository @Inject constructor(
  private val firebaseDataSource: FirebaseDataSource,
) : RecordRepository {

  override fun getRecordsFlow(): Flow<List<Record>> {
    return firebaseDataSource.getRecordsFlow()
  }

  override suspend fun createRecord(date: LocalDate, text: String) {
    firebaseDataSource.createRecord(date, text)
  }
}