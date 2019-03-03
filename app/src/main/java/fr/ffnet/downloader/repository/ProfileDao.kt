package fr.ffnet.downloader.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProfile(profileEntity: ProfileEntity)

    @Query("UPDATE ProfileEntity SET myFavoritesList = :myFavorites, myStoriesList = :myStories WHERE profileId = :profileId")
    fun updateProfile(profileId: String, myFavorites: String, myStories: String)

    @Query("SELECT * FROM ProfileEntity WHERE profileId = :profileId")
    fun getProfile(profileId: String): ProfileEntity?

    @Query("SELECT * FROM ProfileEntity")
    fun getProfile(): List<ProfileEntity>

}
