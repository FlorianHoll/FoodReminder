package com.example.foodreminderapp

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.foodreminderapp.current_items.data.FoodItem
import java.text.SimpleDateFormat
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
fun calculateBestBefore(days: Int): String {
    val dates = SimpleDateFormat("yyyy-MM-dd")
    val targetDate = dates.parse(LocalDate.now().plusDays(days.toLong()).toString())
    return dates.format(targetDate!!)
}

fun calculateBestBeforeInGermanDate(days: Int): String {
    val date = calculateBestBefore(days)
    return convertToGermanDate(date)
}

fun convertToGermanDate(date: String): String {
    // hard-coded since the date format is always the same.
    val year = date.take(4)
    val month = date.substring(5, 7)
    val day = date.takeLast(2)
    return "$day.$month.$year"
}

@RequiresApi(Build.VERSION_CODES.O)
fun getDaysLeft(bestBefore: String): Int {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    // Get date of today and the best before date
    val dateNow = dateFormat.parse(LocalDate.now().toString())
    val dateBestBefore = dateFormat.parse(bestBefore)

    // calculate the difference and return it as difference in days
    val difference: Long = dateBestBefore.time - dateNow.time
    val differenceDates = difference / (24 * 60 * 60 * 1000)

    return differenceDates.toInt()
}

fun setBestBeforeText(item: FoodItem): String {
    val daysLeft = getDaysLeft(item.bestBefore)
    return setDaysLeftText(daysLeft)
}

fun setDaysLeftText(daysLeft: Int): String {
    val daysLeftText: String = when {
        daysLeft >= 365*2 -> { "${daysLeft / 365} Jahre" }
        (daysLeft < 365*2) and (daysLeft >= 365) -> { "1 Jahr" }
        (daysLeft < 365) and (daysLeft >= 30*2) -> { "${daysLeft / 30} Monate" }
        (daysLeft < 30*2) and (daysLeft >= 30) -> { "1 Monat" }
        else -> { "$daysLeft Tage"}
    }
    return daysLeftText
}

fun setShortDaysLeftText(daysLeft: Int): String {
    val daysLeftText: String = when {
        daysLeft >= 365 -> { "${daysLeft / 365} J" }
        (daysLeft < 365) and (daysLeft >= 30) -> { "${daysLeft / 30} M" }
        else -> { "$daysLeft T"}
    }
    return daysLeftText
}
