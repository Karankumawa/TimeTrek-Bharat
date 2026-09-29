package com.example.timetrekbharat.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.timetrekbharat.model.State

@Dao
interface StateDao {

    @Query("SELECT * FROM states ORDER BY name ASC")
    fun getAllStates(): LiveData<List<State>>

    @Query("SELECT * FROM states WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteStates(): LiveData<List<State>>

    @Query("SELECT * FROM states WHERE region LIKE '%' || :region || '%' ORDER BY name ASC")
    fun getStatesByRegion(region: String): LiveData<List<State>>

    @Query("SELECT * FROM states WHERE name LIKE '%' || :search || '%' OR region LIKE '%' || :search || '%' ORDER BY name ASC")
    fun searchStates(search: String): LiveData<List<State>>

    @Query("SELECT * FROM states WHERE slug = :slug LIMIT 1")
    fun getStateBySlug(slug: String): LiveData<State>

    @Query("SELECT * FROM states WHERE slug = :slug LIMIT 1")
    fun getStateBySlugSync(slug: String): State?

    @Query("UPDATE states SET isFavorite = :isFav WHERE slug = :slug")
    fun updateFavoriteStatus(slug: String, isFav: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStates(states: List<State>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertState(state: State)

    @Query("DELETE FROM states")
    fun deleteAllStates()
}
