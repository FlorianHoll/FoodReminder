package com.example.foodreminderapp.data

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
fun FoodItem.calculateBestBefore(days: Int): String {
    val dates = SimpleDateFormat("yyyy-MM-dd")
    val targetDate = dates.parse(LocalDate.now().plusDays(days.toLong()).toString())
    return dates.format(targetDate!!)
}
