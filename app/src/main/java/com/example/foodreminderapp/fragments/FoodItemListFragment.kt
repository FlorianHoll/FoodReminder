package com.example.foodreminderapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.foodreminderapp.*
import com.example.foodreminderapp.databinding.FragmentItemListBinding
import com.example.foodreminderapp.current_items.FoodItemListAdapter
import com.example.foodreminderapp.FoodReminderApplication
import com.example.foodreminderapp.current_items.FoodItemListViewModel
import com.example.foodreminderapp.current_items.FoodItemViewModelFactory
import com.example.foodreminderapp.statistics.StatisticsViewModel
import com.example.foodreminderapp.statistics.StatisticsViewModelFactory

private const val TAG = "FoodItemListFragment"

/**
 * Main fragment displaying details for all items in the database.
 */
class FoodItemListFragment : Fragment() {

    private val viewModel: FoodItemListViewModel by activityViewModels {
        FoodItemViewModelFactory(
            (activity?.application as FoodReminderApplication)
                .database.foodItemDao()
        )
    }

    private val statisticsViewModel: StatisticsViewModel by activityViewModels {
        StatisticsViewModelFactory(
            (activity?.application as FoodReminderApplication)
                .statisticsDatabase.statisticsItemDao()
        )
    }

    private val navigationArgs: FoodItemListFragmentArgs by navArgs()

    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)

        // Set action bar title
        val location = navigationArgs.itemsLocation
        (activity as AppCompatActivity).supportActionBar?.title = location
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val location = navigationArgs.itemsLocation
        val adapter = FoodItemListAdapter(
            requireActivity(), viewModel, statisticsViewModel
        )

        binding.rvFoodItems.adapter = adapter

        // Attach an observer on the allItems list to
        // update the UI automatically when the data changes.
        if (location == getString(R.string.chooseListAllItems)) {
            viewModel.allItems.observe(this.viewLifecycleOwner) { items ->
                items.let { adapter.submitList(it) }
            }
        } else {
            viewModel.retrieveItemsByLocation(location)
                .observe(this.viewLifecycleOwner) { items ->
                    items.let { adapter.submitList(it) }
                }
        }

        binding.buttonAddItem.setOnClickListener {
            val action = FoodItemListFragmentDirections
                .actionFoodItemListFragmentToChooseNewFragment()
            this.findNavController().navigate(action)
        }
    }
}