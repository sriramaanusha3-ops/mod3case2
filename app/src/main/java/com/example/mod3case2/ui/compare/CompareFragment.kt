package com.example.mod3case2.ui.compare

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mod3case2.R
import com.example.mod3case2.databinding.FragmentCompareBinding
import com.example.mod3case2.ui.viewmodel.ComparisonViewModel

class CompareFragment : Fragment() {

    private var _binding: FragmentCompareBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ComparisonViewModel by viewModels()

    companion object {
        private const val ARG_PREFILLED_QUESTION = "prefilled_question"

        fun newInstance(prefilledQuestion: String? = null): CompareFragment {
            val fragment = CompareFragment()
            val args = Bundle()
            args.putString(ARG_PREFILLED_QUESTION, prefilledQuestion)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefilled = arguments?.getString(ARG_PREFILLED_QUESTION)
        if (!prefilled.isNullOrBlank()) {
            binding.etQuestionInput.setText(prefilled)
        }

        setupQuestionTypeChips()
        setupExampleSuggestions()
        setupRunButton()
    }

    private fun setupQuestionTypeChips() {
        val chips = listOf(
            binding.chipTypeKnowledge to "Knowledge",
            binding.chipTypeDocument to "Document",
            binding.chipTypeFactual to "Factual",
            binding.chipTypeCustom to "Custom"
        )

        chips.forEach { (view, type) ->
            view.setOnClickListener {
                viewModel.setQuestionType(type)
                updateChipSelection(chips, type)
            }
        }
    }

    private fun updateChipSelection(chips: List<Pair<TextView, String>>, selectedType: String) {
        chips.forEach { (view, type) ->
            if (type == selectedType) {
                view.setBackgroundResource(R.drawable.bg_chip_selected)
                view.setTextColor(resources.getColor(R.color.text_white, null))
                view.text = "● $type"
            } else {
                view.setBackgroundResource(R.drawable.bg_chip_unselected)
                view.setTextColor(resources.getColor(R.color.text_secondary, null))
                view.text = "○ $type"
            }
        }
    }

    private fun setupExampleSuggestions() {
        binding.chipExample1.setOnClickListener {
            binding.etQuestionInput.setText("What is supervised learning?")
        }
        binding.chipExample2.setOnClickListener {
            binding.etQuestionInput.setText("How does CRISPR work?")
        }
        binding.chipExample3.setOnClickListener {
            binding.etQuestionInput.setText("What caused the 2008 financial crisis?")
        }
        binding.chipExample4.setOnClickListener {
            binding.etQuestionInput.setText("What are the main causes of climate change?")
        }
        binding.chipExample5.setOnClickListener {
            binding.etQuestionInput.setText("How does quantum computing differ from classical computing?")
        }
    }

    private fun setupRunButton() {
        binding.btnRunComparison.setOnClickListener {
            val question = binding.etQuestionInput.text.toString().trim()
            if (question.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a question to compare.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(requireContext(), ProcessingActivity::class.java).apply {
                putExtra("extra_question", question)
                putExtra("extra_top_k", 4)
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
