package com.example.mod3case2.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mod3case2.MainActivity
import com.example.mod3case2.R
import com.example.mod3case2.databinding.FragmentDashboardBinding
import com.example.mod3case2.ui.compare.ProcessingActivity
import com.example.mod3case2.ui.viewmodel.DashboardUiState
import com.example.mod3case2.ui.viewmodel.DashboardViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        // Engine online status
        viewModel.engineOnline.observe(viewLifecycleOwner) { isOnline ->
            if (isOnline) {
                binding.tvEngineStatus.text = "● ENGINE ONLINE"
                binding.tvEngineStatus.setBackgroundResource(R.drawable.bg_badge_emerald)
                binding.tvEngineStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_emerald))
            } else {
                binding.tvEngineStatus.text = "○ OFFLINE (LOCAL)"
                binding.tvEngineStatus.setBackgroundResource(R.drawable.bg_badge_amber)
                binding.tvEngineStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow_amber))
            }
        }

        // Metrics data
        viewModel.metrics.observe(viewLifecycleOwner) { metrics ->
            if (metrics != null) {
                val plainAcc = (metrics.accuracy.plainLLM as? Number)?.toInt() ?: 72
                val ragAcc = (metrics.accuracy.rag as? Number)?.toInt() ?: 91
                binding.chartDashboardMetrics.setValues(plainAcc, ragAcc, "Plain LLM", "RAG")
                binding.tvGroundednessValue.text = "${metrics.groundedness.rag ?: "94%"} (RAG)"
                binding.tvLatencyValue.text = "${metrics.latency.plainLLM ?: "1.18s"} vs ${metrics.latency.rag ?: "1.45s"}"
            } else {
                binding.chartDashboardMetrics.setValues(72, 91, "Plain LLM", "RAG")
            }
        }

        // UI State (Demo Experiment)
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DashboardUiState.RunningDemo -> {
                    binding.btnRunDemoExperiment.isEnabled = false
                    binding.btnRunDemoExperiment.text = "Running Demo Experiment..."
                }
                is DashboardUiState.DemoFinished -> {
                    binding.btnRunDemoExperiment.isEnabled = true
                    binding.btnRunDemoExperiment.text = "✦ RUN DEMO EXPERIMENT"
                    val resp = state.demoResponse
                    Toast.makeText(
                        requireContext(),
                        "Demo Complete! Plain LLM: ${resp.plainAccuracyPct}% | RAG: ${resp.ragAccuracyPct}% (+${resp.improvementPct}%)",
                        Toast.LENGTH_LONG
                    ).show()
                }
                is DashboardUiState.Error -> {
                    binding.btnRunDemoExperiment.isEnabled = true
                    binding.btnRunDemoExperiment.text = "✦ RUN DEMO EXPERIMENT"
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.btnRunDemoExperiment.isEnabled = true
                    binding.btnRunDemoExperiment.text = "✦ RUN DEMO EXPERIMENT"
                }
            }
        }
    }

    private fun setupListeners() {
        // Run demo experiment button
        binding.btnRunDemoExperiment.setOnClickListener {
            viewModel.runDemoExperiment()
        }

        // Quick compare card click
        binding.cardQuickCompare.setOnClickListener {
            (activity as? MainActivity)?.navigateToCompare()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
