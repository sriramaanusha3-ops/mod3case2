package com.example.mod3case2.data.repository

import com.example.mod3case2.data.api.ApiClient
import com.example.mod3case2.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EvaluationRepository {

    private fun api() = ApiClient.getService()

    suspend fun checkHealth(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val resp = api().checkHealth()
            if (resp.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Backend returned error ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun compareSystems(question: String, topK: Int = 4, threshold: Double = 0.25): Result<CompareResponse> = withContext(Dispatchers.IO) {
        try {
            val req = CompareRequest(question = question, topK = topK, similarityThreshold = threshold)
            val resp = api().compareSystems(req)
            if (resp.isSuccessful && resp.body() != null) {
                Result.success(resp.body()!!)
            } else {
                Result.failure(Exception("Comparison failed: ${resp.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBenchmarks(): Result<BenchmarkListResponse> = withContext(Dispatchers.IO) {
        try {
            val resp = api().getBenchmarks()
            if (resp.isSuccessful && resp.body() != null) {
                Result.success(resp.body()!!)
            } else {
                Result.failure(Exception("Failed to load benchmarks: ${resp.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun runBenchmarkSuite(): Result<BenchmarkSuiteResponse> = withContext(Dispatchers.IO) {
        try {
            val resp = api().runBenchmarkSuite()
            if (resp.isSuccessful && resp.body() != null) {
                Result.success(resp.body()!!)
            } else {
                Result.failure(Exception("Benchmark suite failed: ${resp.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMetrics(): Result<MetricsResponse> = withContext(Dispatchers.IO) {
        try {
            val resp = api().getMetrics()
            if (resp.isSuccessful && resp.body() != null) {
                Result.success(resp.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch metrics: ${resp.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun recordManualEvaluation(
        question: String,
        plainRating: String,
        ragRating: String,
        evidenceSupport: String,
        notes: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val req = ManualEvaluationRequest(
                question = question,
                plainRating = plainRating,
                ragRating = ragRating,
                evidenceSupport = evidenceSupport,
                notes = notes
            )
            val resp = api().recordEvaluation(req)
            if (resp.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to save evaluation"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHistory(): Result<HistoryListResponse> = withContext(Dispatchers.IO) {
        try {
            val resp = api().getHistory()
            if (resp.isSuccessful && resp.body() != null) {
                Result.success(resp.body()!!)
            } else {
                Result.failure(Exception("Failed to load history"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearHistory(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val resp = api().clearHistory()
            if (resp.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to clear history"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun runDemo(): Result<DemoResponse> = withContext(Dispatchers.IO) {
        try {
            val resp = api().runDemo()
            if (resp.isSuccessful && resp.body() != null) {
                Result.success(resp.body()!!)
            } else {
                Result.failure(Exception("Demo experiment failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
