package com.example.foodreminderapp.current_items

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodreminderapp.R
import com.example.foodreminderapp.current_items.data.FoodItem
import com.example.foodreminderapp.databinding.ListItemBinding
import com.example.foodreminderapp.fragments.FoodItemListFragmentDirections
import com.example.foodreminderapp.statistics.StatisticsViewModel
import com.example.foodreminderapp.utils.setBestBeforeText
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val TAG = "FoodItemListAdapter"


class FoodItemListAdapter(
    private val context: Context,
    private val viewModel: FoodItemListViewModel,
    private val statisticsViewModel: StatisticsViewModel
    ) : ListAdapter<FoodItem, FoodItemListAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item)

        holder.itemView.setOnClickListener {
            val action = FoodItemListFragmentDirections
                .actionListFragmentToItemDetailsFragment(item.id)
            it.findNavController().navigate(action)
        }

        // throw away, i.e. delete and add to statistics as thrown away
        holder.btnDeleteItem.setOnClickListener {
            confirmDeleteAction(item)
        }

        // delete and add to statistics as eaten.
        holder.btnItemEaten.setOnClickListener {
            deleteAndAddToStatistics(item, thrownAway = false)
        }

        // go to editing fragment when edit button is pressed.
        holder.btnEditItem.setOnClickListener {
            val action = FoodItemListFragmentDirections
                .actionListFragmentToCreateEditFragment(item.id)
            it.findNavController().navigate(action)
        }
    }

    private fun deleteAndAddToStatistics(item: FoodItem, thrownAway: Boolean) {
        viewModel.deleteItem(item)
        statisticsViewModel.addNewItem(
            name = item.itemName,
            thrownAway = thrownAway,
            amount = item.amount,
            createdTime = item.added,
            location = item.location
        )
    }

    // Show alert and confirm throwing the item away.
    private fun confirmDeleteAction(item: FoodItem) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.itemDeleteThrowAwayConfirm)
            .setNegativeButton(R.string.itemDeleteNo) { _, _ -> }
            .setPositiveButton(R.string.itemDeleteYes) { _, _ ->
                deleteAndAddToStatistics(item, thrownAway = true)
            }
            .show()
    }

    class ItemViewHolder(private var binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val itemTitle: TextView = binding.itemTitle
        private val itemDaysLeft: TextView = binding.itemDaysLeft
        private val itemLocation: TextView = binding.itemLocation
        val btnDeleteItem: ImageView = binding.deleteItem
        val btnEditItem: ImageView = binding.editItem
        val btnItemEaten: ImageView = binding.itemEaten

        fun bind(item: FoodItem) {
            val daysLeftText = setBestBeforeText(item)
            val displayedItemName = when (item.amount) {
                1 -> item.itemName
                else -> "${item.itemName} (${item.amount})"
            }
            itemTitle.text = displayedItemName
            itemDaysLeft.text = daysLeftText
            itemLocation.text = item.location
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<FoodItem>() {
            override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
                return oldItem.itemName == newItem.itemName
            }
        }
    }
}
