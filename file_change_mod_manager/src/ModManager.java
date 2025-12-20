/*
 * Author Stephanos B
 * Date: 16/12/2025
 */

import Objects.Game;
import Objects.GameState;
import Objects.Mod;
import Objects.ModFile;
import Objects.ModManifest;
import Utils.FileUtil;
import Utils.GameIO;
import Utils.ModIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    // TODO: remove `mod_manager/` from paths before packaging!
    private static final String TEMP_DIR = "mod_manager/.temp/"; // Temporary directory for mod operations.
    private static final String TRASH_DIR = "mod_manager/.temp/trash/"; // Trash directory.
    private static final String MANIFEST_DIR = ".mod_manifests/"; // Where in the game_root are manifests stored.

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
     * @param dirName The directory name of the mod located in ./temp, doubles as
     *                the mod's name.
     * @return Complete Mod that was created. Allows quick access to the exact data
     *         written without needing to read the JSON. (Mainly for data checking)
     */
    public Mod modCompileNew(String dirName) throws Exception {
        /// /// 1. Verify Directory is valid.
        Path tempDir = Path.of(TEMP_DIR, dirName);

        if (!Files.isDirectory(tempDir)) {
            // If path leads to a zip, attempt to extract.
            // Path tempDir = extractToTemp(downloadedZip);
        } else if (!Files.exists(tempDir)) {
            // Verify the dirName given is a valid directory.
            throw new Exception("❌ No such directory found: " + tempDir.toString());
        }
        System.out.println("📦 Collecting info: " + tempDir.getFileName()); // TODO Debugging

        /// /// 2. Prompt user for metadata (name, version, etc.)
        ModManifest mod;
        mod = new ModManifest(collectUserMetadata(new Mod()));
        mod.setGameId(game.getId());
        mod.generateModId(); // Can only create an id once required fields are collected.

        System.out.println("\n📦 Compiling mod: " + tempDir.getFileName()); // TODO Debugging

        /// /// 3. Analyze exsisting files, generate ModFile objects with hashes
        mod.setContentsArr(FileUtil.getDirectoryFiles(tempDir.toString(), tempDir.toString()).toArray(new ModFile[0]));

        /// /// 4. Once the Mod is complete, the Mod.JSON file can be created.
        System.out.println("📦 Writing manifest..."); // TODO Debugging
        Path storagePath = Path.of(game.getModsPath(), mod.getId()); // Path where the Mods will be stored.

        try {
            Path path = tempDir.resolve(MANIFEST_DIR, mod.getId() + ".json");
            Files.createDirectories(path.getParent());
            // Write to JSON
            ModIO.writeModManifest(mod, new File(path.toString()));
            System.out.println("✔ Written! to: " + path.toString()); // Debug

        } catch (FileNotFoundException e) {
            System.out.println("❌ Failed to write Manifest: "
                    + storagePath.resolve(MANIFEST_DIR, mod.getId() + ".json").toString());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        /// /// 5. Final operation: Move to .mod_storage/game_id/mod_id_version /
        try {
            System.out.print("📦 Mod complete! Attempting to move Mod to: " + storagePath + "  "); // TODO Debugging
            // If the directory exsists, then it must be deleted first to prevent unused
            // files being present.
            if (Files.exists(storagePath)) {
                FileUtil.deleteDirectory(storagePath);
            }
            // Files.setAttribute(storagePath, "user:loadOrder",
            // ((Integer)mod.getLoadOrder()).toString());
            Files.move(tempDir, storagePath);
            System.out.println("✔");

        } catch (IOException e) {
            // thrown by deleteDirectory()
            System.err.println("❌ Failed to move or delete exsisting Mod data at path: " + storagePath.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return mod;
    } // modCompileNew()

    /**
     * Removes the given Mod from the game directory.
     * Follows the Mod's data to safely move files into
     * {@code ./temp/trash/[mod_id]}
     * 
     * @param modID The ID of the Mod instance to be removed.
     */
    public void modTrash(String modID) {
        /// /// 1. Find the Mod's manifest from it's ID and read it.
        ModManifest mod;
        Path manifestPath = Path.of(game.getInstallPath(), MANIFEST_DIR).resolve(modID + ".json");

        try {
            mod = ModIO.readModManifest(manifestPath.toFile());
            System.out.println("\tManifest of Mod: " + mod.getName() + " found! ✔");
        } catch (Exception e) {
            System.err.println("❌ Mod manifest does not exsists! " + manifestPath.toString());
            e.printStackTrace();
            return;
        }

        /// /// 2. Use data from manifest to safley remove Mod files.
        System.out.println("🗑 Removing mod: " + mod.getName() + "\n\tMoving files...");
        try {
            Path src;
            Path target = Path.of(TRASH_DIR, mod.getId());
            Path maxPath = Path.of("");
            Path dataPath;

            // Delete any exsiting trash of the Mod
            try {
                FileUtil.deleteDirectory(target);
            } catch (IOException e) {
                System.err.println("❗ Could not delete exsisting contents of Mod in trash! Continuing...");
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

                    System.out.print("\t\tTrying to move: " + src.toString() + " to " + target.resolve(dataPath));
                    Files.move(src, target.resolve(dataPath));
                    System.out.println("  ✔");
                } catch (IOException e) {
                    // catches Files.move() and Files.createDirectories()
                    System.err.println("❌ Error moving file! " + dataPath);
                    e.printStackTrace();
                }
            } // for each

            System.out.println("\t\tCleaning game directories from: " + maxPath);
            FileUtil.cleanDirectories(Path.of(game.getInstallPath()), maxPath);

            System.out.println("\tMod files successfully trashed! ✔");

            /// /// 3. Remove ModManifest from game files.
            System.out.print("\tMoving Mod Manifest...");
            target = target.resolve(MANIFEST_DIR);
            Files.createDirectories(target);
            Files.move(manifestPath, target.resolve(manifestPath.getFileName()));
            FileUtil.cleanDirectories(Path.of(game.getInstallPath()), Path.of(MANIFEST_DIR));
            System.out.println(" ✔");
            ///
            System.out.println("🗑 Mod successfully trashed! ✔");

        } catch (NullPointerException e) {
            // If the ModFiles array is empty, this will catch the null exception.
            System.err.println("❌ The Mod has no contents!");
        } catch (IOException e) {
            // Failed to move Manifest!
            e.printStackTrace();
        }

    } // removeMod()

    /**
     * Deploys the given Mod to the game directory.
     * 
     * The mod must be unpacked into {@code ./temp/} then the Mod.JSON is read
     * 
     * @param modID         The ID of the Mod instance to be removed.
     * @param checkConflict Should file conflicts be with the game_root? (In some
     *                      cases these checks are not needed and add great
     *                      overhead)
     */
    public void deployMod(String modID, Boolean checkConflict) {
        ModManifest mod;
        Path tempDir = Path.of(TEMP_DIR, modID);
        Path storedDir = Path.of(game.getModsPath(), modID);

        /// /// 1. Find the Mod's manifest from it's ID and read it.
        System.out.println("📦 Attempting to deploy mod...");
        try {
            mod = ModIO.readModManifest(storedDir.resolve(MANIFEST_DIR, modID + ".json").toFile());
            System.out.println("\tManifest of Mod: " + mod.getName() + " found! ✔");
        } catch (FileNotFoundException e) {
            System.err.println(
                    "❌ Mod manifest does not exsists! " + storedDir.resolve(MANIFEST_DIR, modID + ".json").toString());
            return;
        } catch (Exception e) {
            System.err.println("❌ Error reading Mod manifest file!");
            e.printStackTrace();
            return;
        }

        /// /// 2. Copy to temp/{mod_id} Mod Files and verify integrity and if items
        /// were left behind.
        try {
            System.out.println("\tChecking conflicts...");
            // Files.createDirectories(tempDir);

            // Try to copy each file from the Manifest to find missing/invalid entries.
            File newFile; // File intended to be added.
            for (ModFile mf : mod.getContentsArr()) {
                newFile = new File(storedDir.resolve(mf.getFilePath()).toString());
                if (!newFile.exists()) {
                    System.err.println(
                            "❌ File in manifest is missing! -> " + newFile.getPath() + "\nStopping deployment!");
                    return;
                } else if (checkConflict && Files.exists(tempDir.resolve(mf.getFilePath()))) {
                    System.out.println("\t\t❗ Conflict found for: " + mf.getFilePath());

                    if (loadOrderCopy(mf.getFilePath(), mod.getLoadOrder())) {
                        if (!Files.exists(tempDir.resolve(mf.getFilePath())))
                            Files.createDirectories(newFile.toPath());
                        Files.copy(newFile.toPath(), tempDir.resolve(mf.getFilePath()),
                                StandardCopyOption.REPLACE_EXISTING); // TODO replace with
                        // copyDirectoryContents()
                    }
                } else {
                    // No conflict
                    System.out.println(
                            "\tCopying files from: " + newFile.toPath() + " to " + tempDir.resolve(mf.getFilePath()));
                    Files.createDirectories(newFile.toPath().getParent());
                    System.out.println("Creating: " + newFile.toPath().getParent().toString());

                    Files.copy(newFile.toPath(), tempDir.resolve(mf.getFilePath()),
                            StandardCopyOption.REPLACE_EXISTING); // TODO replace with
                    // copyDirectoryContents()
                }

            } // for each ModFile

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            System.err.println("❌ Failed to copy to Game_Root!");
            e.printStackTrace();
            return;
        }

        /// /// 3. Copy from temp/{mod_id} to game_root and clean temp.
        try {
            // Files.createDirectories(tempDir.resolve(MANIFEST_DIR));
            FileUtil.copyDirectoryContents(tempDir, Path.of(game.getInstallPath()));
            System.out.println("\tMod copied to temp: " + Path.of(game.getInstallPath()));

            System.out.println("Cleaning temp...");
            FileUtil.deleteDirectory(tempDir);
        } catch (IOException e) {
            System.err.println("❌ Failed to copy Mod files to temp!");
            e.printStackTrace();
            return;
        }

    } // deployMod()

    /**
     * Deploys all mods from in the correct LoadOrder from a GameState.json
     * 
     * @param gameStatePath Path to the GameState.json
     */
    public void deployGameState(Path gameStatePath) {
        if (!Files.exists(gameStatePath)) {
            System.err.println("GameState file does not exsist! " + gameStatePath);
            return;
        }

        GameState gState = new GameState();
        try {
            gState = GameIO.readGameState(gameStatePath.toFile());
            gState.sortDeployedMods();

            for (Mod mod : gState.getDeployedMods()) {
                deployMod(mod.getId(), false); // No checking as the Mods are sorted.
            } // for each Mod

        } catch (NullPointerException e) {
            System.err.println("Nothing to do, GameState has no Mods!");
            return;
        } catch (Exception e) {
            // TODO: handle exception
            return;
        }
    } // deployGameState()

    /// /// /// Utility Methods /// /// ///

    /**
     * 
     * @param mod Mod instance to make changes to.
     * @return the updated version of the passed Mod.
     */
    public static Mod collectUserMetadata(Mod mod) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter mod metadata:");
        System.out.print("*Display Name: ");
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
     * Decides if a ModFile should be deployed or not based on LoadOrder.
     * 
     * @param modFilePath Path of the ModFile being checked for conflicts.
     * @param loadOrder   Load order from the new Mod
     * @return True if the mod has load priority over all other Mod currenlty
     *         deployed.
     */
    private boolean loadOrderCopy(String modFilePath, int loadOrder) {
        /*
         * If the file exsists in game_root there will be a conflit.
         * This will only copy files with no conflicts OR if they have priorety,
         * in which case they will overwrite when copied.
         */

        /// /// 1. Get all the deployed ModManifests.
        List<ModManifest> deployedMods = new ArrayList<ModManifest>();
        File[] files = Path.of(game.getInstallPath(), MANIFEST_DIR).toFile().listFiles();
        for (File i : files) {
            try {
                deployedMods.add(new ModManifest(ModIO.readModManifest(i)));
            } catch (Exception e) {
                System.err.println("\t\t❌ No exsisting deployed mods found!");
                return false;
            }
        } // for each File

        /// /// 2. Check each ModManifest for conflicts.
        for (ModManifest tmpManifest : deployedMods) {
            System.out.println("\t\t\tChecking Manifest: " + tmpManifest.getId()); // TODO debugging
            for (ModFile tmpMF : tmpManifest.getContentsArr()) {
                if (tmpMF.getFilePath().contentEquals(modFilePath)) {

                    if (loadOrder < tmpManifest.getLoadOrder())
                        return false; // If the new Mod is loaded earlier and has an overruling mod, cease checking.
                    else
                        break; // After match is found, stop checking the same file.

                }
            } // for each ModFile
        } // for each manifest

        return true; // If this is reached, the file has load priority over all other Mod.
    } // loadOrderCopy()

} // Class
