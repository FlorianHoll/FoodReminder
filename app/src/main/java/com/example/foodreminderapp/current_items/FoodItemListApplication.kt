package com.example.foodreminderapp.current_items

import android.app.Application
import com.example.foodreminderapp.current_items.data.FoodItemRoomDatabase


class FoodItemListApplication : Application() {
    val database: FoodItemRoomDatabase by lazy { FoodItemRoomDatabase.getDatabase(applicationContext) }
}
