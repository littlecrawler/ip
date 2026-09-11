package noah;

import java.util.Scanner;

/**
 * Runs a command-line task manager that stores and updates tasks.
 */
public class Noah {
    private static final int MAX_TASKS = 100;
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
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
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

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
                    printTaskList(tasks, taskCount);
                } else if (userCommand.equals(MARK_COMMAND)
                        || userCommand.startsWith(MARK_COMMAND + " ")) {
                    markTask(tasks, taskCount, userCommand);
                } else if (userCommand.equals(UNMARK_COMMAND)
                        || userCommand.startsWith(UNMARK_COMMAND + " ")) {
                    unmarkTask(tasks, taskCount, userCommand);
                } else if (userCommand.equals(TODO_COMMAND)
                        || userCommand.startsWith(TODO_COMMAND + " ")) {
                    taskCount = addTodoTask(tasks, taskCount, userCommand);
                } else if (userCommand.equals(DEADLINE_COMMAND)
                        || userCommand.startsWith(DEADLINE_COMMAND + " ")) {
                    taskCount = addDeadlineTask(tasks, taskCount, userCommand);
                } else if (userCommand.equals(EVENT_COMMAND)
                        || userCommand.startsWith(EVENT_COMMAND + " ")) {
                    taskCount = addEventTask(tasks, taskCount, userCommand);
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

    private static void printTaskList(Task[] tasks, int taskCount) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + "." + tasks[i]);
        }
    }

    private static void markTask(Task[] tasks, int taskCount, String userCommand)
            throws NoahException {
        String taskNumberText = userCommand.substring(MARK_COMMAND.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new NoahException("A task number is required. Try: mark 1");
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
        if (taskNumber > taskCount) {
            throw new NoahException("There is no task numbered " + taskNumber + ".");
        }

        int index = taskNumber - 1;
        tasks[index].markAsDone();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + tasks[index]);
    }

    private static void unmarkTask(Task[] tasks, int taskCount, String userCommand)
            throws NoahException {
        String taskNumberText = userCommand.substring(UNMARK_COMMAND.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new NoahException("A task number is required. Try: unmark 1");
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
        if (taskNumber > taskCount) {
            throw new NoahException("There is no task numbered " + taskNumber + ".");
        }

        int index = taskNumber - 1;
        tasks[index].unmarkAsDone();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + tasks[index]);
    }

    private static int addTodoTask(Task[] tasks, int taskCount, String userCommand)
            throws NoahException {
        String description = userCommand.substring(TODO_COMMAND.length()).trim();
        if (description.isEmpty()) {
            throw new NoahException("A todo needs a description. Try: todo read book");
        }

        Todo todo = new Todo(description);
        return addTask(tasks, taskCount, todo);
    }

    private static int addDeadlineTask(Task[] tasks, int taskCount, String userCommand)
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
        return addTask(tasks, taskCount, deadline);
    }

    private static int addEventTask(Task[] tasks, int taskCount, String userCommand)
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
        return addTask(tasks, taskCount, event);
    }

    private static int addTask(Task[] tasks, int taskCount, Task task) {
        tasks[taskCount] = task;
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + tasks[taskCount]);
        taskCount++;
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        return taskCount;
    }
}
