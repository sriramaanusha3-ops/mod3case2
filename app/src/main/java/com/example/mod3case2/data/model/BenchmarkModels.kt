package com.example.mod3case2.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BenchmarkListResponse(
    @SerializedName("total") val total: Int,
    @SerializedName("questions") val questions: List<BenchmarkQuestion>
) : Serializable

data class BenchmarkQuestion(
    @SerializedName("id") val id: String,
    @SerializedName("question") val question: String,
    @SerializedName("category") val category: String?,
    @SerializedName("difficulty") val difficulty: String?,
    @SerializedName("reference_answer") val referenceAnswer: String,
    @SerializedName("relevant_documents") val relevantDocuments: List<String>,
    @SerializedName("key_facts") val keyFacts: List<String>
) : Serializable

data class BenchmarkSuiteResponse(
    @SerializedName("summary") val summary: BenchmarkSummary,
    @SerializedName("questions_evaluated") val questionsEvaluated: Int,
    @SerializedName("results") val results: List<BenchmarkQuestionResult>
) : Serializable

data class BenchmarkSummary(
    @SerializedName("total_evaluated") val totalEvaluated: Int,
    @SerializedName("plain_llm_accuracy_pct") val plainLLMAccuracyPct: Int,
    @SerializedName("rag_accuracy_pct") val ragAccuracyPct: Int,
    @SerializedName("improvement_pct") val improvementPct: Int,
    @SerializedName("plain_correct_count") val plainCorrectCount: Int,
    @SerializedName("plain_partial_count") val plainPartialCount: Int,
    @SerializedName("plain_incorrect_count") val plainIncorrectCount: Int,
    @SerializedName("rag_correct_count") val ragCorrectCount: Int,
    @SerializedName("rag_partial_count") val ragPartialCount: Int,
    @SerializedName("rag_incorrect_count") val ragIncorrectCount: Int,
    @SerializedName("plain_avg_latency_s") val plainAvgLatencyS: Double,
    @SerializedName("rag_avg_latency_s") val ragAvgLatencyS: Double,
    @SerializedName("retrieval_recall_at_k") val retrievalRecallAtK: String,
    @SerializedName("mrr_score") val mrrScore: Double,
    @SerializedName("avg_groundedness_pct") val avgGroundednessPct: Int
) : Serializable

data class BenchmarkQuestionResult(
    @SerializedName("id") val id: String,
    @SerializedName("question") val question: String,
    @SerializedName("category") val category: String?,
    @SerializedName("plain_acc") val plainAcc: Int,
    @SerializedName("rag_acc") val ragAcc: Int,
    @SerializedName("plain_latency_ms") val plainLatencyMs: Long,
    @SerializedName("rag_latency_ms") val ragLatencyMs: Long,
    @SerializedName("winner") val winner: String,
    @SerializedName("sources_count") val sourcesCount: Int,
    @SerializedName("relevant_retrieved") val relevantRetrieved: Boolean,
    @SerializedName("mrr_rank") val mrrRank: Double
) : Serializable
