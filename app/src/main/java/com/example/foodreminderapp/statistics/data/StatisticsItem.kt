package com.example.foodreminderapp.statistics.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "statisticsDatabase")
data class StatisticsItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val location: String,
    val status: String,
    val createdTime: String,
    val endedTime: String,
    val endedDayOfMonth: String,
    val endedDayOfWeek: String,
    val endedWeekNr: String,
    val endedMonth: String,
    val endedYear: String,
    val endedAfterNrDays: String
)