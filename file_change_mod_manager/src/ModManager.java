/*
 * Author Stephanos B
 * Date: 16/12/2025
 */

import Objects.Mod;
import Objects.ModFile;

import java.io.File;

import Objects.Game;

/**
 * Provides the core functionality for managing mods of a given game.
 * 
 * Author: Stephanos B
 * Date: 15/12/2025
 */

public class ModManager {

    private Game game;
    private static final String TEMP_DIR = "temp/"; // Temporary directory for mod operations

    /**
     * Required constructor to specify the game to manage mods for.
     * 
     * @param game
     */
    public ModManager(Game game) {
        this.game = game;
    } // Constructor

    /**
     * Private default constructor for testing purposes only. Will be used to
     * manually specify a Game instance.
     */
    private ModManager() {
        this.game = new Game("testG", "Test Game", "test/game_root", "test/mod_storage/test01");
    } // Private Constructor

    /// /// /// Core Methods /// /// ///

    /**
     * Deploys the given Mod to the game directory.
     * 
     * The mod must be unpacked into ./temp/ then the Mod.JSON is read
     * 
     * @param Mod The Mod Object to deploy.
     */
    public void deployMod(Mod Mod) {
        // TODO
    } // deployMod()

    /**
     * Removes the given Mod from the game directory.
     * Follows the Mod.JSON data to safely remove files.
     */
    public void removeMod(Mod Mod) {
        // TODO
    } // removeMod()

    /**
     * Creates a pak/archive Mod for a given set of files and creats a Mod.JSON to
     * allow deployment.
     * The raw mod is expanded/copied into ./temp/{modname} before being packed and
     * a Mod.JSON created.
     */
    public void pakMod() {
        // TODO
    } // pakMod()

    /// /// /// Util Methods /// /// ///

    /**
     * Retrieves a Mod by its path from the Mod.
     * 
     * @param path The path of the Mod to retrieve.
     * @return The Mod object if found, otherwise null.
     */
    public Mod getModByPath(String path) {
        // TODO
        return null;
    } // getModByPath()

    /// /// /// Json Methods /// /// ///

    /**
     * Creates a Mod.JSON file for the given Mod in the TEMP_DIR.
     * 
     * @param Mod Complete Mod object to create JSON for.
     */
    public void createModJson(Mod Mod) {
        // creates a fully pathed file name.
        String fileName = (TEMP_DIR + Mod.getName().toLowerCase().replaceAll(" ", "_") + ".json");

        try {
            // Write to JSON
            ModIO.writeMod(Mod, new File(fileName));
            System.out.println("Written!"); // TODO Remove Debug
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // createModJson()

} // Class
