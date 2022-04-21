package com.example.foodreminderapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.foodreminderapp.FoodReminderApplication
import com.example.foodreminderapp.R
import com.example.foodreminderapp.current_items.FoodItemListViewModel
import com.example.foodreminderapp.current_items.FoodItemViewModelFactory
import com.example.foodreminderapp.databinding.FragmentStatisticsOverviewBinding
import com.example.foodreminderapp.statistics.StatisticsViewModelFactory
import com.example.foodreminderapp.statistics.StatisticsViewModel
import java.time.LocalDate

class StatisticsOverviewFragment : Fragment() {

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

    private val endTime: String = LocalDate.now().toString()
    private var _binding: FragmentStatisticsOverviewBinding? = null
    private val binding get() = _binding!!

    // Get the beginning (for the SQL query).
    private fun getInterval(): Int {
        val daysInterval = when (
            binding.timeInterval.checkedRadioButtonId
        ) {
            R.id.option_week -> 7
            R.id.option_month -> 30
            R.id.option_year -> 365
            else -> 10000
        }
        return daysInterval
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindValuesToTextViews()
    }

    private fun bindValuesToTextViews() {
        // Retrieve values from the view model (launched as a coroutine).
        val interval = getInterval()
        statisticsViewModel.getAmountEatenAndThrownAway(interval)
        statisticsViewModel.getPercentageThrownAway(interval)

        // Bind to the text views and observe for data changes.
        statisticsViewModel.nrThrownAway.observe(viewLifecycleOwner) { amount ->
            binding.tvThrownAway.text = amount.toString()
        }
        statisticsViewModel.nrEaten.observe(viewLifecycleOwner) { amount ->
            binding.tvEaten.text = amount.toString()
        }
        statisticsViewModel.percentageThrownAway.observe(viewLifecycleOwner) { amount ->
            val percentageText = "$amount%"
            binding.tvPercentageEaten.text = percentageText
        }

    }
}