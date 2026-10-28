package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles ORDER BY isPrimary DESC, id ASC")
    fun getAllProfiles(): Flow<List<UserProfileEntity>>

    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: Long): UserProfileEntity?

    @Query("SELECT * FROM user_profiles WHERE isPrimary = 1 LIMIT 1")
    fun getPrimaryProfile(): Flow<UserProfileEntity?>

    @Query("SELECT COUNT(*) FROM user_profiles")
    suspend fun getProfileCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: UserProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profiles SET isPrimary = 0")
    suspend fun clearPrimaryFlags()

    @Query("UPDATE user_profiles SET isPrimary = 1 WHERE id = :id")
    suspend fun setPrimaryProfile(id: Long)
}
