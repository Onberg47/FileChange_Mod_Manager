/*
 * Author Stephanos B
 * Date: 17/01/2026
 */
package cli;

import java.time.LocalDate;

import core.utils.TrashUtil;

/**
 * Handles commands for the Trash utility
 * 
 * @since v3.3.5
 */
public class TrashCommandHandler {
    private CLIArgs cli;

    public void handleCommand(String command, String[] args, CLIManager cliManager) throws Exception {
        cli = new CLIArgs(args);

        switch (args[1]) {
            case "-h":
                printHelp();
                break;

            case "clean":
            case "-c":
                clean();
                break;

            case "empty":
            case "-e":
                empty();
                break;

            default:
                throw new IllegalArgumentException("Unknown trash args: " + args[1]);
        }
    } // handleCommand()

    private void printHelp() {

        System.out.println("\nTrash Commands:");
        System.out.printf("%-3s, %-15s | %s\n", "-t", "trash [option]", "trash cleaning tool");
        System.out.printf("%6s, %-12s | %s\n", "-h", "", "Specific help.");

        System.out.printf("%6s, %-12s | %s\n", "-c", "clean",
                "Cleans the Trash direcotry based on file LastModified dates and a maxiumon size.");
        System.out.printf("%20s | %s\n", "--s <number>", "maximum size in Megabytes of direcotry");
        System.out.printf("%20s | %s\n", "--d <number>", "Days older than to clean out");

        System.out.printf("%6s, %-12s | %s\n", "-e", "empty", "Empties the entire Trash direcotry.");
    }

    private void empty() throws Exception {
        TrashUtil.emptyTrash();
    }

    private void clean() throws Exception {
        TrashUtil.cleanTrash(
                Long.parseLong(cli.getRequired("s")),
                LocalDate.now().minusDays(
                        Long.parseLong(cli.getRequired("d"))));
    }
} // Class
