package com.example.mod3case2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mod3case2.data.model.BenchmarkQuestion
import com.example.mod3case2.databinding.ItemBenchmarkQuestionBinding

class BenchmarkAdapter(
    private var questions: List<BenchmarkQuestion> = emptyList(),
    private val onItemClick: (BenchmarkQuestion) -> Unit
) : RecyclerView.Adapter<BenchmarkAdapter.BenchmarkViewHolder>() {

    fun updateQuestions(newList: List<BenchmarkQuestion>) {
        this.questions = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BenchmarkViewHolder {
        val binding = ItemBenchmarkQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BenchmarkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BenchmarkViewHolder, position: Int) {
        holder.bind(questions[position])
    }

    override fun getItemCount(): Int = questions.size

    inner class BenchmarkViewHolder(private val binding: ItemBenchmarkQuestionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BenchmarkQuestion) {
            binding.tvBenchQuestionId.text = item.id.uppercase()
            binding.tvBenchCategory.text = item.category ?: "General"
            binding.tvBenchDifficulty.text = item.difficulty ?: "Benchmark"
            binding.tvBenchQuestionText.text = item.question
            binding.tvBenchDocs.text = "Linked Docs: ${item.relevantDocuments.joinToString(", ")}"

            binding.cardBenchmarkQuestion.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
