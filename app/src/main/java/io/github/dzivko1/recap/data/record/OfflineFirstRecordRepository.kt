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

  override fun getDayRecordsFlow(date: LocalDate): Flow<List<Record>> {
    return firebaseDataSource.getDayRecordsFlow(date)
  }

  override suspend fun saveRecord(id: String?, date: LocalDate, text: String) {
    if (id == null) {
      firebaseDataSource.createRecord(date, text)
    } else {
      firebaseDataSource.editRecord(id, date, text)
    }
  }

  override suspend fun deleteRecord(id: String) {
    firebaseDataSource.deleteRecord(id)
  }
}