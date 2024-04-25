package io.github.dzivko1.recap.model

import com.google.firebase.firestore.DocumentId
import java.time.LocalDate

data class Record(
  val id: String,
  val date: LocalDate,
  val text: String,
)

data class RecordApiModel(
  @DocumentId
  val id: String = "",
  val epochDay: Long = 0,
  val text: String = "",
)

fun Record.toApiModel(): RecordApiModel {
  return RecordApiModel(
    id = id,
    epochDay = date.toEpochDay(),
    text = text
  )
}

fun RecordApiModel.toDomainModel(): Record {
  return Record(
    id = id,
    date = LocalDate.ofEpochDay(epochDay),
    text = text
  )
}
