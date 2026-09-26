package net.alphaben.commentanalyzer.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.alphaben.commentanalyzer.model.Message;

public class MessageCell extends ListCell<Message> {

    private final Label textLabel = new Label();
    private final Label timeLabel = new Label();
    private final VBox bubble = new VBox(4, textLabel, timeLabel);
    private final HBox wrapper = new HBox(bubble);

    public MessageCell() {
        textLabel.setWrapText(true);
        timeLabel.getStyleClass().add("message-time");
        bubble.getStyleClass().add("message-bubble");
        bubble.setPadding(new Insets(8, 12, 8, 12));
        bubble.setMaxWidth(480);
        wrapper.setPadding(new Insets(4, 8, 4, 8));
    }

    @Override
    protected void updateItem(Message msg, boolean empty) {
        super.updateItem(msg, empty);

        if (empty || msg == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        textLabel.setText(msg.getText());
        timeLabel.setText(msg.getTimestamp().toString());

        // Reset alignment / style class each time (cells are recycled)
        wrapper.setAlignment(msg.getRole() == Message.Role.USER
                ? Pos.CENTER_RIGHT
                : Pos.CENTER_LEFT);

        bubble.getStyleClass().removeAll("message-bubble-user", "message-bubble-ai");
        bubble.getStyleClass().add(msg.getRole() == Message.Role.USER
                ? "message-bubble-user"
                : "message-bubble-ai");

        setText(null);
        setGraphic(wrapper);
    }
}
