package com.example.foodreminderapp.fragments

import android.app.DatePickerDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
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
import com.example.foodreminderapp.*
import com.example.foodreminderapp.data.FoodItem
import com.example.foodreminderapp.databinding.FragmentCreateEditItemBinding
import java.util.*

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
    private var selectedDate: String? = null

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
            daysLeft.setText(getDaysLeft(item.bestBefore).toString(), TextView.BufferType.SPANNABLE)
            val checkedLocationId = when (item.location) {
                getString(R.string.chooseListShelf) -> R.id.option_regal
                getString(R.string.chooseListFridge) -> R.id.option_kuehlschrank
                else -> R.id.option_tiefkuehltruhe
            }
            location.check(checkedLocationId)

            // Check if amount is in list of buttons; if not, enter text in field.
            if (item.amount in arrayOf(1, 2, 5, 10)) {
                val checkedAmountId = when (item.amount) {
                    1 -> R.id.option_amount_1
                    2 -> R.id.option_amount_2
                    5 -> R.id.option_amount_5
                    else -> R.id.option_amount_10
                }
                amount.check(checkedAmountId)
            } else {
                amount.clearCheck()
                amountElse.setText(item.amount.toString(), TextView.BufferType.SPANNABLE)
            }

            // Enable choosing date based on calendar
            ivCalendar.setOnClickListener { chooseDate() }

            // Update item when save button is clicked.
            btnSaveFoodItem.setOnClickListener { updateItem() }

        }
    }

    private fun chooseDate() {
        hideKeyboard()
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(
            requireContext(),
            { view, selectedYear, selectedMonth, selectedDayOfMonth ->
                selectedDate = (
                        "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
                        )
                val daysLeft = getDaysLeft(selectedDate!!)
                Toast.makeText(
                    requireActivity(),
                    "Haltbar bis $selectedDayOfMonth.${selectedMonth + 1}.$selectedYear" +
                            " (noch $daysLeft Tage).",
                    Toast.LENGTH_LONG
                ).show()
                binding.daysLeft.setText(daysLeft.toString(), TextView.BufferType.SPANNABLE)
            },
            year,
            month,
            day
        ).show()
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
            R.id.option_kuehlschrank -> getString(R.string.chooseListFridge)
            R.id.option_regal -> getString(R.string.chooseListShelf)
            else -> getString(R.string.chooseListFreezer)
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
            findNavController().navigateUp()
        } else {
            hintAllFieldsRequired()
        }
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
            findNavController().navigateUp()
        } else {
            hintAllFieldsRequired()
        }
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
            viewModel
                .retrieveItem(id)
                .observe(this.viewLifecycleOwner) { selectedItem ->
                    bind(selectedItem)
                }
        } else {
            binding.apply {
                ivCalendar.setOnClickListener { chooseDate() }
                btnSaveFoodItem.setOnClickListener { addNewItem() }
            }
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireActivity()
            .getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken, 0
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        hideKeyboard()
        _binding = null
    }
}
