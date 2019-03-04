package fr.ffnet.downloader.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProfile(profileEntity: ProfileEntity)

    @Query("SELECT * FROM ProfileEntity WHERE profileId = :profileId")
    fun getProfile(profileId: String): ProfileEntity?

    @Query("SELECT * FROM ProfileEntity")
    fun getProfile(): List<ProfileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProfileFanfiction(favoriEntity: ProfileFanfictionEntity)

    @Query("DELETE FROM ProfileFanfictionEntity WHERE profileId = :profileId")
    fun deleteProfileMapping(profileId: String)

}
