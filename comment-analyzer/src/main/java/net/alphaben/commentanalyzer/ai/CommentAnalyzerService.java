package net.alphaben.commentanalyzer.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class CommentAnalyzerService {

    private final ChatClient chatClient;
    private final Resource systemPrompt;

    public CommentAnalyzerService(
            ChatClient.Builder builder,
            @Value("classpath:/prompts/comment-classifier.st")
            Resource systemPrompt
    ) {
        this.chatClient = builder.build();
        this.systemPrompt = systemPrompt;
    }

    public AnalysisResult analyze(String message) {

        var systemMessage =
                new SystemPromptTemplate(systemPrompt)
                        .createMessage();

        return chatClient
                .prompt()
                .messages(systemMessage)
                .user(message)
                .call()
                .entity(AnalysisResult.class);
    }
}
