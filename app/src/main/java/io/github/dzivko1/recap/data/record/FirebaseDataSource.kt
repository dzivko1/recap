package io.github.dzivko1.recap.data.record

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObjects
import io.github.dzivko1.recap.model.Record
import io.github.dzivko1.recap.model.RecordApiModel
import io.github.dzivko1.recap.model.toApiModel
import io.github.dzivko1.recap.model.toDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataSource @Inject constructor(
  private val db: FirebaseFirestore,
  private val auth: FirebaseAuth,
) {

  private val recordsCollection
    get() = getCollectionForUser("records")

  private fun getCollectionForUser(collectionPath: String): CollectionReference {
    val user = checkNotNull(auth.currentUser) { "Firebase user not logged in." }
    return db.collection("users/${user.uid}/$collectionPath")
  }

  suspend fun getRecords(startAfter: Record?, count: Int): List<Record> {
    return recordsCollection
      .orderBy("epochDay", Query.Direction.DESCENDING)
      .orderBy("index", Query.Direction.ASCENDING)
      .let {
        if (startAfter != null) {
          it.startAfter(startAfter.date.toEpochDay(), startAfter.index)
        } else it
      }
      .limit(count.toLong())
      .get().await()
      .toObjects<RecordApiModel>()
      .map { it.toDomainModel() }
  }

  fun getDayRecordsFlow(date: LocalDate): Flow<List<Record>> {
    return recordsCollection
      .whereEqualTo("epochDay", date.toEpochDay())
      .orderBy("index")
      .dataObjects<RecordApiModel>()
      .map { records -> records.map { it.toDomainModel() } }
  }

  suspend fun createRecord(
    date: LocalDate,
    text: String,
  ): Record {
    val dayRecordCount = recordsCollection
      .whereEqualTo("epochDay", date.toEpochDay())
      .count()
      .get(AggregateSource.SERVER)
      .await().count.toInt()

    val record = Record(
      index = dayRecordCount,
      date = date,
      text = text,
      createdAt = Timestamp.now(),
      updatedAt = null
    )

    val id = recordsCollection.add(record.toApiModel()).await().id
    return record.copy(id = id)
  }

  suspend fun editRecord(
    id: String,
    date: LocalDate,
    text: String,
  ) {
    recordsCollection.document(id).set(
      mapOf(
        "epochDay" to date.toEpochDay(),
        "text" to text,
        "updatedAt" to Timestamp.now()
      ),
      SetOptions.merge()
    ).await()
  }

  suspend fun deleteRecord(id: String) {
    recordsCollection.document(id).delete().await()
  }

  fun setRecordsOrderAsync(records: List<Record>) {
    records.forEachIndexed { index, record ->
      recordsCollection.document(record.id).set(
        mapOf(
          "index" to index
        ),
        SetOptions.merge()
      )
    }
  }
}