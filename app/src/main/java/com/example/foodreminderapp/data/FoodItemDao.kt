package com.example.foodreminderapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the database
 */
@Dao
interface FoodItemDao {

    @Query("SELECT * from fooditem ORDER BY name ASC")
    fun getItems(): Flow<List<FoodItem>>

    @Query("SELECT * from fooditem WHERE best_before <= :date ORDER BY best_before ASC")
    fun getItemsForNextDays(date: String): Flow<List<FoodItem>>

    @Query(
        "SELECT * from fooditem " +
                "WHERE best_before <= :date AND location = :location"
    )
    fun hasToGoByLocation(date: String, location: String): Flow<List<FoodItem>>

    @Query("SELECT * from fooditem WHERE id = :id")
    fun getItem(id: Int): Flow<FoodItem>

    @Query("SELECT * from fooditem WHERE location = :location ORDER BY name ASC")
    fun getItemsByLocation(location: String): Flow<List<FoodItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: FoodItem)

    @Update
    suspend fun update(item: FoodItem)

    @Delete
    suspend fun delete(item: FoodItem)
}
