package com.example.foodreminderapp.current_items.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FoodItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val itemName: String,
    @ColumnInfo(name = "best_before")
    val bestBefore: String,
    val location: String,
    val durability: Int,
    val amount: Int = 1,
    val added: String
)
