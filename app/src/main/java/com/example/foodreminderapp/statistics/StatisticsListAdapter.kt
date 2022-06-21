package com.example.foodreminderapp.statistics

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodreminderapp.databinding.StatisticsItemBinding
import com.example.foodreminderapp.statistics.data.DisplayableStatisticsItem


private const val TAG = "StatisticsListAdapter"

open class StatisticsListAdapter(
    private val context: Context,
    private val statisticsViewModel: StatisticsViewModel,
    private val interval: Int
) : ListAdapter<DisplayableStatisticsItem, StatisticsListAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            StatisticsItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            context,
            statisticsViewModel
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ItemViewHolder(
        private var binding: StatisticsItemBinding,
        private val context: Context,
        private val statisticsViewModel: StatisticsViewModel
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DisplayableStatisticsItem) {

            binding.apply {
                val percentageChange = (
                        item.percentageThrownThisPeriod.toFloat() /
                                item.percentageThrownLastPeriod.toFloat()
                        )
                val rotationForArrow = (
                        -(percentageChange - 100.0 ) * .45
                        )
                itemTitle.text = item.name
                val percentageText =
                    "${item.percentageThrownThisPeriod}%"
                tvPercentage.text = percentageText
                if (rotationForArrow.isFinite()) {
                    ivArrow.rotation = rotationForArrow.toFloat()
                }
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DisplayableStatisticsItem>() {
            override fun areItemsTheSame(
                oldItem: DisplayableStatisticsItem,
                newItem: DisplayableStatisticsItem
            ): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: DisplayableStatisticsItem,
                newItem: DisplayableStatisticsItem
            ): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }
}
