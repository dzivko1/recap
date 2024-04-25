package io.github.dzivko1.recap.data.record

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import io.github.dzivko1.recap.model.Record
import io.github.dzivko1.recap.model.RecordApiModel
import io.github.dzivko1.recap.model.toDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

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

  fun getRecordsFlow(): Flow<List<Record>> {
    return recordsCollection.dataObjects<RecordApiModel>().map { records ->
      records.map { it.toDomainModel() }
    }
  }

  suspend fun createRecord(
    date: LocalDate,
    text: String,
  ) {
    recordsCollection.add(
      RecordApiModel(
        epochDay = date.toEpochDay(),
        text = text
      )
    ).await()
  }
}