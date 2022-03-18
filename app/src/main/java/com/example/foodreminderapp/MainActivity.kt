package com.example.foodreminderapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.example.foodreminderapp.adapter.FoodItemAdapter
import com.example.foodreminderapp.data.DataSource
import com.example.foodreminderapp.model.FoodItem
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    private lateinit var foodItemAdapter: FoodItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myDataset = DataSource().loadItems()
        val recyclerView = findViewById<RecyclerView>(R.id.rvFoodItems)

        foodItemAdapter = FoodItemAdapter(this, myDataset)
        recyclerView.adapter = foodItemAdapter
        recyclerView.setHasFixedSize(true)

        val addButton = findViewById<FloatingActionButton>(R.id.buttonAddItem)
        addButton.setOnClickListener {
            val newFoodItem = FoodItem("Success", 2, "shelf")
            foodItemAdapter.addFoodItem(newFoodItem)
        }
    }
}