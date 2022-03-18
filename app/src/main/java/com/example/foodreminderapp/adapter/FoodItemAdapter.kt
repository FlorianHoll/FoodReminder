package com.example.foodreminderapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodreminderapp.R
import com.example.foodreminderapp.model.FoodItem

class FoodItemAdapter(
    private val context: Context,
    private val foodItems: MutableList<FoodItem>
) : RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder>() {

    class FoodItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val itemTitle: TextView = view.findViewById(R.id.item_title)
        val itemDaysLeft: TextView = view.findViewById(R.id.item_days_left)
        val itemStorage: TextView = view.findViewById(R.id.item_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        return FoodItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        val currentFoodItem = foodItems[position]
        holder.itemTitle.text = currentFoodItem.title
        holder.itemDaysLeft.text = currentFoodItem.days_left.toString()
        holder.itemStorage.text = currentFoodItem.storage

    }

    override fun getItemCount() = foodItems.size

    fun addFoodItem(item: FoodItem) {
        foodItems.add(item)
        notifyItemInserted(foodItems.size - 1)
    }

    fun throwAwayItem(item: FoodItem, itemIndex: Int) {
        foodItems.remove(item)
        notifyItemRemoved(itemIndex)
    }

}