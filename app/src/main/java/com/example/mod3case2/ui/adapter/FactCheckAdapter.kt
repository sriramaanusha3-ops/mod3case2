package com.example.mod3case2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mod3case2.R
import com.example.mod3case2.data.model.FactCheckItem
import com.example.mod3case2.databinding.ItemFactCheckBinding

class FactCheckAdapter(
    private var claims: List<FactCheckItem> = emptyList()
) : RecyclerView.Adapter<FactCheckAdapter.FactCheckViewHolder>() {

    fun updateClaims(newClaims: List<FactCheckItem>) {
        this.claims = newClaims
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactCheckViewHolder {
        val binding = ItemFactCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FactCheckViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FactCheckViewHolder, position: Int) {
        holder.bind(claims[position], position + 1)
    }

    override fun getItemCount(): Int = claims.size

    inner class FactCheckViewHolder(private val binding: ItemFactCheckBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(claim: FactCheckItem, index: Int) {
            binding.tvClaimText.text = "Claim ${String.format("%02d", index)}: ${claim.claimText}"

            // Plain LLM status
            if (claim.plainLLMSupported) {
                binding.tvPlainStatus.text = "✓ Supported"
                binding.tvPlainStatus.setBackgroundResource(R.drawable.bg_badge_purple)
                binding.tvPlainStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.purple_bright))
            } else {
                binding.tvPlainStatus.text = "✕ Unsupported"
                binding.tvPlainStatus.setBackgroundResource(R.drawable.bg_badge_coral)
                binding.tvPlainStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.red_coral))
            }

            // RAG status
            if (claim.ragSupported) {
                binding.tvRagStatus.text = "✓ Supported by source"
                binding.tvRagStatus.setBackgroundResource(R.drawable.bg_badge_emerald)
                binding.tvRagStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.green_emerald))
            } else {
                binding.tvRagStatus.text = "△ Partial"
                binding.tvRagStatus.setBackgroundResource(R.drawable.bg_badge_amber)
                binding.tvRagStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.yellow_amber))
            }
        }
    }
}
