package com.example.foodreminderapp.fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.foodreminderapp.FoodItemListApplication
import com.example.foodreminderapp.FoodItemListViewModel
import com.example.foodreminderapp.FoodItemViewModelFactory
import com.example.foodreminderapp.R
import com.example.foodreminderapp.data.FoodItem
import com.example.foodreminderapp.databinding.FragmentCreateEditItemBinding

/**
 * Fragment to add or update an item in the database.
 */
class CreateEditItemFragment : Fragment() {

    private val viewModel: FoodItemListViewModel by activityViewModels {
        FoodItemViewModelFactory(
            (activity?.application as FoodItemListApplication).database.foodItemDao()
        )
    }
    private val navigationArgs: CreateEditItemFragmentArgs by navArgs()

    lateinit var item: FoodItem

    private var _binding: FragmentCreateEditItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateEditItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Check if entries are valid; if so, returns true.
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.itemName.text.toString(),
            binding.daysLeft.text.toString(),
        )
    }

    // Bind views with passed information.
    private fun bind(item: FoodItem) {
        binding.apply {
            itemName.setText(item.itemName, TextView.BufferType.SPANNABLE)
            daysLeft.setText(item.getDaysLeft(), TextView.BufferType.SPANNABLE)
            val checkedItemId = when (item.location) {
                "Regal" -> R.id.option_regal
                "Kühlschrank" -> R.id.option_kuehlschrank
                else -> R.id.option_tiefkuehlschrank
            }
            binding.location.check(checkedItemId)

            // TODO: Check if amount is in (1, 2, 5, 10); if yes, check box; if
            //  no, enter text in field

            // Update item when save button is clicked.
            btnSaveFoodItem.setOnClickListener { updateItem() }
        }
    }

    private fun getAmount(): Int {

        val enteredAmount: Editable? = binding.amountElse.text
        val amount = if (!enteredAmount.isNullOrEmpty()) {
            enteredAmount.toString().toInt()
        } else {
            when (binding.amount.checkedRadioButtonId) {
                R.id.option_amount_1 -> 1
                R.id.option_amount_2 -> 2
                R.id.option_amount_5 -> 5
                else -> 10
            }
        }
        return amount
    }

    // Return which option was checked in the radio group.
    private fun getLocation(): String {
        val foodItemLocation = when (
            binding.location.checkedRadioButtonId
        ) {
            R.id.option_kuehlschrank -> "Kühlschrank"
            R.id.option_regal -> "Regal"
            else -> "Tiefkühlschrank"
        }
        return foodItemLocation
    }

    // Insert the new item into the database and navigate back to the list.
    private fun addNewItem() {
        if (isEntryValid()) {
            viewModel.addNewItem(
                itemName = binding.itemName.text.toString(),
                itemDaysLeft = binding.daysLeft.text.toString().toInt(),
                itemLocation = getLocation(),
                itemAmount = getAmount()
            )
            navigateBackToList()
        } else { hintAllFieldsRequired() }
    }

    // Update an existing item in the database and navigates back to the list.
    private fun updateItem() {
        if (isEntryValid()) {
            viewModel.updateItem(
                itemId = this.navigationArgs.itemId,
                itemName = this.binding.itemName.text.toString(),
                itemDaysLeft = binding.daysLeft.text.toString().toInt(),
                itemLocation = getLocation(),
                itemAmount = getAmount()
            )
            navigateBackToList()
        } else { hintAllFieldsRequired() }
    }

    // Navigate back to the list fragment.
    private fun navigateBackToList() {
        val action = CreateEditItemFragmentDirections
            .actionCreateEditFragmentToListFragment()
        findNavController().navigate(action)
    }

    // Display toast that all input fields are required.
    private fun hintAllFieldsRequired() {
        Toast.makeText(
            context, "Alle Felder müssen ausgefüllt werden.", Toast.LENGTH_LONG
        ).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.itemId
        // The default id that is passed is -1;
        // therefore, if an id is passed, it is not a new item.
        if (id > 0) {
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
                bind(selectedItem)
            }
        } else {
            binding.btnSaveFoodItem.setOnClickListener {
                addNewItem()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}
