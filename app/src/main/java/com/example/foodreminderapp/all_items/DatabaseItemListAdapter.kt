package com.example.foodreminderapp.all_items

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
import com.example.foodreminderapp.all_items.data.DatabaseItem
import com.example.foodreminderapp.databinding.DatabaseItemBinding
import com.example.foodreminderapp.fragments.FoodItemListFragmentDirections


class DatabaseItemListAdapter(
    private val context: Context,
    private val viewModel: DatabaseItemListViewModel
    ) : ListAdapter<DatabaseItem, DatabaseItemListAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DatabaseItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)

        holder.bind(current)

        holder.btnMinusOne.setOnClickListener {
            Toast.makeText(context, "-1", Toast.LENGTH_LONG).show()
        }

//        holder.itemView.setOnClickListener {
//            val action = FoodItemListFragmentDirections
//                .actionListFragmentToItemDetailsFragment(current.id)
//            it.findNavController().navigate(action)
//        }

//        // go to editing fragment when edit button is pressed.
//        holder.btnEditItem.setOnClickListener {
//            val action = FoodItemListFragmentDirections
//                .actionListFragmentToCreateEditFragment(current.id)
//            it.findNavController().navigate(action)
//        }

    }

    class ItemViewHolder(private var binding: DatabaseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val btnPlusOne: ImageView = binding.plusOne
        val btnMinusOne: ImageView = binding.minusOne

        fun bind(item: DatabaseItem) {
            binding.apply {
                itemTitle.text = item.itemName
                itemLocation.text = item.location
                itemDurability.text = item.durability.toString()
                itemAmount.text = item.defaultAmount.toString()
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DatabaseItem>() {
            override fun areItemsTheSame(oldItem: DatabaseItem, newItem: DatabaseItem): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: DatabaseItem, newItem: DatabaseItem): Boolean {
                return oldItem.itemName == newItem.itemName
            }
        }
    }
}
