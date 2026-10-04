import os
from pathlib import Path
from typing import List, Dict, Any

class DocumentLoader:
    """Loads and extracts text and metadata from documents (.txt, .pdf, .md)."""
    
    def __init__(self, data_dir: Path):
        self.data_dir = data_dir
        
    def load_documents(self) -> List[Dict[str, Any]]:
        documents = []
        if not self.data_dir.exists():
            return documents
            
        for file_path in self.data_dir.glob("*.*"):
            ext = file_path.suffix.lower()
            if ext in [".txt", ".md"]:
                doc = self._load_text_file(file_path)
                if doc:
                    documents.append(doc)
            elif ext == ".pdf":
                pdf_docs = self._load_pdf_file(file_path)
                documents.extend(pdf_docs)
                
        return documents

    def _load_text_file(self, file_path: Path) -> Dict[str, Any]:
        try:
            with open(file_path, "r", encoding="utf-8", errors="ignore") as f:
                content = f.read().strip()
                
            return {
                "document_id": f"doc_{file_path.stem}",
                "filename": file_path.name,
                "file_type": file_path.suffix,
                "text": content,
                "pages": 1,
                "sections": self._extract_sections(content)
            }
        except Exception as e:
            print(f"Error loading text file {file_path}: {e}")
            return None

    def _load_pdf_file(self, file_path: Path) -> List[Dict[str, Any]]:
        docs = []
        try:
            import fitz  # PyMuPDF
            doc = fitz.open(file_path)
            for page_num in range(len(doc)):
                page = doc[page_num]
                text = page.get_text().strip()
                if text:
                    docs.append({
                        "document_id": f"doc_{file_path.stem}_p{page_num + 1}",
                        "filename": file_path.name,
                        "file_type": ".pdf",
                        "text": text,
                        "page": page_num + 1,
                        "sections": []
                    })
        except Exception as e:
            print(f"Error loading PDF {file_path} (PyMuPDF fallback): {e}")
        return docs

    def _extract_sections(self, content: str) -> List[str]:
        lines = content.split("\n")
        sections = []
        for line in lines:
            line = line.strip()
            if line.startswith("---") or line.startswith("###") or line.startswith("#"):
                clean = line.replace("-", "").replace("#", "").strip()
                if clean:
                    sections.append(clean)
        return sections
