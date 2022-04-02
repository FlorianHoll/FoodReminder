package com.example.foodreminderapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.foodreminderapp.data.FoodItem
import com.example.foodreminderapp.data.FoodItemDao
import kotlinx.coroutines.launch

/**
 * View Model to keep a reference to the Inventory repository and an up-to-date list of all items.
 *
 */
class FoodItemListViewModel(private val itemDao: FoodItemDao) : ViewModel() {

    // Cache all items form the database using LiveData.
    val allItems: LiveData<List<FoodItem>> = itemDao.getItems().asLiveData()

    // Update an existing item in the database.
    fun updateItem(
        itemId: Int,
        itemName: String,
        itemDaysLeft: Int,
        itemLocation: String,
    ) {
        val updatedItem = FoodItem(
            id = itemId,
            itemName = itemName,
            daysLeft = itemDaysLeft,
            location = itemLocation
        )
        updateItem(updatedItem)
    }

    // Insert a new item into the database.
    fun addNewItem(itemName: String, itemDaysLeft: Int, itemLocation: String) {
        val newItem = FoodItem(
            itemName = itemName, daysLeft = itemDaysLeft, location = itemLocation
        )
        insertItem(newItem)
    }

    private fun updateItem(item: FoodItem) {
        viewModelScope.launch { itemDao.update(item) }
    }

    private fun insertItem(item: FoodItem) {
        viewModelScope.launch { itemDao.insert(item) }
    }

    fun deleteItem(item: FoodItem) {
        viewModelScope.launch { itemDao.delete(item) }
    }

    fun retrieveItem(id: Int): LiveData<FoodItem> {
        return itemDao.getItem(id).asLiveData()
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    fun isEntryValid(itemName: String, itemDaysLeft: String): Boolean {
        return !(itemName.isEmpty() or itemDaysLeft.isEmpty())
//
//        if (itemName.isBlank() || itemDaysLeft.isBlank()) {
//            return false
//        }
//        return true
    }
}


//    /**
//     * Decreases the stock by one unit and updates the database.
//     */
//    fun sellItem(item: Item) {
//        if (item.quantityInStock > 0) {
//            // Decrease the quantity by 1
//            val newItem = item.copy(quantityInStock = item.quantityInStock - 1)
//            updateItem(newItem)
//        }
//    }


/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class FoodItemViewModelFactory(private val itemDao: FoodItemDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodItemListViewModel::class.java)) {
            return FoodItemListViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
