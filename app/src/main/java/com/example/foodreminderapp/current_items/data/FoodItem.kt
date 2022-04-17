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
    @ColumnInfo(name = "location")
    val location: String,
    @ColumnInfo(name = "durability")
    val durability: Int,
    @ColumnInfo(name = "amount")
    val amount: Int = 1
)
