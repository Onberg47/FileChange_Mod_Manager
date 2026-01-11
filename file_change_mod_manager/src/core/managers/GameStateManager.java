/*
 * Author Stephanos B
 * Date: 11/01/2026
 */
package core.managers;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

import core.config.AppConfig;
import core.interfaces.MapSerializable;
import core.io.JsonIO;
import core.objects.Game;
import core.objects.GameState;
import core.objects.Mod;
//import core.utils.Logger;

/**
 * Mannages GameState operations. For both the ModManager and GUI mod managment.
 */
public class GameStateManager {

    private AppConfig config = AppConfig.getInstance();
    // private static Logger log = Logger.getInstance();

    private Path GAME_ROOT_PATH;

    public GameStateManager(Game game) {
        GAME_ROOT_PATH = game.getInstallDirectory();
    }

    /**
     * Adds target Mod to the GameState's deployed Mods. Will create it if missing
     * but warns in case of FilePath error.
     * 
     * @param mod Target Mod
     * @throws Exception Any Fatal error.
     */
    public void gameStateAddMod(Mod mod) throws Exception {
        GameState gState;
        Path GsPath = GAME_ROOT_PATH.resolve(config.getManagerDir().toString(), GameState.FILE_NAME);
        try {
            if (Files.exists(GsPath))
                gState = (GameState) JsonIO.read(GsPath.toFile(), MapSerializable.ObjectTypes.GAME_STATE);
            else
                gState = new GameState();
            gState.appendModOnly(mod); // Will ensure no duplicates occur

            JsonIO.write(gState, GsPath.toFile());
        } catch (Exception e) {
            throw new Exception("Failed to add Mod to GameState", e);
        }
    } // addMod()

    /**
     * Removes the target Mod from the GameState's deployed Mods. Will remove the
     * file if no mods are left.
     * 
     * @param mod Target Mod
     * @throws Exception Any Fatal error.
     */
    public void gameStateRemoveMod(Mod mod) throws Exception {
        GameState gState;
        Path GsPath = GAME_ROOT_PATH.resolve(config.getManagerDir().toString(), GameState.FILE_NAME);

        if (!Files.exists(GsPath))
            throw new FileNotFoundException("File for GameState is missing!");
        try {
            gState = (GameState) JsonIO.read(GsPath.toFile(), MapSerializable.ObjectTypes.GAME_STATE);
            gState.removeMod(mod);

            // If removed Mod was last, delete file.
            if (gState.getDeployedMods().isEmpty())
                Files.delete(GsPath);
            else
                JsonIO.write(gState, GsPath.toFile());

        } catch (Exception e) {
            throw new Exception("Failed to remove Mod from GameState", e);
        }
    } // removeMod()

} // Class
