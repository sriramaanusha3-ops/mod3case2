package com.example.mod3case2.ui.benchmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mod3case2.MainActivity
import com.example.mod3case2.databinding.FragmentBenchmarkBinding
import com.example.mod3case2.ui.adapter.BenchmarkAdapter
import com.example.mod3case2.ui.viewmodel.BenchmarkUiState
import com.example.mod3case2.ui.viewmodel.BenchmarkViewModel

class BenchmarkFragment : Fragment() {

    private var _binding: FragmentBenchmarkBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BenchmarkViewModel by viewModels()
    private lateinit var benchmarkAdapter: BenchmarkAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBenchmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        benchmarkAdapter = BenchmarkAdapter { question ->
            (activity as? MainActivity)?.navigateToCompare(question.question)
        }
        binding.rvBenchmarkQuestions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBenchmarkQuestions.adapter = benchmarkAdapter
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is BenchmarkUiState.QuestionsLoaded -> {
                    benchmarkAdapter.updateQuestions(state.questions)
                    binding.tvBenchmarkProgress.visibility = View.GONE
                    binding.btnRunBenchmarkSuite.isEnabled = true
                }
                is BenchmarkUiState.RunningSuite -> {
                    binding.btnRunBenchmarkSuite.isEnabled = false
                    binding.tvBenchmarkProgress.visibility = View.VISIBLE
                    binding.tvBenchmarkProgress.text = state.currentQuestion
                }
                is BenchmarkUiState.SuiteCompleted -> {
                    binding.btnRunBenchmarkSuite.isEnabled = true
                    binding.tvBenchmarkProgress.visibility = View.GONE
                    binding.cardBenchmarkSummary.visibility = View.VISIBLE

                    val summary = state.result.summary
                    binding.tvSummarySubtitle.text = "${summary.totalEvaluated} Questions Evaluated"
                    binding.tvSummaryPlainAcc.text = "${summary.plainLLMAccuracyPct}%"
                    binding.tvSummaryRAGAcc.text = "${summary.ragAccuracyPct}%"
                    binding.tvSummaryPlainCounts.text = "${summary.plainCorrectCount} / ${summary.plainPartialCount} / ${summary.plainIncorrectCount}"
                    binding.tvSummaryRAGCounts.text = "${summary.ragCorrectCount} / ${summary.ragPartialCount} / ${summary.ragIncorrectCount}"
                    binding.tvSummaryPlainLatency.text = "${summary.plainAvgLatencyS}s"
                    binding.tvSummaryRAGLatency.text = "${summary.ragAvgLatencyS}s"
                    binding.tvSummaryRecallAtK.text = summary.retrievalRecallAtK

                    binding.chartBenchmarkSummary.setValues(
                        summary.plainLLMAccuracyPct,
                        summary.ragAccuracyPct,
                        "Plain LLM",
                        "RAG"
                    )

                    Toast.makeText(requireContext(), "Benchmark suite completed successfully!", Toast.LENGTH_SHORT).show()
                }
                is BenchmarkUiState.Error -> {
                    binding.btnRunBenchmarkSuite.isEnabled = true
                    binding.tvBenchmarkProgress.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun setupListeners() {
        binding.btnRunBenchmarkSuite.setOnClickListener {
            viewModel.runBenchmarkSuite()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
