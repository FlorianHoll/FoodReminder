package com.example.foodreminderapp.fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.foodreminderapp.*
import com.example.foodreminderapp.current_items.data.FoodItem
import com.example.foodreminderapp.databinding.FragmentItemDetailsBinding
import com.example.foodreminderapp.FoodReminderApplication
import com.example.foodreminderapp.current_items.FoodItemListViewModel
import com.example.foodreminderapp.current_items.FoodItemViewModelFactory
import com.example.foodreminderapp.statistics.StatisticsItemListViewModel
import com.example.foodreminderapp.statistics.StatisticsItemViewModelFactory
import com.example.foodreminderapp.utils.convertToGermanDate
import com.example.foodreminderapp.utils.getDifferenceInDays
import com.example.foodreminderapp.utils.setBestBeforeText
import java.lang.NullPointerException

/**
 * Fragment to see item details.
 */
class ItemDetailFragment : DialogFragment() {

    private val viewModel: FoodItemListViewModel by activityViewModels {
        FoodItemViewModelFactory(
            (activity?.application as FoodReminderApplication)
                .database.foodItemDao()
        )
    }

    private val statisticsViewModel: StatisticsItemListViewModel by activityViewModels {
        StatisticsItemViewModelFactory(
            (activity?.application as FoodReminderApplication)
                .statisticsDatabase.statisticsItemDao()
        )
    }

    private val navigationArgs: ItemDetailFragmentArgs by navArgs()

    lateinit var item: FoodItem

    private var _binding: FragmentItemDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Bind views with passed information.
    private fun bind(item: FoodItem) {

        val daysLeftText = setBestBeforeText(item)
        binding.apply {

            // Set basic item information.
            tvItemName.setText(item.itemName, TextView.BufferType.SPANNABLE)
            tvDaysLeftDate.setText(
                convertToGermanDate(item.bestBefore),
                TextView.BufferType.SPANNABLE
            )
            tvDaysLeftInDays.setText(daysLeftText, TextView.BufferType.SPANNABLE)
            tvLocation.setText(item.location, TextView.BufferType.SPANNABLE)

            // Set correct amount and corresponding button text.
            val amount = "Menge: ${item.amount}"
            tvAmount.setText(amount, TextView.BufferType.SPANNABLE)
            if (item.amount > 1) {
                btnEaten.text = getString(R.string.itemDetailsOneEaten)
                btnThrownAway.text = getString(R.string.itemDetailsOneThrownAway)
            } else {
                btnEaten.text = getString(R.string.itemDetailsEaten)
                btnThrownAway.text = getString(R.string.itemDetailsThrownAway)
            }

            // Set correct image according to storage location.
            val imageLocation = when (item.location) {
                getString(R.string.chooseListFridge) -> R.drawable.ic_fridge
                getString(R.string.chooseListFreezer) -> R.drawable.ic_freezer
                else -> R.drawable.ic_shelf_small
            }
            ivLocation.setImageResource(imageLocation)

            // Navigate to edit fragment if button is clicked.
            btnEdit.setOnClickListener {
                val action = ItemDetailFragmentDirections
                    .actionDetailFragmentToEditCreateFragment(item.id)
                findNavController().navigate(action)
            }

            // On press, delete or decrease by one and add to statistics as eaten.
            btnEaten.setOnClickListener {
                when (item.amount) {
                    1 -> deleteAndNavigateBack(item, thrownAway = false)
                    else -> decreaseAmountAndUpdate(item, thrownAway = false)
                }
            }

            // On long press, delete item and add to statistics as eaten.
            btnEaten.setOnLongClickListener {
                deleteAndNavigateBack(item, thrownAway = false)
            }

            // On press, delete or decrease by one and add to statistics as thrown away.
            btnThrownAway.setOnClickListener {
                when (item.amount) {
                    1 -> deleteAndNavigateBack(item, thrownAway = true)
                    else -> decreaseAmountAndUpdate(item, thrownAway = true)
                }
            }

            // On long press, delete item and add to statistics as thrown away.
            btnThrownAway.setOnLongClickListener {
                deleteAndNavigateBack(item, thrownAway = true)
            }
        }
    }

    private fun decreaseAmountAndUpdate(item: FoodItem, thrownAway: Boolean) {
        val newItem = item.copy(amount = item.amount - 1)
        updateItem(newItem)
        addItemToStatisticsDatabase(
            item = item,
            thrownAway = thrownAway
        )
    }

    // The food item that is passed has already been decreased by the specified amount
    private fun updateItem(item: FoodItem) {
        viewModel.updateItem(
            itemId = item.id,
            itemName = item.itemName,
            itemDaysLeft = getDifferenceInDays(item.bestBefore),
            itemLocation = item.location,
            itemAmount = item.amount,
            itemAdded = item.added
        )
    }

    private fun deleteItem(item: FoodItem, thrownAway: Boolean) {
        viewModel.deleteItem(item)
        statisticsViewModel.addNewItem(
            name = item.itemName,
            thrownAway = thrownAway,
            amount = item.amount,
            createdTime = item.added,
            location = item.location
        )
    }

    // Navigate back to the list fragment.
    private fun deleteAndNavigateBack(
        item: FoodItem, thrownAway: Boolean
    ): Boolean {
        deleteItem(item, thrownAway = thrownAway)
        findNavController().navigateUp()
        return true
    }

    private fun addItemToStatisticsDatabase(
        item: FoodItem,
        thrownAway: Boolean
    ) {
        statisticsViewModel.addNewItem(
            name = item.itemName,
            thrownAway = thrownAway,
            amount = 1,
            createdTime = item.added,
            location = item.location
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id: Int = navigationArgs.itemId
        viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
            try {
                bind(selectedItem)
            } catch (e: NullPointerException) {
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken,
            0
        )
        _binding = null
    }
}
