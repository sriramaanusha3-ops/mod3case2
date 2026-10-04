package com.example.mod3case2.ui.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mod3case2.R
import com.example.mod3case2.data.model.ExperimentHistoryItem
import com.example.mod3case2.databinding.ItemHistoryBinding

class HistoryAdapter(
    private var historyList: List<ExperimentHistoryItem> = emptyList(),
    private val onItemClick: (ExperimentHistoryItem) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    fun updateHistory(newList: List<ExperimentHistoryItem>) {
        this.historyList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount(): Int = historyList.size

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ExperimentHistoryItem) {
            binding.tvHistoryQuestion.text = item.question
            binding.tvHistoryAccuracy.text = "RAG: ${item.ragAccuracyPct}% • Plain: ${item.plainAccuracyPct}%"
            binding.tvHistorySources.text = "${item.sourcesCount} Sources"

            // Format Winner Badge
            when (item.winner.uppercase()) {
                "RAG" -> {
                    binding.tvHistoryWinner.text = "RAG WON"
                    binding.tvHistoryWinner.setBackgroundResource(R.drawable.bg_badge_cyan)
                    binding.tvHistoryWinner.setTextColor(ContextCompat.getColor(itemView.context, R.color.cyan_accent))
                }
                "PLAIN_LLM" -> {
                    binding.tvHistoryWinner.text = "PLAIN LLM WON"
                    binding.tvHistoryWinner.setBackgroundResource(R.drawable.bg_badge_purple)
                    binding.tvHistoryWinner.setTextColor(ContextCompat.getColor(itemView.context, R.color.purple_bright))
                }
                else -> {
                    binding.tvHistoryWinner.text = "TIE / CLOSE"
                    binding.tvHistoryWinner.setBackgroundResource(R.drawable.bg_badge_amber)
                    binding.tvHistoryWinner.setTextColor(ContextCompat.getColor(itemView.context, R.color.yellow_amber))
                }
            }

            // Time calculation
            val timeMillis = if (item.timestamp > 100000000000L) item.timestamp else item.timestamp * 1000L
            val relativeTime = DateUtils.getRelativeTimeSpanString(
                timeMillis,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
            )
            binding.tvHistoryTime.text = relativeTime

            binding.cardHistoryItem.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
