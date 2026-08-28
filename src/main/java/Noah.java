import java.util.Scanner;

public class Noah {
    public static void main(String[] args) {
        // ASCII-art banner generated using http://www.network-science.de/ascii/

        String banner = "NNNNNNNN        NNNNNNNN                                  hhhhhhh\n"
        + "N:::::::N       N::::::N                                  h:::::h\n"
        + "N::::::::N      N::::::N                                  h:::::h\n"
        + "N:::::::::N     N::::::N                                  h:::::h\n"
        + "N::::::::::N    N::::::N   ooooooooooo     aaaaaaaaaaaaa   h::::h hhhhh\n"
        + "N:::::::::::N   N::::::N oo:::::::::::oo   a::::::::::::a  h::::hh:::::hhh\n"
        + "N:::::::N::::N  N::::::No:::::::::::::::o  aaaaaaaaa:::::a h::::::::::::::hh\n"
        + "N::::::N N::::N N::::::No:::::ooooo:::::o           a::::a h:::::::hhh::::::h\n"
        + "N::::::N  N::::N:::::::No::::o     o::::o    aaaaaaa:::::a h::::::h   h::::::h\n"
        + "N::::::N   N:::::::::::No::::o     o::::o  aa::::::::::::a h:::::h     h:::::h\n"
        + "N::::::N    N::::::::::No::::o     o::::o a::::aaaa::::::a h:::::h     h:::::h\n"
        + "N::::::N     N:::::::::No::::o     o::::oa::::a    a:::::a h:::::h     h:::::h\n"
        + "N::::::N      N::::::::No:::::ooooo:::::oa::::a    a:::::a h:::::h     h:::::h\n"
        + "N::::::N       N:::::::No:::::::::::::::oa:::::aaaa::::::a h:::::h     h:::::h\n"
        + "N::::::N        N::::::N oo:::::::::::oo  a::::::::::aa:::ah:::::h     h:::::h\n"
        + "NNNNNNNN         NNNNNNN   ooooooooooo     aaaaaaaaaa  aaaahhhhhhh     hhhhhhh";
        String cutoffLine = "\n===============================================================================\n";
        System.out.println(cutoffLine);
        System.out.println(banner);
        System.out.println(cutoffLine);
        // Greeting wording refined with OpenAI Codex.
        System.out.println("Ad astra abyssosque!");
        System.out.println("Hello! I'm Noah.");
        System.out.println("What can I do for you?");
        System.out.println(cutoffLine);

        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[100];
        boolean[] isDone = new boolean[100];
        int taskCount = 0;

        while (true) {
            String userCommand = scanner.nextLine();
            System.out.println(cutoffLine);
            if (userCommand.equals("bye")) {
                System.out.println("Farewell, Traveler!");
                System.out.println("Hope to see you again soon.");
                System.out.println(cutoffLine);
                break;
            } else if (userCommand.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + ".["
                            + (isDone[i] ? "X" : " ") + "] " + tasks[i]);
                }
            } else if (userCommand.startsWith("mark ")) {
                int index = Integer.parseInt(userCommand.substring(5)) - 1;
                isDone[index] = true;
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  [X] " + tasks[index]);
            } else if (userCommand.startsWith("unmark ")) {
                int index = Integer.parseInt(userCommand.substring(7)) - 1;
                isDone[index] = false;
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  [ ] " + tasks[index]);
            } else {
                tasks[taskCount++] = userCommand;
                System.out.println("added: " + userCommand);
            }
            System.out.println(cutoffLine);
        }
        scanner.close();
    }
}
