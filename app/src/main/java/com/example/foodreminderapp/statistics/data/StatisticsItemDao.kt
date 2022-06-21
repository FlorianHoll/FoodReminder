package com.example.foodreminderapp.statistics.data

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
                "AND thrownAway = :thrownAway "
    )
    suspend fun getAmountForInterval(
        startTime: String,
        endTime: String,
        thrownAway: Boolean
    ): Int

    @Query(
        "SELECT CAST(AVG(thrownAway) * 100 AS INT)  FROM statisticsDatabase " +
                "WHERE endedDate > :startTime " +
                "AND endedDate <= :endTime "
    )
    suspend fun getPercentageForInterval(
        startTime: String,
        endTime: String
    ): Int

    @Query(
        "WITH currenttimeperiod AS (" +
                "SELECT " +
                "   name AS nameThisPeriod, " +
                "   CAST(AVG(thrownAway) * 100 AS INT) AS percentageThrownThisPeriod, " +
                "   SUM(amount) AS amountThisPeriod, " +
                "   SUM(thrownAway = 1) AS NrThrownThisPeriod, " +
                "   SUM(thrownAway = 0) AS NrEatenThisPeriod " +
                "FROM statisticsDatabase " +
                        "WHERE endedDate > :startDateThisPeriod " +
                        "GROUP BY name ), " +
            "lasttimeperiod AS (" +
                "SELECT " +
                "   name, " +
                "   CAST(AVG(thrownAway) * 100 AS INT) AS percentageThrownLastPeriod, " +
                "   SUM(amount) AS amountLastPeriod, " +
                "   SUM(thrownAway = 1) AS NrThrownLastPeriod, " +
                "   SUM(thrownAway = 0) AS NrEatenLastPeriod " +
                "FROM statisticsDatabase " +
                        "WHERE endedDate > :startDateLastPeriod " +
                        "AND endedDate <= :startDateThisPeriod " +
                        "GROUP BY name ) " +
            "SELECT " +
                "nameThisPeriod AS name, " +
                "percentageThrownThisPeriod, " +
                "amountThisPeriod, " +
                "NrThrownThisPeriod, " +
                "NrEatenThisPeriod, " +
                "percentageThrownLastPeriod, " +
                "amountLastPeriod, " +
                "NrThrownLastPeriod, " +
                "NrEatenLastPeriod " +
            "FROM currenttimeperiod " +
                "LEFT JOIN lasttimeperiod " +
                "ON currenttimeperiod.nameThisPeriod = lasttimeperiod.name " +
                "WHERE percentageThrownThisPeriod >= :percentageLimit " +
                "AND nameThisPeriod LIKE '%' || :searchQuery  || '%' " +
                "ORDER BY percentageThrownThisPeriod DESC " +
                "LIMIT :limit"
    )
    fun getItemInformationForThisAndLastTimePeriod(
        startDateThisPeriod: String,
        startDateLastPeriod: String,
        limit: Int = 10,
        percentageLimit: Int = 1,
        searchQuery: String = ""
    ): Flow<List<DisplayableStatisticsItem>>

    @Query(
        "WITH currenttimeperiod AS (" +
                "SELECT " +
                "   name AS nameThisPeriod, " +
                "   CAST(AVG(thrownAway) * 100 AS INT) AS percentageThrownThisPeriod, " +
                "   SUM(amount) AS amountThisPeriod, " +
                "   SUM(thrownAway = 1) AS NrThrownThisPeriod, " +
                "   SUM(thrownAway = 0) AS NrEatenThisPeriod " +
                "FROM statisticsDatabase " +
                "WHERE endedDate > :startDateThisPeriod " +
                "GROUP BY name ), " +
                "lasttimeperiod AS (" +
                "SELECT " +
                "   name, " +
                "   CAST(AVG(thrownAway) * 100 AS INT) AS percentageThrownLastPeriod, " +
                "   SUM(amount) AS amountLastPeriod, " +
                "   SUM(thrownAway = 1) AS NrThrownLastPeriod, " +
                "   SUM(thrownAway = 0) AS NrEatenLastPeriod " +
                "FROM statisticsDatabase " +
                "WHERE endedDate > :startDateLastPeriod " +
                "AND endedDate <= :startDateThisPeriod " +
                "GROUP BY name ) " +
                "SELECT " +
                "nameThisPeriod AS name, " +
                "percentageThrownThisPeriod, " +
                "amountThisPeriod, " +
                "NrThrownThisPeriod, " +
                "NrEatenThisPeriod, " +
                "percentageThrownLastPeriod, " +
                "amountLastPeriod, " +
                "NrThrownLastPeriod, " +
                "NrEatenLastPeriod " +
                "FROM currenttimeperiod " +
                "LEFT JOIN lasttimeperiod " +
                "ON currenttimeperiod.nameThisPeriod = lasttimeperiod.name " +
                "WHERE nameThisPeriod LIKE '%' || :searchQuery  || '%' "
    )
    fun getItem(
        startDateThisPeriod: String,
        startDateLastPeriod: String,
        searchQuery: String = ""
    ): Flow<DisplayableStatisticsItem>

    @Query(
        "SELECT * FROM statisticsDatabase " +
                "WHERE name LIKE '%' || :searchQuery || '%' "
    )
    fun searchDatabase(searchQuery: String): Flow<List<StatisticsItem>>

    @Query(
        "SELECT CAST(AVG(endedAfterNrDays) * 100 AS INT)  FROM statisticsDatabase " +
                "WHERE endedDate > :startTime " +
                "AND endedDate <= :endTime " +
                "AND name = :name " +
                "AND thrownAway = :thrownAway"
    )
    suspend fun getAverageThrownAwayTime(
        startTime: String,
        endTime: String,
        name: String,
        thrownAway: Boolean
    ): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: StatisticsItem)

    @Update
    suspend fun update(item: StatisticsItem)

}

