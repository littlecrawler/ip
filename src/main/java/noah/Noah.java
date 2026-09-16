package noah;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

import noah.exception.NoahException;
import noah.storage.Storage;
import noah.task.Deadline;
import noah.task.Event;
import noah.task.Task;
import noah.task.Todo;

/**
 * Runs a command-line task manager that stores and updates tasks.
 */
public class Noah {
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DELETE_COMMAND = "delete";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final Path DATA_FILE_PATH = Path.of("data", "noah.txt");
    private static final String SEPARATOR =
            "\n===============================================================================\n";

    // ASCII-art banner generated using http://www.network-science.de/ascii/
    private static final String BANNER = """
            NNNNNNNN        NNNNNNNN                                  hhhhhhh
            N:::::::N       N::::::N                                  h:::::h
            N::::::::N      N::::::N                                  h:::::h
            N:::::::::N     N::::::N                                  h:::::h
            N::::::::::N    N::::::N   ooooooooooo     aaaaaaaaaaaaa   h::::h hhhhh
            N:::::::::::N   N::::::N oo:::::::::::oo   a::::::::::::a  h::::hh:::::hhh
            N:::::::N::::N  N::::::No:::::::::::::::o  aaaaaaaaa:::::a h::::::::::::::hh
            N::::::N N::::N N::::::No:::::ooooo:::::o           a::::a h:::::::hhh::::::h
            N::::::N  N::::N:::::::No::::o     o::::o    aaaaaaa:::::a h::::::h   h::::::h
            N::::::N   N:::::::::::No::::o     o::::o  aa::::::::::::a h:::::h     h:::::h
            N::::::N    N::::::::::No::::o     o::::o a::::aaaa::::::a h:::::h     h:::::h
            N::::::N     N:::::::::No::::o     o::::oa::::a    a:::::a h:::::h     h:::::h
            N::::::N      N::::::::No:::::ooooo:::::oa::::a    a:::::a h:::::h     h:::::h
            N::::::N       N:::::::No:::::::::::::::oa:::::aaaa::::::a h:::::h     h:::::h
            N::::::N        N::::::N oo:::::::::::oo  a::::::::::aa:::ah:::::h     h:::::h
            NNNNNNNN         NNNNNNN   ooooooooooo     aaaaaaaaaa  aaaahhhhhhh     hhhhhhh
            """;

    /**
     * Starts the program and processes user commands until the user exits.
     *
     * @param args Command-line arguments; not used.
     */
    public static void main(String[] args) {
        System.out.println(SEPARATOR);
        System.out.print(BANNER);
        System.out.println(SEPARATOR);
        // Greeting wording refined with OpenAI Codex.
        System.out.println("Ad astra abyssosque!");
        System.out.println("Hello! I'm Noah.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);

        Scanner scanner = new Scanner(System.in);
        Storage storage = new Storage(DATA_FILE_PATH);
        ArrayList<Task> tasks = loadTasks(storage);

        while (true) {
            String userCommand = scanner.nextLine();
            System.out.println(SEPARATOR);
            try {
                if (userCommand.equals("bye")) {
                    System.out.println("Farewell, Traveler!");
                    System.out.println("Hope to see you again soon.");
                    System.out.println(SEPARATOR);
                    break;
                } else if (userCommand.equals("list")) {
                    printTaskList(tasks);
                } else if (userCommand.equals(DELETE_COMMAND)
                        || userCommand.startsWith(DELETE_COMMAND + " ")) {
                    deleteTask(tasks, userCommand);
                    storage.saveTasks(tasks);
                } else if (userCommand.equals(MARK_COMMAND)
                        || userCommand.startsWith(MARK_COMMAND + " ")) {
                    markTask(tasks, userCommand);
                    storage.saveTasks(tasks);
                } else if (userCommand.equals(UNMARK_COMMAND)
                        || userCommand.startsWith(UNMARK_COMMAND + " ")) {
                    unmarkTask(tasks, userCommand);
                    storage.saveTasks(tasks);
                } else if (userCommand.equals(TODO_COMMAND)
                        || userCommand.startsWith(TODO_COMMAND + " ")) {
                    addTodoTask(tasks, userCommand);
                    storage.saveTasks(tasks);
                } else if (userCommand.equals(DEADLINE_COMMAND)
                        || userCommand.startsWith(DEADLINE_COMMAND + " ")) {
                    addDeadlineTask(tasks, userCommand);
                    storage.saveTasks(tasks);
                } else if (userCommand.equals(EVENT_COMMAND)
                        || userCommand.startsWith(EVENT_COMMAND + " ")) {
                    addEventTask(tasks, userCommand);
                    storage.saveTasks(tasks);
                } else {
                    throw new NoahException("I don't recognize that command. Please try again.");
                }
            } catch (NoahException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(SEPARATOR);
        }
        scanner.close();
    }

    /**
     * Loads saved tasks, or starts with an empty list if loading fails.
     *
     * @param storage Storage used to load tasks.
     * @return Tasks loaded successfully, or an empty list after an error.
     */
    private static ArrayList<Task> loadTasks(Storage storage) {
        try {
            return storage.loadTasks();
        } catch (NoahException e) {
            System.out.println(e.getMessage());
            System.out.println(SEPARATOR);
            return new ArrayList<>();
        }
    }

    /**
     * Prints all stored tasks in their displayed order.
     *
     * @param tasks Tasks currently stored.
     */
    private static void printTaskList(ArrayList<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Marks the task selected by its displayed task number as done.
     *
     * @param tasks Tasks currently stored.
     * @param userCommand Full command entered by the user.
     * @throws NoahException If the task number is missing or invalid.
     */
    private static void markTask(ArrayList<Task> tasks, String userCommand)
            throws NoahException {
        int index = parseTaskIndex(tasks, userCommand, MARK_COMMAND);
        tasks.get(index).markAsDone();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + tasks.get(index));
    }

    /**
     * Marks the task selected by its displayed task number as not done.
     *
     * @param tasks Tasks currently stored.
     * @param userCommand Full command entered by the user.
     * @throws NoahException If the task number is missing or invalid.
     */
    private static void unmarkTask(ArrayList<Task> tasks, String userCommand)
            throws NoahException {
        int index = parseTaskIndex(tasks, userCommand, UNMARK_COMMAND);
        tasks.get(index).unmarkAsDone();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + tasks.get(index));
    }

    /**
     * Deletes the task selected by its displayed task number.
     *
     * @param tasks Tasks currently stored.
     * @param userCommand Full command entered by the user.
     * @throws NoahException If the task number is missing or invalid.
     */
    private static void deleteTask(ArrayList<Task> tasks, String userCommand)
            throws NoahException {
        int index = parseTaskIndex(tasks, userCommand, DELETE_COMMAND);
        Task removedTask = tasks.remove(index);

        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + removedTask);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Parses and validates the task number supplied after a command word.
     *
     * @param tasks Tasks currently stored.
     * @param userCommand Full command entered by the user.
     * @param command Command word whose task number is being parsed.
     * @return Zero-based index of the selected task.
     * @throws NoahException If the task number is missing or invalid.
     */
    private static int parseTaskIndex(ArrayList<Task> tasks, String userCommand,
            String command) throws NoahException {
        String taskNumberText = userCommand.substring(command.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new NoahException("A task number is required. Try: " + command + " 1");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new NoahException("The task number must be a positive integer.");
        }
        if (taskNumber <= 0) {
            throw new NoahException("The task number must be a positive integer.");
        }
        if (taskNumber > tasks.size()) {
            throw new NoahException("There is no task numbered " + taskNumber + ".");
        }

        return taskNumber - 1;
    }

    /**
     * Creates and adds a todo task from the user's command.
     *
     * @param tasks Tasks currently stored.
     * @param userCommand Full command entered by the user.
     * @throws NoahException If the todo description is missing.
     */
    private static void addTodoTask(ArrayList<Task> tasks, String userCommand)
            throws NoahException {
        String description = userCommand.substring(TODO_COMMAND.length()).trim();
        if (description.isEmpty()) {
            throw new NoahException("A todo needs a description. Try: todo read book");
        }

        Todo todo = new Todo(description);
        addTask(tasks, todo);
    }

    /**
     * Creates and adds a deadline task from the user's command.
     *
     * @param tasks Tasks currently stored.
     * @param userCommand Full command entered by the user.
     * @throws NoahException If required deadline details are missing.
     */
    private static void addDeadlineTask(ArrayList<Task> tasks, String userCommand)
            throws NoahException {
        String description = userCommand.substring(DEADLINE_COMMAND.length()).trim();
        if (description.isEmpty()) {
            throw new NoahException(
                    "A deadline needs a description. Try: deadline return book /by Sunday");
        }

        Deadline deadline;
        boolean hasBy = description.equals("/by")
                || description.startsWith("/by ")
                || description.endsWith(" /by")
                || description.contains(" /by ");
        if (hasBy) {
            int byIndex = description.indexOf("/by");
            String taskDescription = description.substring(0, byIndex).trim();
            String by = description.substring(byIndex + "/by".length()).trim();
            if (taskDescription.isEmpty() || by.isEmpty()) {
                throw new NoahException(
                        "A deadline needs a description and date. "
                                + "Try: deadline return book /by Sunday");
            }
            deadline = new Deadline(taskDescription, by);
        } else {
            deadline = new Deadline(description);
        }
        addTask(tasks, deadline);
    }

    /**
     * Creates and adds an event task from the user's command.
     *
     * @param tasks Tasks currently stored.
     * @param userCommand Full command entered by the user.
     * @throws NoahException If required event details are missing.
     */
    private static void addEventTask(ArrayList<Task> tasks, String userCommand)
            throws NoahException {
        String description = userCommand.substring(EVENT_COMMAND.length()).trim();
        if (description.isEmpty()) {
            throw new NoahException(
                    "An event needs a description. Try: event meeting /from 2pm /to 4pm");
        }

        Event event;
        boolean hasFrom = description.equals("/from")
                || description.startsWith("/from ")
                || description.endsWith(" /from")
                || description.contains(" /from ");
        boolean hasTo = description.equals("/to")
                || description.startsWith("/to ")
                || description.endsWith(" /to")
                || description.contains(" /to ");
        if (hasFrom || hasTo) {
            if (!hasFrom || !hasTo) {
                throw new NoahException(
                        "An event needs a description, start, and end. "
                                + "Try: event meeting /from 2pm /to 4pm");
            }

            int fromIndex = description.indexOf("/from");
            int toIndex = description.indexOf("/to");
            if (fromIndex >= toIndex) {
                throw new NoahException(
                        "An event needs a description, start, and end. "
                                + "Try: event meeting /from 2pm /to 4pm");
            }

            String taskDescription = description.substring(0, fromIndex).trim();
            String from = description.substring(
                    fromIndex + "/from".length(), toIndex).trim();
            String to = description.substring(toIndex + "/to".length()).trim();
            if (taskDescription.isEmpty() || from.isEmpty() || to.isEmpty()) {
                throw new NoahException(
                        "An event needs a description, start, and end. "
                                + "Try: event meeting /from 2pm /to 4pm");
            }
            event = new Event(taskDescription, from, to);
        } else {
            event = new Event(description);
        }
        addTask(tasks, event);
    }

    /**
     * Adds a task and reports the updated number of stored tasks.
     *
     * @param tasks Tasks currently stored.
     * @param task Task to add.
     */
    private static void addTask(ArrayList<Task> tasks, Task task) {
        tasks.add(task);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
    }
}
