package com.example.foodreminderapp.data

import com.example.foodreminderapp.model.FoodItem

class DataSource {

    fun loadItems(): MutableList<FoodItem> {
        return mutableListOf<FoodItem>(
            FoodItem("Banane", 12, "shelf"),
            FoodItem("Apfel", 10, "shelf"),
            FoodItem("Karotte", 5, "kuehlschrank"),
            FoodItem("Tofu", 5, "kuehlschrank"),
            FoodItem("Soja-Bällchen", 23, "kuehlschrank"),
            FoodItem("Curry von gestern", 1, "kuehlschrank"),
            FoodItem("Paprika", 23, "kuehlschrank"),
            FoodItem("test", 22, "hm, weiß auch nicht"),
            FoodItem("Brot", 5, "Regal"),
            FoodItem("Bio-Roggenbrot", 12399, "Regal")
        )
    }

}