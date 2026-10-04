import re
from typing import List, Dict, Any

class TextChunker:
    """Chunks documents into overlapping or section-aware chunks for vector indexing."""
    
    def __init__(self, chunk_size: int = 300, chunk_overlap: int = 50):
        self.chunk_size = chunk_size
        self.chunk_overlap = chunk_overlap

    def chunk_documents(self, documents: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        all_chunks = []
        chunk_idx = 0
        
        for doc in documents:
            text = doc["text"]
            filename = doc["filename"]
            base_page = doc.get("page", 1)
            
            # Split into paragraphs / logical sections first
            sections = re.split(r'\n(?=--- SECTION|\n# |\n## |\n\n)', text)
            
            for s_idx, section in enumerate(sections):
                section_clean = section.strip()
                if not section_clean:
                    continue
                    
                words = section_clean.split()
                if len(words) <= self.chunk_size:
                    # Fits in one chunk
                    chunk_idx += 1
                    all_chunks.append({
                        "chunk_id": f"chunk_{chunk_idx:04d}",
                        "document_id": doc["document_id"],
                        "filename": filename,
                        "page": base_page + (s_idx // 2),
                        "text": section_clean,
                        "word_count": len(words),
                        "char_count": len(section_clean)
                    })
                else:
                    # Slide over section
                    step = max(1, self.chunk_size - self.chunk_overlap)
                    for start in range(0, len(words), step):
                        chunk_words = words[start:start + self.chunk_size]
                        if not chunk_words:
                            break
                        chunk_text = " ".join(chunk_words)
                        chunk_idx += 1
                        all_chunks.append({
                            "chunk_id": f"chunk_{chunk_idx:04d}",
                            "document_id": doc["document_id"],
                            "filename": filename,
                            "page": base_page + (s_idx // 2),
                            "text": chunk_text,
                            "word_count": len(chunk_words),
                            "char_count": len(chunk_text)
                        })
                        if start + self.chunk_size >= len(words):
                            break
                            
        return all_chunks
