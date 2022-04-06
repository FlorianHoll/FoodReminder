package com.example.foodreminderapp.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.abs
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDate.now
/**
 * Entity data class represents a single row in the database.
 */
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
) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDaysLeft(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        // Get date of today and the best before date
        val dateNow = dateFormat.parse(now().toString())
        val dateBestBefore = dateFormat.parse(this.bestBefore)

        // calculate the difference and return it as difference in days
        val difference: Long = dateBestBefore.time - dateNow.time
        val differenceDates = difference / (24 * 60 * 60 * 1000)

        return differenceDates.toString()
    }

}


///**
// * Returns the passed in price in currency format.
// */
//fun FoodItem.getFormattedPrice(): String =
//    NumberFormat.getCurrencyInstance().format(itemPrice)