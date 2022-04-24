package com.example.foodreminderapp

import android.app.Application
import com.example.foodreminderapp.all_items.data.ItemDatabase
import com.example.foodreminderapp.current_items.data.FoodItemRoomDatabase
import com.example.foodreminderapp.statistics.data.StatisticsDatabase

private const val TAG = "FoodReminderApplication"

class FoodReminderApplication : Application() {

    val database: FoodItemRoomDatabase by lazy {
        FoodItemRoomDatabase.getDatabase(applicationContext)
    }

    val itemDatabase: ItemDatabase by lazy {
        ItemDatabase.getDatabase(applicationContext)
    }

    val statisticsDatabase: StatisticsDatabase by lazy {
        StatisticsDatabase.getDatabase(applicationContext)
    }

}
