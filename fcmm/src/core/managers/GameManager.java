/**
 * Author Stephanos B
 * Date 29/12/2025
 */
package core.managers;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import core.config.AppConfig;
import core.interfaces.MapSerializable;
import core.io.JsonIO;
import core.objects.Game;
import core.utils.DateUtil;
import core.utils.FileUtil;
import core.utils.Logger;
import core.utils.ScannerUtil;

/**
 * For performing core operations for Game managment.
 * 
 * @author Stephanos B
 */
public class GameManager {
    private static final AppConfig config = AppConfig.getInstance();
    private static final Logger log = Logger.getInstance();

    private static final Path ICON_DIR = config.getGameDir().resolve("icons");

    /// /// /// Core Methods /// /// ///

    /**
     * For CLI use, checks user input.
     * Creates a new Game.json file from the provided meta data. Checks if game
     * files but will not create missing directores, as these should already exsist
     * if correct.
     * This supports updating exsisting instances.
     * 
     * @param metaMap
     */
    public static Game addGame(HashMap<String, Object> metaMap) throws Exception {
        log.logEntry(0, "\n📦 Adding new game...");
        Game game = new Game();

        /// 1. read meta data.
        try {
            log.logEntry(1, "Reading meta data...");
            game.setFromMap(metaMap);
        } catch (Exception e) {
            throw new Exception("Failed to process Meta data.", e);
        }

        /// 2. Verify game paths.
        log.logEntry(1, "Verifying game paths...");
        Path path;
        try {
            path = game.getInstallDirectory();
            log.logEntry(1, null, "Checking path: " + path.toString()); // silent log
            if (!path.isAbsolute()) {
                log.logWarning(1, "Game installation path is not absolute.", null);
            }
            if (!Files.exists(path)) {
                log.logWarning(1, "Could not find Game intall path at: " + path.toString()
                        + "\n\tThis should exsist, will NOT create. Update if path is invalid.", null);
            }
            log.logEntry(2, "✔ Game path is good.");

            path = game.getStoreDirectory();
            log.logEntry(1, null, "Checking path: " + path.toString());
            if (!path.isAbsolute()) {
                log.logWarning(1, "Mod storage path is not absolute.", null);
            }
            if (!Files.exists(path)) {
                log.logWarning(1, "Could not find Mod storage path at: " + path.toString() + "\n\tCreating it...",
                        null);
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    throw new Exception("Failed to create directories for mod storage! " + path.toString(), e);
                }
            }
            log.logEntry(2, "✔ Mod storage path is good.");
        } catch (InvalidPathException e) {
            throw new Exception("Could not convert paths.", e);
        } catch (Exception e) {
            throw new Exception("Failed to instantiate Game.", e);
        }

        /// 3. Write JSON
        saveGame(game);
        return game;
    } // addGame()

    /**
     * Updates an exsisting game but reading the exsisting file and overriding any
     * new meta data passed in metaMap.
     * 
     * @param gameId  Game ID to update.
     * @param metaMap An incomplete metaMap of ONLY the fields to override. Can
     *                support id-changes.
     */
    public static void updateGame(String gameId, HashMap<String, Object> metaMap) throws Exception {
        Game game = new Game();
        try {
            game = GameManager.getGameById(gameId);
            if (!game.getId().equals(gameId)) {
                log.logWarning("GameID has changed. Trashing old file", null);
                Files.move(
                        config.getGameDir().resolve(gameId + ".json"),
                        config.getTrashDir().resolve("games", gameId + ".json__" + DateUtil.getNumericTimestamp()));
            }
            GameManager.addGame(metaMap); // creates a new game, using the exsisting game data.
            log.logEntry(0, "Game updated.");
        } catch (Exception e) {
            throw new Exception("Failed to update Game: " + gameId, e);
        }
    } // updateGame()

    /**
     * Removes a Game.json file and any corrosponding icon image to trash. Trashed
     * games and icons are stored in: {@code ~trash/games/} and
     * {@code ~trash/games/icons/} respectively.
     * 
     * @param gameId Game ID to remove.
     */
    public static void removeGame(String gameId) throws Exception {
        log.logEntry(0, "🗑 Removing Game: " + gameId);
        Path targetDir = config.getTrashDir().resolve(gameId);
        Path gameFilePath = config.getGameDir().resolve(gameId + ".json");

        if (!Files.exists(gameFilePath)) {
            throw new Exception("Failed to find game file: " + gameFilePath.toString());
        }

        /// Remove any mods associated with the game.
        try {
            Game game = getGameById(gameId);
            ModManager manager = new ModManager(game);

            log.logEntry(1, "Trashing Enabled Mods if present...");
            manager.trashAll();

            log.logEntry(1, "Trashing Disabled Mods if present...");
            if (game.getStoreDirectory().toFile().list().length > 0) {
                log.logEntry(2, "Copying Mods to trash: " + targetDir);
                FileUtil.copyDirectoryContents(game.getStoreDirectory(), targetDir, null);

                log.logEntry(2, "Deleting original Mod storage");
                FileUtil.deleteDirectory(game.getStoreDirectory());
            }
        } catch (Exception e) {
            throw new Exception("Faild to remove exsisting Mods of game: " + e.getMessage(), e);
        }

        /// Remove the game itself
        try {
            log.logEntry(1, "Trying to move game file to trash...");
            targetDir = config.getTrashDir().resolve(gameId, gameId + ".json__" + DateUtil.getNumericTimestamp());

            if (!Files.exists(targetDir.getParent()))
                Files.createDirectories(targetDir.getParent());
            Files.move(gameFilePath, targetDir);

            gameFilePath = ICON_DIR.resolve(gameId);
            targetDir = config.getTrashDir().resolve("games", "icons", gameId);

            if (Files.exists(gameFilePath) && Files.isRegularFile(gameFilePath)) {
                log.logEntry(1, "Icon file found moving to trash at: " + targetDir.toString());

                if (!Files.exists(targetDir.getParent()))
                    Files.createDirectories(targetDir.getParent());
                Files.move(gameFilePath, targetDir);
            }

            log.logEntry(0, "🗑 Game " + gameId + "sucessfully trashed.");
        } catch (Exception e) {
            throw new Exception("Faild to delete game.", e);
        }
    } // removeGame()

    /// /// /// Public Helper Methods /// /// ///

    /**
     * Used to collet user data before compileGame is run. Pass it directly to
     * compileGame when using CLI.
     * 
     * @return HashMap<String, String> of keyed metaData
     * @throws Exception
     */
    public static HashMap<String, Object> collectUserMetadata() throws Exception {
        String[][] queryMatrix = {
                {
                        "id",
                        "name",
                        "releaseVersion",
                        "installDirectory",
                        "storeDirectory"
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
     * Writes a {@code Game.json}
     * 
     * @param game Game instance to save.
     */
    public static void saveGame(Game game) throws Exception {
        Path path = config.getGameDir().resolve(game.getId() + ".json");
        try {
            log.logEntry(1, "Writing JSON file for Game " + game.getName());
            if (!Files.exists(path)) {
                log.logWarning("File not found, creating new one.", null);
                Files.createDirectories(path.getParent());
            }
            JsonIO.write(game, path.toFile());

            log.logEntry(0, "📦 Game " + game.getId() + " written.");
        } catch (Exception e) {
            throw new Exception("Failed to write Game.json file.", e);
        }
    } // saveGame()

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
                    MapSerializable.ObjectTypes.GAME);
        } catch (Exception e) {
            throw new Exception("Failed to get Game by ID: " + path.toString(), e);
        }
        return tmp;
    } // getGameById()

    /**
     * Made for GUI use.
     * 
     * @return A List<Game> of all the valid game profiles found in the ModManager's
     *         game-directory.
     */
    public static List<Game> getAllGames() {
        try (Stream<Path> paths = Files.list(config.getGameDir())) {
            return paths.map(path -> {
                if (!Files.isRegularFile(path))
                    return null; // ignore non-regular files.
                try {
                    return (Game) JsonIO.read(path.toFile(), MapSerializable.ObjectTypes.GAME);
                } catch (InvalidObjectException e) {
                    // differentiate between files that are not game types and other errors.
                    log.logError(e.getMessage(), e);
                    return null;
                } catch (Exception e) {
                    log.logError("Error reading file: " + path.toString() + " -> ", e);
                    return null;
                }
            }).filter(Objects::nonNull).toList();
        } catch (Exception e) {
            log.logError("Error reading manifest directory.", e);
            return null;
        }
    } // getAllGames()

} // Class
