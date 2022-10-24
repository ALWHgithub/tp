package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CATEGORY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PERSON;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRIORITY;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_TASKS;

import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.task.Description;
import seedu.address.model.task.Priority;
import seedu.address.model.task.Task;
import seedu.address.model.task.TaskCategory;
import seedu.address.model.task.TaskDeadline;
import seedu.address.model.task.TaskName;


/**
 * Adds a Task in the address book.
 */
public class EditTaskCommand extends Command {
    public static final String COMMAND_WORD = "editTask";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the task identified "
            + "by the index number used in the displayed task list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_DESCRIPTION + "DESCRIPTION] "
            + "[" + PREFIX_PRIORITY + "PRIORITY (low/medium/high)] "
            + "[" + PREFIX_CATEGORY + "CATEGORY (database/frontend/backend/uiux/presentation/others)] "
            + "[" + PREFIX_DEADLINE + "DEADLINE (YYYY-MM-DD)] "
            + "[" + PREFIX_PERSON + "PERSON EMAIL ADDRESS (must be a valid email)] "
            + "[" + PREFIX_DONE + "DONE (t/f)]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_NAME + "Create Initial UIUX Design"
            + PREFIX_DEADLINE + "2023-01-01";

    public static final String MESSAGE_EDIT_TASK_SUCCESS = "Edited Task: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists in the address book.";

    private static final Name NO_PERSON_ASSIGNED = new Name("No person currently assigned");
    private final Index index;
    private final EditTaskCommand.EditTaskDescriptor editTaskDescriptor;

    /**
     * @param index              of the task in the filtered task list to edit
     * @param editTaskDescriptor details to edit the task with
     */
    public EditTaskCommand(Index index, EditTaskCommand.EditTaskDescriptor editTaskDescriptor) {
        requireNonNull(index);
        requireNonNull(editTaskDescriptor);

        this.index = index;
        this.editTaskDescriptor = new EditTaskCommand.EditTaskDescriptor(editTaskDescriptor);
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Task createEditedTask(Task taskToEdit, EditTaskCommand.EditTaskDescriptor editTaskDescriptor) {
        assert editTaskDescriptor != null;

        TaskName updatedName = editTaskDescriptor.getName().orElse(taskToEdit.getName());
        TaskCategory updatedCategory = editTaskDescriptor.getCategory().orElse(taskToEdit.getCategory());
        Description updatedDescription = editTaskDescriptor.getDescription().orElse(taskToEdit.getDescription());
        Priority updatedPriority = editTaskDescriptor.getPriority().orElse(taskToEdit.getPriority());
        TaskDeadline updatedDeadline = editTaskDescriptor.getDeadline().orElse(taskToEdit.getDeadline());
        Person updatedPerson = editTaskDescriptor.getPerson()
                .orElse(taskToEdit.getPerson());
        Boolean updatedIsDone = editTaskDescriptor.getIsDone().orElse(taskToEdit.isDone());

        return new Task(updatedName, updatedDescription, updatedPriority, updatedCategory, updatedDeadline,
                updatedPerson, updatedIsDone);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Task> lastShownList = model.getFilteredTaskList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }

        Task taskToEdit = lastShownList.get(index.getZeroBased());
        Task editedTask = createEditedTask(taskToEdit, editTaskDescriptor);

        if (!taskToEdit.isSameTask(editedTask) && model.hasTask(editedTask)) {
            throw new CommandException(MESSAGE_DUPLICATE_TASK);
        }

        model.setTask(taskToEdit, editedTask);
        model.updateFilteredTaskList(PREDICATE_SHOW_ALL_TASKS);
        model.update();
        return new CommandResult(String.format(MESSAGE_EDIT_TASK_SUCCESS, editedTask));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditTaskCommand)) {
            return false;
        }

        // state check
        EditTaskCommand e = (EditTaskCommand) other;
        return index.equals(e.index)
                && editTaskDescriptor.equals(e.editTaskDescriptor);
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditTaskDescriptor {
        // Identity fields
        private TaskName name;
        private TaskCategory category;
        private Description description;

        // Data fields
        private Priority priority;
        private TaskDeadline deadline;
        private Person person;
        private boolean isDone;

        private Name personName;

        public EditTaskDescriptor() {
        }

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditTaskDescriptor(EditTaskCommand.EditTaskDescriptor toCopy) {
            setName(toCopy.name);
            setCategory(toCopy.category);
            setDescription(toCopy.description);
            setPriority(toCopy.priority);
            setDeadline(toCopy.deadline);
            setPerson(toCopy.person);
            setDone(toCopy.isDone);
            setPersonName(toCopy.personName);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, category, description, priority,
                    deadline, person, isDone);
        }

        public Optional<TaskName> getName() {
            return Optional.ofNullable(name);
        }

        public void setName(TaskName name) {
            this.name = name;
        }

        public Optional<TaskCategory> getCategory() {
            return ofNullable(category);
        }

        public void setCategory(TaskCategory category) {
            this.category = category;
        }

        public Optional<Description> getDescription() {
            return ofNullable(description);
        }

        public void setDescription(Description description) {
            this.description = description;
        }

        public Optional<Priority> getPriority() {
            return ofNullable(priority);
        }

        public void setPriority(Priority priority) {
            this.priority = priority;
        }

        public Optional<TaskDeadline> getDeadline() {
            return ofNullable(deadline);
        }

        public void setDeadline(TaskDeadline deadline) {
            this.deadline = deadline;
        }

        public void setPerson(Person person) {
            this.person = person;
        }

        public Optional<Person> getPerson() {
            return ofNullable(person);
        }

        public void setDone(boolean isDone) {
            this.isDone = isDone;
        }

        public Optional<Boolean> getIsDone() {
            return Optional.of(isDone);
        }

        public void setPersonName(Name personName) {
            this.personName = personName;
        }

        public Optional<Name> getpersonName() {
            return Optional.of(personName);
        }


        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditTaskCommand.EditTaskDescriptor)) {
                return false;
            }

            // state check
            EditTaskCommand.EditTaskDescriptor e = (EditTaskCommand.EditTaskDescriptor) other;

            return getName().equals(e.getName())
                    && getCategory().equals(e.getCategory())
                    && getDescription().equals(e.getDescription())
                    && getPriority().equals(e.getPriority())
                    && getDeadline().equals(e.getDeadline())
                    && getPerson().equals(e.getPerson())
                    && getIsDone().equals(e.getIsDone());
        }
    }
}
