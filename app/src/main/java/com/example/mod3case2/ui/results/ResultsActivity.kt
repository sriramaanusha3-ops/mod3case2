package com.example.mod3case2.ui.results

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mod3case2.R
import com.example.mod3case2.data.model.CompareResponse
import com.example.mod3case2.databinding.ActivityResultsBinding
import com.example.mod3case2.ui.adapter.FactCheckAdapter
import com.example.mod3case2.ui.adapter.SourceAdapter
import com.example.mod3case2.ui.viewmodel.ComparisonViewModel

class ResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultsBinding
    private val viewModel: ComparisonViewModel by viewModels()

    private lateinit var sourceAdapter: SourceAdapter
    private lateinit var factCheckAdapter: FactCheckAdapter

    private var responseData: CompareResponse? = null
    private var isReferenceExpanded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        responseData = intent.getSerializableExtra("extra_response") as? CompareResponse

        if (responseData == null) {
            Toast.makeText(this, "No comparison data provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerViews()
        bindResults(responseData!!)
        setupAnswerTabs()
        setupReferenceToggle()
        setupManualEvaluation(responseData!!)

        binding.btnBackResults.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerViews() {
        // Sources
        sourceAdapter = SourceAdapter { source ->
            Toast.makeText(this, "Source: ${source.document} (Page ${source.page ?: 1})", Toast.LENGTH_SHORT).show()
        }
        binding.rvRetrievedSources.layoutManager = LinearLayoutManager(this)
        binding.rvRetrievedSources.adapter = sourceAdapter

        // Fact Check
        factCheckAdapter = FactCheckAdapter()
        binding.rvFactChecks.layoutManager = LinearLayoutManager(this)
        binding.rvFactChecks.adapter = factCheckAdapter
    }

    private fun bindResults(data: CompareResponse) {
        binding.tvResultsQuestion.text = data.question

        // Winner Banner
        val winner = data.evaluation.winner.uppercase()
        if (winner == "RAG") {
            binding.tvWinnerTitle.text = "RAG"
            binding.tvWinnerTitle.setTextColor(ContextCompat.getColor(this, R.color.cyan_accent))
            binding.cardWinnerBanner.strokeColor = ContextCompat.getColor(this, R.color.border_glow_cyan)
        } else if (winner == "PLAIN_LLM") {
            binding.tvWinnerTitle.text = "PLAIN LLM"
            binding.tvWinnerTitle.setTextColor(ContextCompat.getColor(this, R.color.purple_bright))
            binding.cardWinnerBanner.strokeColor = ContextCompat.getColor(this, R.color.border_glow_purple)
        } else {
            binding.tvWinnerTitle.text = "NO CLEAR WINNER"
            binding.tvWinnerTitle.setTextColor(ContextCompat.getColor(this, R.color.yellow_amber))
            binding.cardWinnerBanner.strokeColor = ContextCompat.getColor(this, R.color.yellow_amber)
        }
        binding.tvWinnerReason.text = data.evaluation.winnerReason

        // Plain LLM Answer & Latency
        binding.tvPlainLLMAnswer.text = data.plainLLM.answer
        binding.tvPlainLLMLatency.text = "${String.format("%.2f", data.plainLLM.latencyMs / 1000.0)} s"

        // RAG Answer & Latency & Sources
        binding.tvRAGAnswer.text = data.rag.answer
        binding.tvRAGLatency.text = "${String.format("%.2f", data.rag.latencyMs / 1000.0)} s"
        binding.tvRAGSourcesCount.text = "${data.rag.sources.size}"
        binding.tvRAGChunksBadge.text = "${data.rag.chunksRetrieved} Retrieved Chunks"

        // Sources List
        sourceAdapter.updateSources(data.rag.sources)

        // Retrieval Quality
        val topSim = data.evaluation.metrics.topSimilarityScore
        binding.tvTopSimilarityVal.text = String.format("%.3f", topSim)
        binding.pbTopSimilarity.progress = (topSim * 1000).toInt()
        binding.tvChunksRetrievedVal.text = "${data.rag.chunksRetrieved}"
        binding.tvDocsUsedVal.text = "${data.evaluation.metrics.documentsUsed}"

        // Reference Answer
        val refAns = data.evaluation.referenceAnswer
        if (!refAns.isNullOrBlank()) {
            binding.tvReferenceContent.text = refAns
            binding.cardReferenceAnswer.visibility = View.VISIBLE
        } else {
            binding.cardReferenceAnswer.visibility = View.GONE
        }

        // Fact Check Claims
        val factChecks = data.evaluation.claimAnalysis.factChecks
        factCheckAdapter.updateClaims(factChecks)

        // Hallucination Heuristic
        val plainHall = data.evaluation.claimAnalysis.plainLLMHallucination
        val ragHall = data.evaluation.claimAnalysis.ragHallucination
        binding.tvPlainHallucination.text = "${plainHall.potentialUnsupportedClaims} potential unsupported claims (${plainHall.estimationType})"
        binding.tvRAGHallucination.text = "${ragHall.potentialUnsupportedClaims} potential unsupported claims (${ragHall.estimationType})"

        // Metrics & Dual Chart
        val plainAcc = data.evaluation.metrics.plainLLMAccuracyPct
        val ragAcc = data.evaluation.metrics.ragAccuracyPct
        binding.chartResultsMetrics.setValues(plainAcc, ragAcc, "Plain LLM", "RAG")
        binding.tvMetricsGroundedness.text = "RAG: ${data.evaluation.metrics.ragGroundednessPct}%"
        binding.tvMetricsSourceSupport.text = data.evaluation.metrics.sourceSupportRating
    }

    private fun setupAnswerTabs() {
        binding.tabPlainLLM.setOnClickListener {
            binding.tabPlainLLM.setBackgroundResource(R.drawable.bg_chip_selected)
            binding.tabRAG.setBackgroundResource(R.drawable.bg_chip_unselected)
            binding.cardPlainLLMAnswer.visibility = View.VISIBLE
            binding.cardRAGAnswer.visibility = View.GONE
        }

        binding.tabRAG.setOnClickListener {
            binding.tabRAG.setBackgroundResource(R.drawable.bg_chip_selected)
            binding.tabPlainLLM.setBackgroundResource(R.drawable.bg_chip_unselected)
            binding.cardRAGAnswer.visibility = View.VISIBLE
            binding.cardPlainLLMAnswer.visibility = View.GONE
        }
    }

    private fun setupReferenceToggle() {
        binding.layoutReferenceHeader.setOnClickListener {
            isReferenceExpanded = !isReferenceExpanded
            if (isReferenceExpanded) {
                binding.tvReferenceContent.visibility = View.VISIBLE
                binding.tvReferenceToggle.text = "[ Hide ]"
            } else {
                binding.tvReferenceContent.visibility = View.GONE
                binding.tvReferenceToggle.text = "[ View ]"
            }
        }
    }

    private fun setupManualEvaluation(data: CompareResponse) {
        viewModel.manualEvalSubmitted.observe(this) { submitted ->
            if (submitted) {
                binding.tvEvalSaveStatus.visibility = View.VISIBLE
                binding.btnSaveEvaluation.isEnabled = false
                binding.btnSaveEvaluation.text = "EVALUATION SAVED"
            }
        }

        binding.btnSaveEvaluation.setOnClickListener {
            val plainRating = when (binding.rgPlainRating.checkedRadioButtonId) {
                R.id.rbPlainCorrect -> "Correct"
                R.id.rbPlainPartial -> "Partially Correct"
                else -> "Incorrect"
            }

            val ragRating = when (binding.rgRAGRating.checkedRadioButtonId) {
                R.id.rbRAGCorrect -> "Correct"
                R.id.rbRAGPartial -> "Partially Correct"
                else -> "Incorrect"
            }

            viewModel.submitManualEvaluation(
                question = data.question,
                plainRating = plainRating,
                ragRating = ragRating,
                evidenceSupport = "Fully Supported",
                notes = "Manual judgment from research results screen"
            )
        }
    }
}
