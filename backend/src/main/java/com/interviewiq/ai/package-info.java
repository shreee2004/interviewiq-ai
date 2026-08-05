/**
 * The only package permitted to call external LLM providers (OpenAI/Gemini). Exposes
 * {@code AiOrchestrationService} as the sole entry point every other feature package
 * depends on — never an HTTP client directly — so provider swaps, retries, timeouts,
 * and structured-output parsing live in exactly one place. Also owns
 * {@code api_usage_logs} cost/latency tracking (docs/DATABASE.md §2.10).
 *
 * See docs/ARCHITECTURE.md §3 ("Rules") and §5 for why this boundary exists.
 */
package com.interviewiq.ai;
