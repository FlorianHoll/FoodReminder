package com.example.foodreminderapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.foodreminderapp.FoodReminderApplication
import com.example.foodreminderapp.R
import com.example.foodreminderapp.databinding.FragmentStatisticsOverviewBinding
import com.example.foodreminderapp.statistics.StatisticsListAdapter
import com.example.foodreminderapp.statistics.StatisticsListAdapterFromOverview
import com.example.foodreminderapp.statistics.StatisticsViewModelFactory
import com.example.foodreminderapp.statistics.StatisticsViewModel
import com.example.foodreminderapp.utils.calculateTargetDate
import kotlin.math.sign

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
        // Initial binding.
        bindValuesToTextViews()
        bindRecyclerView()

        // If time interval is changed, update values.
        bindOnIntervalChange()

        binding.allItemsCard.setOnClickListener {
            val navigationAction = StatisticsOverviewFragmentDirections
                .actionStatisticsFragmentToStatisticsAllItemsFragment()
            findNavController().navigate(navigationAction)
        }
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
        statisticsViewModel.getCurrentPercentageThrownAway(interval)
        statisticsViewModel.getThrownAwayChange(interval)

        // Bind to the text views and observe for data changes.
        statisticsViewModel.nrThrownAway.observe(viewLifecycleOwner) { amount ->
            binding.tvThrownAway.text = when (amount) {
                null -> "0"
                else -> amount.toString()
            }
        }

        statisticsViewModel.nrEaten.observe(viewLifecycleOwner) { amount ->
            binding.tvEaten.text = when (amount) {
                null -> "0"
                else -> amount.toString()
            }
        }

        statisticsViewModel.percentageThrownAway.observe(viewLifecycleOwner) { amount ->
            val percentageText = when (amount) {
                null -> "0%"
                else -> "$amount%"
            }
            binding.tvPercentageEaten.text = percentageText
        }

        statisticsViewModel.changeInThrownAwayPercentage.observe(
            viewLifecycleOwner
        ) { changeInPercentage ->
            binding.ivArrow.rotation = (-changeInPercentage * .45).toFloat()
            val color = when {
                changeInPercentage < 0 -> Color.GREEN
                changeInPercentage > 0 -> Color.RED
                else -> Color.BLACK
            }
            binding.ivArrow.setColorFilter(color)
        }

    }

    private fun bindRecyclerView() {
        val interval = getInterval()

        // Prepare recycler view.
        adapter = StatisticsListAdapterFromOverview(
            requireActivity(), statisticsViewModel, interval
        )
        binding.rvDatabaseItems.adapter = adapter

        // Get all items for the chosen time interval and
        // compare it to the previous time interval of same length.
        statisticsViewModel.getAllItemsForPeriod(
            startDateThisPeriod = calculateTargetDate(days = -interval),
            startDateLastPeriod = calculateTargetDate(days = (-interval * 2))
        ).observe(viewLifecycleOwner) { items ->
            items.let { adapter.submitList(it) }
        }
    }


}