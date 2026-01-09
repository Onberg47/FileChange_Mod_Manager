/*
 * Author Stephanos B
 * Date: 30/12/2025
 */
package cli;

import java.util.HashMap;

import core.managers.GameManager;
import core.objects.Game;
import core.utils.FileUtil;

/**
 * Handles commands for Game Managment
 */
public class GameCommandHandler {

    private CLIArgs cli;
    private GameManager gm;

    public void handleCommand(String command, String[] args, CLIManager cliManager) throws Exception {
        cli = new CLIArgs(args);
        gm = new GameManager();

        switch (command.toLowerCase()) {
            case "list":
                listGames();
                break;
            case "add":
            case "-a":
                addGame();
                break;
            case "remove":
            case "-r":
                removeGame();
                break;
            case "update":
            case "-u":
                updateGame();
                break;
            case "mod":
            case "-m":
                // Switch to mod state
                String gameId = cli.getRequired("id");
                Game game = GameManager.getGameById(gameId);// findGame(gameId);
                cliManager.setCurrentGame(game);
                break;
            default:
                throw new IllegalArgumentException("Unknown game command: " + command);
        }
    } // handleCommand()

    private void listGames() throws Exception {
        System.out.println(FileUtil.printGames());
    }

    private void addGame() throws Exception {
        gm.addGame(GameManager.collectUserMetadata());
    }

    private void removeGame() throws Exception{
        gm.removeGame(
                cli.getRequired("id"));
    }

    private void updateGame() throws Exception {
        HashMap<String, Object> tmpMap = new HashMap<>();
        String gameid = cli.getRequired("id");

        System.out.println("Enter new data for the Game. (Leave empty to not update)");
        tmpMap = GameManager.collectUserMetadata();

        gm.updateGame(
                gameid,
                tmpMap);
    } // updateGame()

} // Class
