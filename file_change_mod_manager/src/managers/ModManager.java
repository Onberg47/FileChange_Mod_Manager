package managers;
/*
 * Author Stephanos B
 * Date: 16/12/2025
 */

import utils.DateUtil;
import utils.FileUtil;
import utils.HashUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Date;
import java.util.Scanner;

import interfaces.JsonSerializable;
import io.JsonIO;
import objects.FileLineage;
import objects.FileVersion;
import objects.Game;
import objects.GameState;
import objects.Mod;
import objects.ModFile;
import objects.ModManifest;

/**
 * Provides the core functionality for managing mods of a given game.
 * 
 * @author Stephanos B
 */
public class ModManager {

    private Game game;
    // TODO: remove `mod_manager/` from paths before packaging!
    private static final Path TEMP_DIR = Path.of("mod_manager/.temp/"); // Temporary directory for mod operations.
    private static final Path TRASH_DIR = Path.of("mod_manager/.temp/trash/"); // Trash directory.
    private static final Path MANIFEST_DIR = Path.of(".mod_manager/manifests"); // Where in the game_root are manifests
                                                                                // stored.
    private static final Path BACKUP_DIR = MANIFEST_DIR.resolve(".mod_manager/backups/"); // Where original game files
                                                                                          // are backed up
    // if to be overridden.
    private static final Path LINEAGE_DIR = MANIFEST_DIR.resolve(".mod_manager/lineage/"); // Where ModFile Linage.jsons
                                                                                           // are stored.

    private final Path GAME_PATH; // cannot be determined prior to constuctor but is final.

    // private int indentLevel = 0;

    /**
     * Required constructor to specify the game to manage mods for.
     * 
     * @param game
     */
    public ModManager(Game game) {
        this.game = game;
        GAME_PATH = Path.of(game.getInstallPath());
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
    public Mod modCompileNew(String dirName) {
        /// /// 1. Verify Directory is valid.
        // Path tempDir = Path.of(TEMP_DIR, dirName);
        Path tempDir = TEMP_DIR.resolve(dirName);

        if (!Files.isDirectory(tempDir)) {
            // If path leads to a zip, attempt to extract.
            // Path tempDir = extractToTemp(downloadedZip);
        } else if (!Files.exists(tempDir)) {
            // Verify the dirName given is a valid directory.
            System.err.println("❌ No such directory found: " + tempDir.toString());
            return null;
        }
        System.out.println("📦 Collecting info: " + tempDir.getFileName()); // TODO Debugging

        /// /// 2. Prompt user for metadata (name, version, etc.)
        ModManifest mod;
        mod = new ModManifest(collectUserMetadata(new Mod()));
        mod.setGameId(game.getId());
        mod.generateModId(); // Can only create an id once required fields are collected.

        System.out.println("\n📦 Compiling mod: " + tempDir.getFileName()); // TODO Debugging

        /// /// 3. Analyze exsisting files, generate ModFile objects with hashes
        mod.setContentsArr(FileUtil.getDirectoryModFiles(tempDir, tempDir).toArray(new ModFile[0]));

        /// /// 4. Once the Mod is complete, the Mod.JSON file can be created.
        System.out.println("📦 Writing manifest..."); // TODO Debugging
        Path storagePath = Path.of(game.getModsPath(), mod.getId()); // Path where the Mods will be stored.

        try {
            Path path = tempDir.resolve(MANIFEST_DIR.toString(), mod.getId() + ".json");
            Files.createDirectories(path.getParent());
            // Write to JSON
            JsonIO.write(mod, new File(path.toString()));
            System.out.println("✔ Written! to: " + path.toString()); // Debug

        } catch (FileNotFoundException e) {
            System.out.println("❌ Failed to write Manifest: "
                    + storagePath.resolve(MANIFEST_DIR.toString(), mod.getId() + ".json").toString());
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
            return mod;
        } catch (IOException e) {
            // thrown by deleteDirectory()
            System.err.println("❌ Failed to move or delete exsisting Mod data at path: " + storagePath.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } // modCompileNew()

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
        Path tempDir = TEMP_DIR.resolve(modID);
        Path storedDir = Path.of(game.getModsPath(), modID);

        /// /// 1. Find the Mod's manifest from it's ID and read it.
        System.out.println("📦 Attempting to deploy mod...");
        try {
            // mod = ModManifestIO.read(storedDir.resolve(MANIFEST_DIR.toString(), modID +
            // ".json").toFile());
            mod = (ModManifest) JsonIO.read(storedDir.resolve(MANIFEST_DIR.toString(), modID + ".json").toFile(),
                    JsonSerializable.ObjectTypes.MOD_MANIFEST);
            System.out.println("\tManifest of Mod: " + mod.getName() + " found! ✔");

            Path manPath = MANIFEST_DIR.resolve(modID + ".json");
            Files.createDirectories(tempDir.resolve(manPath.getParent()));
            Files.copy(storedDir.resolve(manPath), tempDir.resolve(manPath)); // copy the manifest first so we have it
                                                                              // incase of partial copy.

        } catch (FileNotFoundException e) {
            System.err.println(
                    "❌ Mod manifest does not exsists! "
                            + storedDir.resolve(MANIFEST_DIR.toString(), modID + ".json").toString());
            return;
        } catch (IOException e) {
            System.err.println("❌ Error copying Mod manifest!" + "\n" + e.getMessage());
            return;
        } catch (Exception e) {
            System.err.println("❌ Error reading Mod manifest file!" + "\n" + e.getMessage());
            return;
        }

        /// /// 2. Copy to temp/{mod_id} Mod Files and verify integrity and if items
        /// were left behind.
        try {
            System.out.println("\tChecking conflicts...");

            // Try to copy each file from the Manifest to find missing/invalid entries.
            File newFile; // File intended to be added from storage.
            for (ModFile mf : mod.getContentsArr()) {
                newFile = new File(storedDir.resolve(mf.getFilePath()).toString());
                /// /// 2.1 Does file exsist in Mod Storage?
                if (!newFile.exists()) {
                    System.err.println(
                            "❌ File in manifest is missing! -> " + newFile.getPath() + "\nStopping deployment!");
                    return;
                }
                /// /// 2.2 If theres a conflict, decide if it should be copied
                if (checkConflict && Files.exists(GAME_PATH.resolve(mf.getFilePath()))) {
                    System.out.println("\t\t❗ Conflict found for: " + mf.getFilePath());

                    /*
                     * If the file exists in game_root there will be a conflict.
                     * This will only copy files with no conflicts OR if they have priority,
                     * in which case they will overwrite when copied.
                     * 
                     * If the file should NOT be copied then continue to the next loop itteration,
                     * skipping the copy lines.
                     */
                    if (!ifLoadPriority(mf.getFilePath(), mod.getId(), mod.getLoadOrder())) {
                        System.out.println("\t\t\tSkipping file: " + Path.of(mf.getFilePath()).getFileName());
                        continue;
                    }
                }

                /// /// 2.3 Verify the file to be copied is what the manifest expects.
                if (!HashUtil.verifyFileIntegrity(newFile.toPath(), mf.getHash(), mf.getSize())) {
                    System.err.println("❌ File integrity failed! File found at " + newFile.getPath()
                            + " does not match the Manifest!");
                    return;
                }

                /// /// 2.4 Only reached if the file should be copied.
                System.out.println("\t\tCopying files from: " + newFile.toPath() + " to "
                        + tempDir.resolve(mf.getFilePath()));
                Files.createDirectories(tempDir.resolve(mf.getFilePath()).getParent());
                Files.copy(newFile.toPath(), tempDir.resolve(mf.getFilePath()), StandardCopyOption.REPLACE_EXISTING);
            } // for each ModFile

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            System.err.println("❌ Failed to copy to Game_Root!" + "\n" + e.getMessage());
            return;
        }

        /// /// 3. Copy from temp/{mod_id} to game_root and clean temp.
        try {
            FileUtil.copyDirectoryContents(tempDir, GAME_PATH, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("\tMod copied from temp to: " + GAME_PATH);
            System.out.println("\tCleaning temp...");
            FileUtil.deleteDirectory(tempDir);

            System.out.println("📦 Mod successfully deployed!");
        } catch (IOException e) {
            System.err.println("❌ Failed to copy Mod files to temp!" + "\n" + e.getMessage());
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
            System.err.println("❌ GameState file does not exsist! " + gameStatePath);
            return;
        }

        GameState gState = new GameState();
        try {
            // gState = GameStateIO.read(gameStatePath.toFile());
            gState = (GameState) JsonIO.read(gameStatePath.toFile(), JsonSerializable.ObjectTypes.GAME_STATE);
            gState.sortDeployedMods();

            for (Mod mod : gState.getDeployedMods()) {
                deployMod(mod.getId(), false); // No checking as the Mods are sorted.
            } // for each Mod

        } catch (NullPointerException e) {
            System.err.println("❌ Nothing to do, GameState has no Mods!");
            return;
        } catch (Exception e) {
            // TODO: handle exception
            return;
        }
    } // deployGameState()

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
        Path manifestPath = GAME_PATH.resolve(MANIFEST_DIR.toString(), modID + ".json");
        // TODO better date format

        try {
            mod = (ModManifest) JsonIO.read(manifestPath.toFile(), JsonSerializable.ObjectTypes.MOD_MANIFEST);
            System.out.println("\tManifest of Mod: " + mod.getName() + " found! ✔");
        } catch (Exception e) {
            System.err.println("❌ Mod manifest does not exsists! " + manifestPath.toString() + "\n" + e.getMessage());
            return;
        }

        /// /// 2. Use data from manifest to safley remove Mod files.
        System.out.println("🗑 Removing mod: " + mod.getName() + "\n\tMoving files...");
        try {
            Path src;
            Path target = TRASH_DIR.resolve(mod.getId() + "__" + DateUtil.getNumericTimestamp());
            Path maxPath = Path.of("");
            Path dataPath;

            // Delete any exsiting trash of the Mod
            try {
                FileUtil.deleteDirectory(target);
            } catch (IOException e) {
                System.err.println("❗ Could not delete exsisting contents of Mod in trash! " + "\n" + e.getMessage()
                        + "\nContinuing...");
            }

            for (ModFile mf : mod.getContentsArr()) {
                dataPath = Path.of(mf.getFilePath());
                src = GAME_PATH.resolve(dataPath);

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

                    System.out.println("\t\tTrying to move: " + src.toString() + " to " + target.resolve(dataPath));
                    Files.move(src, target.resolve(dataPath));
                    System.out.println("✔");
                } catch (IOException e) {
                    // catches Files.move() and Files.createDirectories()
                    System.err.println("❗ Error moving file! " + dataPath + "\n" + e.getMessage());
                }
            } // for each

            System.out.println("\t\tCleaning game directories from: " + maxPath);
            FileUtil.cleanDirectories(GAME_PATH, maxPath); // Clean the mod content.

            System.out.println("\tMod files successfully trashed! ✔");

            /// /// 3. Remove ModManifest from game files.
            System.out.print("\tMoving Mod Manifest...");
            target = target.resolve(MANIFEST_DIR);
            Files.createDirectories(target);
            Files.move(manifestPath, target.resolve(manifestPath.getFileName()));

            // clean the .manifest/ if it's empty.
            FileUtil.cleanDirectories(GAME_PATH, MANIFEST_DIR);
            System.out.println(" ✔");

            System.out.println("🗑 Mod successfully trashed! ✔");
        } catch (NullPointerException e) {
            // If the ModFiles array is empty, this will catch the null exception.
            System.err.println("❌ The Mod has no contents!");
        } catch (IOException e) {
            // Failed to move Manifest!
            e.printStackTrace();
        }
    } // removeMod()

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

        // Download date
        String dateString = new Date().toInstant().toString();
        Instant instant = Instant.parse(dateString);
        mod.setDownloadDate(Date.from(instant));

        System.out.print("Download URL (Mod page): ");
        mod.setDownloadLink(scanner.nextLine().trim());

        scanner.close();
        return mod;
    } // collectUserMetadata()

    // TODO make private after enough testing is done
    /**
     * Decides if a ModFile should be deployed or not based on LoadOrder.
     * 
     * @param modFilePath Path of the ModFile being checked for conflicts.
     * @param modID       The ID of the current Mod. Used to identify it's own
     *                    manifest to avoid testing itself for conflicts (which will
     *                    result in a conflict for every file)
     * @param loadOrder   Load order from the new Mod
     * @return True if the mod has load priority over all other Mod currently
     *         deployed.
     */
    public boolean ifLoadPriority(String modFilePath, String modID, int loadOrder) {
        File manifestDir = GAME_PATH.resolve(MANIFEST_DIR).toFile();

        // Check if directory exists and is readable
        if (!manifestDir.exists() || !manifestDir.isDirectory()) {
            System.err.println("❗ Manifest directory doesn't exist or isn't a directory");
            return true; // If no manifests exist, this mod has priority
        }

        File[] files = manifestDir.listFiles();
        if (files == null) {
            System.err.println("❗ Cannot list files in manifest directory");
            return true;
        }

        for (File i : files) {
            try {
                if (i.getName().compareTo(modID + ".json") == 0) {
                    continue; // skip checking itself.
                }
                // ModManifest manifest = ModManifestIO.read(i);
                ModManifest manifest = (ModManifest) JsonIO.read(i, JsonSerializable.ObjectTypes.MOD_MANIFEST);
                for (ModFile tmpMF : manifest.getContentsArr()) {
                    if (tmpMF.getFilePath().contentEquals(modFilePath)) {
                        if (loadOrder > manifest.getLoadOrder())
                            return false; // If the new Mod is loaded earlier and has an overruling mod, cease checking.
                        else
                            break; // After match is found, stop checking the same file.
                    }
                } // for each ModFile

            } catch (NullPointerException e) {
                System.err.println("❗ Contents array is null for " + i.getName());
                continue;
            } catch (Exception e) {
                System.err.println("❌ Error processing manifest file " + i.getName() + ": "
                        + e.getMessage());
                e.printStackTrace();
                return true;
            }
        } // for each File
        return true; // If this is reached, the file has load priority over all other Mod.
    }// ifLoadPriority()

    /**
     * Handles the entire process fo copying a specific mod file.
     * 
     * 
     * @param sourceDir   The source directory the ModFile is relative to.
     * @param targetDir   The target directory the ModFile is relative to.
     * @param modFilePath The relative path and file being copied.
     * @param mod         Mod instance being deployed or the source of the new file
     *                    (one in the same)
     * @throws Exception   Other fatal errors.
     * @throws IOException File IO errors.
     */
    private void copyModFile(Path sourceDir, Path targetDir, Path modFilePath, Mod mod) throws Exception {
        if (!Files.exists(targetDir.getParent())) {
            // If the parent directories don't exsist create them.
            // Therefore the file won't exsist so no conflict.
            try {
                Files.createDirectories(targetDir.getParent());
            } catch (IOException e) {
                throw new Exception("Failed to create directories: " + targetDir.getParent(), e);
            }
        }

        FileLineage fl; // declare because no matter what we will write/rewrite.
        ModFile modFile = new ModFile(
                modFilePath.toString(),
                HashUtil.computeFileHash(sourceDir.resolve(modFilePath)),
                Files.size(sourceDir.resolve(modFilePath)));

        Path lineagePath = LINEAGE_DIR.resolve(modFile.getFilePath() + ".json"); // where it should be.
        Boolean copy = false;

        if (Files.exists(targetDir)) { // If the file exsists (conflict)
            // create and instance of the exsisting ModFile.

            if (HashUtil.verifyFileIntegrity(targetDir, modFile.getHash(), modFile.getSize())) {
                /// If the hashes match, then the files are identical, (no conflict)
                System.err.println("❗ Files are identical, no action required!");
                return;
            }

            if (!Files.exists(lineagePath)) { // If no FileLineage then it must be a Game file
                try { // Create backup.
                    Files.copy(targetDir.resolve(modFilePath), BACKUP_DIR.resolve(modFile.getFilePath() + ".backup"));
                } catch (IOException e) {
                    // Clarifying that it is the Game File backup copy that has failed.
                    throw new Exception("Error creating file backup! ", e);
                }
                // Setup lineage
                fl = new FileLineage(
                        new ModFile(modFilePath.toString(),
                                HashUtil.computeFileHash(targetDir.resolve(modFilePath)),
                                Files.size(targetDir.resolve(modFilePath))),
                        "GAME"); // initialize with Game Version
                fl.pushVersion(mod.getId(), modFile.getHash()); // Add the new Version
                // COPY
                copy = true;

            } else { // Else FileLineage exsists
                // read exsisting lineage.

                fl = (FileLineage) JsonIO.read(
                        lineagePath.toFile(),
                        JsonSerializable.ObjectTypes.FILE_LINEAGE);

                if (fl.insertOrderedVersion(new FileVersion(mod.getId(), modFile.getHash()), MANIFEST_DIR) == 0) {
                    // If it was top:
                    fl.pushVersion(mod.getId(), modFile.getHash());
                    // COPY
                    copy = true;

                } else {
                    // NO COPY! (only case in which no copy is to be made)
                    copy = false;
                }

            }
        } else { /// No Lineage
            // Make lineage for new file.
            fl = new FileLineage(modFile, mod.getId());
            // COPY
            copy = true;
        }

        JsonIO.write(fl, lineagePath.toFile()); // write/rewrite.
        if (copy)
            Files.copy(
                    sourceDir.resolve(modFilePath), targetDir.resolve(modFilePath),
                    StandardCopyOption.REPLACE_EXISTING);

    } // copyModFile()

} // Class