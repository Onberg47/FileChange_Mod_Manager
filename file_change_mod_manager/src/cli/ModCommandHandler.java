/*
 * Author Stephanos B
 * Date: 30/12/2025
 */
package cli;

import core.managers.ModManager;
import core.objects.Game;
import core.utils.FileUtil;

/**
 * Handles commands for Mod Managment of a Game
 */
public class ModCommandHandler {

    private CLIArgs cli;
    private Game game;
    private ModManager manager;

    public void handleCommand(String command, String[] args, CLIManager cliManager) throws Exception {
        cli = new CLIArgs(args);
        game = cliManager.getCurrentGame();
        manager = new ModManager(game);

        switch (command.toLowerCase()) {
            case "list":
            case "-l":
                listMods();
                break;
            case "deploy":
            case "-d":
                deployMod();
                break;
            case "remove":
            case "-r":
                removeMod();
                break;
            case "compile":
            case "-c":
                compileMod();
                break;
            case "game":
            case "-g":
                // Switch back to game state
                cliManager.clearCurrentGame();
                break;
            default:
                throw new IllegalArgumentException("Unknown mod command: " + command);
        }
    } // handleCommand()

    private void listMods() throws Exception {
        if (!cli.hasFlag("s"))
            System.out.println(FileUtil.printGameState(game));
        else
            System.out.println(FileUtil.printStoredMods(game));
    }

    private void deployMod() {
        manager.deployMod(
                cli.getRequired("id"));
    }

    private void removeMod() {
        manager.trashMod(
                cli.getRequired("id"));
    }

    private void compileMod() throws Exception {
        manager.compileMod(
                cli.getRequired("dir"),
                ModManager.collectUserMetadata());
    }
} // Class