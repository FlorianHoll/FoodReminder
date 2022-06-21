package com.example.foodreminderapp.statistics

import androidx.lifecycle.*
import com.example.foodreminderapp.statistics.data.StatisticsItem
import com.example.foodreminderapp.statistics.data.StatisticsItemDao
import com.example.foodreminderapp.statistics.data.DisplayableStatisticsItem
import com.example.foodreminderapp.utils.calculateTargetDate
import com.example.foodreminderapp.utils.getDifferenceInDays
import kotlinx.coroutines.*
import java.lang.NullPointerException
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

    private var _changeInThrownAwayPercentage = MutableLiveData<Int>()
    val changeInThrownAwayPercentage: LiveData<Int>
        get() = _changeInThrownAwayPercentage

    private var _thrownAwayAfterNrDays = MutableLiveData<Int>()
    val thrownAwayAfterNrDays: LiveData<Int>
        get() = _thrownAwayAfterNrDays

    private var _eatenAfterNrDays = MutableLiveData<Int>()
    val eatenAfterNrDays: LiveData<Int>
        get() = _eatenAfterNrDays


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

    fun getAverageEatenAndThrownAwayTime(
        name: String, interval: Int
    ) {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            _eatenAfterNrDays.value = getAverageNrDays(name, interval, false)
            _thrownAwayAfterNrDays.value = getAverageNrDays(name, interval, true)
        }
    }

    private suspend fun getAverageNrDays(
        name: String, interval: Int, thrownAway: Boolean
    ): Int {
        val startTime = calculateTargetDate(-interval)
        return itemDao.getAverageThrownAwayTime(
            startTime = startTime,
            endTime = LocalDate.now().toString(),
            name = name,
            thrownAway = thrownAway
        )
    }

    fun getCurrentPercentageThrownAway(
        interval: Int
    ) {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            val startTime = calculateTargetDate(-interval)
            val endTime = LocalDate.now().toString()
            _percentageThrownAway.value = getPercentageInPeriod(
                startTime, endTime
            )
        }
    }

    fun getThrownAwayChange(
        interval: Int
    ) {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            // Calculate the percentage of thrown away items for both the
            // current and the previous time period.
            val startTimePrevious = calculateTargetDate(-interval * 2)
            val endTimePrevious = calculateTargetDate(-interval)
            var previousThrownAway = 0
            var currentThrownAway = 0
            // A NullPointerException might occur if the item was not used
            // in the specified interval.
            try {
                previousThrownAway = getPercentageInPeriod(
                    startTimePrevious, endTimePrevious
                )
            } catch (e: NullPointerException) {
            }

            val startTimeCurrent = calculateTargetDate(-interval + 1)
            try {
                currentThrownAway = getPercentageInPeriod(
                    startTimeCurrent, LocalDate.now().toString()
                )
            } catch (e: NullPointerException) {
            }

            // calculate the change in percentage.
            _changeInThrownAwayPercentage.value = (
                    currentThrownAway - previousThrownAway
                    )
        }

    }

    private suspend fun getPercentageInPeriod(
        startTime: String, endTime: String
    ): Int {
        return itemDao.getPercentageForInterval(
            startTime = startTime,
            endTime = endTime
        )
    }

    fun getAllItemsForPeriod(
        startDateThisPeriod: String,
        startDateLastPeriod: String,
        limit: Int = 10,
        percentageLimit: Int = 1,
        searchQuery: String = ""
    ): LiveData<List<DisplayableStatisticsItem>> {
        return itemDao.getItemInformationForThisAndLastTimePeriod(
            startDateThisPeriod,
            startDateLastPeriod,
            limit,
            percentageLimit,
            searchQuery
        ).asLiveData()
    }

    fun getItem(
        startDateThisPeriod: String,
        startDateLastPeriod: String,
        itemName: String
    ): LiveData<DisplayableStatisticsItem> {
        return itemDao.getItem(
            startDateThisPeriod,
            startDateLastPeriod,
            itemName
        ).asLiveData()
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
