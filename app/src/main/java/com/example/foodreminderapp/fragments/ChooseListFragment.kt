package com.example.foodreminderapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.foodreminderapp.FoodItemListApplication
import com.example.foodreminderapp.FoodItemListViewModel
import com.example.foodreminderapp.FoodItemViewModelFactory
import com.example.foodreminderapp.databinding.FragmentChooseListBinding

/**
 * Fragment to add or update an item in the database.
 */
class ChooseListFragment : Fragment() {

    private val viewModel: FoodItemListViewModel by activityViewModels {
        FoodItemViewModelFactory(
            (activity?.application as FoodItemListApplication).database.foodItemDao()
        )
    }

    private var _binding: FragmentChooseListBinding? = null
    private val binding get() = _binding!!
    private lateinit var chosenList: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun navigateToList(chosenList: String) {
        val action = ChooseListFragmentDirections
            .actionChooseListToFoodItemList(chosenList)
        findNavController().navigate(action)
    }

    private fun navigateToAddItem() {
        val action = ChooseListFragmentDirections
            .actionChooseListToCreateEditFragment()
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            // Navigate to chosen list.
            btnAll.setOnClickListener { navigateToList("all") }
            btnFridge.setOnClickListener { navigateToList("Kühlschrank") }
            btnShelf.setOnClickListener { navigateToList("Regal") }
            btnFreezer.setOnClickListener { navigateToList("Tiefkühlschrank") }

            // Navigate to new item if the button is clicked.
            btnAddItem.setOnClickListener { navigateToAddItem() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
