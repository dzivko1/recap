package io.github.dzivko1.recap.data.record

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import io.github.dzivko1.recap.model.*
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

  private val userId: String
    get() = checkNotNull(auth.currentUser?.uid) { "Firebase user not logged in." }

  private val userRef: DocumentReference
    get() = db.document("users/$userId")

  private suspend fun getUser(): RecapUser {
    return userRef.get().await().toObject<RecapUserApiModel>()?.toDomainModel()
      ?: RecapUser(id = userId, tagSummary = TagSummary(emptyList()))
  }

  private fun Transaction.getUser(): RecapUser {
    return get(userRef).toObject<RecapUserApiModel>()?.toDomainModel()
      ?: RecapUser(id = userId, tagSummary = TagSummary(emptyList()))
  }

  private val recordsCollection: CollectionReference
    get() = getCollectionForUser("records")

  private fun getCollectionForUser(collectionPath: String): CollectionReference {
    return db.collection("users/$userId/$collectionPath")
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
    tags: List<String>,
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
      tags = tags,
      createdAt = Timestamp.now(),
      updatedAt = null
    )

    val recordRef = recordsCollection.document()
    db.runTransaction { transaction ->
      val tagSummary = transaction.getUser().tagSummary.withNewTags(tags)
      transaction.set(userRef, mapOf("tags" to tagSummary.tags), SetOptions.merge())
      transaction.set(recordRef, record.toApiModel())
    }.await()

    return record.copy(id = recordRef.id)
  }

  suspend fun editRecord(
    id: String,
    date: LocalDate,
    text: String,
    newTags: List<String>,
    oldTags: List<String>,
  ) {
    db.runTransaction { transaction ->
      val tagSummary = transaction.getUser().tagSummary
        .withRemovedTags(oldTags)
        .withNewTags(newTags)

      transaction.update(
        recordsCollection.document(id),
        mapOf(
          "epochDay" to date.toEpochDay(),
          "text" to text,
          "tags" to newTags,
          "updatedAt" to Timestamp.now()
        )
      )
      transaction.update(userRef, "tags", tagSummary.tags)
    }.await()
  }

  suspend fun deleteRecord(id: String, tags: List<String>) {
    // Doing this in a transaction doesn't seem to trigger the snapshot listener for some reason
    // (deletion not reflected in the UI)
    val tagSummary = getUser().tagSummary.withRemovedTags(tags)
    db.runBatch { batch ->
      batch.delete(recordsCollection.document(id))
      batch.set(userRef, mapOf("tags" to tagSummary.tags), SetOptions.merge())
    }.await()
  }

  fun setRecordsOrderAsync(records: List<Record>) {
    records.forEachIndexed { index, record ->
      recordsCollection.document(record.id)
        .set(
          mapOf("index" to index),
          SetOptions.merge()
        )
    }
  }
}