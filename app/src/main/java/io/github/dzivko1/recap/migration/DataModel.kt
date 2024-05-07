package io.github.dzivko1.recap.migration

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Entity
data class Day(
  val date: LocalDate,

  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
)

@Dao
interface DayDao {
  @Transaction
  @Query("SELECT * FROM day ORDER BY date DESC")
  fun getAllWithContent(): Flow<List<DayContent>>
}

data class DayContent(
  @Embedded val day: Day,
  @Relation(
    parentColumn = "id",
    entityColumn = "dayID"
  ) val records: List<Record>,
) {
  val orderedRecords get() = records.sortedBy { it.order }
}

@Entity(
  foreignKeys = [ForeignKey(
    entity = Day::class,
    parentColumns = ["id"],
    childColumns = ["dayID"]
  )],
  indices = [Index("dayID")]
)
data class Record(
  var text: String,
  val dayID: Long,
  var order: Int = -1,

  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
)