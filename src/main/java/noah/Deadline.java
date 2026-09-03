package noah;

/**
 * Represents a task that can be marked as done or not done.
 */
public class Deadline extends Task {
    protected String by;

    /**
     * Creates a task with the given description.
     * New tasks are not done by default.
     *
     * @param description Description of the task.
     */
    public Deadline(String description) {
        super(description);
    }

    /**
     * Creates a task with the given description and due date.
     * New tasks are not done by default.
     *
     * @param description Description of the task.
     * @param by The due date of the task.
     */
    public Deadline(String description, String by) {
        this(description);
        this.by = by;
    }

    /**
     * Returns the task in the format used when displaying the task list.
     *
     * @return Task status followed by its description.
     */
    @Override
    public String toString() {
        if (by != null) {
            return "[D]" + super.toString() + " (by: " + by + ")";
        }
        return "[D]" + super.toString();
    }
}
