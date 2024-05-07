package io.github.dzivko1.recap.migration

import android.content.Context
import androidx.room.Room
import io.github.dzivko1.recap.data.record.RecordRepository
import kotlinx.coroutines.flow.first

suspend fun migrateData(context: Context, recordRepository: RecordRepository) {
  val db = Room.databaseBuilder(
    context,
    Database::class.java, "app_database.db"
  ).build()

  val dayDao = db.dayDao()
  val data = dayDao.getAllWithContent().first()

  data.sortedBy { it.day.date }
    .forEach { dayContent ->
      dayContent.orderedRecords
        .forEach { record ->
          recordRepository.saveRecord(
            date = dayContent.day.date,
            text = record.text
          )
        }
    }
}