package com.example.foodreminderapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.foodreminderapp.databinding.FragmentCreateEditItemBinding

private const val TAG = "CreateEditItemFragment"

class CreateEditItemFragment : Fragment() {

    companion object {
        val CREATE_EDIT_DAYS_LEFT = "days"
        val CREATE_EDIT_NAME = "name"
        val CREATE_EDIT_LOCATION = "location"
        val CREATE_EDIT_POSITION = "position"
        val CREATE_EDIT_IS_EDITED = "edited"
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
        // get information to carry over to the list
        val foodItemName = binding.createEditItemNameEditText.text.toString()
        val foodItemDays = binding.createEditDaysLeftEditText.text.toString()
        val foodItemLocation = when(
            binding.createEditLocation.checkedRadioButtonId
        ) {
            R.id.option_kuehlschrank -> "Kühlschrank"
            R.id.option_regal -> "Regal"
            else -> "Tiefkühlschrank"
        }

        // check if all fields are filled; if so, display toast to fill them, else, continue
        if (arrayOf(foodItemDays, foodItemName).any { it.isEmpty() }) {
            Toast.makeText(
                context, "Alle Felder müssen ausgefüllt werden.", Toast.LENGTH_SHORT
            ).show()
        } else {
            // catch error if days is not numeric
            try {
                foodItemDays.toInt()

                Log.d(
                    TAG, "Food Item Name: $foodItemName, " +
                            "Days: $foodItemDays, " +
                            "Location:$foodItemLocation"
                )

                // show toast
                val edited = arguments?.getBoolean(CREATE_EDIT_IS_EDITED)!!
                val toastText = if (edited) "Item aktualisiert" else "Item hinzugefügt"
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()

                // navigate back to list with new information
                val navigationAction = CreateEditItemFragmentDirections
                    .actionCreateEditFragmentToListFragment(
                        itemListName = foodItemName,
                        itemListDaysLeft = foodItemDays,
                        itemListLocation = foodItemLocation,
                        itemListEdited = edited,
                        itemListPosition = arguments?.getInt(CREATE_EDIT_POSITION)!!,
                        itemListNewItemAdded = !edited
                    )
                findNavController().navigate(navigationAction)

            } catch (exception: NumberFormatException) {
                Toast.makeText(
                    context,
                    "Haltbarkeit in Tagen muss eine Zahl sein.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getFragmentArguments(): Array<String> {
        val foodName = arguments?.getString(CREATE_EDIT_NAME).toString()
        val foodDaysLeft = arguments?.getString(CREATE_EDIT_DAYS_LEFT).toString()
        val foodLocation = arguments?.getString(CREATE_EDIT_LOCATION).toString()
        return arrayOf(foodName, foodDaysLeft, foodLocation)
    }

    private fun preFillTextFields() {
        val (foodName, foodDaysLeft, foodLocation) = getFragmentArguments()
        binding.createEditItemNameEditText.setText(foodName)
        binding.createEditDaysLeftEditText.setText(foodDaysLeft)
        val checkedItemId = when(foodLocation) {
            "Regal" -> R.id.option_regal
            "Kühlschrank" -> R.id.option_kuehlschrank
            else -> R.id.option_tiefkuehlschrank
        }
        binding.createEditLocation.check(checkedItemId)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}