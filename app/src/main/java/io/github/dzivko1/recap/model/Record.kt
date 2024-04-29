package io.github.dzivko1.recap.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import io.github.dzivko1.recap.data.util.epochTimestamp
import java.time.LocalDate

data class Record(
  val id: String = "",
  val date: LocalDate,
  val text: String,
  val createdAt: Timestamp,
  val updatedAt: Timestamp?,
)

data class RecordApiModel(
  @DocumentId
  val id: String = "",
  val epochDay: Long = 0,
  val text: String = "",
  val createdAt: Timestamp = epochTimestamp(),
  val updatedAt: Timestamp? = null,
)

fun Record.toApiModel(): RecordApiModel {
  return RecordApiModel(
    id = id,
    epochDay = date.toEpochDay(),
    text = text,
    createdAt = createdAt,
    updatedAt = updatedAt
  )
}

fun RecordApiModel.toDomainModel(): Record {
  return Record(
    id = id,
    date = LocalDate.ofEpochDay(epochDay),
    text = text,
    createdAt = createdAt,
    updatedAt = updatedAt
  )
}
