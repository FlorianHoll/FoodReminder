package com.example.foodreminderapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodreminderapp.data.FoodItem
import com.example.foodreminderapp.databinding.ListItemBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class FoodItemListAdapter(
    private val context: Context,
    private val viewModel: FoodItemListViewModel
    ) : ListAdapter<FoodItem, FoodItemListAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)

        holder.bind(current)

        // delete when delete button is clicked
        holder.btnDeleteItem.setOnClickListener {
            confirmDeleteAction(current)
        }

        holder.btnItemEaten.setOnClickListener {
            confirmDeleteAction(current)
        }

        // go to editing fragment when edit button is pressed
        holder.btnEditItem.setOnClickListener {
            Toast.makeText(context, "Edit item", Toast.LENGTH_LONG).show()
            val action = FoodItemListFragmentDirections
                .actionListFragmentToCreateEditFragment(current.id)
            it.findNavController().navigate(action)
        }

    }

    // Show alert and confirm deletion
    private fun confirmDeleteAction(item: FoodItem) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.itemDeleteTitle)
            .setMessage(R.string.itemDeleteText)
            .setCancelable(false)
            .setNegativeButton(R.string.itemDeleteNo) { _, _ -> }
            .setPositiveButton(R.string.itemDeleteYes) { _, _ ->
                viewModel.deleteItem(item)
            }
            .show()
    }

    class ItemViewHolder(private var binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val itemTitle: TextView = binding.itemTitle
        val itemDaysLeft: TextView = binding.itemDaysLeft
        val itemLocation: TextView = binding.itemLocation
        val btnDeleteItem: ImageView = binding.deleteItem
        val btnEditItem: ImageView = binding.editItem
        val btnItemEaten: ImageView = binding.itemEaten

        fun bind(item: FoodItem) {
            binding.itemTitle.text = item.itemName
            binding.itemDaysLeft.text = item.daysLeft.toString()
            binding.itemLocation.text = item.location
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
