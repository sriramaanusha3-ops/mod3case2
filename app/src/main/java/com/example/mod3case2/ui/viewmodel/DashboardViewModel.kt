package com.example.mod3case2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mod3case2.data.model.DemoResponse
import com.example.mod3case2.data.model.MetricsResponse
import com.example.mod3case2.data.repository.EvaluationRepository
import kotlinx.coroutines.launch

sealed class DashboardUiState {
    object Idle : DashboardUiState()
    object RunningDemo : DashboardUiState()
    data class DemoFinished(val demoResponse: DemoResponse) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

class DashboardViewModel(
    private val repository: EvaluationRepository = EvaluationRepository()
) : ViewModel() {

    private val _uiState = MutableLiveData<DashboardUiState>(DashboardUiState.Idle)
    val uiState: LiveData<DashboardUiState> = _uiState

    private val _engineOnline = MutableLiveData<Boolean>(true)
    val engineOnline: LiveData<Boolean> = _engineOnline

    private val _metrics = MutableLiveData<MetricsResponse?>()
    val metrics: LiveData<MetricsResponse?> = _metrics

    init {
        refreshDashboard()
    }

    fun refreshDashboard() {
        viewModelScope.launch {
            repository.checkHealth().onSuccess {
                _engineOnline.value = true
            }.onFailure {
                _engineOnline.value = false
            }

            repository.getMetrics().onSuccess { data ->
                _metrics.value = data
            }
        }
    }

    fun runDemoExperiment() {
        _uiState.value = DashboardUiState.RunningDemo
        viewModelScope.launch {
            repository.runDemo().onSuccess { resp ->
                _uiState.value = DashboardUiState.DemoFinished(resp)
                refreshDashboard()
            }.onFailure { err ->
                _uiState.value = DashboardUiState.Error(err.localizedMessage ?: "Failed to execute demo experiment")
            }
        }
    }
}
