package com.example.foodreminderapp.all_items

import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.foodreminderapp.all_items.data.DatabaseItem
import com.example.foodreminderapp.all_items.data.DatabaseItemDao
import com.example.foodreminderapp.current_items.data.FoodItem
import kotlinx.coroutines.*
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
    private fun addNewItem(
        itemName: String,
        itemDurability: Int,
        itemLocation: String,
        itemAmount: Int
    ) {
        val newItem = DatabaseItem(
            itemName = itemName,
            location = itemLocation,
            durability = itemDurability,
            defaultAmount = itemAmount,
            lastAdded = LocalDate.now().toString()
        )
        insertItem(newItem)
    }

    fun searchDatabase(query: String): LiveData<List<DatabaseItem>> {
        return runBlocking {
            itemDao.searchDatabase(query).asLiveData()
        }
    }

    fun updateItemDatabase(item: FoodItem) {
        viewModelScope.launch {
            // Check if item exists; if so, update it; else, create it.
            val itemExists = itemDao.itemNameExists(item.itemName.lowercase())
            if (itemExists) {
                Log.d(TAG, "Item ${item.itemName} does exist already, updating item.")

                // Retrieve item from database in order to update it.
                val databaseItem = itemDao.getItemByName(item.itemName)

                // new default amount is the weighted mean
                val newDefaultAmount = (
                        (
                                databaseItem.timesEaten *
                                        databaseItem.defaultAmount +
                                        item.amount) /
                                (databaseItem.timesEaten + 1)
                        )

                // default new durability is also the weighted mean
                val newDefaultDurability = (
                        (
                                databaseItem.durability *
                                        databaseItem.timesEaten +
                                        item.durability) /
                                (databaseItem.timesEaten + 1)
                        )

                // Update item in database
                itemDao.update(
                    // copy in order to keep the ID intact.
                    databaseItem.copy(
                        timesEaten = databaseItem.timesEaten + 1,
                        defaultAmount = newDefaultAmount,
                        location = item.location,  // update default location as well
                        durability = newDefaultDurability,
                        lastAdded = LocalDate.now().toString()
                    )
                )

            } else {
                Log.d(TAG, "Item does not exist yet, adding item to database.")

                // create new item, ID is automatically incremented.
                val newItem = DatabaseItem(
                    itemName = item.itemName,
                    location = item.location,
                    durability = item.durability,
                    defaultAmount = item.amount,
                    lastAdded = LocalDate.now().toString()
                )
                insertItem(newItem)
            }
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
