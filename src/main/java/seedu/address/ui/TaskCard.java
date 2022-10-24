package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.task.Task;


/**
 * An UI component that displays information of a {@code Person}.
 */
public class TaskCard extends UiPart<Region> {

    private static final String FXML = "TaskListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Task task;

    @FXML
    private HBox cardPane;
    @FXML
    private Label taskName;
    @FXML
    private Label id;
    @FXML
    private Label description;
    @FXML
    private Label priority;
    @FXML
    private Label category;
    @FXML
    private Label deadline;
    @FXML
    private Label email;
    @FXML
    private Label status;

    /**
     * Creates a {@code TaskCard} with the given {@code Task} and index to display.
     */
    public TaskCard(Task task, int displayedIndex) {
        super(FXML);
        this.task = task;
        id.setText(displayedIndex + ".");
        taskName.setText("Name: " + task.getName().toString());
        description.setText("Description: " + task.getDescription().toString());
        priority.setText("Priority: " + task.getPriority().toString());
        category.setText("Category: " + task.getCategory().toString());
        deadline.setText("Deadline: " + task.getDeadline().toString());
        email.setText("Email: " + task.getPersonEmailAddress().toString());
        status.setText("Status: " + booleanConvertor(task.getStatus()));

    }

    public String booleanConvertor(boolean isDone) {
        return isDone ? "Completed" : "Not Completed";
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonCard)) {
            return false;
        }

        // state check
        TaskCard card = (TaskCard) other;
        return id.getText().equals(card.id.getText())
                && task.equals(card.task);
    }
}
