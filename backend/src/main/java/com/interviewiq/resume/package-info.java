/**
 * Resume upload, parsing, and AI-driven analysis (score, ATS score, missing skills,
 * grammar/improvement suggestions). Owns {@code resumes} and its child tables plus
 * {@code resume_analyses} (docs/DATABASE.md §2.4). Delegates all LLM calls to
 * {@link com.interviewiq.ai}. See docs/API_DESIGN.md §4.
 */
package com.interviewiq.resume;
