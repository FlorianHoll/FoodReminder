package com.example.foodreminderapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.foodreminderapp.FoodReminderApplication
import com.example.foodreminderapp.R
import com.example.foodreminderapp.databinding.FragmentStatisticsOverviewBinding
import com.example.foodreminderapp.statistics.StatisticsListAdapter
import com.example.foodreminderapp.statistics.StatisticsViewModelFactory
import com.example.foodreminderapp.statistics.StatisticsViewModel
import com.example.foodreminderapp.utils.calculateTargetDate

class StatisticsOverviewFragment : Fragment() {

    private val statisticsViewModel: StatisticsViewModel by activityViewModels {
        StatisticsViewModelFactory(
            (activity?.application as FoodReminderApplication)
                .statisticsDatabase.statisticsItemDao()
        )
    }

    private var _binding: FragmentStatisticsOverviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StatisticsListAdapter

    // Get the time interval.
    private fun getInterval(): Int {
        val daysInterval = when (
            binding.timeInterval.checkedRadioButtonId
        ) {
            R.id.option_week -> 1
            R.id.option_month -> 7
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
        // Initial binding.
        bindValuesToTextViews()
        bindRecyclerView()

        // If time interval is changed, update values.
        bindOnIntervalChange()
    }

    private fun bindOnIntervalChange() {
        binding.timeInterval.setOnCheckedChangeListener { _, _ ->
            bindValuesToTextViews()
            bindRecyclerView()
        }
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

    private fun bindRecyclerView() {
        val interval = getInterval()

        // Prepare recycler view.
        adapter = StatisticsListAdapter(
            requireActivity(),
            statisticsViewModel
        )
        binding.rvDatabaseItems.adapter = adapter

        // Get all items for the chosen time interval and
        // compare it to the previous time interval of same length.
        statisticsViewModel.getAllItemsForPeriod(
            startDateLastPeriod = calculateTargetDate(days = -interval),
            startDateThisPeriod = calculateTargetDate(days = (-interval*2))
        ).observe(viewLifecycleOwner) {items ->
            items.let { adapter.submitList(it) }
        }
    }


}