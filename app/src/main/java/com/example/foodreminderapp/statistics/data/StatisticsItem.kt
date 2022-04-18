package com.example.foodreminderapp.statistics.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "statisticsDatabase")
data class StatisticsItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val amount: Int,
    val status: String,
    val createdTime: String,
    val endedDate: String,
    val endedDayOfMonth: Int,
    val endedDayOfWeek: Int,
    val endedWeekNr: Int,
    val endedMonth: Int,
    val endedYear: Int,
    val endedAfterNrDays: Int,
    val location: String
)