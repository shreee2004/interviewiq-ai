/**
 * The core product: interview session lifecycle, the live WebSocket question/answer
 * turn loop, and transcript persistence. Owns {@code interview_sessions} and
 * {@code interview_turns} (docs/DATABASE.md §2.5). Delegates evaluation to
 * {@link com.interviewiq.evaluation} and all LLM calls to {@link com.interviewiq.ai}.
 * See docs/ARCHITECTURE.md §5 for the full turn-by-turn sequence diagram and
 * docs/API_DESIGN.md §5 for the REST + WebSocket contract.
 */
package com.interviewiq.interview;
