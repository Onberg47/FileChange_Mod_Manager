/*
 * Author Stephanos B
 * Date: 30/12/2025
 */
package cli;

import java.util.Scanner;

import core.objects.Game;

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
        System.out.println("Type 'exit' or 'quit' to exit");
        System.out.println();

        do {
            printPrompt();
            String input = scanner.nextLine().trim();

            if (input.isEmpty())
                continue;

            String[] args = input.split("\\s+");
            String command = args[0].toLowerCase();

            try {
                if (command.equals("exit") || command.equals("quit")) {
                    System.out.println("Exiting...");
                    break;
                }

                if (command.equals("help")) {
                    printHelp();
                    continue;
                }

                if (currentGame == null) {
                    gameHandler.handleCommand(command, args, this);
                } else {
                    modHandler.handleCommand(command, args, this);
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }

        } while (true);

        scanner.close();
    }

    private void printPrompt() {
        if (currentGame == null) {
            System.out.print("game_manager: > ");
        } else {
            System.out.print("mod_manager: [" + currentGame.getId() + "] > ");
        }
    }

    private void printHelp() {
        if (currentGame == null) {
            System.out.println("Game Manager Commands:");
            System.out.println("  list          - List all managed games");
            System.out.println("  add           - Add a new game profile");
            System.out.println("  remove        - Remove a game profile");
            System.out.println("  update        - Update a game profile");
            System.out.println("  mod --id      - Enter mod manager for a game");
            System.out.println("  help          - Show this help");
            System.out.println("  exit/quit     - Exit the program");
        } else {
            System.out.println("Mod Manager Commands:");
            System.out.println("  list [--s]    - List deployed mods (or stored mods with --s)");
            System.out.println("  deploy --id   - Deploy a mod");
            System.out.println("  remove --id   - Remove a mod");
            System.out.println("  compile --dir - Compile a new mod");
            System.out.println("  game          - Return to game manager");
            System.out.println("  help          - Show this help");
            System.out.println("  exit/quit     - Exit the program");
        }
    }

    // Getters and setters
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