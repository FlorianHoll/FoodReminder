package com.example.foodreminderapp.statistics.data


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database class with a singleton INSTANCE object.
 */
@Database(entities = [StatisticsItem::class], version = 1, exportSchema = false)
abstract class StatisticsDatabase : RoomDatabase() {

    abstract fun statisticsItemDao(): StatisticsItemDao

    companion object {
        @Volatile
        private var INSTANCE: StatisticsDatabase? = null

        fun getDatabase(context: Context): StatisticsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StatisticsDatabase::class.java,
                    "statistics_item_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}