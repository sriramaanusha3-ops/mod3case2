# mod3case2 — AI Evaluation Lab
### LLM vs RAG • Factual Accuracy Benchmark

An academic and experimental comparison laboratory comparing **Plain Large Language Models (LLM)** against **Retrieval-Augmented Generation (RAG)** systems on knowledge-heavy questions across multiple technical domains.

---

## 1. System Architecture

```text
                           SAME QUESTION
                                │
             ┌──────────────────┴──────────────────┐
             ↓                                     ↓
        PLAIN LLM                                 RAG
             ↓                                     ↓
       Direct Answer                        Query Embedding
     (Parametric Memory)                           ↓
                                             Vector Search
                                                   ↓
                                           Relevant Chunks
                                                   ↓
                                            LLM + Context
                                                   ↓
                                            Grounded Answer
             └──────────────────┬──────────────────┘
                                ↓
                           EVALUATION
                                ↓
                • Factual Accuracy
                • Semantic Groundedness
                • Source Support Rating
                • Hallucination Heuristic
                • End-to-End Latency
```

---

## 2. Core Comparison Principles & Fairness Protocol

1. **Fair Comparison Baseline**: The exact same question, underlying LLM engine, and temperature parameters are applied to both pipelines.
2. **The Only Difference**:
   - **System A (Plain LLM)**: Zero retrieved context; generates purely from learned parametric weights.
   - **System B (RAG)**: Encodes the question into a 384-dimensional vector, searches the FAISS/vector store, retrieves top-k chunks with page attribution, and feeds this verified evidence into the LLM context.
3. **Explicit Evaluation Methodology**:
   - **Automated Reference Benchmark**: Evaluates outputs against structured ground-truth facts and reference documents (`benchmark_questions.json`).
   - **Manual Expert Review**: Evaluators can record judgments (`Correct`, `Partially Correct`, `Incorrect`, `Not Enough Evidence`) and store them in the persistent experiment database.

---

## 3. Technology Stack

### Android Client
- **Language**: Kotlin 2.0+
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **UI Framework**: Native XML Layouts + ViewBinding + Material Components (No Compose)
- **Theme**: *AI Benchmark — Midnight Lab* (`#050812` background, purple/cyan glowing accents)
- **Networking**: Retrofit 2 + OkHttp 4 + Logging Interceptor + Coroutines
- **Data Serialization**: Gson

### Python FastAPI Backend
- **Framework**: FastAPI + Uvicorn
- **Embedding Engine**: `sentence-transformers/all-MiniLM-L6-v2` + robust TF-IDF fallback
- **Vector Search**: FAISS Inner-Product / Cosine Similarity Index
- **Document Ingestion**: Multi-format loader (PDF via PyMuPDF, TXT, MD) + semantic section-aware chunker
- **Generative Synthesis**: Google Gemini API / OpenAI API / offline neural synthesizer

---

## 4. Backend Setup & Quickstart

### Prerequisites
- Python 3.10+
- Android Studio Ladybug or newer

### Setup Steps

1. Open a terminal in the `backend/` directory:
   ```bash
   cd backend
   ```

2. Create and activate a virtual environment:
   ```bash
   # Windows PowerShell
   python -m venv venv
   .\venv\Scripts\Activate.ps1
   ```

3. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```

4. Configure environment (optional, built-in offline engine runs automatically):
   ```bash
   cp .env.example .env
   # Add your GEMINI_API_KEY or OPENAI_API_KEY if desired
   ```

5. Launch the FastAPI server:
   ```bash
   uvicorn app:app --host 0.0.0.0 --port 8000
   ```

6. Verify server health:
   - Open [http://localhost:8000/](http://localhost:8000/) or [http://localhost:8000/docs](http://localhost:8000/docs)

---

## 5. Android Emulator Networking

For Android emulators, `http://10.0.2.2:8000/` routes directly to the host machine's `localhost:8000`. This is configured by default in `ApiClient.kt` and editable in the **Settings** tab with a live connection tester.

---

## 6. API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/` | Root server status and vector store indexing statistics |
| `GET` | `/health` | Live subsystem health check |
| `POST` | `/compare` | Executes simultaneous Plain LLM vs RAG comparison and evaluation |
| `GET` | `/benchmarks` | Returns 10 multi-domain knowledge-heavy benchmark questions |
| `POST` | `/benchmarks/run` | Executes complete batch benchmark suite with academic metrics |
| `GET` | `/metrics` | Returns aggregate accuracy, groundedness, and latency statistics |
| `POST` | `/evaluate` | Records human reviewer evaluation judgments |
| `GET` | `/history` | Fetches persistent experiment history |
| `DELETE` | `/history` | Clears experiment history |
| `POST` | `/demo` | Runs fast demonstration experiment on sample benchmark questions |

---

## 7. Knowledge Base Corpus

The backend indexes realistic educational documents covering:
1. `Machine_Learning_Fundamentals.txt`
2. `CRISPR_and_Gene_Editing.txt`
3. `The_2008_Financial_Crisis.txt`
4. `Climate_Science_and_Global_Warming.txt`
5. `Quantum_Computing_Principles.txt`
6. `Deep_Learning_and_Neural_Networks.txt`
7. `Cybersecurity_and_Zero_Trust.txt`
8. `Distributed_Database_Systems.txt`
9. `Artificial_Intelligence_Concepts.txt`
10. `Cloud_Computing_Architectures.txt`

---

## 8. Academic Limitations

For academic credibility:
- **Retrieval Dependency**: RAG output quality is bounded by the relevance and quality of the retrieved chunks.
- **Latency Trade-Off**: RAG introduces 200–500ms of additional latency due to query vectorization and chunk ranking.
- **Heuristic Factuality**: Automated semantic similarity and claim heuristics approximate grounding rather than providing mathematical proof of factuality.
