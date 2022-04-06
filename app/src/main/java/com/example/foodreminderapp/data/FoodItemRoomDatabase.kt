package com.example.foodreminderapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database class with a singleton INSTANCE object.
 */
@Database(entities = [FoodItem::class], version = 3, exportSchema = false)
abstract class FoodItemRoomDatabase : RoomDatabase() {

    abstract fun foodItemDao(): FoodItemDao

    companion object {
        @Volatile
        private var INSTANCE: FoodItemRoomDatabase? = null

        fun getDatabase(context: Context): FoodItemRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodItemRoomDatabase::class.java,
                    "food_item_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}