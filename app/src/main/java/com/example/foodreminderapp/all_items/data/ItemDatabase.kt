package com.example.foodreminderapp.all_items.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Prepopulated database from file.
 */
@Database(entities = [DatabaseItem::class], version = 1, exportSchema = true)
abstract class ItemDatabase : RoomDatabase() {

    abstract fun itemDatabaseDao(): DatabaseItemDao

    companion object {
        @Volatile
        private var INSTANCE: ItemDatabase? = null

        fun getDatabase(context: Context): ItemDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemDatabase::class.java,
                    "item_database"
                )
                    .createFromAsset("itemdatabase/all_items.db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}