/**
 * Author Stephanos B
 * Date 29/12/2025
 */
package core.managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.HashMap;

import core.config.AppConfig;
import core.interfaces.JsonSerializable;
import core.io.JsonIO;
import core.objects.Game;
import core.utils.DateUtil;
import core.utils.ScannerUtil;

/**
 * For performing core operations for Game managment.
 * 
 * @author Stephanos B
 */
public class GameManager {
    private static AppConfig config = AppConfig.getInstance();

    private final Path ICON_DIR = config.getGameDir().resolve("icons");

    private Game game;

    /// /// /// Core Methods /// /// ///

    /**
     * Creates a new Game.json file from the provided meta data. Checks if game
     * files but will not create missing directores, as these should already exsist
     * if correct.
     * This supports updating exsisting instances.
     * 
     * @param metaMap
     */
    public void addGame(HashMap<String, String> metaMap) throws Exception {
        System.out.println("\n📦 Adding new game..."); // TODO Debugging
        if (this.game == null) {
            System.out.println("init new Game!");
            this.game = new Game(); // Only assigns a fresh instance if non-exsists.
        }

        /// 1. read meta data /
        try {
            System.out.println("\tReading meta data...");

            if (metaMap.containsKey("id")) // This prevents missing values being set to null, allowing updates.
                this.game.setId(metaMap.get("id"));

            if (metaMap.containsKey("releaseVersion"))
                this.game.setReleaseVersion(metaMap.get("releaseVersion"));

            if (metaMap.containsKey("name"))
                this.game.setName(metaMap.get("name"));

            if (metaMap.containsKey("installPath")) {
                this.game.setInstallPath(metaMap.get("installPath"));
            }

            if (metaMap.containsKey("modsPath"))
                this.game.setModsPath(metaMap.get("modsPath"));

        } catch (Exception e) {
            throw new Exception("Failed to process Meta data.", e);
        }

        /// 2. Verify game paths.
        System.out.println("\tVerifying game paths...");
        Path path;
        try {
            path = Path.of(this.game.getInstallPath());
            System.out.println("Checking path: " + path.toString());
            if (!path.isAbsolute()) {
                System.err.println("❌ Game installation path is not absolute!");
            } else if (!Files.exists(path)) {
                System.err.println("❌ Could not find Game intall path at: " + path.toString()
                        + "\n\tThis should exsist, will not create. Update if path is invalid.");
            }
            System.out.println("\t\t✔ Game path is good.");

            path = Path.of(game.getModsPath());
            if (!path.isAbsolute()) {
                System.err.println("❌ Mod storage path is not absolute!");
            } else if (!Files.exists(path)) {
                System.err.println("❗ Could not find Mod storage path at: " + path.toString() + "\n\tCreating it...");
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    System.err.println("❌ Failed to create directories for mod storage! " + path.toString());
                    e.printStackTrace();
                    return;
                }
            }
            System.out.println("\t\t✔ Mod storage path is good.");
        } catch (InvalidPathException e) {
            throw new Exception("Could not convert paths.", e);
        } catch (Exception e) {
            System.err.println("Failed to instantiate Game");
            e.printStackTrace();
        }

        /// 3. Write JSON
        System.out.println("\tWriting JSON file for Game " + game.getName());
        path = config.getGameDir().resolve(game.getId() + ".json");
        try {
            if (!Files.exists(path))
                Files.createDirectories(path.getParent());
            JsonIO.write(game, path.toFile());

            System.out.println("📦 New game added!"); // TODO Debugging
        } catch (Exception e) {
            throw new Exception("Failed to write Game.json file.", e);
        }

    } // addGame()

    /**
     * Removes a Game.json file and any corrosponding icon image to trash. Trashed
     * games and icons are stored in: {@code ~trash/games/} and
     * {@code ~trash/games/icons/} respectively.
     * 
     * @param gameId Game ID to remove.
     */
    public void removeGame(String gameId) throws Exception {
        System.out.println("🗑 Removing Game: " + gameId);
        Path path = config.getGameDir().resolve(gameId + ".json");
        if (!Files.exists(path)) {
            throw new Exception("Failed to find file: " + path.toString());
        }

        try {
            System.out.println("\tTrying to move file to trash...");
            Path target = config.getTrashDir().resolve("games", gameId + ".json__" + DateUtil.getNumericTimestamp());
            if (!Files.exists(target.getParent()))
                Files.createDirectories(target.getParent());
            Files.move(path, target);

            path = ICON_DIR.resolve(gameId);
            target = config.getTrashDir().resolve("games", "icons", gameId);

            if (Files.exists(path) && Files.isRegularFile(path)) {
                System.out.println("\tIcon file found moving to trash at: " + target.toString());

                if (!Files.exists(target.getParent()))
                    Files.createDirectories(target.getParent());
                Files.move(path, target);
            }
            System.out.println("🗑 Game sucessfully trashed!");
        } catch (Exception e) {
            throw new Exception("Faild to delete game.", e);
        }
    } // removeGame()

    /**
     * Updates an exsisting game but reading the exsisting file and overriding any
     * new meta data passed in metaMap.
     * 
     * @param gameId  Game ID to update.
     * @param metaMap An incomplete metaMap of ONLY the fields to override. Can
     *                support id-changes.
     */
    public void updateGame(String gameId, HashMap<String, String> metaMap) throws Exception {

        try {
            game = GameManager.getGameById(gameId);
            this.addGame(metaMap); // creates a new game, using the exsisting game data.
            System.out.println("Game updated.");

            if (!game.getId().equals(gameId)) {
                // ID has been changed, must remove old file.
                Files.move(
                        config.getGameDir().resolve(gameId + ".json"),
                        config.getTrashDir().resolve("games", gameId + ".json__" + DateUtil.getNumericTimestamp()));
            }

        } catch (Exception e) {
            throw new Exception("Failed to update Game: " + gameId, e);
        }
    } // updateGame()

    /// /// /// Core Helper Methods /// /// ///

    /// /// /// Public Helper Methods /// /// ///

    /**
     * Used to collet user data before compileGame is run. Pass it directly to
     * compileGame when using CLI.
     * 
     * @return HashMap<String, String> of keyed metaData
     * @throws Exception
     */
    public static HashMap<String, String> collectUserMetadata() throws Exception {
        String[][] queryMatrix = {
                {
                        "id",
                        "name",
                        "releaseVersion",
                        "installPath",
                        "modsPath"
                },
                {
                        "ID. You must enter this for CLI usage. (leave empty to auto generate)\n Enter",
                        "Display Name", "Release Version",
                        "Absolute Mods Install Path",
                        "Absolute Mods storage Path" }
        };
        return ScannerUtil.checklistConsole(queryMatrix);
    } // collectUserMetadata()

    /**
     * Fetch a Game instance by its Id. Handles various checks internally.
     * 
     * @param gameId Id of the game to read. (The name of the json file)
     * @return Complete Game instance if successful. Null on fail.
     */
    public static Game getGameById(String gameId) throws Exception {
        Game tmp = new Game();
        Path path = config.getGameDir().resolve(gameId + ".json");
        try {
            tmp = (Game) JsonIO.read(
                    path.toFile(),
                    JsonSerializable.ObjectTypes.GAME);
        } catch (Exception e) {
            throw new Exception("❌ Failed to get Game by ID: " + path.toString(), e);
        }
        return tmp;
    } // getGameById()

} // Class
