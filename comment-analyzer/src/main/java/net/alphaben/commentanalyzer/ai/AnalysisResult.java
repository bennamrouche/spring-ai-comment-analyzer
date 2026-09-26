package net.alphaben.commentanalyzer.ai;


public record AnalysisResult(
        Classification classification,
        double confidence,
        String reason
) {
}