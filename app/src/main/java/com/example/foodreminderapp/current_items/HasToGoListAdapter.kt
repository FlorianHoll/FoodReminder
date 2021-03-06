package com.example.foodreminderapp.current_items

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodreminderapp.current_items.FoodItemListViewModel
import com.example.foodreminderapp.R
import com.example.foodreminderapp.current_items.data.FoodItem
import com.example.foodreminderapp.databinding.HasToGoListItemBinding
import com.example.foodreminderapp.fragments.HasToGoFragmentDirections
import com.example.foodreminderapp.setBestBeforeText
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val TAG = "HasToGoListAdapter"


class HasToGoListAdapter(
    private val context: Context,
    private val viewModel: FoodItemListViewModel
) : ListAdapter<FoodItem, HasToGoListAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            HasToGoListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item)

        holder.itemView.setOnClickListener {
            val action = HasToGoFragmentDirections
                .actionHasToGoToItemDetailFragment(item.id)
            Log.d(TAG, "Navigated with ID ${item.id}.")
            it.findNavController().navigate(action)
        }

        // delete (with warning) when item was thrown away
        holder.btnDeleteItem.setOnClickListener {
            confirmDeleteAction(item)
        }

        // delete (without warning) when item was eaten
        holder.btnItemEaten.setOnClickListener {
            viewModel.deleteItem(item)
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

    class ItemViewHolder(private var binding: HasToGoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val btnDeleteItem: ImageView = binding.deleteItem
        val btnItemEaten: ImageView = binding.itemEaten

        fun bind(item: FoodItem) {
            val daysLeftText = setBestBeforeText(item)
            binding.apply {
                itemDaysLeft.text = daysLeftText
                var itemName = item.itemName
                if (itemName.length > 9) {
                    itemName = itemName.take(9).plus("...")
                }
                val displayedItemName = when (item.amount) {
                    1 -> itemName
                    else -> "$itemName (${item.amount})"
                }
                itemTitle.text = displayedItemName
            }

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
