package com.example.foodreminderapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.foodreminderapp.FoodReminderApplication
import com.example.foodreminderapp.current_items.FoodItemListViewModel
import com.example.foodreminderapp.current_items.FoodItemViewModelFactory
import com.example.foodreminderapp.R
import com.example.foodreminderapp.databinding.FragmentChooseListBinding

/**
 * Fragment to add or update an item in the database.
 */
class ChooseListFragment : Fragment() {

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
            .actionChooseListToChooseNewFragment()
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            // Navigate to chosen list.
            btnAll.setOnClickListener { navigateToList(getString(R.string.chooseListAllItems)) }
            btnFridge.setOnClickListener { navigateToList(getString(R.string.chooseListFridge)) }
            btnShelf.setOnClickListener { navigateToList(getString(R.string.chooseListShelf)) }
            btnFreezer.setOnClickListener { navigateToList(getString(R.string.chooseListFreezer)) }

            // Navigate to new item if the button is clicked.
            btnAddItem.setOnClickListener { navigateToAddItem() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
