package com.example.foodreminderapp.data

import com.example.foodreminderapp.model.FoodItem

class DataSource {

    fun loadItems(): MutableList<FoodItem> {
        return mutableListOf<FoodItem>(
            FoodItem("Banane", 12, "Regal"),
            FoodItem("Apfel", 10, "Regal"),
            FoodItem("Karotte", 5, "Kühlschrank"),
            FoodItem("Tofu", 5, "Kühlschrank"),
            FoodItem("Soja-Bällchen", 23, "Kühlschrank"),
            FoodItem("Curry von gestern", 1, "Kühlschrank"),
            FoodItem("Paprika", 23, "Kühlschrank"),
            FoodItem("test", 22, "Tiefkühlschrank"),
            FoodItem("Brot", 5, "Regal"),
            FoodItem("Bio-Roggenbrot", 12399, "Regal")
        )
    }
}