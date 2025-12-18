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
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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
    private static final String TRASH_DIR = "temp/trash/"; // Temporary directory for mod operations

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
     * Compiles a new Mod from source files located in {@code ./temp}
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
            throw new Exception("❌ No such directory found: " + tempDir.toString());
        }
        System.out.println("📦 Compiling mod: " + tempDir.getFileName()); // TODO Debugging

        // 3. Analyze exsisting files, generate ModFile objects with hashes
        mod.setContentsArr(FileUtil.getDirectoryFiles(tempDir.toString(), tempDir.toString()).toArray(new ModFile[0]));

        // 4. Ounce the Mod is complete, the Mod.JSON file can be created.
        // Done in temp to ensure all operations occur there incase of failure.
        System.out.println("📦 Writing manifest..."); // TODO Debugging
        createModJson(mod, tempDir.resolve("mod.json"));

        // 5. Final operation: Move to .mod_storage/game_id/mod_id_version/
        Path storagePath = Path.of(game.getModsPath(), mod.getId());
        try {
            System.out.print("📦 Mod complete! Attempting to move Mod to: " + storagePath); // TODO Debugging
            // If the directory exsists, then it must be deleted first to prevent unused
            // files being present.
            if (Files.exists(storagePath)) {
                FileUtil.deleteDirectory(storagePath);
            }
            Files.move(tempDir, storagePath, StandardCopyOption.REPLACE_EXISTING); // REPLACE_EXISTING should be
                                                                                   // redundant, since the directroy is
                                                                                   // removed first.
            System.out.println("  ✔");

        } catch (IOException e) {
            // thrown by deleteDirectory()
            System.err.println("❌ Failed to delete exsisting Mod data at path: " + storagePath.toString());
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
     * The mod must be unpacked into {@code ./temp/} then the Mod.JSON is read
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
     * Follows the Mod's data to safely move files into
     * {@code ./temp/trash/[mod_id]}
     * 
     * @param mod The Mod instance to be removed.
     */
    public void modTrash(Mod mod) {

        System.out.println("📦 Removing mod: " + mod.getName() + "\n\tMoving files..."); // TODO Debugging
        try {
            Path src;
            Path target = Path.of(TRASH_DIR, mod.getId());
            Path maxPath = Path.of("");
            Path dataPath;

            // Delete any exsiting trash of the Mod
            try {
                FileUtil.deleteDirectory(target);
            } catch (IOException e) {
                System.err.println("❌ Could not delete exsisting contents of Mod in trash!");
                e.printStackTrace();
            }

            for (ModFile mf : mod.getContentsArr()) {
                dataPath = Path.of(mf.getFilePath());
                src = Path.of(game.getInstallPath(), dataPath.toString());

                // Using another try_catch so file-moving errors won't interrupt.
                try {
                    /*
                     * Must only be the parent for Directory creation!
                     * Ensures the directories are created to mirror the mod's deployment structure.
                     * Attempting this for files with no relative parent causes a
                     * NullPointerException
                     */
                    if (dataPath.getParent() != null) {
                        Files.createDirectories(target.resolve(dataPath.getParent()));

                        if (dataPath.getParent().toString().length() > maxPath.toString().length()) {
                            maxPath = dataPath.getParent();
                            // System.out.println("New max dir: " + maxPath.toString());
                        }

                    }

                    // TODO Debugging
                    System.out.print("\t\tTrying to move: " + src.toString() + " to " + target.resolve(dataPath));
                    Files.move(src, target.resolve(dataPath), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("  ✔");
                } catch (IOException e) {
                    // catches Files.move() and Files.createDirectories()
                    System.err.println("❌ Error moving file! " + dataPath); // TODO: Better logging
                    e.printStackTrace();
                }
            } // for each

            System.out.println("\tCleaning game directories from: " + maxPath); // TODO Debugging
            cleanDirectories(Path.of(game.getInstallPath()), maxPath);
            System.out.println("📦 Mod successfully trashed! ✔"); // TODO Debugging

        } catch (NullPointerException e) {
            // If the ModFiles array is empty, this will catch the null exception.
            System.err.println("❌ The Mod has no contents!"); // TODO: Better logging
        }

    } // removeMod()

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
            System.out.println("✔ Written!"); // TODO Remove Debug
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } // createModJson()

    /// /// /// Util Methods /// /// ///

    // TODO make private after testing
    /**
     * Attempts to delete all directories listed in the given path if they are
     * empty, tracking how many are removed.
     * This can handle the Path pointing to a non-directory, simply ignoring it (if
     * the file does not exists)
     * 
     * @param relative Path that leads to the working Path but is NOT checked for
     *                 cleaning.
     * 
     * @param working  Path to clean through. (Will not branch off this path, simply
     *                 walks back)
     * @return Int that counts how many empty directories were removed. -1 if path
     *         is invalid.
     */
    public static int cleanDirectories(Path relative, Path working) {
        // The file might not exsist by the path to it might.
        if (!Files.exists(relative.resolve(working)) && working.getParent() == null) {
            System.err.println("❌ Invalid path! [realtive] -> [working] : " + relative + " -> " + working);
            return -1;
        }

        int i = 0; // counts how many directories are removed.
        boolean last = false; // is last itteration?
        do {
            if (working.getParent() == null) {
                last = true;
            }

            // System.out.println("Trying to delete: " + relative.resolve(working)); // TODO
            try {
                Files.deleteIfExists(relative.resolve(working));
                i++;
            } catch (DirectoryNotEmptyException e) {
                // If the directory is no empty, returns because all subsequent parents are
                // thereby not empty either.
                System.err.println("❌ Directory " + relative + working + " is not empty.");
                return i;
            } catch (IOException e) {
                // Other error
                e.printStackTrace();
            }

            working = working.getParent();
            // System.out.println(" Now at: " + working); // TODO

        } while (!last);

        return i;
    } // cleanDirectories()

} // Class
