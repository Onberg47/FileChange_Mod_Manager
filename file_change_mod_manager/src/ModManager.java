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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Scanner;

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

        // 1. Prompt user for metadata (name, version, etc.)
        Mod mod = new Mod();
        mod.setGameId(game.getId());
        mod = collectUserMetadata(mod);
        mod.generateModId(); // Can only create an id once required fields are collected.

        // 2. Extract to temp/
        // Path tempDir = extractToTemp(downloadedZip);
        Path tempDir = Path.of(TEMP_DIR, modName);

        if (!Files.exists(tempDir) || !Files.isDirectory(tempDir)) {
            // Verify the modName given is a valid directory.
            throw new Exception("No such directory found: " + tempDir.toString());
        }
        System.out.println("📦 Compiling mod: " + tempDir.getFileName()); // TODO Debugging

        // 3. Analyze files, generate ModFile objects with hashes
        mod.setContentsArr(FileUtil.getDirectoryFiles(tempDir.toString()).toArray(new ModFile[0]));
        /*
         * List<ModFile> files = FileUtil.getDirectoryFiles(tempDir.toString());
         * Mod mod = new Mod(game.getId(), ModSource.OTHER, modName, files.toArray(new
         * ModFile[0]));
         */

        // 4. Ounce the Mod is complete, the Mod.JSON file can be created.
        // Done in temp to ensure all operations occur there incase of failure.
        System.out.println("📦 Writing manifest..."); // TODO Debugging
        createModJson(mod, tempDir.resolve("mod.json"));

        // 5. Final operation: Move to .mod_storage/game_id/mod_id_version/
        Path storagePath = Path.of(game.getModsPath(), mod.getId());
        try {
            System.out.println("📦 Mod complete! Attempting to move Mod to: " + storagePath); // TODO Debugging
            // If the directory exsists, then it must be deleted first to prevent unused
            // files being present.
            if (Files.exists(storagePath)) {
                FileUtil.deleteDirectory(storagePath);
            }
            Files.move(tempDir, storagePath, StandardCopyOption.REPLACE_EXISTING); // REPLACE_EXISTING should be
                                                                                   // redundant, since the directroy is
                                                                                   // removed first.

        } catch (IOException e) {
            // thrown by deleteDirectory()
            System.err.println("Failed to delete exsisting Mod data at path: " + storagePath.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mod;
    } // compileNewMod()

    /**
     * 
     * @param mod Mod instance to make changes to.
     * @return the updated version of the passed Mod.
     */
    public static Mod collectUserMetadata(Mod mod) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter mod metadata:");
        System.out.print("*Dsiplay Name: ");
        mod.setName(scanner.nextLine().trim());

        System.out.print("Description: ");
        mod.setDescription(scanner.nextLine().trim());

        System.out.print(String.format("*Download Source (%s): ", Mod.ModSource.values().toString()));
        mod.setDownloadSource(scanner.nextLine().trim());

        System.out.print("Load Order (default 1): ");
        String loadOrderInput = scanner.nextLine().trim();
        mod.setLoadOrder(loadOrderInput.isEmpty() ? 1 : Integer.parseInt(loadOrderInput));

        System.out.print("*Version (default 1.0): ");
        mod.setVersion(scanner.nextLine().trim());

        // TODO Download date

        System.out.print("Download URL (Mod page): ");
        mod.setDownloadLink(scanner.nextLine().trim());

        scanner.close();
        return mod;
    } // collectUserMetadata()

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
        String fileName = outputPath.toString(); // for debugging

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
