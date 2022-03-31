package com.example.foodreminderapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.foodreminderapp.adapter.FoodItemAdapter
import com.example.foodreminderapp.data.DataSource
import com.example.foodreminderapp.databinding.FragmentItemListBinding
import androidx.navigation.fragment.findNavController
import com.example.foodreminderapp.model.FoodItem


class FoodItemListFragment : Fragment() {

    companion object {
        val ITEM_LIST_NAME = "itemListName"
        val ITEM_LIST_DAYS_LEFT = "itemListDaysLeft"
        val ITEM_LIST_LOCATION = "itemListLocation"
        val ITEM_LIST_EDITED = "itemListEdited"
        val ITEM_LIST_POSITION = "itemListPosition"
        val ITEM_LIST_NEW_ITEM = "itemListNewItemAdded"
    }

    private var _binding: FragmentItemListBinding? = null

    private val binding get() = _binding!!

    private lateinit var foodItemRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Display the list of items
        val myDataset = DataSource().loadItems()
        foodItemRecyclerView = binding.rvFoodItems
        val foodItemAdapter = FoodItemAdapter(requireActivity(), myDataset)
        foodItemRecyclerView.adapter = foodItemAdapter

        // Update item if it was edited
        val edited = arguments?.getBoolean(ITEM_LIST_EDITED)!!
        val (foodName, foodDaysLeft, foodLocation) = getFragmentArguments()
        if (edited) {
            foodItemAdapter.updateFoodItem(
                position = arguments?.getInt(ITEM_LIST_POSITION)!!,
                foodName = foodName,
                foodDays = foodDaysLeft,
                foodLocation = foodLocation
            )
        }

        // Add new item if new item was added
        val newItemAdded = arguments?.getBoolean(ITEM_LIST_NEW_ITEM)!!
        if (newItemAdded) {
            val newFoodItem = FoodItem(foodName, foodDaysLeft.toInt(), foodLocation)
            foodItemAdapter.addFoodItem(newFoodItem)
        }

        // Navigate to create/edit screen if plus is clicked
        val addButton = binding.buttonAddItem
        addButton.setOnClickListener {
            val navigationAction = FoodItemListFragmentDirections
                .actionListFragmentToCreateEditFragment()
            findNavController().navigate(navigationAction)
        }

        // foodItemRecyclerView.setHasFixedSize(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getFragmentArguments(): Array<String> {
        val foodName = arguments?.getString(ITEM_LIST_NAME).toString()
        val foodDaysLeft = arguments?.getString(ITEM_LIST_DAYS_LEFT).toString()
        val foodLocation = arguments?.getString(ITEM_LIST_LOCATION).toString()
        return arrayOf(foodName, foodDaysLeft, foodLocation)
    }

//
//
//    val addButton = findViewById<FloatingActionButton>(R.id.buttonAddItem)
//    addButton.setOnClickListener {
//        val newFoodItem = FoodItem("Success", 2, "shelf")
//        foodItemAdapter.addFoodItem(newFoodItem)
//    }
}
