package net.alphaben.commentanalyzer.model;


import net.alphaben.commentanalyzer.ai.AnalysisResult;

public record MessageAnalysis(
        String message,
        AnalysisResult result
) {
}
