import numpy as np
from typing import List, Union
from sklearn.feature_extraction.text import TfidfVectorizer

class EmbeddingService:
    """Provides dense embeddings for semantic retrieval with sentence-transformers or high-performance vector fallback."""
    
    def __init__(self, model_name: str = "all-MiniLM-L6-v2"):
        self.model_name = model_name
        self.st_model = None
        self.fallback_vectorizer = None
        self._init_model()

    def _init_model(self):
        try:
            from sentence_transformers import SentenceTransformer
            # Try loading sentence-transformers
            self.st_model = SentenceTransformer(self.model_name)
            print(f"[EmbeddingService] Loaded SentenceTransformer: {self.model_name}")
        except Exception as e:
            print(f"[EmbeddingService] Note: Using robust TF-IDF dense vectorizer ({e})")
            self.st_model = None
            self.fallback_vectorizer = TfidfVectorizer(
                ngram_range=(1, 2),
                max_features=384,
                sublinear_tf=True
            )

    def fit_corpus(self, corpus_texts: List[str]):
        """Fits fallback vectorizer if active."""
        if self.st_model is None and self.fallback_vectorizer is not None:
            if corpus_texts:
                self.fallback_vectorizer.fit(corpus_texts)

    def encode(self, texts: Union[str, List[str]]) -> np.ndarray:
        """Encodes string or list of strings into normalized L2 float32 vectors."""
        is_single = isinstance(texts, str)
        text_list = [texts] if is_single else texts
        
        if not text_list:
            return np.zeros((0, 384), dtype=np.float32)

        if self.st_model is not None:
            try:
                embeddings = self.st_model.encode(
                    text_list,
                    convert_to_numpy=True,
                    normalize_embeddings=True
                )
                return embeddings[0] if is_single else embeddings
            except Exception as e:
                print(f"[EmbeddingService] ST encode error ({e}), switching to dense fallback.")
                self.st_model = None

        # Fallback dense encoder
        if self.fallback_vectorizer is None or not hasattr(self.fallback_vectorizer, "vocabulary_"):
            self.fallback_vectorizer = TfidfVectorizer(
                ngram_range=(1, 2),
                max_features=384,
                sublinear_tf=True
            )
            self.fallback_vectorizer.fit(text_list + ["machine learning artificial intelligence climate crispr quantum"])

        sparse = self.fallback_vectorizer.transform(text_list)
        dense = sparse.toarray().astype(np.float32)
        
        # Pad or slice to 384 dims
        if dense.shape[1] < 384:
            padded = np.zeros((dense.shape[0], 384), dtype=np.float32)
            padded[:, :dense.shape[1]] = dense
            dense = padded
        elif dense.shape[1] > 384:
            dense = dense[:, :384]

        # L2 normalize
        norms = np.linalg.norm(dense, axis=1, keepdims=True)
        norms[norms == 0] = 1.0
        normalized = dense / norms
        
        return normalized[0] if is_single else normalized
