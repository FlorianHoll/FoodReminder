package com.example.foodreminderapp.fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
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
import com.example.foodreminderapp.data.FoodItem
import com.example.foodreminderapp.data.getDaysLeft
import com.example.foodreminderapp.databinding.FragmentItemDetailsBinding

private const val TAG = "ItemDetailFragment"

/**
 * Fragment to see item details.
 */
class ItemDetailFragment : Fragment() {

    private val viewModel: FoodItemListViewModel by activityViewModels {
        FoodItemViewModelFactory(
            (activity?.application as FoodItemListApplication).database.foodItemDao()
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
        binding.apply {
            tvItemName.setText(item.itemName, TextView.BufferType.SPANNABLE)
            tvDaysLeftDate.setText(item.bestBefore, TextView.BufferType.SPANNABLE)
            tvDaysLeftInDays.setText(item.getDaysLeft(), TextView.BufferType.SPANNABLE)
            tvLocation.setText(item.location, TextView.BufferType.SPANNABLE)

            // Navigate to edit fragment if button is clicked
            btnEdit.setOnClickListener {
                val action = ItemDetailFragmentDirections
                    .actionDetailFragmentToEditCreateFragment(item.id)
                findNavController().navigate(action)
            }

            // Delete item if eaten
            btnEaten.setOnClickListener {
                deleteAndNavigateBack(item)
            }

            // Delete item if thrown away
            btnThrownAway.setOnClickListener {
                deleteAndNavigateBack(item)
            }
        }
    }

    private fun deleteItem(item: FoodItem) {
        viewModel.deleteItem(item)
    }

    // TODO: Add logic to decrease amount by 1.
//    // Update an existing item in the database and navigates back to the list.
//    private fun updateItem() {
//        if (isEntryValid()) {
//            viewModel.updateItem(
//                itemId = this.navigationArgs.itemId,
//                itemName = this.binding.itemName.text.toString(),
//                itemDaysLeft = binding.daysLeft.text.toString().toInt(),
//                itemLocation = getCheckedLocation()
//            )
//            navigateBackToList()
//        } else { hintAllFieldsRequired() }
//    }

    // Navigate back to the list fragment.
    private fun deleteAndNavigateBack(item: FoodItem) {
        deleteItem(item)
        val action = ItemDetailFragmentDirections
            .actionDetailFragmentToListFragment()
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
        viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
            bind(selectedItem)
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