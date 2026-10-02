package noah;

import java.nio.file.Path;

import noah.exception.NoahException;
import noah.parser.Parser;
import noah.storage.Storage;
import noah.task.Task;
import noah.task.TaskList;
import noah.ui.Ui;

/**
 * Coordinates command parsing, task updates, console interaction, and storage.
 */
public class Noah {
    private static final Path DATA_FILE_PATH = Path.of("data", "noah.txt");

    private final Ui ui;
    private final Storage storage;

    /**
     * Creates Noah with a console user interface and task-file storage.
     */
    public Noah() {
        ui = new Ui();
        storage = new Storage(DATA_FILE_PATH);
    }

    /**
     * Starts the program and processes user commands until the user exits.
     *
     * @param args Command-line arguments; not used.
     */
    public static void main(String[] args) {
        new Noah().run();
    }

    /**
     * Loads saved tasks and processes commands until the user exits.
     */
    public void run() {
        ui.showWelcome();
        TaskList tasks = loadTasks();
        boolean isExit = false;

        while (!isExit) {
            String userCommand = ui.readCommand();
            ui.showLine();
            try {
                isExit = executeCommand(tasks, userCommand);
            } catch (NoahException e) {
                ui.showError(e.getMessage());
            }
            ui.showLine();
        }
        ui.close();
    }

    /**
     * Loads saved tasks, or starts with an empty list if loading fails.
     *
     * @return Tasks loaded successfully, or an empty list after an error.
     */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.loadTasks());
        } catch (NoahException e) {
            ui.showError(e.getMessage());
            ui.showLine();
            return new TaskList();
        }
    }

    /**
     * Processes a command and saves tasks after an operation changes the list.
     *
     * @param tasks Tasks currently stored.
     * @param userCommand Full command entered by the user.
     * @return Whether the user requested to exit.
     * @throws NoahException If the command is invalid or saving fails.
     */
    private boolean executeCommand(TaskList tasks, String userCommand) throws NoahException {
        String command = Parser.parseCommandWord(userCommand);
        switch (command) {
        case "bye":
            ui.showGoodbye();
            return true;
        case "list":
            ui.showTaskList(tasks.getTasks());
            return false;
        case "todo":
        case "deadline":
        case "event":
            Task addedTask = Parser.parseTask(userCommand);
            tasks.add(addedTask);
            ui.showTaskAdded(addedTask, tasks.size());
            break;
        case "delete":
            Task deletedTask = tasks.delete(Parser.parseTaskNumber(userCommand));
            ui.showTaskDeleted(deletedTask, tasks.size());
            break;
        case "mark":
            Task markedTask = tasks.mark(Parser.parseTaskNumber(userCommand));
            ui.showTaskMarked(markedTask);
            break;
        case "unmark":
            Task unmarkedTask = tasks.unmark(Parser.parseTaskNumber(userCommand));
            ui.showTaskUnmarked(unmarkedTask);
            break;
        default:
            throw new NoahException("That command isn't in my adventurer's handbook!\n"
                    + "Try: list, todo read book, or bye.");
        }
        storage.saveTasks(tasks.getTasks());
        return false;
    }
}
