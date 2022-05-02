package com.example.foodreminderapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.foodreminderapp.FoodReminderApplication
import com.example.foodreminderapp.R
import com.example.foodreminderapp.databinding.FragmentStatisticsItemDetailsBinding
import com.example.foodreminderapp.statistics.StatisticsListAdapter
import com.example.foodreminderapp.statistics.StatisticsViewModelFactory
import com.example.foodreminderapp.statistics.StatisticsViewModel
import com.example.foodreminderapp.statistics.data.StatisticsItemDisplay
import com.example.foodreminderapp.utils.calculateTargetDate

private const val TAG = "StatisticsDetailsFragment"

class StatisticsItemDetailsFragment : DialogFragment() {

    private val statisticsViewModel: StatisticsViewModel by activityViewModels {
        StatisticsViewModelFactory(
            (activity?.application as FoodReminderApplication)
                .statisticsDatabase.statisticsItemDao()
        )
    }

    private var _binding: FragmentStatisticsItemDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StatisticsListAdapter

    private val navigationArgs: StatisticsItemDetailsFragmentArgs by navArgs()

    // Get the time interval.
    private fun getInterval(): Int {
        val daysInterval = when (
            binding.timeInterval.checkedRadioButtonId
        ) {
            R.id.option_week -> 1
            R.id.option_month -> 7
            R.id.option_year -> 30
            else -> 10000
        }
        return daysInterval
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsItemDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initial binding.
        bindItem()

        // If time interval is changed, update values.
        bindOnIntervalChange()

    }

    private fun bindOnIntervalChange() {
        binding.timeInterval.setOnCheckedChangeListener { _, _ ->
            bindItem()
        }
    }

//    private fun bindRadioGroup(item = StatisticsItemDisplay) {
//        // Check the location.
//        val checkedLocationId = when (item.location) {
//            getString(R.string.chooseListShelf) -> R.id.option_regal
//            getString(R.string.chooseListFridge) -> R.id.option_kuehlschrank
//            else -> R.id.option_tiefkuehltruhe
//        }
//        location.check(checkedLocationId)
//
//    }

    private fun bindItem() {

        val interval = getInterval()

        val name = navigationArgs.itemName

        // Get all items for the chosen time interval and
        // compare it to the previous time interval of same length.
        statisticsViewModel.getItem(
            startDateLastPeriod = calculateTargetDate(days = -interval),
            startDateThisPeriod = calculateTargetDate(days = (-interval * 2)),
            itemName = name
        ).observe(viewLifecycleOwner) { item ->
            val timePeriodText = when (
                binding.timeInterval.checkedRadioButtonId
            ) {
                R.id.option_week -> "letzte Woche"
                R.id.option_month -> "letzten Monat"
                R.id.option_year -> "letztes Jahr"
                else -> ""
            }
            binding.tvItemName.text = name
            Log.d(TAG, "Item is the following: ${item}.")

            if (item != null) {
                val eatenHintText = getString(
                    R.string.statisticsDetailsEaten,
                    item.NrEatenLastPeriod
                )
                val thrownHintText = getString(
                    R.string.statisticsDetailsThrownAway,
                    item.NrThrownLastPeriod
                )

                binding.apply {

                    val eatenLastPeriod = when  {
                        timePeriodText != "" -> "(${item.NrEatenLastPeriod}x $timePeriodText.)"
                        else -> "insgesamt"
                    }
                    val thrownAwayLastPeriod = when {
                        timePeriodText != "" -> "(${item.NrThrownLastPeriod}x $timePeriodText.)"
                        else -> "insgesamt"
                    }
                    tvAmountEatenThisPeriod.text = "${item.NrEatenThisPeriod} x"
                    tvAmountThrownAwayThisPeriod.text = "${item.NrThrownThisPeriod} x"

                    tvAmountEatenLastPeriod.text = eatenLastPeriod
                    tvamountThrownAwayLastPeriod.text = thrownAwayLastPeriod

                    tvAmountEatenHint.text = eatenHintText
                    tvAmountThrownAwayHint.text = thrownHintText

                }
            } else {
                binding.apply {
                    tvAmountEatenThisPeriod.text = "0x"
                    tvAmountThrownAwayThisPeriod.text = "0x"
                    tvAmountEatenLastPeriod.text = "(0x $timePeriodText.)"
                    tvamountThrownAwayLastPeriod.text = "(0x $timePeriodText.)"
                }
            }
        }
    }


}