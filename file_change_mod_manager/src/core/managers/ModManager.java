/*
 * Author Stephanos B
 * Date: 16/12/2025
 */
package core.managers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

import core.interfaces.JsonSerializable;
import core.io.JsonIO;
import core.objects.FileLineage;
import core.objects.FileVersion;
import core.objects.Game;
import core.objects.GameState;
import core.objects.Mod;
import core.objects.ModFile;
import core.objects.ModManifest;
import core.utils.DateUtil;
import core.utils.FileUtil;
import core.utils.HashUtil;

/**
 * Provides the core functionality for managing mods of a given game.
 * 
 * @author Stephanos B
 */
public class ModManager {

    // #region
    // From config: TODO: remove `mod_manager/` from paths before packaging!
    private static final Path TEMP_DIR = Path.of("mod_manager/.temp/"); // Temporary directory for mod operations.
    private static final Path TRASH_DIR = Path.of("mod_manager/.temp/trash/"); // Trash directory.

    // Where in the game_root are manifests
    private static final Path MANAGER_DIR = Path.of(".mod_manager/");
    // From game_root: Where are manifests stored.
    private static final Path MANIFEST_DIR = MANAGER_DIR.resolve("manifests/");
    // From game_root: Where original game files are backed up if to be overridden.
    private static final Path BACKUP_DIR = MANAGER_DIR.resolve("backups/");
    // From game_root: Where ModFile Linage.jsons are stored.
    private static final Path LINEAGE_DIR = MANAGER_DIR.resolve("lineages/");

    private Game game;
    private final Path GAME_PATH; // Path to the Game_Root directory where mods are deployed.
                                  // (cannot be determined prior to constuctor but is final.)
    // #endregion

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
    // #region

    /**
     * Compiles a new Mod from source files located in {@code ./temp}
     * The compiled mod is stored in the game's mod storage, ready for deployment.
     * 
     * @param dirName The directory name of the mod located in ./temp, doubles as
     *                the mod's name.
     * @return Complete Mod that was created. Allows quick access to the exact data
     *         written without needing to read the JSON. (Mainly for data checking)
     */
    public Mod compileMod(String dirName) {
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
     * @param modId         The ID of the Mod instance to be removed.
     * @param checkConflict Should file conflicts be with the game_root? (In some
     *                      cases these checks are not needed and add great
     *                      overhead)
     */
    public void deployMod(String modId) {
        ModManifest mod;
        Path tempDir = TEMP_DIR.resolve(modId + "__" + DateUtil.getNumericTimestamp());
        Path storedDir = Path.of(game.getModsPath(), modId);

        try {
            /// 1. Find the Mod's manifest from it's ID and read it.
            System.out.println("📦 Attempting to deploy mod...");
            try {
                mod = (ModManifest) JsonIO.read(storedDir.resolve(MANIFEST_DIR.toString(), modId + ".json").toFile(),
                        JsonSerializable.ObjectTypes.MOD_MANIFEST);
                System.out.println("\tManifest of Mod: " + mod.getName() + " found! ✔");

                Path manPath = MANIFEST_DIR.resolve(modId + ".json");
                Files.createDirectories(tempDir.resolve(manPath.getParent()));
                Files.copy(storedDir.resolve(manPath), tempDir.resolve(manPath)); // copy the manifest first so we have
                                                                                  // it
                                                                                  // incase of partial copy.

            } catch (FileNotFoundException e) {
                throw new Exception("❌ Mod manifest does not exsists! "
                        + storedDir.resolve(MANIFEST_DIR.toString(), modId + ".json").toString(), e);
            } catch (IOException e) {
                throw new Exception("❌ Error copying Mod manifest!" + "\n" + e.getStackTrace(), e);
            } catch (Exception e) {
                throw new Exception("❌ Error reading Mod manifest file!" + "\n" + e.getStackTrace(), e);
            }

            /// 2. Copy to temp/{mod_id} Mod Files and verify integrity and if items were
            // left behind.
            try {
                System.out.println("\tCopying files...");
                for (ModFile mf : mod.getContentsArr()) { // Try to copy each file from the Manifest.
                    copyModFile(storedDir, tempDir, Path.of(mf.getFilePath()), mod);
                }

            } catch (FileNotFoundException e) {
                throw new Exception("❌ Missing File!" + e.getStackTrace(), e);
            } catch (IOException e) {
                throw new Exception("❌ Failed IO operation!" + e.getStackTrace(), e);
            } catch (Exception e) {
                throw new Exception("❌ Failed safe copy operation!" + e.getStackTrace() + e);
            }

            /// 3. Copy from temp/{mod_id} to game_root and clean temp.
            try {
                FileUtil.copyDirectoryContents(tempDir, GAME_PATH, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("\tMod copied from temp to: " + GAME_PATH);
                System.out.println("\tCleaning temp...");
                FileUtil.deleteDirectory(tempDir);

                System.out.println("📦 Mod successfully deployed!");
            } catch (IOException e) {
                throw new Exception("❌ Failed to copy Mod files to temp!", e);
            }

            /// 4. Add to GameState
            this.gameStateAddMod(mod);

        } catch (Exception e) {
            System.err.println("❌ Fatal Error!\n" + e.getMessage() + "\nTemp files remain for review/recovery.");
            e.printStackTrace();
            /*
             * //Not cleaning to allow debugging
             * if (Files.exists(tempDir))
             * try {
             * System.err.println("Cleaning temp/...");
             * FileUtil.deleteDirectory(tempDir);
             * System.err.println("temp/ Cleaning completed.");
             * } catch (IOException f) {
             * System.err.println("❌ ERROR! Failed to clean temp." + f);
             * e.printStackTrace();
             * }
             */
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
                deployMod(mod.getId());
            } // for each Mod

        } catch (NullPointerException e) {
            System.err.println("❌ Nothing to do, GameState has no Mods!");
            return;
        } catch (Exception e) {
            System.err.println("❌ Fatal Error: " + e.getMessage());
            return;
        }
    } // deployGameState()

    /**
     * Removes the given Mod from the game directory.
     * Follows the Mod's data to safely move files into
     * {@code ./temp/trash/[mod_id]}
     * 
     * @param modId The ID of the Mod instance to be removed.
     * @see Doc/diagrams/ModFile_trash_logic.png in Project for logic-breakdown.
     */
    public void trashMod(String modId) {
        /// /// 1. Find the Mod's manifest from it's ID and read it.
        ModManifest mod;
        Path manifestPath = MANIFEST_DIR.resolve(modId + ".json");

        // Create directories in Trash.
        try {
            mod = (ModManifest) JsonIO.read(GAME_PATH.resolve(manifestPath).toFile(),
                    JsonSerializable.ObjectTypes.MOD_MANIFEST);
            System.out.println("\t✔ Manifest of Mod: " + mod.getName() + " found!");
        } catch (Exception e) {
            System.err.println("❌ Mod manifest does not exsists! " + e.getMessage());
            return;
        }

        /// /// 2. Use data from manifest to safley remove Mod files.
        System.out.println("🗑 Removing mod: " + mod.getName() + "\n\tMoving files...");
        try {
            Path src;
            Path targetDir = TRASH_DIR.resolve(mod.getId() + "__" + DateUtil.getNumericTimestamp());

            // Delete any exsiting trash of the Mod and create target directory.
            try {
                if (Files.exists(targetDir)) {
                    // If trashed mod (with same timestamp) exsists delete it because its an error.
                    FileUtil.deleteDirectory(targetDir);
                }
                Files.createDirectories(targetDir.resolve(MANIFEST_DIR)); // create the target for safe use.
            } catch (IOException e) {
                System.err.println("❗ Could not delete exsisting contents of Mod in trash! " + "\n" + e.getMessage()
                        + "\nContinuing...");
            }

            Path mfPath; // Path of ModFile entry.
            for (ModFile mf : mod.getContentsArr()) {
                mfPath = Path.of(mf.getFilePath());
                src = GAME_PATH.resolve(mfPath);
                Path flPath = GAME_PATH.resolve(LINEAGE_DIR.resolve(mfPath + ".json"));
                if (!Files.exists(flPath)) {
                    System.err.println("❗ Error: No File Lineage found at: " + flPath);
                    return;
                }

                try { // Stop single file errors from haulting entire process.
                    FileLineage fl = (FileLineage) JsonIO.read(
                            flPath.toFile(),
                            JsonSerializable.ObjectTypes.FILE_LINEAGE);
                    fl.removeAllOf(modId); // Current Mod has forefit any ownership.

                    if (fl.getStack().isEmpty()) {
                        // if now empty, remove empty lineage as there is no owner left.
                        System.out.println("\t⚪ No other owner for: " + mfPath);

                        System.out.println(
                                "\t\tMove to trash: " + src.toString() + " to " + targetDir.resolve(mfPath));
                        if (!Files.exists(targetDir.resolve(mfPath)))
                            Files.createDirectories(targetDir.resolve(mfPath).getParent());
                        Files.move(src, targetDir.resolve(mfPath));

                        // Trash empty FileLineage/
                        Path flTarget = targetDir.resolve(LINEAGE_DIR.resolve(mfPath + ".json"));

                        if (!Files.exists(flTarget))
                            Files.createDirectories(flTarget.getParent());
                        Files.move(flPath, flTarget);

                        // cleaning any empty directories...
                        FileUtil.cleanDirectories(GAME_PATH, mfPath.getParent());
                        FileUtil.cleanDirectories(GAME_PATH, LINEAGE_DIR);

                    } else { // other Owners exsist
                        System.out.println("\t⚫ Other owner(s) found for: " + mfPath);

                        System.out.println(
                                "\t\tCopy to trash: " + src.toString() + " to " + targetDir.resolve(mfPath));
                        if (!Files.exists(targetDir.resolve(mfPath)))
                            Files.createDirectories(targetDir.resolve(mfPath).getParent());
                        Files.copy(src, targetDir.resolve(mfPath));

                        if (!HashUtil.verifyFileIntegrity(mfPath, fl.peek().getHash())) {
                            // If hashes differ, must trash current and restore.
                            System.out.println("\t\t⚫ File must be restored...");

                            if (fl.peek().getModId().equals(FileVersion.GAME_OWNER)) {
                                // Restore from backup
                                System.out.println("\t\t\tRestoring from Game Backups...");
                                this.restoreBackup(mf.getFilePath());

                                // Last owner should be GAME, so remove FileLineage.
                                if (fl.getStack().size() == 1) {
                                    System.out.println("Trashing empty lineage");
                                    Path tmpPath = targetDir.resolve(LINEAGE_DIR.resolve(mfPath + ".json"));

                                    if (!Files.exists(tmpPath.getParent()))
                                        Files.createDirectories(tmpPath.getParent());
                                    Files.move(flPath, tmpPath);
                                } else {
                                    System.err
                                            .println("❗ GAME is not the ONLY entry in File Lineage when it should be!");
                                }
                            } else {
                                // Restore from Storage for Mod now current owner.
                                System.out
                                        .println("\t\t\tRestoring from Mod " + fl.peek().getModId() + "'s Storage...");
                                this.restoreFromStorage(fl.peek().getModId(), mf.getFilePath());
                            }

                        } else {
                            // File still exsists and is the same. No action.
                            System.out.println("\t\t⚪ File remained the same.");
                        }

                        // Update FileLineage.
                        // If the fileLineage does not exsist it was deliberately trashed, so donot
                        // re-create it.
                        if (Files.exists(flPath))
                            JsonIO.write(fl, flPath.toFile());
                    }

                    System.out.println("\t\t✔ File Trashed.");
                } catch (IOException e) {
                    // catches Files.move() and Files.createDirectories()
                    System.err.println("❗ Error moving file! " + mfPath + "\n" + e.getMessage());
                } catch (Exception e) {
                    System.err.println("❌ Falta Error: " + e.getMessage());
                    e.printStackTrace();
                    return;
                }
            } // for each
            System.out.println("\t✔ Mod files successfully trashed!");

            /// /// 4. Remove ModManifest from game files.
            System.out.print("\tTrashing Mod Manifest...");
            Files.move(GAME_PATH.resolve(manifestPath), targetDir.resolve(manifestPath));

            /// /// 3. Remove Mod from GameState
            this.gameStateRemoveMod(mod);

            // clean the .manifest/ if it's empty.
            FileUtil.cleanDirectories(GAME_PATH, MANIFEST_DIR);
            System.out.println(" ✔");

            System.out.println("🗑 Mod successfully trashed!");
        } catch (NullPointerException e) {
            // If the ModFiles array is empty, this will catch the null exception.
            System.err.println("❌ The Mod has no contents!" + e.getMessage());
        } catch (IOException e) {
            System.err.println("❌ Fatal IO Error! " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Fatal error! " + e.getMessage());
            e.printStackTrace();
        }
    } // trashMod()

    // #endregion
    /// /// /// Core Utility /// /// ///
    // #region

    /**
     * Handles the entire process fo copying a specific mod file. This can be used
     * to copy from storage to temp (recommended) or target could be Game_PATH.
     * File conflicts are hard-set to copare against contents of Game_PATH, not the
     * target. (With good reasons)
     * 
     * 
     * @param sourceDir   The source directory the ModFile is relative to.
     * @param targetDir   The target directory the ModFile is relative to.
     * @param modFilePath The relative path (String) and file being copied.
     * @param mod         Mod instance being deployed or the source of the new file
     *                    (one in the same)
     * @throws IOException File IO errors.
     * @throws Exception   Other fatal errors.
     * 
     * @see Doc/diagrams/ModFile_copy_logic.png in Project for logic-breakdown.
     */
    private void copyModFile(Path sourceDir, Path targetDir, Path modFilePath, Mod mod) throws Exception {
        String modId = mod.getId();
        int loadOrder = mod.getLoadOrder();

        if (!Files.exists(targetDir.getParent())) {
            // If the parent directories don't exsist create them.
            // Therefore the file won't exsist so no conflict.
            try {
                Files.createDirectories(targetDir.getParent());
            } catch (IOException e) {
                throw new IOException("Failed to create directorie(s): " + targetDir.getParent(), e);
            }
        }
        // NOTE: Missing file checks are handled by the JsonIO methods and will
        // propagate.

        FileLineage fl; // declare because no matter what we will write/rewrite.
        ModFile modFile = new ModFile(
                modFilePath.toString(),
                HashUtil.computeFileHash(sourceDir.resolve(modFilePath)),
                Files.size(sourceDir.resolve(modFilePath)));

        Path lineagePath = LINEAGE_DIR.resolve(modFile.getFilePath() + ".json"); // where it should be.
        Boolean copy = false;

        if (Files.exists(GAME_PATH.resolve(modFilePath))) { // If the file exsists (conflict)
            // create and instance of the exsisting ModFile.
            System.out.println("\t⚫ Found file conflict, resolving...");

            if (!Files.exists(GAME_PATH.resolve(lineagePath))) { // If no FileLineage then it must be a Game file
                try { // Create BACKUP.
                    System.out.println("✔ Base Game file found: " + GAME_PATH.resolve(modFilePath)
                            + " Creating a backup: " + BACKUP_DIR.resolve(modFilePath + ".backup"));

                    Path backupPath = GAME_PATH.resolve(BACKUP_DIR.resolve(modFilePath + ".backup"));
                    if (!Files.exists(backupPath.getParent()))
                        Files.createDirectories(backupPath.getParent());

                    System.out.println("Trying to copy: " + GAME_PATH.resolve(modFilePath) + " to " + backupPath);
                    Files.copy(GAME_PATH.resolve(modFilePath), backupPath);
                } catch (IOException e) {
                    // Clarifying that it is the Game File backup copy that has failed.
                    throw new Exception("Error creating file backup! " + e.getMessage(), e);
                }
                // Setup lineage
                fl = new FileLineage(
                        new ModFile(modFilePath.toString(),
                                HashUtil.computeFileHash(sourceDir.resolve(modFilePath)),
                                Files.size(sourceDir.resolve(modFilePath))),
                        FileVersion.GAME_OWNER); // initialize with Game Version
                fl.pushVersion(modId, modFile.getHash()); // Add the new Version
                // COPY
                copy = true;

            } else { // Else FileLineage exsists
                // read exsisting lineage.

                System.out.println("\t\t✔ Exsisting Lineage found.");
                fl = (FileLineage) JsonIO.read(
                        GAME_PATH.resolve(lineagePath).toFile(),
                        JsonSerializable.ObjectTypes.FILE_LINEAGE);

                try {
                    if (fl.insertOrderedVersion(new FileVersion(modId, modFile.getHash()),
                            GAME_PATH.resolve(MANIFEST_DIR), loadOrder) == 0) {
                        // If it was top:
                        fl.pushVersion(modId, modFile.getHash());
                        // COPY
                        copy = true;
                        System.out.println("\t✔ Pushed as new owner in lineage.");

                    } else {
                        // NO COPY! (only case in which no copy is to be made)
                        copy = false;
                        System.out.println("\t\t❗ File is owned by higher prioirty Mod. Inserting as \"Wants to Own\"");
                    }
                } catch (Exception e) {
                    System.out.println("\t\t" + e.getMessage() + "\n\t\t\tDid not re-insert Mod, skipping...");
                }
            }
            if (HashUtil.verifyFileIntegrity(targetDir, modFile.getHash(), modFile.getSize())) {
                // If the hashes match, then the files are identical.
                System.out.println("\t\tFiles are identical, no copy required.");
                copy = false;
            }
        } else { /// No conflict
            System.out.println("\t⚪ No found File conflicts.");
            Files.createDirectories(targetDir.resolve(modFilePath).getParent());

            // Make lineage for new file.
            fl = new FileLineage(modFile, modId);
            // COPY
            copy = true;
        }
        System.out.println("\t\tWriting updated lineage at: " + targetDir.resolve(lineagePath));
        Files.createDirectories(targetDir.resolve(lineagePath).getParent()); // won't exsist in temp.
        JsonIO.write(fl, targetDir.resolve(lineagePath).toFile()); // write new version in temp.

        if (copy) {
            Files.copy(
                    sourceDir.resolve(modFilePath), targetDir.resolve(modFilePath),
                    StandardCopyOption.REPLACE_EXISTING);
            System.out.println("\t✔ File copied from: " + sourceDir.resolve(modFilePath) + " to "
                    + targetDir.resolve(modFilePath) + "\n");
        }

    } // copyModFile()

    /**
     * Replaces the exsisting Game-file with its Backup.
     * 
     * @param modFilePath The relative ModFile path to be restored.
     * @throws FileNotFoundException If there is no backup to restore.
     * @throws IOException           File IO errors.
     * @throws Exception             A process error
     */
    private void restoreBackup(String modFilePath) throws Exception {
        Path backup = GAME_PATH.resolve(BACKUP_DIR.resolve(modFilePath + ".backup"));
        Path gameModFile = GAME_PATH.resolve(modFilePath);

        if (!Files.exists(backup)) {
            throw new FileNotFoundException("COuld not find backup for file " + modFilePath + " --> " + backup);
        }
        if (gameModFile.getParent() != null && !Files.exists(gameModFile.getParent())) {
            // If the parent directories don't exsist create them.
            // Therefore the file won't exsist so no conflict.
            try {
                Files.createDirectories(gameModFile.getParent());
            } catch (IOException e) {
                throw new IOException("Failed to create directorie(s): " + gameModFile.getParent(), e);
            }
        }

        // Move because the backup is used up.
        Files.move(backup, gameModFile, StandardCopyOption.REPLACE_EXISTING);
        // clean BACKUP directories
        FileUtil.cleanDirectories(GAME_PATH, BACKUP_DIR.resolve(modFilePath + ".backup").getParent());
    } // restoreBackup()

    /**
     * Replaces the exsisting Game-file by restoring a file from a Mod in storage.
     * 
     * @param modId       ModId to retreive matching file from.
     * @param modFilePath ModFile path in manifest to fetch.
     * @throws Exception Throws is Mod Storage file or manifest is missing.
     */
    private void restoreFromStorage(String modId, String modFilePath) throws Exception {
        Path source = Path.of(game.getModsPath(), modId, modFilePath);
        if (!Files.exists(source)) {
            throw new FileNotFoundException(
                    "Source file in storage not found: " + source.toString());
        }
        Files.copy(source, GAME_PATH.resolve(modFilePath), StandardCopyOption.REPLACE_EXISTING);
    } // restoreFromManifest()

    // #endregion
    /// /// /// Helpers /// /// ///
    // #region

    /**
     * 
     * @param mod Mod instance to make changes to.
     * @return the updated version of the passed Mod.
     */
    private static Mod collectUserMetadata(Mod mod) {
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

        // TODO Download date. Left to default at .now()

        System.out.print("Download URL (Mod page): ");
        mod.setDownloadLink(scanner.nextLine().trim());

        scanner.close();
        return mod;
    } // collectUserMetadata()

    /// /// GameState

    /**
     * Adds target Mod to the GameState's deployed Mods. Will create it if missing
     * but warns in case of FilePath error.
     * 
     * @param mod Target Mod
     * @throws Exception Any Fatal error.
     */
    private void gameStateAddMod(Mod mod) throws Exception {
        GameState gState;
        Path GsPath = GAME_PATH.resolve(MANAGER_DIR.toString(), GameState.FILE_NAME);

        try {

            if (Files.exists(GsPath))
                gState = (GameState) JsonIO.read(GsPath.toFile(), JsonSerializable.ObjectTypes.GAME_STATE);
            else
                gState = new GameState();
            gState.appendMod(mod);

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
    private void gameStateRemoveMod(Mod mod) throws Exception {
        GameState gState;
        Path GsPath = GAME_PATH.resolve(MANAGER_DIR.toString(), GameState.FILE_NAME);

        if (!Files.exists(GsPath))
            throw new FileNotFoundException("File for GameState is missing!");
        try {
            gState = (GameState) JsonIO.read(GsPath.toFile(), JsonSerializable.ObjectTypes.GAME_STATE);
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

    /**
     * Used to display the contents of the GameState to the user in CLI
     * 
     * @return
     * @throws Exception
     */
    private String loadGameState() throws Exception {
        GameState gState;
        Path GsPath = GAME_PATH.resolve(MANAGER_DIR.toString(), GameState.FILE_NAME);

        if (!Files.exists(GsPath))
            System.out.println("Could not find: " + GameState.FILE_NAME + ", creating it.");
        try {
            gState = (GameState) JsonIO.read(GsPath.toFile(), JsonSerializable.ObjectTypes.GAME_STATE);
            return gState.toString();

        } catch (Exception e) {
            throw new Exception("Failed to add Mod to GameState", e);
        }
    } // readGameState()

    // #endregion
    /// /// /// Public Helpers /// /// ///
    // #region

    /**
     * Get a the toString of the current GameState after reading the file if
     * exsists.
     * 
     * @return
     * @throws Exception If the File is missing.
     */
    public String printGameState() throws Exception {
        return loadGameState();
    }

    /**
     * Get the instance of a Mod.
     * 
     * @param modId
     * @return Null if failed.
     */
    public Mod getModById(String modId) throws Exception {
        Mod mod = new Mod();
        Path path = Path.of(game.getModsPath(), modId).resolve(MANIFEST_DIR.toString(), modId + ".json");

        try {
            mod = (Mod) JsonIO.read(
                    path.toFile(),
                    JsonSerializable.ObjectTypes.MOD);
        } catch (InvalidObjectException e) {
            throw new Exception("❌ Mod file does not exsists! " + path.toString(), e);
        } catch (Exception e) {
            throw new FileNotFoundException("❌ Mod file does not exsists! " + path.toString());
        }
        return mod;
    } // getModById()

    /**
     * Get the instance of a ModManifest.
     * 
     * @param modId
     * @return
     */
    public ModManifest getModManifestById(String modId) throws Exception {
        ModManifest mod = new ModManifest();
        Path path = Path.of(game.getModsPath(), modId).resolve(MANIFEST_DIR.toString(), modId + ".json");

        try {
            mod = (ModManifest) JsonIO.read(
                    path.toFile(),
                    JsonSerializable.ObjectTypes.MOD_MANIFEST);
        } catch (InvalidObjectException e) {
            throw new Exception("❌ Mod file does not exsists! " + path.toString(), e);
        } catch (Exception e) {
            throw new FileNotFoundException("❌ Mod manifest does not exsists! " + path.toString());
        }
        return mod;
    } // getModManifestById()

    // #endregion
} // Class