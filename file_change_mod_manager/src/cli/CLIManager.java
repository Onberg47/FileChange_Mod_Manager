/*
 * Author Stephanos B
 * Date: 30/12/2025
 */
package cli;

import java.util.Scanner;

import core.config.AppConfig;
import core.objects.Game;
import core.utils.Logger;

/**
 * Central CLI command handler.
 * 
 * @author Stephanos B
 */
public class CLIManager {
    private Game currentGame;
    private GameCommandHandler gameHandler;
    private ModCommandHandler modHandler;
    private Scanner scanner;

    public CLIManager() {
        this.gameHandler = new GameCommandHandler();
        this.modHandler = new ModCommandHandler();
        this.scanner = new Scanner(System.in);
    }

    /**
     * MAIN. Run the CLI interactively.
     * 
     * @param args
     */
    public static void main(String[] args) {
        CLIManager cli = new CLIManager();
        cli.run();
    }

    public void run() {
        System.out.println("Mod Manager CLI - Type 'help' for commands");
        System.out.println("Type 'exit' or 'quit' to exit\n");

        do {
            printPrompt();
            String input = scanner.nextLine().trim();

            if (input.isEmpty())
                continue;

            String[] args = input.split("\\s+");
            String command = args[0].toLowerCase();

            try {
                if (command.equals("exit") || command.equals("quit") || command.equals("^C")) {
                    System.out.println("Exiting...");
                    break;
                }
                if (command.equals("help")) {
                    printHelp();
                    continue;
                }

                if (command.equals("info")) {
                    System.out.println(AppConfig.getInstance().toString());
                    continue;
                }

                // continue
                if (currentGame == null) {
                    gameHandler.handleCommand(command, args, this);
                } else {
                    modHandler.handleCommand(command, args, this);
                }
            } catch (Exception e) {
                Logger.getInstance().logError("CLI Error.", e);
            }

        } while (true);

        scanner.close();
    } // run()

    private void printPrompt() {
        if (currentGame == null) {
            System.out.print("game_manager: > ");
        } else {
            System.out.print("mod_manager: [" + currentGame.getId() + "] > ");
        }
    }

    private void printHelp() {

        System.out.println("\nProgram Commands:");
        System.out.printf("%-15s | %s\n", "info", "Show general info about the program and config");
        System.out.printf("%-3s, %-10s | %s\n", "-h", "help", "Show this help");
        System.out.printf("%-15s | %s\n", "exit / quit", "Exit the program");

        if (currentGame == null) {
            System.out.println("\nGame Manager Commands:");
            // System.out.println(" list - List all managed games");
            // System.out.println(" add - Add a new game profile");
            // System.out.println(" remove --id - Remove a game profile");
            // System.out.println(" update --id - Update a game profile");
            // System.out.println(" mod --id - Enter mod manager for a game");
            // System.out.println(" help - Show this help");
            // System.out.println(" exit/quit - Exit the program");

            System.out.printf("%-3s, %-10s | %s\n", "-L", "list", "List all game profiles");

            System.out.printf("%-3s, %-10s | %s\n", "-A", "add", "Add a new game profile");

            System.out.printf("%-3s, %-10s | %s\n", "-R", "remove", "Remove a game profile");
            System.out.printf("%15s | %s\n", "--id <target>", "target game id");
            System.out.printf("%15s | %s\n", "[--atomic]", "removed files will not be left in trash");

            System.out.printf("%-3s, %-10s | %s\n", "-U", "update", "Update a game profile");
            System.out.printf("%15s | %s\n", "--id <target>", "target game id");

            System.out.printf("%-3s, %-10s | %s\n", "-M", "mod", "Enter mod manager for a game");
            System.out.printf("%15s | %s\n", "--id <target>", "target game id");

        } else {
            System.out.println("\nMod Manager Commands:");
            // System.out.println(" list [--s] - List deployed mods (or stored mods with
            // --s)");
            // System.out.println(" deploy --id - Deploy a mod");
            // System.out.println(" remove --id - Remove a mod");
            /// System.out.println(" compile --dir - Compile a new mod");
            // System.out.println(" game - Return to game manager");
            // System.out.println(" help - Show this help");
            // System.out.println(" exit/quit - Exit the program");

            System.out.printf("%-3s, %-10s | %s\n", "-L", "list", "List all available Mods");
            System.out.printf("%15s | %s\n", "[--i]", "Only installed Mods");
            System.out.printf("%15s | %s\n", "[--u]", "Only uninstalled Mods");

            System.out.printf("%-3s, %-10s | %s\n", "-D", "deploy", "Deploy a mod to game files");

            System.out.printf("%-3s, %-10s | %s\n", "-R", "remove", "Remove a mod from game files");
            System.out.printf("%15s | %s\n", "--id <target>", "target mod id");
            System.out.printf("%15s | %s\n", "--all", "removes all mod from game files");
            System.out.printf("%15s | %s\n", "[--atomic]", "removed files will not be left in trash");

            System.out.printf("%-3s, %-10s | %s\n", "-o", "order", "reorder a mod");
            System.out.printf("%15s | %s\n", "--id <target>", "target mod id");
            System.out.printf("%15s | %s\n", "--n <number>", "new load order");

            System.out.printf("%-3s, %-10s | %s\n", "-c", "compile", "Compile a new mod");
            System.out.printf("%15s | %s\n", "--dir <name>", "name-only of the directory within temp");

            System.out.printf("%-15s | %s\n", "delete", "Delete a mod from storage, cannot be installed");
            System.out.printf("%15s | %s\n", "--id <target>", "target mod id");
            System.out.printf("%15s | %s\n", "[--atomic]", "removed files will not be left in trash");

            System.out.printf("%-3s, %-10s | %s\n", "-G", "game", "Return to game manager");
        }
    }

    /// /// /// Getters and setters /// /// ///
    public Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game game) {
        this.currentGame = game;
    }

    public void clearCurrentGame() {
        this.currentGame = null;
    }
} // Class