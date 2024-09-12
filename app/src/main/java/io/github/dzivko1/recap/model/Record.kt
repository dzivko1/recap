package io.github.dzivko1.recap.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import io.github.dzivko1.recap.data.util.epochTimestamp
import java.time.LocalDate

data class Record(
  val id: String = "",
  val index: Int,
  val date: LocalDate,
  val text: String,
  val tags: List<String>,
  val createdAt: Timestamp,
  val updatedAt: Timestamp?,
) {
  fun passesTagFilters(tagFilters: List<String>): Boolean {
    return tagFilters.isEmpty() || tags.any { tagFilters.contains(it) }
  }
}

data class RecordApiModel(
  @DocumentId
  val id: String = "",
  val index: Int = -1,
  val epochDay: Long = -1,
  val text: String = "",
  val tags: List<String> = emptyList(),
  val createdAt: Timestamp = epochTimestamp(),
  val updatedAt: Timestamp? = null,
)

fun Record.toApiModel(): RecordApiModel {
  return RecordApiModel(
    id = id,
    index = index,
    epochDay = date.toEpochDay(),
    text = text,
    tags = tags,
    createdAt = createdAt,
    updatedAt = updatedAt
  )
}

fun RecordApiModel.toDomainModel(): Record {
  return Record(
    id = id,
    index = index,
    date = LocalDate.ofEpochDay(epochDay),
    text = text,
    tags = tags,
    createdAt = createdAt,
    updatedAt = updatedAt
  )
}
