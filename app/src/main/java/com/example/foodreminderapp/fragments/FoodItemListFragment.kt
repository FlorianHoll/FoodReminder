package com.example.foodreminderapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.foodreminderapp.FoodItemListAdapter
import com.example.foodreminderapp.FoodItemListApplication
import com.example.foodreminderapp.FoodItemListViewModel
import com.example.foodreminderapp.FoodItemViewModelFactory
import com.example.foodreminderapp.databinding.FragmentItemListBinding

/**
 * Main fragment displaying details for all items in the database.
 */
class FoodItemListFragment : Fragment() {
    private val viewModel: FoodItemListViewModel by activityViewModels {
        FoodItemViewModelFactory(
            (activity?.application as FoodItemListApplication).database.foodItemDao()
        )
    }

    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FoodItemListAdapter(requireActivity(), viewModel)

        binding.rvFoodItems.adapter = adapter

        // Attach an observer on the allItems list to
        // update the UI automatically when the data changes.
        viewModel.allItems.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }

        binding.buttonAddItem.setOnClickListener {
            val action = FoodItemListFragmentDirections
                .actionListFragmentToCreateEditFragment()
            this.findNavController().navigate(action)
        }
    }
}
