/*
 * Author Stephanos B
 * Date: 26/12/2025
 */
package cli;

import core.managers.GameManager;
import core.managers.ModManager;
import core.objects.Game;
import core.utils.FileUtil;

/**
 * Command interface to use ModManager. For console and GUI wrapper use.
 */
public class ModManagerCLI {
    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }

        String command = args[0];

        try {
            switch (command) {
                case "list":
                    handleList(args);
                    break;

                case "compile":
                    handleCompile(args);
                    break;
                case "deploy":
                    handleDeploy(args);
                    break;
                case "remove":
                    handleRemove(args);
                    break;

                case "gui":
                    // Launch the GUI version
                    // ModManagerGUI.main(args);
                    break;
                default:
                    System.err.println("Unknown command: " + command);
                    printHelp();
                    System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    } // PSVM()

    private static void printHelp() {
        System.out.println("Usage: ");
        // List
        System.out.printf("\n%-4s, %-8s | %s\n", "-L", "list", "List all deployed Mods");
        System.out.printf("%14s |\n", "Arguments:");
        System.out.printf("%14s | %s\n", "--game", "Specify the Game being modified by Id");
        System.out.printf("%14s |\n", "Options:");
        System.out.printf("%14s | %s\n", "--s", "List Available Mods from Storage");

        // Deploy
        System.out.printf("\n%-4s, %-8s | %s\n", "-D", "deploy", "Deploy a stored Mod", "--mod", "ID of Mod");
        System.out.printf("%14s |\n", "[ Arguments ]");
        System.out.printf("%14s | %s\n", "--game", "Specify the Game being modified by Id");

        // Remove
        System.out.printf("\n%-4s, %-8s | %s\n", "-R", "remove", "Remove a deployed Mod", "--mod", "ID of Mod");
        System.out.printf("%14s |\n", "[ Arguments ]");
        System.out.printf(" %14s | %s\n", "--game", "Specify the Game being modified by Id");

        // Compile
        System.out.printf("\n%-4s, %-8s | %s\n", "-C", "compile", "Compile a new Mod to storage", "--dir",
                "name of directory with Mod contents");
        System.out.printf("%14s |\n", "[ Arguments ]");
        System.out.printf("%14s | %s\n", "--game", "Specify the Game being modified by Id");
        System.out.printf("%14s | %s\n", "--dir", "Name of directory with Mod contents");

    }

    /// /// /// Commands /// /// ///

    /**
     * Lists all [Deployed / Stored] Mods for a Game. (Default is deployed.)
     * 
     * @param args Command Args
     * @throws Exception
     */
    private static void handleList(String[] args) throws Exception {
        CLIArgs cli = new CLIArgs(args);

        // Required arguments
        String gameId = cli.getRequired("game");
        // Optional arguments
        Boolean location = cli.hasFlag("s");

        // Load game config
        Game game = GameManager.getGameById(gameId);
        if (game == null) {
            throw new Exception("Game not found: " + gameId);
        }

        ModManager manager = new ModManager(game);
        // TODO
        if (!location)
            System.out.print(FileUtil.printGameState(game));
        else
            System.out.println(FileUtil.printStoredGames(game));

    } // handleListDeployed()

    /**
     * Compiles a new Mod by directory name.
     * 
     * @param args Command Args
     * @throws Exception
     */
    private static void handleCompile(String[] args) throws Exception {
        CLIArgs cli = new CLIArgs(args);

        // Required arguments
        String gameId = cli.getRequired("game");
        String directory = cli.getRequired("dir");

        // Load game config
        Game game = GameManager.getGameById(gameId);
        if (game == null) {
            throw new Exception("Game not found: " + gameId);
        }
        ModManager manager = new ModManager(game);

        // RUN
        manager.compileMod(directory);

    } // handleCompile()

    /**
     * Deply a new Mod by ID
     * 
     * @param args Command Args
     * @throws Exception
     */
    private static void handleDeploy(String[] args) throws Exception {
        CLIArgs cli = new CLIArgs(args);

        // Required arguments
        String gameId = cli.getRequired("game");
        String modId = cli.getRequired("mod");

        // Load game config
        Game game = GameManager.getGameById(gameId);
        if (game == null) {
            throw new Exception("Game not found: " + gameId);
        }
        ModManager manager = new ModManager(game);

        // RUN
        manager.deployMod(modId);

    } // handleDeploy()

    /**
     * Removes the target Mod by ID
     * 
     * @param args Command Args
     * @throws Exception
     */
    private static void handleRemove(String[] args) throws Exception {
        CLIArgs cli = new CLIArgs(args);

        // Required arguments
        String gameId = cli.getRequired("game");
        String modId = cli.getRequired("mod");

        // Load game config
        Game game = GameManager.getGameById(gameId);
        if (game == null) {
            throw new Exception("Game not found: " + gameId);
        }
        ModManager manager = new ModManager(game);

        // RUN
        manager.trashMod(modId);

    } // handleRemove()

} // Class

/*
 * System.exit(0); // Success
 * System.exit(1); // General error
 * System.exit(2); // Invalid arguments
 * System.exit(3); // File not found
 */