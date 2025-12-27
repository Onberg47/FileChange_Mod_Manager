/*
 * Author Stephanos B
 * Date: 26/12/2025
 */
package cli;

import core.managers.GameManager;
import core.managers.ModManager;
import core.objects.Game;

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
        System.out.println("help"); // TODO
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
        //String location = cli.getString("location", null);

        // Load game config
        Game game = GameManager.getGameById(gameId);
        if (game == null) {
            throw new Exception("Game not found: " + gameId);
        }

        ModManager manager = new ModManager(game);
        // TODO
        System.out.print(manager.printGameState());

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