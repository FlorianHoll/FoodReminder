package com.example.foodreminderapp.statistics

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodreminderapp.databinding.StatisticsItemBinding
import com.example.foodreminderapp.statistics.data.StatisticsItemDisplay


private const val TAG = "StatisticsListAdapter"

class StatisticsListAdapter(
    private val context: Context,
    private val statisticsViewModel: StatisticsViewModel
) : ListAdapter<StatisticsItemDisplay, StatisticsListAdapter.ItemViewHolder>(DiffCallback) {

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
        val current = getItem(position)
        holder.bind(current)
    }

    class ItemViewHolder(
        private var binding: StatisticsItemBinding,
        private val context: Context,
        private val statisticsViewModel: StatisticsViewModel
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StatisticsItemDisplay) {

            binding.apply {
                val percentageChange = (
                        item.percentageThrownThisPeriod.toFloat() /
                                item.percentageThrownLastPeriod.toFloat()
                        )
                val rotationForArrow = (
                        (percentageChange - 100.0 ) * .7
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
        private val DiffCallback = object : DiffUtil.ItemCallback<StatisticsItemDisplay>() {
            override fun areItemsTheSame(
                oldItem: StatisticsItemDisplay,
                newItem: StatisticsItemDisplay
            ): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: StatisticsItemDisplay,
                newItem: StatisticsItemDisplay
            ): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }
}