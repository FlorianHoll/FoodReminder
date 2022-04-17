package com.example.foodreminderapp.all_items

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.foodreminderapp.all_items.data.DatabaseItem
import com.example.foodreminderapp.all_items.data.DatabaseItemDao
import kotlinx.coroutines.launch
import java.time.LocalDate

private const val TAG = "DatbaseItemListViewModel"

/**
 * View Model to keep a reference to the Inventory repository and an up-to-date list of all items.
 *
 */
class DatabaseItemListViewModel(private val itemDao: DatabaseItemDao) : ViewModel() {

    var selectedItems: MutableMap<Int, DatabaseItem> = mutableMapOf()

    // Cache all items form the database using LiveData.
    val allItems: LiveData<List<DatabaseItem>> = itemDao.getItems().asLiveData()

    // Insert a new item into the database.
    fun addNewItem(
        itemName: String,
        itemDaysLeft: Int,
        itemLocation: String,
        itemAmount: Int
    ) {
        val newItem = DatabaseItem(
            itemName = itemName,
            location = itemLocation,
            durability = itemDaysLeft,
            defaultAmount = itemAmount,
            lastAdded = LocalDate.now().toString()
        )
        insertItem(newItem)
    }


    fun updateItemName(item: DatabaseItem) {
        viewModelScope.launch {
            itemDao.update(item.copy(itemName = "Test"))
        }
    }

    fun searchDatabase(query: String): LiveData<List<DatabaseItem>> {
        return itemDao.searchDatabase(query).asLiveData()
    }

    fun updateItemAmount(item: DatabaseItem, newAmount: Int) {
        viewModelScope.launch {
            itemDao.update(item.copy(defaultAmount = newAmount))
        }
    }

    fun updateItemLocation(item: DatabaseItem, newLocation: String) {
        viewModelScope.launch {
            itemDao.update(item.copy(location = newLocation))
        }
    }

    fun addOneAmount(item: DatabaseItem) {
        viewModelScope.launch {
            itemDao.update(item.copy(timesEaten = item.timesEaten + 1))
        }
    }

    private fun insertItem(item: DatabaseItem) {
        viewModelScope.launch { itemDao.insert(item) }
    }

    fun retrieveItem(id: Int): LiveData<DatabaseItem> {
        return itemDao.getItem(id).asLiveData()
    }

    fun retrieveItemsByLocation(location: String): LiveData<List<DatabaseItem>> {
        return itemDao.getItemsByLocation(location).asLiveData()
    }

    fun isEntryValid(itemName: String, itemDaysLeft: String): Boolean {
        return !(itemName.isEmpty() or itemDaysLeft.isEmpty())
    }
}


class DatabaseItemViewModelFactory(
    private val databaseItemDao: DatabaseItemDao
    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DatabaseItemListViewModel::class.java)) {
            return DatabaseItemListViewModel(databaseItemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
