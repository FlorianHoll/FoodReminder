package com.example.foodreminderapp.statistics

import androidx.lifecycle.*
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
class StatisticsViewModel(private val itemDao: StatisticsItemDao) : ViewModel() {

    // Cache all items form the database using LiveData.
    val allItems: LiveData<List<StatisticsItem>> = itemDao.getItems().asLiveData()

    private var _nrThrownAway = MutableLiveData<Int>()
    val nrThrownAway: LiveData<Int>
        get() = _nrThrownAway

    private var _nrEaten = MutableLiveData<Int>()
    val nrEaten: LiveData<Int>
        get() = _nrEaten

    private var _percentageThrownAway = MutableLiveData<Int>()
    val percentageThrownAway: LiveData<Int>
        get() = _percentageThrownAway

    fun getAmountEatenAndThrownAway(
        interval: Int,
    ) {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            _nrThrownAway.value = getAmountInInterval(interval, true)
            _nrEaten.value = getAmountInInterval(interval, false)
        }
    }

    private suspend fun getAmountInInterval(
        interval: Int, thrownAway: Boolean
    ): Int {
        val startTime = calculateTargetDate(-interval)
        return itemDao.getAmountForInterval(
            startTime = startTime,
            endTime = LocalDate.now().toString(),
            thrownAway = thrownAway
        )
    }

    fun getPercentageThrownAway(
        interval:Int
    ) {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            _percentageThrownAway.value = getPercentageInInterval(interval)
        }
    }

    private suspend fun getPercentageInInterval(
        interval: Int
    ): Int {
        val startTime = calculateTargetDate(-interval)
        return itemDao.getPercentageForInterval(
            startTime = startTime,
            endTime = LocalDate.now().toString()
        )
    }

    fun getItemsByInterval(
        startTime: String, endTime: String
    ): LiveData<List<StatisticsItem>> {
        return runBlocking {
            itemDao.getItemsForTimeInterval(startTime, endTime).asLiveData()
        }
    }

    fun searchDatabase(query: String): LiveData<List<StatisticsItem>> {
        return runBlocking {
            itemDao.searchDatabase(query).asLiveData()
        }
    }

    // Insert a new item into the database.
    fun addNewItem(
        name: String,
        thrownAway: Boolean,
        amount: Int,
        createdTime: String,
        location: String
    ) {
        val calendar = Calendar.getInstance()
        val newItem = StatisticsItem(
            name = name,
            amount = 1,
            createdTime = createdTime,
            location = location,
            thrownAway = thrownAway,
            endedDate = LocalDate.now().toString(),
            endedDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
            endedDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK),
            endedWeekNr = calendar.get(Calendar.WEEK_OF_YEAR),
            endedMonth = calendar.get(Calendar.MONTH) + 1,
            endedYear = calendar.get(Calendar.YEAR),
            // Since the difference is negative, sign has to be reverted.
            endedAfterNrDays = -(getDifferenceInDays(createdTime)),
        )
        for (i in 1..amount) {
            insertItem(newItem)
        }
    }


//    fun getNrThrownAway(interval: Int) {
//        return runBlocking {
//            val startDate = calculateTargetDate(-interval)
//            itemDao.getNrThrownAwayForInterval(
//                startTime = startDate,
//                endTime = LocalDate.now().toString()
//            )
//        }
//    }

//    fun getNrEaten(interval: Int) {
//        return runBlocking {
//            val startDate = calculateTargetDate(-interval)
//            itemDao.getNrEatenForInterval(
//                startTime = startDate,
//                endTime = LocalDate.now().toString()
//            )
//        }
//    }

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


class StatisticsViewModelFactory(
    private val statisticsDao: StatisticsItemDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            return StatisticsViewModel(statisticsDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
