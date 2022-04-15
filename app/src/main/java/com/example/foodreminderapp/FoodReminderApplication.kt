package com.example.foodreminderapp

import android.app.Application
import com.example.foodreminderapp.all_items.data.ItemDatabase
import com.example.foodreminderapp.current_items.data.FoodItemRoomDatabase


class FoodReminderApplication : Application() {
    val database: FoodItemRoomDatabase by lazy {
        FoodItemRoomDatabase.getDatabase(applicationContext)
    }

    val itemDatabase: ItemDatabase by lazy {
        ItemDatabase.getDatabase(applicationContext)
    }

}
