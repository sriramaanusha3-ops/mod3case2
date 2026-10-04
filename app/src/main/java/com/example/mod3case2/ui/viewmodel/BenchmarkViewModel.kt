package com.example.mod3case2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mod3case2.data.model.BenchmarkQuestion
import com.example.mod3case2.data.model.BenchmarkSuiteResponse
import com.example.mod3case2.data.repository.EvaluationRepository
import kotlinx.coroutines.launch

sealed class BenchmarkUiState {
    object Idle : BenchmarkUiState()
    object LoadingQuestions : BenchmarkUiState()
    data class QuestionsLoaded(val questions: List<BenchmarkQuestion>) : BenchmarkUiState()
    data class RunningSuite(val progress: Int, val total: Int, val currentQuestion: String) : BenchmarkUiState()
    data class SuiteCompleted(val result: BenchmarkSuiteResponse) : BenchmarkUiState()
    data class Error(val message: String) : BenchmarkUiState()
}

class BenchmarkViewModel(
    private val repository: EvaluationRepository = EvaluationRepository()
) : ViewModel() {

    private val _uiState = MutableLiveData<BenchmarkUiState>(BenchmarkUiState.Idle)
    val uiState: LiveData<BenchmarkUiState> = _uiState

    init {
        loadBenchmarkQuestions()
    }

    fun loadBenchmarkQuestions() {
        _uiState.value = BenchmarkUiState.LoadingQuestions
        viewModelScope.launch {
            repository.getBenchmarks().onSuccess { resp ->
                _uiState.value = BenchmarkUiState.QuestionsLoaded(resp.questions)
            }.onFailure { err ->
                _uiState.value = BenchmarkUiState.Error(err.localizedMessage ?: "Failed to load benchmark questions")
            }
        }
    }

    fun runBenchmarkSuite() {
        _uiState.value = BenchmarkUiState.RunningSuite(0, 10, "Initializing academic benchmark suite...")
        viewModelScope.launch {
            repository.runBenchmarkSuite().onSuccess { resp ->
                _uiState.value = BenchmarkUiState.SuiteCompleted(resp)
            }.onFailure { err ->
                _uiState.value = BenchmarkUiState.Error(err.localizedMessage ?: "Failed to run benchmark suite")
            }
        }
    }
}
