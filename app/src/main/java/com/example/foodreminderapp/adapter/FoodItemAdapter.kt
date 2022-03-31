package com.example.foodreminderapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.foodreminderapp.FoodItemListFragmentDirections
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
        val itemEaten: ImageView = view.findViewById(R.id.item_eaten)
        val deleteItem: ImageView = view.findViewById(R.id.delete_item)
        val editItem: ImageView = view.findViewById(R.id.edit_item)
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
        val currentTitle = currentFoodItem.title
        val currentDaysLeft = context.getString(
            R.string.itemDaysLeft, currentFoodItem.days_left.toString()
        )
        val currentStorage = currentFoodItem.storage

        // display title, storage etc.
        holder.apply {
            itemTitle.text = currentTitle
            itemDaysLeft.text = currentDaysLeft
            itemStorage.text = currentStorage
        }

        // navigate to edit fragment if edit button is clicked
        holder.editItem.setOnClickListener { view ->
            val navigationAction = FoodItemListFragmentDirections
                .actionListFragmentToCreateEditFragment(
                    name = currentTitle,
                    days = currentDaysLeft,
                    location = currentStorage,
                    position = position,
                    edited = true
                )
            view.findNavController().navigate(navigationAction)
        }

        holder.deleteItem.setOnClickListener {
            throwAwayItem(position)
        }

    }

    override fun getItemCount() = foodItems.size

    fun updateFoodItem(
        position: Int,
        foodName: String,
        foodDays: String,
        foodLocation: String
    ) {
        val newFoodItem = FoodItem(foodName, foodDays.toInt(), foodLocation)
        foodItems[position] = newFoodItem
        notifyItemChanged(position)
    }

    fun addFoodItem(item: FoodItem) {
        foodItems.add(item)
        notifyItemInserted(foodItems.size - 1)
    }

    fun throwAwayItem(position: Int) {
        foodItems.removeAt(position)
        notifyDataSetChanged()
    }

}