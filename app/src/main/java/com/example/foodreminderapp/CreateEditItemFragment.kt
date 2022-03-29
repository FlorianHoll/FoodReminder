package com.example.foodreminderapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.foodreminderapp.databinding.FragmentCreateEditItemBinding

class CreateEditItemFragment : Fragment() {

    companion object {
        val CREATE_EDIT_DAYS_LEFT = "days"
        val CREATE_EDIT_NAME = "name"
        val CREATE_EDIT_LOCATION = "location"
    }

    private var _binding: FragmentCreateEditItemBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateEditItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preFillTextFields()

        binding.btnSaveFoodItem.setOnClickListener {
            createOrUpdateFoodItem()
        }

    }

    private fun createOrUpdateFoodItem() {
        val foodItemName = binding.createEditItemNameEditText.text.toString()
        val foodItemDays = binding.createEditDaysLeftEditText.text.toString()
        val foodItemLocation = binding.createEditLocationEditText.text.toString()

        // navigate back to list with new information
        val navigationAction = CreateEditItemFragmentDirections
            .actionCreateEditFragmentToListFragment(
                itemListName = foodItemName,
                itemListDaysLeft = foodItemDays,
                itemListLocation = foodItemLocation,
                itemListNewItem = ArgumentsAreEmpty(),
                itemListPosition = 0  //TODO: Case statement
            )
            findNavController().navigate(navigationAction)
        }

    private fun getFragmentArguments(): Array<String> {
        val foodName = arguments?.getString(CREATE_EDIT_NAME).toString()
        val foodDaysLeft = arguments?.getString(CREATE_EDIT_DAYS_LEFT).toString()
        val foodLocation = arguments?.getString(CREATE_EDIT_LOCATION).toString()
        return arrayOf(foodName, foodDaysLeft, foodLocation)
    }

    private fun ArgumentsAreEmpty(): Boolean {
        val arguments = getFragmentArguments()
        // if arguments are empty, the item is not edited; else it is.
        return arguments.all { it.isEmpty() }
    }

    private fun preFillTextFields() {

        val (foodName, foodDaysLeft, foodLocation) = getFragmentArguments()

        // if item is edited, pre-fill text fields
        if (!ArgumentsAreEmpty()) {
            binding.createEditItemNameEditText.setText(foodName)
            binding.createEditDaysLeftEditText.setText(foodDaysLeft)
            binding.createEditLocationEditText.setText(foodLocation)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}