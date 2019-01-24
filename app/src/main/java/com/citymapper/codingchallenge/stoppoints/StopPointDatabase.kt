package com.citymapper.codingchallenge.stoppoints

import androidx.room.*

@Entity
data class StopPointDb(
    @PrimaryKey val id: String = "id",
    val name: String = "name",
    var arrivalTimes: List<ArrivalDb> = emptyList()
)

@Entity
data class ArrivalDb(
    val id: String = "id",
    val time: Int = 10
)

@Database(entities = [StopPointDb::class], version = 1)
@TypeConverters(ArrivalTimeConverter.class)
abstract class StopPointDatabase : RoomDatabase() {
    abstract fun stopPointDao(): StopPointDao
}

@Dao
interface StopPointDao {
    @Insert
    fun addAll(stopPoints: List<StopPointDb>)

    @Query("SELECT * FROM stoppointdb")
    fun getAll(): List<StopPointDb>

    @Query("SELECT * FROM stoppointdb WHERE id = :id")
    fun get(id: String): StopPointDb?

    @Update
    fun update(stopPoint: StopPointDb)

    @Query("DELETE FROM stoppointdb")
    fun deleteAll()
}
