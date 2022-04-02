package com.example.foodreminderapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity data class represents a single row in the database.
 */
@Entity
data class FoodItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val itemName: String,
    @ColumnInfo(name = "days_left")
    val daysLeft: Int,
    @ColumnInfo(name = "location")
    val location: String,
//    @ColumnInfo(name = "co2")
//    val co2: Float
)

///**
// * Returns the passed in price in currency format.
// */
//fun FoodItem.getFormattedPrice(): String =
//    NumberFormat.getCurrencyInstance().format(itemPrice)