package com.example.foodreminderapp.statistics
//
//
//import android.content.Context
//import android.content.res.Configuration
//import android.graphics.Color
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import android.widget.*
//import androidx.cardview.widget.CardView
//import androidx.core.content.ContextCompat
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.example.foodreminderapp.R
//import com.example.foodreminderapp.all_items.DatabaseItemListViewModel
//import com.example.foodreminderapp.all_items.data.DatabaseItem
//import com.example.foodreminderapp.current_items.FoodItemListViewModel
//import com.example.foodreminderapp.current_items.data.FoodItem
//import com.example.foodreminderapp.databinding.DatabaseItemBinding
//import com.example.foodreminderapp.statistics.data.StatisticsItem
//import com.example.foodreminderapp.utils.calculateTargetDate
//import com.example.foodreminderapp.utils.setShortTimeLeftText
//import java.time.LocalDate
//import kotlin.math.max
//
//
//private const val TAG = "DatabaseItemListAdapter"
//
//class StatisticsListAdapter(
//    private val context: Context,
//    private val databaseItemViewModel: DatabaseItemListViewModel,
//    private val currentItemsViewModel: FoodItemListViewModel
//) : ListAdapter<StatisticsItem, StatisticsListAdapter.ItemViewHolder>(DiffCallback) {
//
//    private val selectedItems = databaseItemViewModel.selectedItems
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
//        return ItemViewHolder(
//            DatabaseItemBinding.inflate(
//                LayoutInflater.from(parent.context), parent, false
//            ),
//            context,
//            databaseItemViewModel,
//            isInDarkMode()
//        )
//    }
//
//    fun checkIfSelectionIsEmpty(): Boolean {
//        return selectedItems.isEmpty()
//    }
//
//    fun addSelectedItems() {
//        selectedItems.forEach { (_, item) ->
//            Log.d(
//                TAG,
//                "Adding item to current items with following information: " +
//                        "name: ${item.itemName}; " +
//                        "location: ${item.location}; " +
//                        "\"amount: ${item.defaultAmount}."
//            )
//
//            // Update item in item database.
//            databaseItemViewModel.updateItemDatabase(
//                FoodItem(
//                    itemName = item.itemName,
//                    bestBefore = calculateTargetDate(item.durability),
//                    location = item.location,
//                    durability = item.durability,
//                    amount = item.defaultAmount,
//                    added = LocalDate.now().toString()
//                )
//            )
//
//            // Add item to database of current items.
//            currentItemsViewModel.addNewItem(
//                itemName = item.itemName,
//                itemDaysLeft = item.durability,
//                itemLocation = item.location,
//                itemAmount = item.defaultAmount,
//                itemAdded = LocalDate.now().toString()
//            )
//        }
//
//        // Give user feedback how many items have been added.
//        Toast.makeText(
//            context,
//            "${selectedItems.size} Lebensmittel hinzugefügt.",
//            Toast.LENGTH_LONG
//        ).show()
//    }
//
//    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
//        val current = getItem(position)
//
//        fun changeAmountBy(amount: Int) {
//            val oldAmount = holder.itemAmount.text.toString().toInt()
//            val newAmount = max(oldAmount + amount, 1)
//            holder.itemAmount.text = newAmount.toString()
//            // Save to map as well so that it can be retrieved on new bind
//            if (selectedItems.containsKey(current.id)) {
//                val itemBeforeChanges = selectedItems[current.id]
//                selectedItems[current.id] = itemBeforeChanges!!.copy(defaultAmount = newAmount)
//            }
//            Log.d(TAG, "$selectedItems")
//        }
//
//        fun changeLocation() {
//            val fridge = context.getString(R.string.chooseListFridge)
//            val freezer = context.getString(R.string.chooseListFreezer)
//            val shelf = context.getString(R.string.chooseListShelf)
//            // toggle location on click
//            val newLocation = when (holder.itemLocation.text) {
//                fridge -> freezer
//                freezer -> shelf
//                else -> fridge
//            }
//            holder.itemLocation.text = newLocation
//            // Save to map as well
//            if (selectedItems.containsKey(current.id)) {
//                val itemBeforeChanges = selectedItems[current.id]
//                selectedItems[current.id] = itemBeforeChanges!!.copy(location = newLocation)
//            }
//            Log.d(TAG, "stored now as $newLocation: ${selectedItems[current.id]?.location}")
//            Log.d(TAG, "$selectedItems")
//        }
//
//        holder.bind(current)
//
//        holder.checkedAdd.setOnClickListener {
//            // store (or delete) item from selected Items map and set color
//            if (selectedItems.containsKey(current.id)) {
//                Log.d(TAG, "Item ${current.itemName} removed from selected, set to white.")
//                selectedItems.remove(current.id)
//                if (isInDarkMode()) {
//                    holder.itemCard.setCardBackgroundColor(
//                        ContextCompat.getColor(context, R.color.secondary)
//                    )
//                } else {
//                    holder.itemCard.setBackgroundColor(
//                        ContextCompat.getColor(context, R.color.white)
//                    )
//                }
//            } else {
//                databaseItemViewModel.selectedItems[current.id] = current
//                holder.itemCard.setCardBackgroundColor(
//                    ContextCompat.getColor(context, R.color.primary_variant_dark)
//                )
//                Log.d(TAG, "Color for item ${current.itemName} set to orange.")
//            }
//        }
//
//        holder.itemCard.setOnClickListener {
//            holder.checkedAdd.performClick()
//        }
//
//        holder.btnMinusOne.setOnClickListener { changeAmountBy(-1) }
//        holder.btnMinusOne.setOnLongClickListener { changeAmountBy(-10); true }
//
//        holder.btnPlusOne.setOnClickListener { changeAmountBy(1) }
//        holder.btnPlusOne.setOnLongClickListener { changeAmountBy(10); true }
//
//        holder.itemLocation.setOnClickListener { changeLocation() }
//
//    }
//
//    private fun isInDarkMode(): Boolean {
//        return context.resources.configuration.uiMode and
//                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
//    }
//
//    class ItemViewHolder(
//        private var binding: StatisticsItem,
//        private val context: Context,
//        private val databaseItemViewModel: DatabaseItemListViewModel,
//        private val isInDarkMode: Boolean,
//    ) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        val itemTitle: TextView = binding.itemTitle
//        val itemLocation: TextView = binding.itemLocation
//        val itemDurability: TextView = binding.itemDurability
//        val itemAmount: TextView = binding.itemAmount
//        val btnPlusOne: ImageView = binding.plusOne
//        val btnMinusOne: ImageView = binding.minusOne
//        val checkedAdd: CheckBox = binding.checkItem
//        val itemCard: CardView = binding.cardView
//
//        fun bind(item: DatabaseItem) {
//
//            if (databaseItemViewModel.selectedItems.containsKey(item.id)) {
//                val newItem: DatabaseItem? = databaseItemViewModel.selectedItems[item.id]
//                binding.apply {
//                    checkItem.isChecked = true
//                    itemCard.setCardBackgroundColor(
//                        ContextCompat.getColor(context, R.color.primary_variant_dark)
//                    )
//                    itemAmount.text = newItem?.defaultAmount.toString()
//                    itemLocation.text = newItem?.location
//                }
//            } else {
//                binding.apply { checkItem.isChecked = false }
//                if (isInDarkMode) {
//                    itemCard.setCardBackgroundColor(
//                        ContextCompat.getColor(context, R.color.secondary)
//                    )
//                } else {
//                    itemCard.setBackgroundColor(
//                        ContextCompat.getColor(context, R.color.white)
//                    )
//                }
//
//                itemAmount.text = item.defaultAmount.toString()
//                itemLocation.text = item.location
//            }
//
//            binding.apply {
//                itemTitle.text = item.itemName
//                val durability = setShortTimeLeftText(item.durability)
//                itemDurability.text = durability
//            }
//        }
//    }
//
//    companion object {
//        private val DiffCallback = object : DiffUtil.ItemCallback<DatabaseItem>() {
//            override fun areItemsTheSame(
//                oldItem: DatabaseItem,
//                newItem: DatabaseItem
//            ): Boolean {
//                return oldItem === newItem
//            }
//
//            override fun areContentsTheSame(
//                oldItem: DatabaseItem,
//                newItem: DatabaseItem
//            ): Boolean {
//                return oldItem.itemName == newItem.itemName
//            }
//        }
//    }
//}
