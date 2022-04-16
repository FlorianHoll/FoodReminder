package com.example.foodreminderapp.all_items.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "ItemsDatabase")
data class DatabaseItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val itemName: String,
    @ColumnInfo(name = "location")
    val location: String,
    @ColumnInfo(name = "durability")
    val durability: Int,
    @ColumnInfo(name = "default_amount")
    val defaultAmount: Int = 1,
    @ColumnInfo(name = "times_eaten")
    val timesEaten: Int = 1,
    @ColumnInfo(name = "last_added")
    val lastAdded: String
) {
    @Ignore var checked: Boolean = false
    @Ignore var currentAmount: Int = defaultAmount
    @Ignore var currentLocation: String = location
}