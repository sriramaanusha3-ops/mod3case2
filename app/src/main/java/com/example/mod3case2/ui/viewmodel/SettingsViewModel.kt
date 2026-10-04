package com.example.mod3case2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mod3case2.data.api.ApiClient
import com.example.mod3case2.data.repository.EvaluationRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: EvaluationRepository = EvaluationRepository()
) : ViewModel() {

    private val _currentBaseUrl = MutableLiveData<String>(ApiClient.getBaseUrl())
    val currentBaseUrl: LiveData<String> = _currentBaseUrl

    private val _connectionStatus = MutableLiveData<String>("Not tested")
    val connectionStatus: LiveData<String> = _connectionStatus

    private val _isConnecting = MutableLiveData<Boolean>(false)
    val isConnecting: LiveData<Boolean> = _isConnecting

    fun updateBaseUrl(newUrl: String) {
        ApiClient.setBaseUrl(newUrl)
        _currentBaseUrl.value = ApiClient.getBaseUrl()
        testConnection()
    }

    fun testConnection() {
        _isConnecting.value = true
        _connectionStatus.value = "Connecting to backend..."
        viewModelScope.launch {
            repository.checkHealth().onSuccess {
                _isConnecting.value = false
                _connectionStatus.value = "ONLINE • Connection verified"
            }.onFailure { err ->
                _isConnecting.value = false
                _connectionStatus.value = "OFFLINE • ${err.localizedMessage}"
            }
        }
    }
}
