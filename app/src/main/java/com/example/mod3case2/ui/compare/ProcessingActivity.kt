package com.example.mod3case2.ui.compare

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mod3case2.R
import com.example.mod3case2.databinding.ActivityProcessingBinding
import com.example.mod3case2.ui.results.ResultsActivity
import com.example.mod3case2.ui.viewmodel.ComparisonUiState
import com.example.mod3case2.ui.viewmodel.ComparisonViewModel

class ProcessingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProcessingBinding
    private val viewModel: ComparisonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProcessingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val question = intent.getStringExtra("extra_question") ?: "What is supervised learning?"
        val topK = intent.getIntExtra("extra_top_k", 4)
        binding.tvProcessingQuestion.text = "\"$question\""

        viewModel.setTopK(topK)
        setupObservers()
        viewModel.runComparison(question)
    }

    private fun setupObservers() {
        val stepRows = listOf(
            Pair(binding.tvStep1Text, binding.tvStep1Icon),
            Pair(binding.tvStep2Text, binding.tvStep2Icon),
            Pair(binding.tvStep3Text, binding.tvStep3Icon),
            Pair(binding.tvStep4Text, binding.tvStep4Icon),
            Pair(binding.tvStep5Text, binding.tvStep5Icon),
            Pair(binding.tvStep6Text, binding.tvStep6Icon)
        )

        viewModel.uiState.observe(this) { state ->
            when (state) {
                is ComparisonUiState.Processing -> {
                    val currentStep = state.stepIndex
                    for (i in 0 until stepRows.size) {
                        val (textView, iconView) = stepRows[i]
                        val stepNum = i + 1
                        if (stepNum < currentStep) {
                            // Completed step
                            iconView.text = "✓"
                            iconView.setTextColor(ContextCompat.getColor(this, R.color.green_emerald))
                            textView.setTextColor(ContextCompat.getColor(this, R.color.text_white))
                        } else if (stepNum == currentStep) {
                            // Active step
                            iconView.text = "●"
                            iconView.setTextColor(ContextCompat.getColor(this, R.color.cyan_accent))
                            textView.setTextColor(ContextCompat.getColor(this, R.color.cyan_accent))
                        } else {
                            // Future step
                            iconView.text = "○"
                            iconView.setTextColor(ContextCompat.getColor(this, R.color.text_muted))
                            textView.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
                        }
                    }
                }
                is ComparisonUiState.Success -> {
                    // Mark all complete
                    stepRows.forEach { (text, icon) ->
                        icon.text = "✓"
                        icon.setTextColor(ContextCompat.getColor(this, R.color.green_emerald))
                        text.setTextColor(ContextCompat.getColor(this, R.color.text_white))
                    }

                    // Open Results Activity
                    val intent = Intent(this, ResultsActivity::class.java).apply {
                        putExtra("extra_response", state.response)
                    }
                    startActivity(intent)
                    finish()
                }
                is ComparisonUiState.Error -> {
                    Toast.makeText(this, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                    finish()
                }
                else -> {}
            }
        }
    }
}
