package com.example.foodreminderapp.statistics.data

data class StatisticsItemDisplay(
    val name: String,
    val percentageThrownThisPeriod: Int,
    val amountThisPeriod: Int,
    val NrThrownThisPeriod: Int,
    val NrEatenThisPeriod: Int,
    val percentageThrownLastPeriod: Int,
    val amountLastPeriod: Int,
    val NrThrownLastPeriod: Int,
    val NrEatenLastPeriod: Int,
)