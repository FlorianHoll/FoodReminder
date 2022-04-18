package com.example.foodreminderapp.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.foodreminderapp.statistics.data.StatisticsItem
import com.example.foodreminderapp.statistics.data.StatisticsItemDao
import com.example.foodreminderapp.utils.calculateTargetDate
import com.example.foodreminderapp.utils.getDifferenceInDays
import kotlinx.coroutines.*
import java.time.LocalDate
import java.util.*

private const val TAG = "DatbaseItemListViewModel"

/**
 * View Model to keep a reference to the Inventory repository and an up-to-date list of all items.
 *
 */
class StatisticsItemListViewModel(private val itemDao: StatisticsItemDao) : ViewModel() {

    // Cache all items form the database using LiveData.
    val allItems: LiveData<List<StatisticsItem>> = itemDao.getItems().asLiveData()

    // Insert a new item into the database.
    fun addNewItem(
        name: String,
        thrownAway: Boolean,
        amount: Int,
        createdTime: String,
        location: String
    ) {
        val status = when (thrownAway) {
            true -> "thrown"
            else -> "eaten"
        }
        val calendar = Calendar.getInstance()
        val newItem = StatisticsItem(
            name = name,
            amount = amount,
            createdTime = createdTime,
            location = location,
            status = status,
            endedDate = LocalDate.now().toString(),
            endedDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
            endedDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK),
            endedWeekNr = calendar.get(Calendar.WEEK_OF_YEAR),
            endedMonth = calendar.get(Calendar.MONTH) + 1,
            endedYear = calendar.get(Calendar.YEAR),
            // Since the difference is negative, sign has to be reverted.
            endedAfterNrDays = -(getDifferenceInDays(createdTime)),
        )
        insertItem(newItem)
    }

    fun searchDatabase(query: String): LiveData<List<StatisticsItem>> {
        return runBlocking {
            itemDao.searchDatabase(query).asLiveData()
        }
    }

    fun getNrThrownAway(interval: Int) {
        return runBlocking {
            val startDate = calculateTargetDate(-interval)
            itemDao.getNrThrownAwayForInterval(
                startTime = startDate,
                endTime = LocalDate.now().toString()
            )
        }
    }

    fun getNrEaten(interval: Int) {
        return runBlocking {
            val startDate = calculateTargetDate(-interval)
            itemDao.getNrEatenForInterval(
                startTime = startDate,
                endTime = LocalDate.now().toString()
            )
        }
    }

    fun insertItem(item: StatisticsItem) {
        viewModelScope.launch { itemDao.insert(item) }
    }

    fun retrieveItem(id: Int): LiveData<StatisticsItem> {
        return itemDao.getItem(id).asLiveData()
    }

    fun isEntryValid(itemName: String, itemDaysLeft: String): Boolean {
        return !(itemName.isEmpty() or itemDaysLeft.isEmpty())
    }
}


class StatisticsItemViewModelFactory(
    private val statisticsDao: StatisticsItemDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsItemListViewModel::class.java)) {
            return StatisticsItemListViewModel(statisticsDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
