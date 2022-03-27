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


class FoodItemListFragment : Fragment() {

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

        // Navigate to create/edit screen if plus is clicked
        val addButton = binding.buttonAddItem
        addButton.setOnClickListener {
            val navigationAction = FoodItemListFragmentDirections
                .actionListFragmentToCreateEditFragment("", "", "")
            findNavController().navigate(navigationAction)
        }

        // foodItemRecyclerView.setHasFixedSize(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//
//
//    val addButton = findViewById<FloatingActionButton>(R.id.buttonAddItem)
//    addButton.setOnClickListener {
//        val newFoodItem = FoodItem("Success", 2, "shelf")
//        foodItemAdapter.addFoodItem(newFoodItem)
//    }
}
