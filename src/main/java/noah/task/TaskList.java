package noah.task;

import java.util.ArrayList;
import java.util.List;

import noah.exception.NoahException;

/**
 * Manages tasks and validates the task numbers displayed to the user.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(List.of());
    }

    /**
     * Creates a task list with the supplied tasks in their current order.
     * Copies the list so later changes to the supplied list do not add or
     * remove tasks here.
     *
     * @param tasks Tasks loaded from storage.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes the task selected by its displayed number.
     *
     * @param taskNumber One-based task number shown to the user.
     * @return Task that was removed.
     * @throws NoahException If the task number is invalid.
     */
    public Task delete(int taskNumber) throws NoahException {
        return tasks.remove(toIndex(taskNumber));
    }

    /**
     * Marks the selected task as done.
     *
     * @param taskNumber One-based task number shown to the user.
     * @return Task after its completion status was updated.
     * @throws NoahException If the task number is invalid.
     */
    public Task mark(int taskNumber) throws NoahException {
        Task task = tasks.get(toIndex(taskNumber));
        task.markAsDone();
        return task;
    }

    /**
     * Marks the selected task as not done.
     *
     * @param taskNumber One-based task number shown to the user.
     * @return Task after its completion status was updated.
     * @throws NoahException If the task number is invalid.
     */
    public Task unmark(int taskNumber) throws NoahException {
        Task task = tasks.get(toIndex(taskNumber));
        task.unmarkAsDone();
        return task;
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return Current task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a snapshot for display or saving whose list cannot be modified.
     * The task objects themselves are shared with this task list.
     *
     * @return Tasks in their displayed order.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Converts a displayed task number to a valid list index.
     *
     * @param taskNumber One-based task number shown to the user.
     * @return Zero-based index of the selected task.
     * @throws NoahException If the task number is non-positive or out of range.
     */
    private int toIndex(int taskNumber) throws NoahException {
        if (taskNumber <= 0) {
            throw new NoahException("Oops, task numbers start at 1! Use a whole number from list.");
        }
        if (taskNumber > tasks.size()) {
            throw new NoahException("No task numbered " + taskNumber
                    + " on the board! Type list to check your task numbers.");
        }
        return taskNumber - 1;
    }
}
