package com.example.mod3case2.data.api

import com.example.mod3case2.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("/")
    suspend fun getRootStatus(): Response<Map<String, Any>>

    @GET("/health")
    suspend fun checkHealth(): Response<Map<String, Any>>

    @POST("/compare")
    suspend fun compareSystems(@Body request: CompareRequest): Response<CompareResponse>

    @GET("/benchmarks")
    suspend fun getBenchmarks(): Response<BenchmarkListResponse>

    @POST("/benchmarks/run")
    suspend fun runBenchmarkSuite(): Response<BenchmarkSuiteResponse>

    @GET("/metrics")
    suspend fun getMetrics(): Response<MetricsResponse>

    @POST("/evaluate")
    suspend fun recordEvaluation(@Body request: ManualEvaluationRequest): Response<Map<String, Any>>

    @GET("/history")
    suspend fun getHistory(): Response<HistoryListResponse>

    @DELETE("/history")
    suspend fun clearHistory(): Response<Map<String, Any>>

    @POST("/demo")
    suspend fun runDemo(): Response<DemoResponse>
}
