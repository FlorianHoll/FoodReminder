package com.example.foodreminderapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.foodreminderapp.all_items.DatabaseItemListAdapter
import com.example.foodreminderapp.all_items.DatabaseItemListViewModel
import com.example.foodreminderapp.all_items.DatabaseItemViewModelFactory
import com.example.foodreminderapp.FoodReminderApplication
import com.example.foodreminderapp.R
import com.example.foodreminderapp.current_items.FoodItemListViewModel
import com.example.foodreminderapp.current_items.FoodItemViewModelFactory
import com.example.foodreminderapp.databinding.FragmentChooseNewBinding

class ChooseNewFragment : Fragment(), SearchView.OnQueryTextListener {

    private val viewModel: DatabaseItemListViewModel by activityViewModels {
        DatabaseItemViewModelFactory(
            (activity?.application as FoodReminderApplication).itemDatabase.itemDatabaseDao()
        )
    }

    private val currentItemsViewModel: FoodItemListViewModel by activityViewModels {
        FoodItemViewModelFactory(
            (activity?.application as FoodReminderApplication).database.foodItemDao()
        )
    }

    private var _binding: FragmentChooseNewBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DatabaseItemListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun navigateToAddItem() {
        val action = ChooseNewFragmentDirections
            .actionChooseNewFragmentToCreateEditFragment(fromChoose = true)
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DatabaseItemListAdapter(
            requireActivity(),
            viewModel,
            currentItemsViewModel
        )
        binding.rvDatabaseItems.adapter = adapter
        viewModel.allItems.observe(this.viewLifecycleOwner) { items ->
            items.let { adapter.submitList(it) }
        }

        val searchView = binding.searchItems
        searchView.setOnQueryTextListener(this)

        // Navigate to completely new item if button is pressed
        binding.btnNewItem.setOnClickListener {
            navigateToAddItem()
        }

        // If take over selection button is clicked, check if selection is not empty.
        binding.btnAddSelected.setOnClickListener {
            val selectionIsEmpty = adapter.checkIfSelectionIsEmpty()
            if (selectionIsEmpty) {
                showHintSelectionIsEmpty()
            } else {
                addItemsAndNavigate()
            }
        }

    }

    private fun showHintSelectionIsEmpty() {
        Toast.makeText(
            context,
            "Es ist kein Item ausgew??hlt.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun addItemsAndNavigate() {
        adapter.addSelectedItems()
        val action = ChooseNewFragmentDirections
            .actionChooseNewToFoodItemList(getString(R.string.chooseListAllItems))
        viewModel.selectedItems.clear()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear viewModel UI data.
        viewModel.selectedItems.clear()
        _binding = null
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchDatabase(query)
        }
        return true
    }

    private fun searchDatabase(query: String) {
        val searchQuery = "%$query%"
        viewModel.searchDatabase(searchQuery).observe(
            this
        ) { list ->
            list.let { adapter.submitList(it) }
        }
    }

}
