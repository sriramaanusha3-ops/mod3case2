from typing import List, Dict, Any
from .embeddings import EmbeddingService
from .vector_store import VectorStore

class Retriever:
    """Retrieves top relevant chunks for a question."""
    
    def __init__(self, embedding_service: EmbeddingService, vector_store: VectorStore):
        self.embedding_service = embedding_service
        self.vector_store = vector_store

    def retrieve(self, query: str, top_k: int = 4, threshold: float = 0.25) -> List[Dict[str, Any]]:
        query_vec = self.embedding_service.encode(query)
        raw_results = self.vector_store.search(query_vec, top_k=top_k, threshold=threshold)
        
        # Format sources
        sources = []
        for r in raw_results:
            sources.append({
                "chunk_id": r.get("chunk_id", ""),
                "document": r.get("filename", ""),
                "page": r.get("page", 1),
                "score": r.get("score", 0.0),
                "text": r.get("text", "")
            })
        return sources
