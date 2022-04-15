package com.example.foodreminderapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.foodreminderapp.all_items.DatabaseItemListAdapter
import com.example.foodreminderapp.all_items.DatabaseItemListViewModel
import com.example.foodreminderapp.all_items.DatabaseItemViewModelFactory
import com.example.foodreminderapp.FoodReminderApplication
import com.example.foodreminderapp.databinding.FragmentNewItemChooseBinding


class ChooseNewFragment : Fragment() {

    private val viewModel: DatabaseItemListViewModel by activityViewModels {
        DatabaseItemViewModelFactory(
            (activity?.application as FoodReminderApplication).itemDatabase.itemDatabaseDao()
        )
    }

    private var _binding: FragmentNewItemChooseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewItemChooseBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun navigateToAddItem() {
        val action = ChooseListFragmentDirections
            .actionChooseListToCreateEditFragment()
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = DatabaseItemListAdapter(requireActivity(), viewModel)
        binding.rvDatabaseItems.adapter = adapter

        viewModel.allItems.observe(this.viewLifecycleOwner) { items ->
            items.let { adapter.submitList(it) }
        }

        binding.btnNewItem.setOnClickListener {
            val action = ChooseNewFragmentDirections
                .actionChooseNewToCreateEditFragment()
            this.findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
