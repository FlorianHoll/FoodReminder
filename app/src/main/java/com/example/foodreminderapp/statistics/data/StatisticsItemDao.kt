package com.example.foodreminderapp.statistics.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Database access object to access the database
 */
@Dao
interface StatisticsItemDao {
    @Query("SELECT * from itemsdatabase")
    fun getItems(): Flow<List<StatisticsItem>>

    @Query(
        "SELECT * from statisticsDatabase " +
                "WHERE endedTime > :startTime " +
                "AND endedTime <= :endTime "
    )
    fun getItemsForTimeInterval(
        startTime: String ,
        endTime: String = LocalDate.now().toString()
    )

    @Query("SELECT * from statisticsDatabase WHERE name = :itemName")
    fun getItem(itemName: String): Flow<StatisticsItem>

    @Query("SELECT * from statisticsDatabase WHERE id = :id")
    fun getItem(id: Int): Flow<StatisticsItem>

    @Query(
        "SELECT * FROM statisticsDatabase " +
                "WHERE name LIKE '%' || :searchQuery || '%' "
    )
    fun searchDatabase(searchQuery: String): Flow<List<StatisticsItem>>

    @Query("SELECT EXISTS (SELECT 1 FROM itemsdatabase WHERE LOWER(name) = :itemName)")
    suspend fun itemNameExists(itemName: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: StatisticsItem)

    @Update
    suspend fun update(item: StatisticsItem)

}

