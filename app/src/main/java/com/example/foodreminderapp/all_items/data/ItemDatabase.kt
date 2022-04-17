package com.example.foodreminderapp.all_items.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Prepopulated database from file.
 */
@Database(entities = [DatabaseItem::class], version = 1, exportSchema = false)
abstract class ItemDatabase : RoomDatabase() {

    abstract fun itemDatabaseDao(): DatabaseItemDao

    companion object {
        @Volatile
        private var INSTANCE: ItemDatabase? = null

//        // this instantiaion is called the first time the software is updated
//        fun getInstanceAfterSoftwareUpdate(context: Context): ItemDatabase {
//
//            synchronized(this) {
//                var instance = INSTANCE
//
//                if (instance == null) {
//                    instance = Room.databaseBuilder(
//                        context.applicationContext,
//                        ItemDatabase::class.java,
//                        "item_database")
//                        .createFromAsset("itemdatabase/all_items.db")
//                        .build()
//
//                    INSTANCE = instance
//                }
//                return instance
//            }
//        }
//
//        fun getInstance(context: Context): ItemDatabase {
//
//            synchronized(this) {
//
//                var instance = INSTANCE
//
//                if (instance == null) {
//
//                    Log.i("MyRoom", "reading locally")
//                    instance = Room.databaseBuilder(
//                        context.applicationContext,
//                        ItemDatabase:: class.java,
//                        "item_database"
//                    )
//                        .fallbackToDestructiveMigration()
//                        .build()
//
//                    INSTANCE = instance
//                }
//
//                return instance
//            }
//        }

        fun getDatabase(context: Context): ItemDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemDatabase::class.java,
                    "item_database"
                )
                    .createFromAsset("itemdatabase/all_items.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}