package io.github.dzivko1.recap.migration

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDate

@Database(entities = [Day::class, Record::class], version = 3)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
  abstract fun dayDao(): DayDao
}

class Converters {
  @TypeConverter
  fun dateToEpochDay(date: LocalDate): Long {
    return date.toEpochDay()
  }

  @TypeConverter
  fun epochDayToDate(epochDay: Long): LocalDate {
    return LocalDate.ofEpochDay(epochDay)
  }
}