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
    @Query("SELECT * from statisticsDatabase")
    fun getItems(): Flow<List<StatisticsItem>>

    @Query(
        "SELECT * from statisticsDatabase " +
                "WHERE endedDate > :startTime " +
                "AND endedDate <= :endTime "
    )
    fun getItemsForTimeInterval(
        startTime: String ,
        endTime: String
    ): Flow<List<StatisticsItem>>

    @Query(
        "SELECT SUM(amount) from statisticsDatabase " +
                "WHERE endedDate > :startTime " +
                "AND endedDate <= :endTime " +
                "AND status = 'thrown' "
    )
    fun getNrThrownAwayForInterval(
        startTime: String,
        endTime: String
    ): Int

    @Query(
        "SELECT SUM(amount) from statisticsDatabase " +
                "WHERE endedDate > :startTime " +
                "AND endedDate <= :endTime " +
                "AND status = 'eaten' "
    )
    fun getNrEatenForInterval(
        startTime: String,
        endTime: String
    ): Int

    @Query("SELECT * from statisticsDatabase WHERE id = :id")
    fun getItem(id: Int): Flow<StatisticsItem>

    @Query(
        "SELECT * FROM statisticsDatabase " +
                "WHERE name LIKE '%' || :searchQuery || '%' "
    )
    fun searchDatabase(searchQuery: String): Flow<List<StatisticsItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: StatisticsItem)

    @Update
    suspend fun update(item: StatisticsItem)

}

