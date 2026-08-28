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
        while (true) {
            String userCommand = scanner.nextLine();
            System.out.println(cutoffLine);
            if (userCommand.equals("bye")) {
                System.out.println("Farewell, Traveler!");
                System.out.println("Hope to see you again soon.");
                System.out.println(cutoffLine);
                break;
            }
            System.out.println(userCommand);
            System.out.println(cutoffLine);
        }
        scanner.close();
    }
}
