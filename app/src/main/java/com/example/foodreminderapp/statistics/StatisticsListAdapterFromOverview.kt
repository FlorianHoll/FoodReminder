package com.example.foodreminderapp.statistics

import android.content.Context
import androidx.navigation.findNavController
import com.example.foodreminderapp.fragments.StatisticsOverviewFragmentDirections

class StatisticsListAdapterFromOverview(
    private val context: Context,
    private val statisticsViewModel: StatisticsViewModel,
    private val interval: Int
) : StatisticsListAdapter(
    context, statisticsViewModel, interval
) {

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            val action = StatisticsOverviewFragmentDirections
                .actionStatisticsFragmentToStatisticsItemDetailsFragment(
                    item.name,
                    interval
                )
            it.findNavController().navigate(action)
        }
    }
}