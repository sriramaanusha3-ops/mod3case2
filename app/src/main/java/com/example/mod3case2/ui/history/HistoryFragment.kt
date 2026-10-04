package com.example.mod3case2.ui.history

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mod3case2.databinding.FragmentHistoryBinding
import com.example.mod3case2.ui.adapter.HistoryAdapter
import com.example.mod3case2.ui.results.ResultsActivity
import com.example.mod3case2.ui.viewmodel.HistoryUiState
import com.example.mod3case2.ui.viewmodel.HistoryViewModel

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupSearch()
        setupClearHistory()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter { item ->
            if (item.fullData != null) {
                val intent = Intent(requireContext(), ResultsActivity::class.java).apply {
                    putExtra("extra_response", item.fullData)
                }
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Opening comparison for: ${item.question}", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvHistoryList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistoryList.adapter = historyAdapter
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is HistoryUiState.Success -> {
                    binding.tvHistoryEmpty.visibility = View.GONE
                    binding.rvHistoryList.visibility = View.VISIBLE
                    historyAdapter.updateHistory(state.history)
                }
                is HistoryUiState.Empty -> {
                    binding.rvHistoryList.visibility = View.GONE
                    binding.tvHistoryEmpty.visibility = View.VISIBLE
                    binding.tvHistoryEmpty.text = state.message
                }
                is HistoryUiState.Error -> {
                    binding.rvHistoryList.visibility = View.GONE
                    binding.tvHistoryEmpty.visibility = View.VISIBLE
                    binding.tvHistoryEmpty.text = state.message
                }
                else -> {}
            }
        }
    }

    private fun setupSearch() {
        binding.etHistorySearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterHistory(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupClearHistory() {
        binding.btnClearHistory.setOnClickListener {
            viewModel.clearAllHistory()
            Toast.makeText(requireContext(), "History cleared", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
