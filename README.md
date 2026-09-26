# AI Comment Analyzer

A simple desktop application built with **Java 21, Spring Boot, Spring AI, Ollama, and JavaFX**.

The application analyzes comments or messages and classifies each one as:

- Positive
- Neutral
- Negative
- Toxic

It was created as a small project to explore how Spring AI can be integrated into a Java desktop application.

## Features

- Analyze multiple messages at once
- One message per line
- Local AI inference using Ollama
- Structured AI responses
- Four classifications:
  - `POSITIVE`
  - `NEUTRAL`
  - `NEGATIVE`
  - `TOXIC`
- Confidence score
- Short explanation for each result
- Summary counters
- Colored classification labels
- Concurrent processing using `ExecutorService`
- Maximum of 2 AI requests processed at the same time
- External system prompt loaded from `resources/prompts`

## Tech Stack

- Java 21
- Spring Boot
- Spring AI
- Ollama
- JavaFX
- Gradle
- FXML
- CSS
- JUnit

## Architecture

```text
JavaFX UI
    |
    v
MainController
    |
    v
ExecutorService
    |
    v
CommentAnalyzerService
    |
    v
Spring AI ChatClient
    |
    v
Ollama
    |
    v
Local AI Model
```

The JavaFX controller does not communicate directly with Ollama.

All AI logic is handled by `CommentAnalyzerService`.

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── net/alphaben/commentanalyzer/
│   │       ├── ai/
│   │       │   ├── AnalysisResult.java
│   │       │   ├── Classification.java
│   │       │   └── CommentAnalyzerService.java
│   │       │
│   │       └── ui/
│   │           ├── MainController.java
│   │           └── MessageAnalysis.java
│   │
│   └── resources/
│       ├── application.yml
│       ├── prompts/
│       │   └── comment-classifier.st
│       ├── fxml/
│       │   └── main-view.fxml
│       └── css/
│           └── app.css
│
└── test/
    └── java/
        └── ...
```

## Requirements

Before running the application, install:

- Java 21
- Ollama
- Gradle or the included Gradle Wrapper

Check your Java version:

```bash
java --version
```

## Install Ollama

Install Ollama from its official website.

Then verify that it is running:

```bash
ollama --version
```

## Download a Model

For example:

```bash
ollama pull qwen3:4b
```

Check installed models:

```bash
ollama list
```

## Configuration

Configure Spring AI in:

```text
src/main/resources/application.yml
```

Example:

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434

      chat:
        model: qwen3:4b

        options:
          temperature: 0
```

A low temperature is used because this project is focused on classification rather than creative text generation.

## System Prompt

The AI system prompt is stored separately:

```text
src/main/resources/prompts/comment-classifier.st
```

This keeps prompt engineering separate from the Java code.

The model is instructed to classify messages into exactly one of:

```text
POSITIVE
NEUTRAL
NEGATIVE
TOXIC
```

It also returns:

```json
{
  "classification": "POSITIVE",
  "confidence": 0.97,
  "reason": "Expresses appreciation and positive sentiment."
}
```

Spring AI converts this structured response directly into a Java record.

## Running the Application

Make sure Ollama is running:

```bash
ollama serve
```

Then start the application:

```bash
./gradlew run
```

Depending on the Gradle configuration, you may also run it directly from IntelliJ IDEA.

## Usage

Enter one message per line:

```text
I really love this application!
The meeting starts at 10 AM.
I don't like this update.
You're completely useless and an idiot.
```

Then click:

```text
Analyze All
```

Expected classifications:

```text
POSITIVE
NEUTRAL
NEGATIVE
TOXIC
```

## Arabic Support

The application can also be tested with Arabic:

```text
هذا التطبيق رائع جدًا
الاجتماع سيبدأ الساعة العاشرة
هذا التحديث سيئ ولم يعجبني
أنت غبي ولا تفهم أي شيء
```

Or Moroccan Darija:

```text
هاد التطبيق زوين بزاف
الاجتماع غدا مع العشرة
هاد التحديث خايب وما عجبنيش
نتا غبي وما كاتفهم والو
```

Actual classification quality depends on the selected Ollama model.

## Concurrent Processing

Messages are processed using an `ExecutorService`:

```java
Executors.newFixedThreadPool(2);
```

This means a maximum of two messages are sent for AI analysis at the same time.

For example:

```text
Thread 1 -> Message 1 -> Message 3 -> Message 5

Thread 2 -> Message 2 -> Message 4 -> Message 6
```

This avoids sending too many concurrent requests to the local Ollama model.

## Testing

The AI service can be tested using Spring Boot integration tests.

Example:

```java
@SpringBootTest
class CommentAnalyzerServiceTest {

    @Autowired
    private CommentAnalyzerService analyzer;

    @Test
    void shouldClassifyPositiveMessage() {
        var result = analyzer.analyze(
                "I really love this application!"
        );

        assertThat(result.classification())
                .isEqualTo(Classification.POSITIVE);
    }
}
```

Run tests with:

```bash
./gradlew test
```

Tests that call the real AI model require Ollama to be running.

## Why This Project?

This project was mainly created to learn and experiment with:

- Spring AI
- local LLMs
- structured AI output
- prompt engineering
- JavaFX with Spring
- concurrency in Java 21
- integrating AI into traditional Java applications

## Future Improvements

Possible next steps:

- Better Arabic and Darija classification
- Dedicated sentiment and toxicity models
- Batch analysis
- CSV import/export
- Analysis history
- Charts and statistics
- Model selection from the UI
- Performance comparison between Ollama models
- ONNX or DJL-based classifiers
- RAG experiments with Spring AI

## License

This project is intended for learning and experimentation.
