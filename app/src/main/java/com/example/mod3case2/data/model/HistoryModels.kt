package com.example.mod3case2.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class HistoryListResponse(
    @SerializedName("total") val total: Int,
    @SerializedName("history") val history: List<ExperimentHistoryItem>
) : Serializable

data class ExperimentHistoryItem(
    @SerializedName("id") val id: String,
    @SerializedName("question") val question: String,
    @SerializedName("winner") val winner: String,
    @SerializedName("rag_accuracy_pct") val ragAccuracyPct: Int,
    @SerializedName("plain_accuracy_pct") val plainAccuracyPct: Int,
    @SerializedName("sources_count") val sourcesCount: Int,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("plain_latency_s") val plainLatencyS: Double,
    @SerializedName("rag_latency_s") val ragLatencyS: Double,
    @SerializedName("full_data") val fullData: CompareResponse?
) : Serializable

data class MetricsResponse(
    @SerializedName("accuracy") val accuracy: MetricDetail,
    @SerializedName("groundedness") val groundedness: MetricDetailStr,
    @SerializedName("source_support") val sourceSupport: MetricDetailStr,
    @SerializedName("latency") val latency: MetricDetailStr,
    @SerializedName("total_experiments_run") val totalExperimentsRun: Int
) : Serializable

data class MetricDetail(
    @SerializedName("plain_llm") val plainLLM: Any?,
    @SerializedName("rag") val rag: Any?,
    @SerializedName("delta") val delta: String?
) : Serializable

data class MetricDetailStr(
    @SerializedName("plain_llm") val plainLLM: String?,
    @SerializedName("rag") val rag: String?
) : Serializable

data class ManualEvaluationRequest(
    @SerializedName("comparison_id") val comparisonId: String? = "",
    @SerializedName("question") val question: String,
    @SerializedName("plain_rating") val plainRating: String,
    @SerializedName("rag_rating") val ragRating: String,
    @SerializedName("evidence_support") val evidenceSupport: String = "Fully Supported",
    @SerializedName("notes") val notes: String = ""
) : Serializable

data class DemoResponse(
    @SerializedName("status") val status: String,
    @SerializedName("title") val title: String,
    @SerializedName("plain_accuracy_pct") val plainAccuracyPct: Int,
    @SerializedName("rag_accuracy_pct") val ragAccuracyPct: Int,
    @SerializedName("improvement_pct") val improvementPct: Int,
    @SerializedName("questions_tested") val questionsTested: List<DemoQuestionTested>
) : Serializable

data class DemoQuestionTested(
    @SerializedName("question") val question: String,
    @SerializedName("plain_acc") val plainAcc: Int,
    @SerializedName("rag_acc") val ragAcc: Int,
    @SerializedName("winner") val winner: String
) : Serializable
