/**
 * Final interview report PDF generation (charts, radar, timeline, AI summary).
 * Reads the aggregated {@code interview_reports} row built by
 * {@link com.interviewiq.evaluation} and renders it to the {@code pdf_url} artifact.
 * See docs/API_DESIGN.md §5 ({@code GET /interviews/{id}/report/pdf}).
 */
package com.interviewiq.report;
