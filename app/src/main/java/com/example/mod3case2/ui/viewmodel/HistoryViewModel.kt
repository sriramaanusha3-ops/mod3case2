package com.example.mod3case2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mod3case2.data.model.ExperimentHistoryItem
import com.example.mod3case2.data.repository.EvaluationRepository
import kotlinx.coroutines.launch

sealed class HistoryUiState {
    object Loading : HistoryUiState()
    data class Success(val history: List<ExperimentHistoryItem>) : HistoryUiState()
    data class Empty(val message: String) : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
}

class HistoryViewModel(
    private val repository: EvaluationRepository = EvaluationRepository()
) : ViewModel() {

    private val _uiState = MutableLiveData<HistoryUiState>(HistoryUiState.Loading)
    val uiState: LiveData<HistoryUiState> = _uiState

    private var allHistory: List<ExperimentHistoryItem> = emptyList()

    init {
        fetchHistory()
    }

    fun fetchHistory() {
        _uiState.value = HistoryUiState.Loading
        viewModelScope.launch {
            repository.getHistory().onSuccess { resp ->
                allHistory = resp.history
                if (allHistory.isEmpty()) {
                    _uiState.value = HistoryUiState.Empty("No experiments recorded yet. Run a comparison to see it here!")
                } else {
                    _uiState.value = HistoryUiState.Success(allHistory)
                }
            }.onFailure { err ->
                _uiState.value = HistoryUiState.Error(err.localizedMessage ?: "Failed to load history")
            }
        }
    }

    fun filterHistory(query: String) {
        if (query.isBlank()) {
            if (allHistory.isEmpty()) {
                _uiState.value = HistoryUiState.Empty("No experiments recorded yet.")
            } else {
                _uiState.value = HistoryUiState.Success(allHistory)
            }
            return
        }

        val filtered = allHistory.filter {
            it.question.contains(query, ignoreCase = true) ||
            it.winner.contains(query, ignoreCase = true)
        }

        if (filtered.isEmpty()) {
            _uiState.value = HistoryUiState.Empty("No matching experiments found for '$query'")
        } else {
            _uiState.value = HistoryUiState.Success(filtered)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory().onSuccess {
                allHistory = emptyList()
                _uiState.value = HistoryUiState.Empty("Experiment history cleared.")
            }
        }
    }
}
