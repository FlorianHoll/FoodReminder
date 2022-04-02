package com.example.foodreminderapp

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
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
import com.example.foodreminderapp.data.FoodItem
import com.example.foodreminderapp.data.getDaysLeft
import com.example.foodreminderapp.databinding.FragmentCreateEditItemBinding

/**
 * Fragment to add or update an item in the Inventory database.
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

    /**
     * Returns true if the EditTexts are not empty
     */
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.itemName.text.toString(),
            binding.daysLeft.text.toString(),
        )
    }

    /**
     * Binds views with the passed in [item] information.
     */
    private fun bind(item: FoodItem) {
//        val price = "%.2f".format(item.itemPrice)
        binding.apply {
            itemName.setText(item.itemName, TextView.BufferType.SPANNABLE)
            daysLeft.setText(item.getDaysLeft(), TextView.BufferType.SPANNABLE)
            val checkedItemId = when (item.location) {
                "Regal" -> R.id.option_regal
                "Kühlschrank" -> R.id.option_kuehlschrank
                else -> R.id.option_tiefkuehlschrank
            }
            binding.location.check(checkedItemId)
            // Update item when save button is clicked.
            btnSaveFoodItem.setOnClickListener { updateItem() }
        }
    }

    private fun getCheckedLocation(): String {
        val foodItemLocation = when (
            binding.location.checkedRadioButtonId
        ) {
            R.id.option_kuehlschrank -> "Kühlschrank"
            R.id.option_regal -> "Regal"
            else -> "Tiefkühlschrank"
        }
        return foodItemLocation
    }

    /**
     * Inserts the new Item into database and navigates up to list fragment.
     */
    private fun addNewItem() {
        if (isEntryValid()) {
            viewModel.addNewItem(
                itemName = binding.itemName.text.toString(),
                itemDaysLeft = binding.daysLeft.text.toString().toInt(),
                itemLocation = getCheckedLocation()
            )
            navigateBackToList()
        } else { hintAllFieldsRequired() }
    }

    /**
     * Updates an existing Item in the database and navigates up to list fragment.
     */
    private fun updateItem() {
        if (isEntryValid()) {
            viewModel.updateItem(
                itemId = this.navigationArgs.itemId,
                itemName = this.binding.itemName.text.toString(),
                itemDaysLeft = binding.daysLeft.text.toString().toInt(),
                itemLocation = getCheckedLocation()
            )
            navigateBackToList()
        } else { hintAllFieldsRequired() }
    }

    private fun navigateBackToList() {
        val action = CreateEditItemFragmentDirections
            .actionCreateEditFragmentToListFragment()
        findNavController().navigate(action)
    }

    private fun hintAllFieldsRequired() {
        Toast.makeText(
            context, "Alle Felder müssen ausgefüllt werden.", Toast.LENGTH_LONG
        ).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.itemId
        // The default id that is passed is -1;
        // therefore, if an id is passed, it is not a new ite.
        if (id > 0) {
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
                item = selectedItem
                bind(item)
            }
        } else {
            binding.btnSaveFoodItem.setOnClickListener {
                addNewItem()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}
