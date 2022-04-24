package com.example.foodreminderapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.foodreminderapp.FoodReminderApplication
import com.example.foodreminderapp.R
import com.example.foodreminderapp.databinding.FragmentStatisticsAllItemsBinding
import com.example.foodreminderapp.statistics.StatisticsListAdapter
import com.example.foodreminderapp.statistics.StatisticsViewModelFactory
import com.example.foodreminderapp.statistics.StatisticsViewModel
import com.example.foodreminderapp.utils.calculateTargetDate

class StatisticsAllItemsFragment : Fragment(), SearchView.OnQueryTextListener {

    private val statisticsViewModel: StatisticsViewModel by activityViewModels {
        StatisticsViewModelFactory(
            (activity?.application as FoodReminderApplication)
                .statisticsDatabase.statisticsItemDao()
        )
    }

    private var _binding: FragmentStatisticsAllItemsBinding? = null
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
        _binding = FragmentStatisticsAllItemsBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initial binding.
        bindRecyclerView()

        // If time interval is changed, update values.
        bindOnIntervalChange()

        val searchView = binding.searchItems
        searchView.setOnQueryTextListener(this)

    }

    private fun bindOnIntervalChange() {
        binding.timeInterval.setOnCheckedChangeListener { _, _ ->
            bindRecyclerView()
        }
    }

    private fun bindRecyclerView() {
        val interval = getInterval()

        // Prepare recycler view.
        adapter = StatisticsListAdapter(requireContext(), statisticsViewModel)
        binding.rvStatisticsItems.adapter = adapter

        // Get all items for the chosen time interval and
        // compare it to the previous time interval of same length.
        statisticsViewModel.getAllItemsForPeriod(
            startDateThisPeriod = calculateTargetDate(days = -interval),
            startDateLastPeriod = calculateTargetDate(days = (-interval * 2)),
            limit = 10000,
            percentageLimit = 0
        ).observe(viewLifecycleOwner) { items ->
            items.let { adapter.submitList(it) }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchDatabase(query)
        }
        return true
    }

    private fun searchDatabase(query: String) {
        val interval = getInterval()
        statisticsViewModel.getAllItemsForPeriod(
            startDateThisPeriod = calculateTargetDate(-interval),
            startDateLastPeriod = calculateTargetDate((-interval * 2)),
            limit = 10000,
            percentageLimit = 0,
            searchQuery = query
        ).observe(
            this
        ) { list ->
            list.let { adapter.submitList(it) }
        }
    }
}