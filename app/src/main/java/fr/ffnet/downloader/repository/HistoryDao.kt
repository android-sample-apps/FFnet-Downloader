package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInHistory(history: HistoryEntity)

    @Query("SELECT * FROM HistoryEntity")
    fun getHistory(): LiveData<List<HistoryEntity>>

}
