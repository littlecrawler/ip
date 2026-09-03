package noah;

/**
 * Represents a task that can be marked as done or not done.
 */
public class Event extends Task {
    protected String from;
    protected String to;

    /**
     * Creates a task with the given description.
     * New tasks are not done by default.
     *
     * @param description Description of the task.
     */
    public Event(String description) {
        super(description);
    }

    /**
     * Creates a task with the given description and period.
     * New tasks are not done by default.
     *
     * @param description Description of the task.
     * @param from Start time.
     * @param to End time.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the task in the format used when displaying the task list.
     *
     * @return Task status followed by its description.
     */
    @Override
    public String toString() {
        if (from != null && to != null) {
            return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
        }
        return "[E]" + super.toString();
    }
}
