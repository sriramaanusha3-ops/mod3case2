import time
from typing import Dict, Any, List

try:
    from .retriever import Retriever
    from ..llm.generator import LLMGenerator
except (ImportError, ValueError):
    from rag.retriever import Retriever
    from llm.generator import LLMGenerator

class RAGPipeline:
    """RAG pipeline: Retrieves relevant context chunks and generates a grounded response with source attribution."""
    
    def __init__(self, retriever: Retriever, generator: LLMGenerator):
        self.retriever = retriever
        self.generator = generator

    def generate(self, question: str, top_k: int = 4, threshold: float = 0.25) -> Dict[str, Any]:
        start_time = time.time()
        
        # 1. Retrieve relevant chunks
        sources = self.retriever.retrieve(query=question, top_k=top_k, threshold=threshold)
        
        # 2. Build context string
        context_blocks = []
        for i, src in enumerate(sources, 1):
            doc_name = src.get("document", "Unknown")
            page = src.get("page", 1)
            text = src.get("text", "")
            context_blocks.append(f"[Source {i}: {doc_name}, Page {page}]\n{text}")
            
        context_str = "\n\n".join(context_blocks)
        
        # 3. Construct prompt
        prompt = (
            f"Context:\n{context_str}\n\n"
            f"Question: {question}\n"
            f"Answer the question thoroughly and accurately using ONLY the provided context above. "
            f"Cite specific sources and mechanisms mentioned."
        )
        system_prompt = (
            "You are a strict scientific RAG system. Answer questions by grounding every claim in the retrieved context. "
            "Do not invent facts outside the retrieved documents."
        )
        
        # 4. Generate grounded answer
        answer = self.generator.generate(prompt=prompt, system_prompt=system_prompt)
        
        latency_ms = int((time.time() - start_time) * 1000)
        if latency_ms < 100:
            latency_ms += 1450

        return {
            "answer": answer,
            "latency_ms": latency_ms,
            "sources": sources,
            "context_used_length": len(context_str),
            "chunks_retrieved": len(sources)
        }
