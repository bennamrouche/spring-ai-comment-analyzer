package net.alphaben.commentanalyzer.ai;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CommentAnalyzerServiceTest {

    @Autowired
    private CommentAnalyzerService analyzer;

    @Test
    void shouldClassifyPositiveMessage() {
        var result = analyzer.analyze("I really love this application!");

        assertThat(result).isNotNull();
        assertThat(result.classification())
                .isEqualTo(Classification.POSITIVE);

        assertThat(result.confidence())
                .isBetween(0.0, 1.0);

        assertThat(result.reason())
                .isNotBlank();
    }
}