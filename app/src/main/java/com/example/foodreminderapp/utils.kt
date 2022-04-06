package com.example.foodreminderapp

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.foodreminderapp.data.FoodItem
import java.text.SimpleDateFormat
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
fun calculateBestBefore(days: Int): String {
    val dates = SimpleDateFormat("yyyy-MM-dd")
    val targetDate = dates.parse(LocalDate.now().plusDays(days.toLong()).toString())
    return dates.format(targetDate!!)
}

fun setBestBeforeText(item: FoodItem): String {
    val daysLeft = item.getDaysLeft().toInt()
    val daysLeftText: String = when {
        daysLeft >= 365*2 -> { "${daysLeft / 365} Jahre" }
        (daysLeft < 365*2) and (daysLeft >= 365) -> { "1 Jahr" }
        (daysLeft < 365) and (daysLeft >= 30*2) -> { "${daysLeft / 30} Monate" }
        (daysLeft < 30*2) and (daysLeft >= 30) -> { "1 Monat" }
        else -> { "$daysLeft Tage"}
    }
    return daysLeftText
}