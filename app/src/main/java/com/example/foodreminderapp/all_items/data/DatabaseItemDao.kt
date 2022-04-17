package com.example.foodreminderapp.all_items.data


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the database
 */
@Dao
interface DatabaseItemDao {

    // TODO: Sort by frequency used.
    @Query(
        "SELECT * from itemsdatabase " +
                "ORDER BY times_eaten DESC, last_added DESC, name ASC"
    )
    fun getItems(): Flow<List<DatabaseItem>>

    @Query("SELECT * from itemsdatabase WHERE id = :id")
    fun getItem(id: Int): Flow<DatabaseItem>

    @Query(
        "SELECT * FROM itemsdatabase " +
                "WHERE name LIKE '%' || :searchQuery || '%' " +
                "ORDER BY times_eaten DESC, last_added DESC, name ASC"
    )
    fun searchDatabase(searchQuery: String): Flow<List<DatabaseItem>>

    @Query("SELECT EXISTS (SELECT 1 FROM itemsdatabase WHERE LOWER(name) = :itemName)")
    suspend fun itemNameExists(itemName: String): Boolean

    @Query("SELECT * FROM itemsdatabase WHERE name = :itemName")
    suspend fun getItemByName(itemName: String): DatabaseItem

    @Query("SELECT * from itemsdatabase WHERE location = :location ORDER BY name ASC")
    fun getItemsByLocation(location: String): Flow<List<DatabaseItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: DatabaseItem)

    @Update
    suspend fun update(item: DatabaseItem)

}

