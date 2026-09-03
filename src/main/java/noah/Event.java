package noah;

/**
 * Represents a task that can be marked as done or not done.
 */
public class Event {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a task with the given description.
     * New tasks are not done by default.
     *
     * @param description Description of the task.
     */
    public Event(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void unmarkAsDone() {
        isDone = false;
    }

    /**
     * Returns the symbol used to display the task's completion status.
     *
     * @return {@code X} if the task is done, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task in the format used when displaying the task list.
     *
     * @return Task status followed by its description.
     */
    @Override
    public String toString() {
        if (description.contains(" /from ") && description.contains(" /to ")) {
            String[] split = description.split(" /from | /to ");
            return "[E][" + getStatusIcon() + "] " + split[0] + " (from: " + split[1] + " to: " + split[2] + ")";
        }
        return "[E][" + getStatusIcon() + "] " + description;
    }
}
