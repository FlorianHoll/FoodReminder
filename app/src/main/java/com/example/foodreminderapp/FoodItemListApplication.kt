package com.example.foodreminderapp

import android.app.Application
import com.example.foodreminderapp.data.FoodItemRoomDatabase


class FoodItemListApplication : Application() {
    val database: FoodItemRoomDatabase by lazy { FoodItemRoomDatabase.getDatabase(applicationContext) }
}
