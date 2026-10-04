package com.example.mod3case2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mod3case2.data.model.SourceItem
import com.example.mod3case2.databinding.ItemSourceBinding

class SourceAdapter(
    private var sources: List<SourceItem> = emptyList(),
    private val onSourceClick: ((SourceItem) -> Unit)? = null
) : RecyclerView.Adapter<SourceAdapter.SourceViewHolder>() {

    fun updateSources(newSources: List<SourceItem>) {
        this.sources = newSources
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        val binding = ItemSourceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SourceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        holder.bind(sources[position], position + 1)
    }

    override fun getItemCount(): Int = sources.size

    inner class SourceViewHolder(private val binding: ItemSourceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(source: SourceItem, index: Int) {
            binding.tvSourceIndex.text = String.format("%02d", index)
            binding.tvSourceFilename.text = source.document
            binding.tvSourceSimilarity.text = "Sim ${String.format("%.3f", source.score)}"
            binding.tvSourcePage.text = "Page ${source.page ?: 1} • Chunk #${source.chunkId ?: index.toString()}"
            binding.tvSourceSnippet.text = source.text

            binding.root.setOnClickListener {
                onSourceClick?.invoke(source)
            }
        }
    }
}
