package com.example.foodreminderapp.fragments

import android.app.DatePickerDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.foodreminderapp.current_items.data.FoodItem
import com.example.foodreminderapp.databinding.FragmentCreateEditItemBinding
import com.example.foodreminderapp.FoodReminderApplication
import com.example.foodreminderapp.all_items.DatabaseItemListViewModel
import com.example.foodreminderapp.all_items.DatabaseItemViewModelFactory
import com.example.foodreminderapp.current_items.FoodItemListViewModel
import com.example.foodreminderapp.current_items.FoodItemViewModelFactory
import com.example.foodreminderapp.utils.calculateTargetDate
import com.example.foodreminderapp.utils.calculateTargetDateInGermanFormat
import com.example.foodreminderapp.utils.getDifferenceInDays
import com.example.foodreminderapp.utils.setTimeLeftText
import java.time.LocalDate
import java.util.*

private const val TAG = "CreateEditFragment"

/**
 * Fragment to add or update an item in the database.
 */
class CreateEditItemFragment : Fragment() {

    private val viewModel: FoodItemListViewModel by activityViewModels {
        FoodItemViewModelFactory(
            (activity?.application as FoodReminderApplication)
                .database.foodItemDao()
        )
    }

    private val databaseItemViewModel: DatabaseItemListViewModel by activityViewModels {
        DatabaseItemViewModelFactory(
            (activity?.application as FoodReminderApplication)
                .itemDatabase.itemDatabaseDao()
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

    // Check if entries are valid.
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.itemName.text.toString(),
            binding.daysLeft.text.toString(),
        )
    }

    private fun isNewItem(): Boolean {
        // The default id that is passed is -1;
        // therefore, if the id is -1, it is a new item; else not.
        return navigationArgs.itemId < 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // if the item is new, it will be saved upon press on the save button.
        if (isNewItem()) {
            binding.apply {
                ivCalendar.setOnClickListener { chooseDate() }
                btnSaveFoodItem.setOnClickListener { addNewItem() }
            }
            // if the item is not new, the information is pre-filled
            // and will be updated upon press on the save button.
        } else {
            val id = navigationArgs.itemId
            viewModel
                .retrieveItem(id)
                .observe(this.viewLifecycleOwner) { selectedItem ->
                    bind(selectedItem)
                }
        }

        // Hide keyboard when radio groups are selected (for nicer user experience).
        binding.apply {
            waitForDaysLeftInput()
            amount.setOnCheckedChangeListener { _, _ -> hideKeyboard() }
            location.setOnCheckedChangeListener { _, _ -> hideKeyboard() }
        }
    }

    // Overridden textChangedListener to inform user about days left and date of item.
    private fun waitForDaysLeftInput() {
        binding.daysLeft.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (binding.daysLeft.text.isNullOrEmpty()) {
                    binding.tvDaysLeftHint.text = ""
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (binding.daysLeft.text!!.isNotEmpty()) {
                    displayDateHint()
                }
            }
        })
    }

    // Bind views with passed information.
    private fun bind(item: FoodItem) {
        binding.apply {
            itemName.setText(item.itemName, TextView.BufferType.SPANNABLE)

            // Set the days left and set hint for user.
            daysLeft.setText(
                getDifferenceInDays(item.bestBefore).toString(),
                TextView.BufferType.SPANNABLE
            )
            displayDateHint()

            // Check the location.
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
            btnSaveFoodItem.setOnClickListener { updateItem(item) }

        }
    }

    // Choose the date from the calendar and bind the new information.
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
                val daysLeft = getDifferenceInDays(selectedDate!!)
                Toast.makeText(
                    requireActivity(),
                    "Haltbar bis $selectedDayOfMonth.${selectedMonth + 1}.$selectedYear" +
                            " (noch $daysLeft Tage).",
                    Toast.LENGTH_LONG
                ).show()
                binding.daysLeft.setText(daysLeft.toString(), TextView.BufferType.SPANNABLE)
                displayDateHint()
            },
            year,
            month,
            day
        ).show()
    }

    private fun displayDateHint() {
        val daysLeft = binding.daysLeft.text.toString().toInt()
        val daysLeftText = setTimeLeftText(daysLeft)
        val bestBeforeDate = calculateTargetDateInGermanFormat(daysLeft)
        val daysLeftHint = "Noch $daysLeftText haltbar (bis $bestBeforeDate)."
        binding.tvDaysLeftHint.text = daysLeftHint
    }

    // Get the selected amount from the radio group.
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

    // Get the selected location from the radio group.
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

            // Add to database that contains the current items.
            val itemName = binding.itemName.text.toString()
            val daysLeft = binding.daysLeft.text.toString().toInt()
            val location = getLocation()
            val amount = getAmount()
            viewModel.addNewItem(
                itemName = itemName,
                itemDaysLeft = daysLeft,
                itemLocation = getLocation(),
                itemAmount = getAmount(),
                itemAdded = LocalDate.now().toString()
            )

            // Update the item database (i.e. add or update the current item).
            databaseItemViewModel.updateItemDatabase(
                FoodItem(
                    id = 100000,
                    itemName = itemName,
                    bestBefore = calculateTargetDate(daysLeft),
                    location = location,
                    amount = amount,
                    durability = daysLeft,
                    added = LocalDate.now().toString()
                )
            )

            // If the last screen in the backstack is the Choosing screen, pop back stack.
            val fromChooseNew = navigationArgs.fromChoose
            if (fromChooseNew) {
                findNavController().popBackStack()
            }
            findNavController().navigateUp()
        } else {
            hintAllFieldsRequired()
        }
    }

    // Update an existing item in the database and navigates back to the list.
    private fun updateItem(itemToUpdate: FoodItem) {
        if (isEntryValid()) {
            viewModel.updateItem(
                itemId = itemToUpdate.id,
                itemName = binding.itemName.text.toString(),
                itemDaysLeft = binding.daysLeft.text.toString().toInt(),
                itemLocation = getLocation(),
                itemAmount = getAmount(),
                itemAdded = itemToUpdate.added
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
