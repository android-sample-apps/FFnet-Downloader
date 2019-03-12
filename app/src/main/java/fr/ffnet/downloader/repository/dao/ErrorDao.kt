package fr.ffnet.downloader.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.ffnet.downloader.repository.entities.ErrorEntity

@Dao
interface ErrorDao {

    @Insert
    fun insertError(error: ErrorEntity)

    @Query("SELECT * FROM ErrorEntity ORDER BY date ASC LIMIT 1")
    fun getErrors(): LiveData<ErrorEntity>

    @Query("DELETE FROM ErrorEntity WHERE id = :errorId")
    fun deleteError(errorId: Int)

}
