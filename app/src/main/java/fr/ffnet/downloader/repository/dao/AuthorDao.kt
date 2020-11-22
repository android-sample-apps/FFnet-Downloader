package fr.ffnet.downloader.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import fr.ffnet.downloader.repository.entities.AuthorEntity
import fr.ffnet.downloader.repository.entities.ProfileFanfictionEntity

@Dao
interface AuthorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProfileFanfiction(favoriteEntity: ProfileFanfictionEntity)

    @Query("DELETE FROM ProfileFanfictionEntity WHERE profileId = :profileId")
    fun deleteProfileMapping(profileId: String)

    @Query("DELETE FROM AuthorEntity WHERE authorId = :authorId")
    fun deleteAuthor(authorId: String)

    @Update
    fun updateAuthor(authorEntity: AuthorEntity)

    @Query("SELECT * FROM AuthorEntity WHERE authorId = :authorId")
    fun getAuthor(authorId: String): AuthorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAuthor(authorEntity: AuthorEntity)

    @Query("SELECT * FROM AuthorEntity")
    fun getSyncedAuthors(): LiveData<List<AuthorEntity>>
}
