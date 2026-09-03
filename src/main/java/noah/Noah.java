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
        Todo[] todos = new Todo[MAX_TASKS];
        Deadline[] deadlines = new Deadline[MAX_TASKS];
        Event[] events = new Event[MAX_TASKS];

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
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    if (tasks[i] != null && todos[i] == null && deadlines[i] == null && events[i] == null) {
                        System.out.println((i + 1) + "." + tasks[i]);
                    } else if (tasks[i] == null && todos[i] != null && deadlines[i] == null && events[i] == null) {
                        System.out.println((i + 1) + "." + todos[i]);
                    } else if (tasks[i] == null && todos[i] == null && deadlines[i] != null && events[i] == null) {
                        System.out.println((i + 1) + "." + deadlines[i]);
                    } else {
                        System.out.println((i + 1) + "." + events[i]);
                    }
                }
            } else if (userCommand.startsWith("mark ")) {
                int index = Integer.parseInt(userCommand.substring(5)) - 1;
                if (tasks[index] != null) {
                    tasks[index].markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks[index]);
                } else if (todos[index] != null) {
                    todos[index].markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + todos[index]);
                } else if (deadlines[index] != null) {
                    deadlines[index].markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + deadlines[index]);
                } else if (events[index] != null) {
                    events[index].markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + events[index]);
                } else {
                    throw new IllegalStateException("No task is stored at position " + (index + 1));
                }
            } else if (userCommand.startsWith("unmark ")) {
                int index = Integer.parseInt(userCommand.substring(7)) - 1;
                if (tasks[index] != null) {
                    tasks[index].unmarkAsDone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + tasks[index]);
                } else if (todos[index] != null) {
                    todos[index].unmarkAsDone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + todos[index]);
                } else if (deadlines[index] != null) {
                    deadlines[index].unmarkAsDone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + deadlines[index]);
                } else if (events[index] != null) {
                    events[index].unmarkAsDone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + events[index]);
                } else {
                    throw new IllegalStateException("No task is stored at position " + (index + 1));
                }
            } else if (userCommand.startsWith("todo ")) {
                String description = userCommand.substring(5);
                todos[taskCount] = new Todo(description);
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + todos[taskCount]);
                taskCount++;
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            } else if (userCommand.startsWith("deadline ")) {
                String description = userCommand.substring(9);
                deadlines[taskCount] = new Deadline(description);
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + deadlines[taskCount]);
                taskCount++;
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            } else if (userCommand.startsWith("event ")) {
                String description = userCommand.substring(6);
                events[taskCount] = new Event(description);
                System.out.println("Got it. I've added this task:");
                System.out.println("  " + events[taskCount]);
                taskCount++;
                System.out.println("Now you have " + taskCount + " tasks in the list.");
            } else {
                tasks[taskCount] = new Task(userCommand);
                taskCount++;
                System.out.println("added: " + userCommand);
            }
            System.out.println(SEPARATOR);
        }
        scanner.close();
    }
}
