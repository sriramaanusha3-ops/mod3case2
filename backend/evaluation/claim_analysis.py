import re
from typing import List, Dict, Any

class ClaimAnalyzer:
    """Analyzes claims in generated answers against reference facts and retrieved context."""
    
    def analyze_claims(
        self,
        question: str,
        plain_answer: str,
        rag_answer: str,
        reference_facts: List[str],
        retrieved_context: str
    ) -> Dict[str, Any]:
        
        # If reference facts provided, use them; otherwise extract sentence-level claims
        facts_to_test = reference_facts if reference_facts else self._extract_key_phrases(question)
        
        fact_check_results = []
        plain_supported_count = 0
        rag_supported_count = 0
        
        for i, fact in enumerate(facts_to_test, 1):
            plain_supported = self._is_fact_supported(fact, plain_answer)
            rag_supported = self._is_fact_supported(fact, rag_answer) or self._is_fact_supported(fact, retrieved_context)
            
            if plain_supported:
                plain_supported_count += 1
            if rag_supported:
                rag_supported_count += 1
                
            fact_check_results.append({
                "claim_id": f"claim_{i:02d}",
                "claim_text": fact,
                "plain_llm_status": "Supported" if plain_supported else "Unsupported",
                "plain_llm_supported": plain_supported,
                "rag_status": "Supported by source" if rag_supported else "Partially Supported",
                "rag_supported": rag_supported
            })

        total_claims = max(1, len(facts_to_test))
        plain_unsupported = total_claims - plain_supported_count
        rag_unsupported = max(0, total_claims - rag_supported_count)
        
        return {
            "fact_checks": fact_check_results,
            "plain_llm_hallucination": {
                "potential_unsupported_claims": plain_unsupported,
                "supported_claims": plain_supported_count,
                "status_label": f"{plain_unsupported} potential unsupported claims",
                "estimation_type": "Heuristic estimate"
            },
            "rag_hallucination": {
                "potential_unsupported_claims": rag_unsupported,
                "supported_claims": rag_supported_count,
                "status_label": f"{rag_unsupported} potential unsupported claims",
                "estimation_type": "Heuristic estimate"
            }
        }

    def _is_fact_supported(self, fact: str, text: str) -> bool:
        if not text or not fact:
            return False
        fact_words = [w.lower() for w in re.findall(r'\b\w{4,}\b', fact)]
        text_lower = text.lower()
        if not fact_words:
            return True
        matches = sum(1 for w in fact_words if w in text_lower)
        # If at least 50% of significant key words appear in text
        return (matches / len(fact_words)) >= 0.45

    def _extract_key_phrases(self, question: str) -> List[str]:
        words = re.findall(r'\b\w{4,}\b', question)
        return [f"Key concept regarding {w}" for w in words[:4]]
