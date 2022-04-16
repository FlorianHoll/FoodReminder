package com.example.foodreminderapp.all_items

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodreminderapp.R
import com.example.foodreminderapp.all_items.data.DatabaseItem
import com.example.foodreminderapp.current_items.FoodItemListViewModel
import com.example.foodreminderapp.databinding.DatabaseItemBinding
import com.example.foodreminderapp.setShortDaysLeftText
import kotlin.math.max


private const val TAG = "DatabaseItemListAdapter"

class DatabaseItemListAdapter(
    private val context: Context,
    private val databaseItemViewModel: DatabaseItemListViewModel,
    private val currentItemsViewModel: FoodItemListViewModel
) : ListAdapter<DatabaseItem, DatabaseItemListAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DatabaseItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    fun addSelectedItems() {
        val allItems = this.currentList
        var checkedCount = 0
        for (item in allItems) {
            if (item.checked) {
                checkedCount++
                Log.d(
                    TAG,
                    "Adding item to current items with following information: " +
                            "name: ${item.itemName}; " +
                            "location: ${item.currentLocation}; " +
                            "\"amount: ${item.currentAmount}."
                )

                // Update item in database
                databaseItemViewModel.updateItemLocation(item, item.currentLocation)
                val weightedAmount = (
                        (item.timesEaten * item.defaultAmount + item.currentAmount) /
                                (item.timesEaten + 1)
                        )
                databaseItemViewModel.updateItemAmount(item, weightedAmount)
                databaseItemViewModel.addOneAmount(item)

                // Add item to current database
                currentItemsViewModel.addNewItem(
                    itemName = item.itemName,
                    itemDaysLeft = item.durability,
                    itemLocation = item.currentLocation,
                    itemAmount = item.currentAmount
                )
            }
        }
        Toast.makeText(
            context, "$checkedCount Lebensmittel hinzugefügt.", Toast.LENGTH_LONG
        ).show()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)

        holder.bind(current)

        fun changeAmountBy(amount: Int) {
            val oldAmount = holder.itemAmount.text.toString().toInt()
            val newAmount = max(oldAmount + amount, 1)
            holder.itemAmount.text = newAmount.toString()
            // Save amount to object as well
            current.currentAmount = newAmount
        }

        fun changeLocation() {
            val fridge = context.getString(R.string.chooseListFridge)
            val freezer = context.getString(R.string.chooseListFreezer)
            val shelf = context.getString(R.string.chooseListShelf)
            val newLocation = when (holder.itemLocation.text) {
                fridge -> freezer
                freezer -> shelf
                else -> fridge
            }
            holder.itemLocation.text = newLocation
            // Save to object as well
            current.currentLocation = newLocation
        }

        fun toggleChecked() {
            current.checked = !current.checked
            val cardColor = when (current.checked) {
                true -> ContextCompat.getColor(context, R.color.primary_variant_dark)
                else -> Color.WHITE
            }
            holder.itemCard.setCardBackgroundColor(cardColor)
        }

        holder.itemCard.setOnClickListener {
            holder.checkedAdd.isChecked = !holder.checkedAdd.isChecked
        }

        holder.checkedAdd.setOnCheckedChangeListener { _, isChecked -> toggleChecked() }

        holder.btnPlusOne.setOnClickListener { changeAmountBy(1) }

        holder.btnPlusOne.setOnLongClickListener {
            changeAmountBy(10)
            true
        }

        holder.btnMinusOne.setOnClickListener { changeAmountBy(-1) }

        holder.btnMinusOne.setOnLongClickListener {
            changeAmountBy(-10)
            true
        }

        holder.itemLocation.setOnClickListener { changeLocation() }

        // this.currentList
        // https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter


//        holder.itemView.setOnClickListener {
//            val action = FoodItemListFragmentDirections
//                .actionListFragmentToItemDetailsFragment(current.id)
//            it.findNavController().navigate(action)
//        }

    }

    class ItemViewHolder(private var binding: DatabaseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val itemTitle: TextView = binding.itemTitle
        val itemLocation: TextView = binding.itemLocation
        val itemDurability: TextView = binding.itemDurability
        val itemAmount: TextView = binding.itemAmount
        val btnPlusOne: ImageView = binding.plusOne
        val btnMinusOne: ImageView = binding.minusOne
        val checkedAdd: CheckBox = binding.checkItem
        val itemCard: CardView = binding.cardView

        fun bind(item: DatabaseItem) {
            binding.apply {
                val durability = setShortDaysLeftText(item.durability)
                itemTitle.text = item.itemName
                itemLocation.text = item.location
                itemDurability.text = durability
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
