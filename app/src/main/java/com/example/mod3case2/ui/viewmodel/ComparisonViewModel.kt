package com.example.mod3case2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mod3case2.data.model.CompareResponse
import com.example.mod3case2.data.repository.EvaluationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class ComparisonUiState {
    object Idle : ComparisonUiState()
    data class Processing(val stepIndex: Int, val stepMessage: String) : ComparisonUiState()
    data class Success(val response: CompareResponse) : ComparisonUiState()
    data class Error(val message: String) : ComparisonUiState()
}

class ComparisonViewModel(
    private val repository: EvaluationRepository = EvaluationRepository()
) : ViewModel() {

    private val _uiState = MutableLiveData<ComparisonUiState>(ComparisonUiState.Idle)
    val uiState: LiveData<ComparisonUiState> = _uiState

    private val _selectedQuestionType = MutableLiveData<String>("Knowledge")
    val selectedQuestionType: LiveData<String> = _selectedQuestionType

    private val _topK = MutableLiveData<Int>(4)
    val topK: LiveData<Int> = _topK

    private val _manualEvalSubmitted = MutableLiveData<Boolean>(false)
    val manualEvalSubmitted: LiveData<Boolean> = _manualEvalSubmitted

    fun setQuestionType(type: String) {
        _selectedQuestionType.value = type
    }

    fun setTopK(k: Int) {
        _topK.value = k
    }

    fun runComparison(question: String) {
        val trimmed = question.trim()
        if (trimmed.isEmpty()) {
            _uiState.value = ComparisonUiState.Error("Please enter a question to compare.")
            return
        }

        viewModelScope.launch {
            try {
                // Step 1: Sending question to Plain LLM
                _uiState.value = ComparisonUiState.Processing(1, "01  Sending question to Plain LLM")
                delay(350)

                // Step 2: Generating query embedding
                _uiState.value = ComparisonUiState.Processing(2, "02  Generating query embedding")
                delay(300)

                // Step 3: Searching knowledge base
                _uiState.value = ComparisonUiState.Processing(3, "03  Searching knowledge base")
                delay(300)

                // Step 4: Building retrieved context
                _uiState.value = ComparisonUiState.Processing(4, "04  Building retrieved context")
                delay(250)

                // Step 5: Generating RAG answer
                _uiState.value = ComparisonUiState.Processing(5, "05  Generating RAG answer")

                val k = _topK.value ?: 4
                val result = repository.compareSystems(trimmed, topK = k)

                // Step 6: Comparing responses
                _uiState.value = ComparisonUiState.Processing(6, "06  Comparing responses")
                delay(300)

                result.onSuccess { response ->
                    _uiState.value = ComparisonUiState.Success(response)
                }.onFailure { error ->
                    _uiState.value = ComparisonUiState.Error(error.localizedMessage ?: "Failed to complete comparison.")
                }
            } catch (e: Exception) {
                _uiState.value = ComparisonUiState.Error(e.localizedMessage ?: "Unexpected error occurred.")
            }
        }
    }

    fun submitManualEvaluation(
        question: String,
        plainRating: String,
        ragRating: String,
        evidenceSupport: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.recordManualEvaluation(question, plainRating, ragRating, evidenceSupport, notes)
            _manualEvalSubmitted.value = true
        }
    }

    fun resetState() {
        _uiState.value = ComparisonUiState.Idle
        _manualEvalSubmitted.value = false
    }
}
