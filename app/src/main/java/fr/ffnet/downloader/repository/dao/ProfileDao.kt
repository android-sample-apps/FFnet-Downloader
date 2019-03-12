package fr.ffnet.downloader.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.ffnet.downloader.repository.entities.ProfileEntity
import fr.ffnet.downloader.repository.entities.ProfileFanfictionEntity

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProfile(profileEntity: ProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProfileFanfiction(favoriEntity: ProfileFanfictionEntity)

    @Query("SELECT * FROM ProfileEntity WHERE profileId = :profileId")
    fun getProfile(profileId: String): ProfileEntity?

    @Query("SELECT * FROM ProfileEntity WHERE isAssociated = 1")
    fun getProfile(): LiveData<ProfileEntity>

    @Query("SELECT profileId FROM ProfileEntity WHERE isAssociated = 1")
    fun getAssociatedProfile(): String?

    @Query("DELETE FROM ProfileFanfictionEntity WHERE profileId = :profileId")
    fun deleteProfileMapping(profileId: String)

    @Query("UPDATE ProfileEntity SET isAssociated = 0 WHERE isAssociated = 1")
    fun dissociateProfile()

    @Query("UPDATE ProfileEntity SET isAssociated = 1 WHERE profileId = :profileId")
    fun associateProfile(profileId: String)

    @Query("SELECT * FROM ProfileEntity")
    fun getProfileHistory(): LiveData<List<ProfileEntity>>

}
