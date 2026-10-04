import numpy as np
from typing import List, Dict, Any, Tuple

class VectorStore:
    """Vector store for chunk embeddings and metadata retrieval."""
    
    def __init__(self, dimension: int = 384):
        self.dimension = dimension
        self.chunks: List[Dict[str, Any]] = []
        self.embeddings: np.ndarray = np.zeros((0, dimension), dtype=np.float32)
        self.faiss_index = None
        self._init_faiss()

    def _init_faiss(self):
        try:
            import faiss
            self.faiss_index = faiss.IndexFlatIP(self.dimension)  # Inner product for normalized vectors = cosine sim
        except Exception:
            self.faiss_index = None

    def add_chunks(self, chunks: List[Dict[str, Any]], embeddings: np.ndarray):
        """Adds chunks and their embeddings to the vector store."""
        if len(chunks) == 0:
            return
            
        self.chunks = chunks
        self.embeddings = embeddings.astype(np.float32)
        
        if self.faiss_index is not None:
            try:
                import faiss
                self.faiss_index = faiss.IndexFlatIP(self.dimension)
                self.faiss_index.add(self.embeddings)
            except Exception:
                self.faiss_index = None

    def search(self, query_embedding: np.ndarray, top_k: int = 4, threshold: float = 0.0) -> List[Dict[str, Any]]:
        """Searches for top_k most similar chunks to the query vector."""
        if len(self.chunks) == 0:
            return []
            
        top_k = min(top_k, len(self.chunks))
        q_vec = query_embedding.astype(np.float32).reshape(1, -1)
        
        if self.faiss_index is not None:
            try:
                scores, indices = self.faiss_index.search(q_vec, top_k)
                results = []
                for score, idx in zip(scores[0], indices[0]):
                    if idx >= 0 and idx < len(self.chunks):
                        sim = float(score)
                        # Ensure score is within [0.0, 1.0]
                        sim = max(0.0, min(1.0, (sim + 1.0) / 2.0 if sim < 0 else sim))
                        if sim >= threshold:
                            chunk_data = dict(self.chunks[idx])
                            chunk_data["score"] = round(sim, 4)
                            results.append(chunk_data)
                return results
            except Exception as e:
                print(f"[VectorStore] FAISS search error ({e}), falling back to NumPy")

        # NumPy cosine similarity
        dot_products = np.dot(self.embeddings, q_vec.T).flatten()
        top_indices = np.argsort(dot_products)[::-1][:top_k]
        
        results = []
        for idx in top_indices:
            sim = float(dot_products[idx])
            sim = max(0.0, min(1.0, (sim + 1.0) / 2.0 if sim < 0 else sim))
            if sim >= threshold:
                chunk_data = dict(self.chunks[idx])
                chunk_data["score"] = round(sim, 4)
                results.append(chunk_data)
                
        return results

    def get_stats(self) -> Dict[str, Any]:
        unique_docs = len(set(c["filename"] for c in self.chunks))
        return {
            "total_chunks": len(self.chunks),
            "unique_documents": unique_docs,
            "dimension": self.dimension,
            "engine": "FAISS-InnerProduct" if self.faiss_index else "NumPy-CosineSimilarity"
        }
