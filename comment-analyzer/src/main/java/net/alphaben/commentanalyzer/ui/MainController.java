package net.alphaben.commentanalyzer.ui;

import jakarta.annotation.PreDestroy;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import net.alphaben.commentanalyzer.ai.AnalysisResult;
import net.alphaben.commentanalyzer.ai.Classification;
import net.alphaben.commentanalyzer.ai.CommentAnalyzerService;
import net.alphaben.commentanalyzer.model.MessageAnalysis;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class MainController implements Initializable {

    private final CommentAnalyzerService analyzerService;

    private final ExecutorService executor =
            Executors.newFixedThreadPool(2);

    @FXML
    private TextArea messageInput;

    @FXML
    private Button analyzeButton;

    @FXML
    private ProgressIndicator loading;

    @FXML
    private ListView<MessageAnalysis> resultsList;

    @FXML
    private Label totalLabel;

    @FXML
    private Label positiveCountLabel;

    @FXML
    private Label neutralCountLabel;

    @FXML
    private Label negativeCountLabel;

    @FXML
    private Label toxicCountLabel;

    public MainController(
            CommentAnalyzerService analyzerService
    ) {
        this.analyzerService = analyzerService;
    }

    @Override
    public void initialize(
            URL location,
            ResourceBundle resources
    ) {
        configureResultList();
        resetSummary();
    }

    @FXML
    private void analyze() {

        List<String> messages = messageInput
                .getText()
                .lines()
                .map(String::trim)
                .filter(message -> !message.isBlank())
                .toList();

        if (messages.isEmpty()) {
            return;
        }

        startLoading();

        resultsList.getItems().clear();
        resetSummary();

        AtomicInteger completed = new AtomicInteger();

        for (String message : messages) {

            executor.submit(() -> {

                try {
                    AnalysisResult result =
                            analyzerService.analyze(message);

                    MessageAnalysis analysis =
                            new MessageAnalysis(
                                    message,
                                    result
                            );

                    Platform.runLater(() -> {
                        addResult(analysis);
                        checkCompleted(
                                completed,
                                messages.size()
                        );
                    });

                } catch (Exception exception) {

                    Platform.runLater(() -> {
                        showError(
                                message,
                                exception
                        );

                        checkCompleted(
                                completed,
                                messages.size()
                        );
                    });
                }
            });
        }
    }

    private void checkCompleted(
            AtomicInteger completed,
            int total
    ) {

        if (completed.incrementAndGet() == total) {
            stopLoading();
        }
    }

    private void addResult(
            MessageAnalysis analysis
    ) {

        resultsList
                .getItems()
                .add(analysis);

        increment(totalLabel);

        switch (analysis.result().classification()) {

            case POSITIVE -> increment(positiveCountLabel);

            case NEUTRAL -> increment(neutralCountLabel);

            case NEGATIVE -> increment(negativeCountLabel);

            case TOXIC -> increment(toxicCountLabel);
        }
    }

    @FXML
    private void clear() {

        messageInput.clear();

        resultsList
                .getItems()
                .clear();

        resetSummary();
    }

    private void startLoading() {

        analyzeButton.setDisable(true);

        loading.setVisible(true);
        loading.setManaged(true);
    }

    private void stopLoading() {

        analyzeButton.setDisable(false);

        loading.setVisible(false);
        loading.setManaged(false);
    }

    private void resetSummary() {

        totalLabel.setText("0");
        positiveCountLabel.setText("0");
        neutralCountLabel.setText("0");
        negativeCountLabel.setText("0");
        toxicCountLabel.setText("0");
    }

    private void increment(Label label) {

        int current =
                Integer.parseInt(
                        label.getText()
                );

        label.setText(
                String.valueOf(current + 1)
        );
    }

    private void showError(
            String message,
            Exception exception
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(
                "Analysis Error"
        );

        alert.setHeaderText(
                "Failed to analyze message"
        );

        alert.setContentText(
                message
                        + "\n\n"
                        + exception.getMessage()
        );

        alert.show();
    }

    private void configureResultList() {

        resultsList.setCellFactory(
                list -> new MessageAnalysisCell()
        );
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
    }

    private static class MessageAnalysisCell
            extends ListCell<MessageAnalysis> {

        @Override
        protected void updateItem(
                MessageAnalysis item,
                boolean empty
        ) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                setTooltip(null);
                return;
            }

            Label messageLabel = new Label(item.message());

            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(Double.MAX_VALUE);

            HBox.setHgrow(
                    messageLabel,
                    Priority.ALWAYS
            );

            Label classificationLabel = new Label(
                    item.result()
                            .classification()
                            .name()
            );

            classificationLabel
                    .getStyleClass()
                    .add("result-classification");

            applyClassificationStyle(
                    classificationLabel,
                    item.result().classification()
            );

            Label confidenceLabel = new Label(
                    "%.0f%%".formatted(
                            item.result()
                                    .confidence()
                                    * 100
                    )
            );

            confidenceLabel
                    .getStyleClass()
                    .add("result-confidence");

            HBox row = new HBox(
                    16,
                    messageLabel,
                    classificationLabel,
                    confidenceLabel
            );

            row.getStyleClass().add("result-row");

            setGraphic(row);

            setTooltip(
                    new Tooltip(
                            item.result().reason()
                    )
            );
        }

        private void applyClassificationStyle(
                Label label,
                Classification classification
        ) {
            String styleClass = switch (classification) {
                case POSITIVE -> "class-positive";
                case NEUTRAL -> "class-neutral";
                case NEGATIVE -> "class-negative";
                case TOXIC -> "class-toxic";
            };

            label.getStyleClass().add(styleClass);
        }
    }

}