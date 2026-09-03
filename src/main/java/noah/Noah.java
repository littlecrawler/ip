package noah;

import java.util.Scanner;

/**
 * Runs a command-line task manager that stores and updates tasks.
 */
public class Noah {
    private static final int MAX_TASKS = 100;
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
            if (userCommand.equals("bye")) {
                System.out.println("Farewell, Traveler!");
                System.out.println("Hope to see you again soon.");
                System.out.println(SEPARATOR);
                break;
            } else if (userCommand.equals("list")) {
                printTaskList(tasks, taskCount);
            } else if (userCommand.startsWith("mark ")) {
                markTask(tasks, userCommand);
            } else if (userCommand.startsWith("unmark ")) {
                unmarkTask(tasks, userCommand);
            } else if (userCommand.startsWith("todo ")) {
                taskCount = addTodoTask(tasks, taskCount, userCommand);
            } else if (userCommand.startsWith("deadline ")) {
                taskCount = addDeadlineTask(tasks, taskCount, userCommand);
            } else if (userCommand.startsWith("event ")) {
                taskCount = addEventTask(tasks, taskCount, userCommand);
            } else {
                taskCount = addGenericTask(tasks, taskCount, userCommand);
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

    private static void markTask(Task[] tasks, String userCommand) {
        int index = Integer.parseInt(userCommand.substring(5)) - 1;
        if (tasks[index] != null) {
            tasks[index].markAsDone();
            System.out.println("Nice! I've marked this task as done:");
            System.out.println("  " + tasks[index]);
        } else {
            throw new IllegalStateException("No task is stored at position " + (index + 1));
        }
    }

    private static void unmarkTask(Task[] tasks, String userCommand) {
        int index = Integer.parseInt(userCommand.substring(7)) - 1;
        if (tasks[index] != null) {
            tasks[index].unmarkAsDone();
            System.out.println("OK, I've marked this task as not done yet:");
            System.out.println("  " + tasks[index]);
        } else {
            throw new IllegalStateException("No task is stored at position " + (index + 1));
        }
    }

    private static int addTodoTask(Task[] tasks, int taskCount, String userCommand) {
        String description = userCommand.substring(5);
        tasks[taskCount] = new Todo(description);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + tasks[taskCount]);
        taskCount++;
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        return taskCount;
    }

    private static int addDeadlineTask(Task[] tasks, int taskCount, String userCommand) {
        String description = userCommand.substring(9);
        if (description.contains(" /by ")) {
            String[] split = description.split(" /by ");
            tasks[taskCount] = new Deadline(split[0], split[1]);
        } else {
            tasks[taskCount] = new Deadline(description);
        }
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + tasks[taskCount]);
        taskCount++;
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        return taskCount;
    }

    private static int addEventTask(Task[] tasks, int taskCount, String userCommand) {
        String description = userCommand.substring(6);
        if (description.contains(" /from ") && description.contains(" /to ")) {
            String[] split = description.split(" /from | /to ");
            tasks[taskCount] = new Event(split[0], split[1], split[2]);
        } else {
            tasks[taskCount] = new Event(description);
        }
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + tasks[taskCount]);
        taskCount++;
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        return taskCount;
    }

    private static int addGenericTask(Task[] tasks, int taskCount, String userCommand) {
        tasks[taskCount] = new Task(userCommand);
        taskCount++;
        System.out.println("added: " + userCommand);
        return taskCount;
    }
}
