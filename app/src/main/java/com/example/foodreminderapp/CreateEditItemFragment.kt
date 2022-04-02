package com.example.foodreminderapp

//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.navigation.fragment.findNavController
//import com.example.foodreminderapp.databinding.FragmentCreateEditItemBinding
//
//private const val TAG = "CreateEditItemFragment"
//
//class CreateEditItemFragment : Fragment() {
//
//    companion object {
//        val CREATE_EDIT_DAYS_LEFT = "days"
//        val CREATE_EDIT_NAME = "name"
//        val CREATE_EDIT_LOCATION = "location"
//        val CREATE_EDIT_POSITION = "position"
//        val CREATE_EDIT_IS_EDITED = "edited"
//    }
//
//    private var _binding: FragmentCreateEditItemBinding? = null
//
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentCreateEditItemBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        preFillTextFields()
//
//        binding.btnSaveFoodItem.setOnClickListener {
//            createOrUpdateFoodItem()
//        }
//
//    }
//
//    private fun createOrUpdateFoodItem() {
//        // get information to carry over to the list
//        val foodItemName = binding.createEditItemNameEditText.text.toString()
//        val foodItemDays = binding.createEditDaysLeftEditText.text.toString()
//        val foodItemLocation = when(
//            binding.createEditLocation.checkedRadioButtonId
//        ) {
//            R.id.option_kuehlschrank -> "Kühlschrank"
//            R.id.option_regal -> "Regal"
//            else -> "Tiefkühlschrank"
//        }
//
//        // check if all fields are filled; if so, display toast to fill them, else, continue
//        if (arrayOf(foodItemDays, foodItemName).any { it.isEmpty() }) {
//            Toast.makeText(
//                context, "Alle Felder müssen ausgefüllt werden.", Toast.LENGTH_SHORT
//            ).show()
//        } else {
//            // catch error if days is not numeric
//            try {
//                foodItemDays.toInt()
//
//                Log.d(
//                    TAG, "Food Item Name: $foodItemName, " +
//                            "Days: $foodItemDays, " +
//                            "Location:$foodItemLocation"
//                )
//
//                // show toast
//                val edited = arguments?.getBoolean(CREATE_EDIT_IS_EDITED)!!
//                val toastText = if (edited) "Item aktualisiert" else "Item hinzugefügt"
//                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
//
//                // navigate back to list with new information
//                val navigationAction = CreateEditItemFragmentDirections
//                    .actionCreateEditFragmentToListFragment(
//                        itemListName = foodItemName,
//                        itemListDaysLeft = foodItemDays,
//                        itemListLocation = foodItemLocation,
//                        itemListEdited = edited,
//                        itemListPosition = arguments?.getInt(CREATE_EDIT_POSITION)!!,
//                        itemListNewItemAdded = !edited
//                    )
//                findNavController().navigate(navigationAction)
//
//            } catch (exception: NumberFormatException) {
//                Toast.makeText(
//                    context,
//                    "Haltbarkeit in Tagen muss eine Zahl sein.",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//    }
//
//    private fun getFragmentArguments(): Array<String> {
//        val foodName = arguments?.getString(CREATE_EDIT_NAME).toString()
//        val foodDaysLeft = arguments?.getString(CREATE_EDIT_DAYS_LEFT).toString()
//        val foodLocation = arguments?.getString(CREATE_EDIT_LOCATION).toString()
//        return arrayOf(foodName, foodDaysLeft, foodLocation)
//    }
//
//    private fun preFillTextFields() {
//        val (foodName, foodDaysLeft, foodLocation) = getFragmentArguments()
//        binding.createEditItemNameEditText.setText(foodName)
//        binding.createEditDaysLeftEditText.setText(foodDaysLeft)
//        val checkedItemId = when(foodLocation) {
//            "Regal" -> R.id.option_regal
//            "Kühlschrank" -> R.id.option_kuehlschrank
//            else -> R.id.option_tiefkuehlschrank
//        }
//        binding.createEditLocation.check(checkedItemId)
//
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}


import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.foodreminderapp.data.FoodItem
import com.example.foodreminderapp.databinding.FragmentCreateEditItemBinding

/**
 * Fragment to add or update an item in the Inventory database.
 */
class CreateEditItemFragment : Fragment() {

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    // to share the ViewModel across fragments.
    private val viewModel: FoodItemListViewModel by activityViewModels {
        FoodItemViewModelFactory(
            (activity?.application as FoodItemListApplication).database.foodItemDao()
        )
    }
    private val navigationArgs: CreateEditItemFragmentArgs by navArgs()

    lateinit var item: FoodItem

    // Binding object instance corresponding to the fragment_add_item.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment
    private var _binding: FragmentCreateEditItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateEditItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.itemName.text.toString(),
            binding.daysLeft.text.toString(),
        )
    }

    /**
     * Binds views with the passed in [item] information.
     */
    private fun bind(item: FoodItem) {
//        val price = "%.2f".format(item.itemPrice)
        binding.apply {
            itemName.setText(item.itemName, TextView.BufferType.SPANNABLE)
            daysLeft.setText(item.daysLeft.toString(), TextView.BufferType.SPANNABLE)
            val checkedItemId = when (item.location) {
                "Regal" -> R.id.option_regal
                "Kühlschrank" -> R.id.option_kuehlschrank
                else -> R.id.option_tiefkuehlschrank
            }
            binding.location.check(checkedItemId)
            // Update item when save button is clicked.
            btnSaveFoodItem.setOnClickListener { updateItem() }
        }
    }

    private fun getCheckedLocation(): String {
        val foodItemLocation = when (
            binding.location.checkedRadioButtonId
        ) {
            R.id.option_kuehlschrank -> "Kühlschrank"
            R.id.option_regal -> "Regal"
            else -> "Tiefkühlschrank"
        }
        return foodItemLocation
    }

    /**
     * Inserts the new Item into database and navigates up to list fragment.
     */
    private fun addNewItem() {
        if (isEntryValid()) {
            viewModel.addNewItem(
                itemName = binding.itemName.text.toString(),
                itemDaysLeft = binding.daysLeft.text.toString().toInt(),
                itemLocation = getCheckedLocation()
            )
            navigateBackToList()
        } else { hintAllFieldsRequired() }
    }

    /**
     * Updates an existing Item in the database and navigates up to list fragment.
     */
    private fun updateItem() {
        if (isEntryValid()) {
            viewModel.updateItem(
                itemId = this.navigationArgs.itemId,
                itemName = this.binding.itemName.text.toString(),
                itemDaysLeft = binding.daysLeft.text.toString().toInt(),
                itemLocation = getCheckedLocation()
            )
            navigateBackToList()
        } else { hintAllFieldsRequired() }
    }

    private fun navigateBackToList() {
        val action = CreateEditItemFragmentDirections
            .actionCreateEditFragmentToListFragment()
        findNavController().navigate(action)
    }

    private fun hintAllFieldsRequired() {
        Toast.makeText(
            context, "Alle Felder müssen ausgefüllt werden.", Toast.LENGTH_LONG
        ).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.itemId
        // The default id that is passed is -1;
        // therefore, if an id is passed, it is not a new ite.
        if (id > 0) {
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
                item = selectedItem
                bind(item)
            }
        } else {
            binding.btnSaveFoodItem.setOnClickListener {
                addNewItem()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}
