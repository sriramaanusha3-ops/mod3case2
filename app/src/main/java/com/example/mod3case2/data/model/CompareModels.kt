package com.example.mod3case2.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CompareRequest(
    @SerializedName("question") val question: String,
    @SerializedName("top_k") val topK: Int = 4,
    @SerializedName("similarity_threshold") val similarityThreshold: Double = 0.25
) : Serializable

data class CompareResponse(
    @SerializedName("question") val question: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("plain_llm") val plainLLM: PlainLLMResult,
    @SerializedName("rag") val rag: RAGResult,
    @SerializedName("evaluation") val evaluation: EvaluationReport
) : Serializable

data class PlainLLMResult(
    @SerializedName("answer") val answer: String,
    @SerializedName("latency_ms") val latencyMs: Long
) : Serializable

data class RAGResult(
    @SerializedName("answer") val answer: String,
    @SerializedName("latency_ms") val latencyMs: Long,
    @SerializedName("sources") val sources: List<SourceItem>,
    @SerializedName("chunks_retrieved") val chunksRetrieved: Int
) : Serializable

data class SourceItem(
    @SerializedName("chunk_id") val chunkId: String?,
    @SerializedName("document") val document: String,
    @SerializedName("page") val page: Int?,
    @SerializedName("score") val score: Double,
    @SerializedName("text") val text: String
) : Serializable

data class EvaluationReport(
    @SerializedName("winner") val winner: String,
    @SerializedName("winner_reason") val winnerReason: String,
    @SerializedName("metrics") val metrics: ComparisonMetrics,
    @SerializedName("semantic_evaluation") val semanticEvaluation: SemanticEvaluation,
    @SerializedName("reference_answer") val referenceAnswer: String?,
    @SerializedName("reference_facts") val referenceFacts: List<String>?,
    @SerializedName("claim_analysis") val claimAnalysis: ClaimAnalysisReport
) : Serializable

data class ComparisonMetrics(
    @SerializedName("winner") val winner: String,
    @SerializedName("winner_reason") val winnerReason: String,
    @SerializedName("plain_llm_accuracy_pct") val plainLLMAccuracyPct: Int,
    @SerializedName("rag_accuracy_pct") val ragAccuracyPct: Int,
    @SerializedName("rag_groundedness_pct") val ragGroundednessPct: Int,
    @SerializedName("source_support_rating") val sourceSupportRating: String,
    @SerializedName("top_similarity_score") val topSimilarityScore: Double,
    @SerializedName("chunks_retrieved") val chunksRetrieved: Int,
    @SerializedName("documents_used") val documentsUsed: Int,
    @SerializedName("plain_latency_s") val plainLatencyS: Double,
    @SerializedName("rag_latency_s") val ragLatencyS: Double,
    @SerializedName("accuracy_delta_pct") val accuracyDeltaPct: Int
) : Serializable

data class SemanticEvaluation(
    @SerializedName("plain_semantic_similarity") val plainSemanticSimilarity: Double,
    @SerializedName("rag_semantic_similarity") val ragSemanticSimilarity: Double,
    @SerializedName("score_label") val scoreLabel: String,
    @SerializedName("reference_available") val referenceAvailable: Boolean
) : Serializable

data class ClaimAnalysisReport(
    @SerializedName("fact_checks") val factChecks: List<FactCheckItem>,
    @SerializedName("plain_llm_hallucination") val plainLLMHallucination: HallucinationDetail,
    @SerializedName("rag_hallucination") val ragHallucination: HallucinationDetail
) : Serializable

data class FactCheckItem(
    @SerializedName("claim_id") val claimId: String,
    @SerializedName("claim_text") val claimText: String,
    @SerializedName("plain_llm_status") val plainLLMStatus: String,
    @SerializedName("plain_llm_supported") val plainLLMSupported: Boolean,
    @SerializedName("rag_status") val ragStatus: String,
    @SerializedName("rag_supported") val ragSupported: Boolean
) : Serializable

data class HallucinationDetail(
    @SerializedName("potential_unsupported_claims") val potentialUnsupportedClaims: Int,
    @SerializedName("supported_claims") val supportedClaims: Int,
    @SerializedName("status_label") val statusLabel: String,
    @SerializedName("estimation_type") val estimationType: String
) : Serializable
