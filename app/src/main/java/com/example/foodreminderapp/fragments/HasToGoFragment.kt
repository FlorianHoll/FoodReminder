package com.example.foodreminderapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.foodreminderapp.*
import com.example.foodreminderapp.databinding.FragmentHasToGoBinding

/**
 * Fragment displaying those items that need to go.
 */
class HasToGoFragment : Fragment() {
    private val viewModel: FoodItemListViewModel by activityViewModels {
        FoodItemViewModelFactory(
            (activity?.application as FoodItemListApplication).database.foodItemDao()
        )
    }

    private var _binding: FragmentHasToGoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHasToGoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fridgeAdapter = HasToGoListAdapter(requireActivity(), viewModel)
        val shelfAdapter = HasToGoListAdapter(requireActivity(), viewModel)
        val freezerAdapter = HasToGoListAdapter(requireActivity(), viewModel)

        binding.rvItemsHaveToGo.adapter = fridgeAdapter

        // Attach an observer on the allItems list to
        // update the UI automatically when the data changes.
        viewModel.allHasToGoItems.observe(this.viewLifecycleOwner) { items ->
            items.let { fridgeAdapter.submitList(it) }
        }
//
//        viewModel.hasToGoByLocation("Kühlschrank")
//            .observe(this.viewLifecycleOwner) { items ->
//                items.let {
//                    fridgeAdapter.submitList(it)
//                }
//            }

    }
}
