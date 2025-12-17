/*
 * Author Stephanos B
 * Date: 16/12/2025
 */

import Objects.Game;
import Objects.Mod;
import Objects.ModFile;
import Objects.Mod.ModSource;
import Utils.FileUtil;
import Utils.ModIO;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.xml.transform.Source;

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
     * Compiles a new Mod from source files located in ./temp
     * The compiled mod is stored in the game's mod storage, ready for deployment.
     * 
     * @param modName The directory name of the mod located in ./temp, doubles as
     *                the mod's name.
     * @return Complete Mod that was created. Allows quick access to the exact data
     *         written without needing to read the JSON. (Mainly for data checking)
     */
    public Mod compileNewMod(String modName) throws Exception {

        // 1. Extract to temp/
        // Path tempDir = extractToTemp(downloadedZip);
        Path tempDir = Path.of(TEMP_DIR + modName);

        // 2. Analyze files, generate ModFile objects with hashes
        List<ModFile> files = FileUtil.getDirectoryFiles(tempDir.toString());
        Mod mod = new Mod(game.getId(), ModSource.OTHER, modName, files.toArray(new ModFile[0]));

        // 3. Prompt user for metadata (name, version, etc.)
        // ModMetadata meta = promptUserForMetadata(tempDir);
        mod.setDescription("Description for mod...");
        mod.setDownloadLink("Link to modPage");
        mod.setLoadOrder(1);
        mod.setVersion("1.2");

        // 6. Move to .mod_storage/game_id/mod_id_version/
        Path storagePath = Path.of(game.getModsPath(), mod.getId());
        Files.move(tempDir, storagePath);

        // 7. Save mod.json
        createModJson(mod, storagePath.resolve("mod.json"));

        return mod;
    } // compileNewMod()

    /**
     * Deploys the given Mod to the game directory.
     * 
     * The mod must be unpacked into ./temp/ then the Mod.JSON is read
     * 
     * @param modPath The path to the Mod.JSON file.
     */
    public void deployMod(String modPath) {
        /*
         * TODO
         * 1. Read Mod.JSON from path to and map to Mod object.
         * 2. For each ModFile in the Mod object, copy it to temp/{modName}
         * 3. Move files from temp/{modName} to the game's root directory if no errors
         * occur.
         * 4. Clean up temp/{modName} after deployment.
         */

    } // deployMod()

    /**
     * Removes the given Mod from the game directory.
     * Follows the Mod.JSON data to safely remove files.
     */
    public void removeMod(/* TODO: decide how to specify mods */) {
        // TODO
        // Maybe instead of deleting, move the files to a "trash" folder in ./temp for
        // recovery?
    } // removeMod()

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
     * @return The path to the created Mod.JSON file. Null if failed.
     */
    public String createModJson(Mod Mod, Path outputPath) {
        // Creates a fully pathed file name.
        // String fileName = (TEMP_DIR + Mod.getName().toLowerCase().replaceAll(" ",
        // "_") + ".json");
        String fileName = outputPath.toString() + "/mod.json";

        try {
            // Write to JSON
            ModIO.writeMod(Mod, new File(fileName));
            System.out.println("Written!"); // TODO Remove Debug
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } // createModJson()

} // Class
