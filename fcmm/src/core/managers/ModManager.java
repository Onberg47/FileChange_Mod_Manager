/*
 * Author Stephanos B
 * Date: 16/12/2025
 */
package core.managers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.lang.reflect.InaccessibleObjectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import core.config.AppConfig;
import core.interfaces.MapSerializable;
import core.io.JsonIO;
import core.objects.FileLineage;
import core.objects.FileVersion;
import core.objects.Game;
import core.objects.GameState;
import core.objects.Mod;
import core.objects.ModFile;
import core.objects.ModManifest;
import core.objects.ModMetadata;
import core.utils.DateUtil;
import core.utils.FileUtil;
import core.utils.HashUtil;
import core.utils.Logger;
import core.utils.ScannerUtil;

/**
 * Provides the core functionality for managing Mods and all related integrity
 * steps of a given game.
 * This is the only method that interacts directly with:
 * ModManifests; GameStates
 * 
 * @author Stephanos B
 */
public class ModManager {
    private static final AppConfig config = AppConfig.getInstance();
    private static final Logger log = Logger.getInstance();

    private final Path GAME_ROOT_PATH; // Path to the Game_Root directory where mods are deployed.
    private Game game;
    private GameState gameState;

    // Comes from config.
    private final Path MANAGER_DIR;

    private final Path BACKUP_DIR;
    private final Path LINEAGE_DIR;
    private final Path MANIFEST_DIR;
    private final Path TEMP_DIR;
    private final Path TRASH_DIR;
    private final Path GAMESTATE_PATH;

    /**
     * Required constructor to specify the game to manage mods for.
     * 
     * @param game
     */
    public ModManager(Game game) {
        this.game = game;
        GAME_ROOT_PATH = game.getInstallDirectory();

        BACKUP_DIR = config.getBackupDir();
        LINEAGE_DIR = config.getLineageDir();
        MANAGER_DIR = config.getManagerDir();
        MANIFEST_DIR = config.getManifestDir();
        TEMP_DIR = config.getTempDir();
        TRASH_DIR = config.getTrashDir();

        GAMESTATE_PATH = GAME_ROOT_PATH.resolve(MANAGER_DIR.toString(), GameState.FILE_NAME);

        try {
            gameState = GameState.loadFromFile(GAMESTATE_PATH);
        } catch (Exception e) {
            gameState = new GameState();

            // If the manager directory exits, thn there should be a GameState.
            if (Files.exists(game.getInstallDirectory().resolve(MANAGER_DIR)))
                log.warning("Could not find GameState.", e);
        }
    } // Constructor

    /// /// /// Core Methods /// /// ///
    // #region

    /**
     * Compiles a new Mod from source files located in {@code ./temp}
     * The compiled mod is stored in the game's mod storage, ready for deployment.
     * 
     * @param dirName The directory name of the mod located in ./temp, doubles as
     *                the mod's name.
     * @param metaMap A HashMap of the Mod's meta data. For the CLI this comes from
     *                {@code collectUserMetadata()} otherwise the GUI will pass it.
     * @return Complete Mod that was created. Allows quick access to the exact data
     *         written without needing to read the JSON. (Mainly for data checking)
     */
    public ModManifest compileMod(final String dirName, final Map<String, Object> metaMap) throws Exception {
        final Path tempDir = TEMP_DIR.resolve(dirName);

        /// 1. Verify Directory is valid.
        if (!Files.exists(tempDir) && Files.isDirectory(tempDir)) { // Verify the dirName given is a valid directory.
            throw new Exception("No such directory found: " + tempDir.toString());
        } else if (!Files.isDirectory(tempDir)) {
            // TODO If path leads to a zip, attempt to extract.
            // Path tempDir = extractToTemp(downloadedZip);
        }
        log.info(0, "📦 Processing meta data: " + tempDir.getFileName());

        /// 2. Process passed HashMap to extract Mod data.
        ModManifest manifest = new ModManifest();
        manifest.setGameId(game.getId());
        try {
            manifest.setFromMap(metaMap);
        } catch (Exception e) {
            throw new Exception("Failed to prase meta data: " + e.getMessage(), e);
        }
        manifest.generateModId(); // Can only create an id once required fields are collected.

        if (!LockManager.lockTempDir(tempDir)) // Lock before reading
            throw new Exception("Could not lock temporary directory");
        try {
            log.info(1, "Readig contents of Mod: " + tempDir.getFileName());

            /// 3. Analyze exsisting files, generate ModFile objects with hashes
            manifest.setContentsArr(FileUtil.getDirectoryModFiles(tempDir, tempDir).toArray(new ModFile[0]));

            /// 4. Once the Mod is complete, the Mod.JSON file can be created.
            log.info(0, "📦 Writing manifest...");
            Path storagePath = game.getStoreDirectory().resolve(manifest.getId()); // Path where the Mods will be
                                                                                   // stored.

            try {
                Path path = tempDir.resolve(MANIFEST_DIR.toString(), manifest.getId() + ".json");
                FileUtil.deleteDirectory(path.getParent());
                Files.createDirectories(path.getParent());
                // Write to JSON
                JsonIO.write(manifest, path.toFile());
                log.info(1, "✔ Written! to: " + path.toString());

            } catch (FileNotFoundException e) {
                throw new Exception("Failed to write Manifest: "
                        + storagePath.resolve(MANIFEST_DIR.toString(), manifest.getId() + ".json").toString(), e);
            } catch (Exception e) {
                throw new Exception("Failed to write manifest: " + e.getMessage(), e);
            }

            /// 5. Final operation: Move to .mod_storage/game_id/mod_id_version /
            try {
                log.info(0, "📦 New Mod " + manifest.getId() + " complete! Attempting to move Mod to: " + storagePath);
                // Delete target directory to preven conflicts.
                if (Files.exists(storagePath)) {
                    FileUtil.deleteDirectory(storagePath);
                }
                Files.move(tempDir, storagePath);
                log.info(0, "✔ done.", "Move complete. Finished.");
                return manifest;
            } catch (IOException e) {
                // thrown by deleteDirectory()
                throw new Exception("Failed to move or delete exsisting Mod data at path: " + storagePath.toString(),
                        e);
            } catch (Exception e) {
                throw new Exception("Failed to move files from temp: " + e.getMessage(), e);
            }
        } finally {
            LockManager.unlockTempDir(tempDir);
        }
    } // modCompileNew()

    /**
     * For GUI. Copies Mod contents to an auto-generated location before compiling.
     * 
     * @param filesDir Path to Mod-contents root directory.
     * @param metaMap  Expected Map of mod Data for compiler.
     * @throws Exception
     */
    public ModManifest compileMod(final Path filesDir, final Map<String, Object> metaMap) throws Exception {
        /// Prepare mod files.
        System.out.println("Compiling from Files from: " + filesDir.toString());

        String modId = metaMap.containsKey("name")
                ? String.format("%05d", metaMap.get("name").toString().hashCode() & 0xffff)
                : "modId"; // should be impossible to not have a name
        String dir = (modId + "__" + DateUtil.getNumericTimestamp());

        Path target = TEMP_DIR.resolve(dir);

        /// Copy files to temp.
        try {
            // Files.copy(filesDir, target);
            FileUtil.copyDirectoryContents(filesDir, target, null);
            log.info(1, "Mod files copied to " + target.toString());
        } catch (Exception e) {
            throw new Exception("Could not copy Mod contents.", e);
        }

        /// Compile like normal.
        return this.compileMod(dir, metaMap);
    } // compileMod()

    /**
     * Deploys the given Mod to the game directory. This does NOT read from a
     * Manifest file, it expects the data to be provided.
     * 
     * @param manifest Ready Manifest instance to work with.
     */
    public void deployMod(ModManifest manifest) throws Exception {
        final String modId = manifest.getId();
        final Path tempDir = TEMP_DIR.resolve(modId + "__" + DateUtil.getNumericTimestamp());
        final Path storedDir = game.getStoreDirectory().resolve(modId);

        log.info(0, "📦 Attempting to deploy Mod " + modId + "...");
        if (!LockManager.lockTempDir(tempDir))
            throw new Exception("Could not lock temporary directory");
        try {

            /// 1. Find and copy the ModManifest's json file.
            try {
                final Path manPath = MANIFEST_DIR.resolve(modId + ".json");
                Files.createDirectories(tempDir.resolve(manPath.getParent()));
                Files.copy(storedDir.resolve(manPath), tempDir.resolve(manPath));
                // copy the manifest first so we have it incase of partial copy.

            } catch (FileNotFoundException e) {
                throw new Exception("Mod manifest does not exsists! "
                        + storedDir.resolve(MANIFEST_DIR.toString(), modId + ".json").toString(), e);
            } catch (IOException e) {
                throw new Exception("Error copying Mod manifest!" + "\n" + e.getStackTrace(), e);
            } catch (Exception e) {
                throw new Exception("Error reading Mod manifest file!" + "\n" + e.getStackTrace(), e);
            }

            /// 2. Copy to temp/{mod_id} Mod Files and verify integrity and if items were
            // left behind.
            try {
                log.info(1, "Copying files to temp...");
                for (ModFile mf : manifest.getContentsArr()) { // Try to copy each file from the Manifest.
                    copyModFile(storedDir, tempDir, mf.getFilePath(), manifest);
                }

            } catch (FileNotFoundException e) {
                throw new Exception("Missing ModFile: " + e.getMessage(), e);
            } catch (IOException e) {
                throw new IOException("Failed IO operation on ModFiles: " + e.getMessage(), e);
            } catch (Exception e) {
                throw new Exception("Failed safe copy operation: " + e.getMessage(), e);
            }

            /// 3. Copy from temp/{mod_id} to game_root and clean temp.
            try {
                FileUtil.copyDirectoryContents(tempDir, GAME_ROOT_PATH, StandardCopyOption.REPLACE_EXISTING);
                log.info(1, "Mod copied from temp to: " + GAME_ROOT_PATH);
                log.info(1, "Cleaning temp...");
                FileUtil.deleteDirectory(tempDir);

                log.info(0, "📦 Mod " + modId + " successfully deployed!");
            } catch (IOException e) {
                throw new Exception("Failed to copy Mod files to temp!", e);
            }

            /// 4. Add to GameState
            this.gameState.appendModOnly(manifest.getAsMod());
            this.gameState.saveToFile(GAMESTATE_PATH);

        } catch (Exception e) {
            throw new Exception("Fatal Error!\n" + e.getMessage() + "\nTemp files remain for review/recovery.", e);
        } finally {
            LockManager.unlockTempDir(tempDir);
        }
    } // deployMod()

    /**
     * Removes the given Mod from the game directory.
     * Follows the Mod's data to safely move files into
     * {@code ./temp/trash/[mod_id]}
     * 
     * @param modId The ID of the Mod to be removed.
     * @see Doc/diagrams/ModFile_trash_logic.png in Project for logic-breakdown.
     */
    public void disableMod(final String modId) throws Exception {
        /// /// 1. Find the Mod's manifest from it's ID and read it.
        log.info(0, "🗑 Trashing mod: " + modId + "...");
        ModManifest manifest;
        final Path targetDir;
        final Path manifestPath = MANIFEST_DIR.resolve(modId + ".json");
        try {
            manifest = (ModManifest) JsonIO.read(GAME_ROOT_PATH.resolve(manifestPath).toFile(),
                    MapSerializable.ObjectTypes.MOD_MANIFEST);
            log.info(1, "✔ Manifest of Mod: " + manifest.getName() + " found!");
        } catch (Exception e) {
            throw e;
        }

        /// /// 2. Use data from manifest to safley remove Mod files.
        log.info(1, "moving files of mod: " + manifest.getName());
        Path src;
        targetDir = TRASH_DIR.resolve(manifest.getId() + "__" + DateUtil.getNumericTimestamp());
        try {
            // Delete any exsiting trash of the Mod and create target directory.
            try {
                if (Files.exists(targetDir)) {
                    // If trashed mod (with same timestamp) exsists delete it because its an error.
                    FileUtil.deleteDirectory(targetDir);
                }
                Files.createDirectories(targetDir.resolve(MANIFEST_DIR)); // create the target for safe
                                                                          // use.
            } catch (IOException e) {
                log.warning("Could not delete exsisting contents of Mod in trash! " + "\n" + e.getMessage()
                        + "\nContinuing...", e);
            }

            Path mfPath; // Path of ModFile entry.
            for (ModFile mf : manifest.getContentsArr()) {
                mfPath = mf.getFilePath();
                src = GAME_ROOT_PATH.resolve(mfPath);
                Path flPath = GAME_ROOT_PATH.resolve(LINEAGE_DIR.resolve(mfPath + ".json"));
                if (!Files.exists(flPath)) {
                    throw new Exception("Error: No File Lineage found at: " + flPath);
                }

                try { // Stop single file errors from haulting entire process.
                    FileLineage fl = (FileLineage) JsonIO.read(
                            flPath.toFile(),
                            MapSerializable.ObjectTypes.FILE_LINEAGE);
                    fl.removeAllOf(modId); // Current Mod has forefit any ownership.

                    if (fl.getStack().isEmpty()) {
                        // if now empty, remove empty lineage as there is no owner left.
                        log.info(1, "⚪ No other owner for: " + mfPath);

                        log.info(2, "Move to trash: " + src.toString() + " to " + targetDir.resolve(mfPath));
                        if (!Files.exists(targetDir.resolve(mfPath)))
                            Files.createDirectories(targetDir.resolve(mfPath).getParent());
                        Files.move(src, targetDir.resolve(mfPath));

                        // Trash empty FileLineage/
                        Path flTarget = targetDir.resolve(LINEAGE_DIR.resolve(mfPath + ".json"));

                        if (!Files.exists(flTarget))
                            Files.createDirectories(flTarget.getParent());
                        Files.move(flPath, flTarget);

                        // cleaning any empty directories...
                        FileUtil.cleanDirectories(GAME_ROOT_PATH, mfPath.getParent());
                        FileUtil.cleanDirectories(GAME_ROOT_PATH, LINEAGE_DIR.resolve(mfPath));

                    } else { // other Owners exsist
                        log.info(1, "⚫ Other owner(s) found for: " + mfPath);

                        log.info(2, "Copy to trash: " + src.toString() + " to " + targetDir.resolve(mfPath));
                        if (!Files.exists(targetDir.resolve(mfPath)))
                            Files.createDirectories(targetDir.resolve(mfPath).getParent());
                        Files.copy(src, targetDir.resolve(mfPath));

                        if (!HashUtil.verifyFileIntegrity(mfPath, fl.peek().getHash())) {
                            // If hashes differ, must trash current and restore.
                            log.info(2, "⚫ File must be restored...");

                            if (fl.peek().getModId().equals(FileVersion.GAME_OWNER)) {
                                // Restore from backup
                                log.info(3, "Restoring from Game Backups...");
                                this.restoreBackup(mf.getFilePath());

                                // Last owner should be GAME, so remove FileLineage.
                                if (fl.getStack().size() == 1) {
                                    log.info(2, "Trashing empty lineage");
                                    Path tmpPath = targetDir
                                            .resolve(LINEAGE_DIR.resolve(mfPath + ".json"));

                                    if (!Files.exists(tmpPath.getParent()))
                                        Files.createDirectories(tmpPath.getParent());
                                    Files.move(flPath, tmpPath);
                                    FileUtil.cleanDirectories(GAME_ROOT_PATH, LINEAGE_DIR.resolve(mfPath).getParent()); // TODO
                                                                                                                        // unify
                                                                                                                        // cleaning
                                } else {
                                    throw new Exception(
                                            "Error: GAME is not the ONLY entry in File Lineage when it should be!");
                                }
                            } else {
                                // Restore from Storage for Mod now current owner.
                                log.info(3, "Restoring from Mod " + fl.peek().getModId() + "'s Storage...");
                                this.restoreFromStorage(fl.peek().getModId(), mf.getFilePath());
                            }

                        } else {
                            // File still exsists and is the same. No action.
                            log.info(2, "⚪ File remained the same.");
                        }

                        // Update FileLineage.
                        // If the fileLineage does not exsist it was deliberately trashed, so donot
                        // re-create it.
                        if (Files.exists(flPath))
                            JsonIO.write(fl, flPath.toFile());
                    }

                    log.info(2, "✔ File Trashed.");
                } catch (IOException e) {
                    // catches Files.move() and Files.createDirectories()
                    throw new Exception("Error moving file! " + mfPath);
                } catch (Exception e) {
                    throw new Exception("Fatal Error: " + e.getMessage(), e);
                }
            } // for each
            log.info(1, "✔ Mod files successfully trashed!");

            /// /// 4. Remove ModManifest from game files.
            log.info(1, "Trashing Mod Manifest...");
            Files.move(GAME_ROOT_PATH.resolve(manifestPath), targetDir.resolve(manifestPath));

            /// /// 3. Remove Mod from GameState
            gameState.removeMod(manifest.getAsMod());
            gameState.saveToFile(GAMESTATE_PATH);

            // clean the .manifest/ if it's empty.
            FileUtil.cleanDirectories(GAME_ROOT_PATH, MANIFEST_DIR);
            log.info(1, "✔", "Cleaned.");

            log.info(0, "🗑 Mod " + modId + " successfully trashed!");
        } catch (NullPointerException e) {
            // If the ModFiles array is empty, this will catch the null exception.
            throw new Exception("The Mod has no contents.", e);
        } catch (IOException e) {
            throw new Exception("Fatal IO Error.", e);
        } catch (Exception e) {
            throw new Exception("Fatal Error! ", e);
        }
    } // trashMod()

    /// /// /// Core Method users /// /// ///

    /**
     * Deploys all mods from in the correct LoadOrder from a GameState.json and
     * saves the file.
     * 
     * @param gState GameState to deploy.
     * @throws Exception Allows fatal throws from deployMod() to propagate.
     */
    public void deployGameState(GameState gState) throws Exception {
        log.info(0, "\n🗄 Starting to deploying GameState...");

        // Try to acquire lock
        if (!LockManager.lockDirectory(game.getId(), game.getInstallDirectory()))
            throw new InaccessibleObjectException("Game directory is locked by another process");

        try {
            List<Mod> diff = mkGameStateDif(gState).getDeployedMods()
                    .stream()
                    .sorted(Comparator.comparing(Mod::isEnabled)) // Get disabled first
                    .toList();
            int changeMax = diff.size();
            int i = 0;
            for (Mod mod : diff) {

                if (mod.isEnabled()) {
                    // log.logEntry("Installing enabled mod: " + mod.getId());
                    deployMod(
                            this.getModManifestById(
                                    mod.getId()).setFromMap(mod.toMap()));
                    // Passes the updated (re-ordered) version from the GameState.
                } else {
                    // log.logEntry("Trashing disabled mod: " + mod.getId());
                    disableMod(mod.getId());
                }
                i++;
                log.info(0, "\n" + Logger.progressBar(i, changeMax));

            } // for each Mod
        } finally {
            // Always release lock
            LockManager.unlockDirectory(game.getId());
        }
        log.info(0, "\n🗄 Done deploying GameState.");
    } // deployGameState()

    ///

    /**
     * Re-write a ModManifest from new, partial data. Does not support re-compiling
     * files.
     * 
     * @param modId   mod Id of Mod to edit.
     * @param metaMap Map of values to change.
     * @throws Exception
     */
    public void editMod(final String modId, final HashMap<String, Object> metaMap) throws Exception {
        ModManifest manifest;
        Path path = game.getStoreDirectory().resolve(modId, MANIFEST_DIR.toString(), modId + ".json");
        int loadOrder = -1;
        log.info(0, "Editting Mod: " + modId);

        /// Trash old if was installed
        if (gameState.containsMod(modId)) {
            loadOrder = gameState.getLoadOrder(modId);
            disableMod(modId);
        }

        /// Write file with changes
        manifest = (ModManifest) JsonIO.read(path.toFile(), MapSerializable.ObjectTypes.MOD_MANIFEST);
        manifest = this.getModManifestById(modId).setFromMap(metaMap);
        JsonIO.write(manifest, path.toFile());

        /// Restore if was installed
        if (loadOrder > -1) {
            manifest.setLoadOrder(loadOrder);
            deployMod(manifest);
        }

        log.info(0, "Mod " + modId + " has been editted to:\n" + manifest.toString());
    }

    /**
     * Used to Update Mods when fileds changed require the Mod to be recompiled
     * because of new files or the ID has changed.
     * 
     * @param modId    Initial ID of Mod prior to updating.
     * @param filesDir Path to new files or Null to skip and use exsisting files.
     * @param metaMap  Map of values to change.
     * @throws Exception
     */
    public void updateMod(final String modId, Path filesDir, final Map<String, Object> metaMap) throws Exception {
        ModManifest manifest = new ModManifest();
        int loadOrder = -1;
        log.info(0, "Updating Mod: " + modId);

        /// Trash if was installed.
        if (gameState.containsMod(modId)) {
            loadOrder = gameState.getLoadOrder(modId);
            disableMod(modId);
        }

        /// Compile new Manifest.
        if (filesDir == null) { // get old contents path.
            filesDir = game.getStoreDirectory().resolve(modId);
        }
        manifest = compileMod(filesDir, metaMap); // this will also write the file

        /// Delete old version if present.
        if (!manifest.getId().equals(modId)) {
            log.info("Mod ID has changed. Deleting old version.");
            FileUtil.deleteDirectory(game.getStoreDirectory().resolve(modId));
        }

        /// Restore if was installed.
        if (loadOrder > -1) {
            deployMod(manifest);
        }
    }

    /**
     * Deletes (moves to trash with a timestamp) a Mod from storage if it is not
     * already installed.
     * 
     * @param modId The ID of the Mod to be deleted.
     * @throws InaccessibleObjectException If the mod is currenlty installed.
     */
    public void deleteMod(final String modId) throws Exception {
        log.info(0, "Mod: " + modId + " is to be deleted...");

        /// 1. Check the mod is not installed anywhere.
        if (gameState.containsMod(modId))
            throw new InaccessibleObjectException("Cannot delete as the Mod is currently deployed");

        /// 2. Delete the mod.
        Files.move(
                game.getStoreDirectory().resolve(modId),
                TRASH_DIR.resolve(modId + "__" + DateUtil.getNumericTimestamp()));
        log.info(0, "Mod has been moved to trash.");
    }

    // #endregion
    /// /// /// Core Utility /// /// ///
    // #region

    /**
     * Handles the entire process fo copying a specific mod file. This can be used
     * to copy from storage to temp (recommended) or target could be Game_PATH.<br>
     * <br>
     * File conflicts are hard-set to compare against contents of Game_PATH, not the
     * target. (With good reasons)<br>
     * <br>
     * Can support restoring files when mods are re-deployed/re-ordered.
     * 
     * @param sourceDir   The source directory the ModFile is relative to.
     *                    eg: ./mod_storage/mod_id/...
     * @param targetDir   The target directory the ModFile is relative to.
     *                    eg: ./temp/mod_id_timestamp/...
     * @param modFilePath The relative path (String) and file being copied.
     * @param mod         Mod instance being deployed or the source of the new file
     *                    (one in the same)
     * @throws IOException File IO errors.
     * @throws Exception   Other fatal errors.
     * 
     * @see Doc/diagrams/ModFile_copy_logic.png in Project for logic-breakdown.
     */
    private void copyModFile(Path sourceDir, final Path targetDir, Path modFilePath, final ModManifest mod)
            throws Exception {
        final String modId = mod.getId();
        final int loadOrder = mod.getLoadOrder();

        if (!Files.exists(targetDir.getParent())) {
            // If the parent directories don't exsist create them.
            // Therefore the file won't exsist so no conflict.
            try {
                log.info("directory created.", "ModFile directory created: " + targetDir.getFileName());
                Files.createDirectories(targetDir.getParent());
            } catch (IOException e) {
                throw new IOException("Failed to create directorie(s): " + targetDir.getParent(), e);
            }
        }
        FileLineage fl; // declare because no matter what we will write/rewrite.
        final ModFile modFile;
        try {
            modFile = new ModFile(
                    modFilePath,
                    HashUtil.computeFileHash(sourceDir.resolve(modFilePath)),
                    Files.size(sourceDir.resolve(modFilePath)));
        } catch (Exception e) {
            throw new Exception("Failed to construct ModFile: " + e.getMessage(), e);
        }

        final Path lineagePath = LINEAGE_DIR.resolve(modFilePath + ".json"); // where it should be.
        boolean copy = false;
        if (Files.exists(GAME_ROOT_PATH.resolve(modFilePath))) { // If the file exsists (conflict)
            // create and instance of the exsisting ModFile.
            log.info(1, "⚫ Found file conflict, resolving...");

            if (!Files.exists(GAME_ROOT_PATH.resolve(lineagePath))) { // If no FileLineage then it must be a Game
                                                                      // file
                try { // Create BACKUP.
                    log.info(2, "✔ Base Game file found: " + GAME_ROOT_PATH.resolve(modFilePath)
                            + " Creating a backup: " + BACKUP_DIR.resolve(modFilePath + ".backup"));

                    Path backupPath = targetDir.resolve(BACKUP_DIR.resolve(modFilePath + ".backup")); // backup in
                                                                                                      // temp
                    if (!Files.exists(backupPath.getParent()))
                        Files.createDirectories(backupPath.getParent());

                    log.info(2, "Trying to copy: " + GAME_ROOT_PATH.resolve(modFilePath) + " to " + backupPath);
                    Files.copy(GAME_ROOT_PATH.resolve(modFilePath), backupPath);
                } catch (IOException e) {
                    // Clarifying that it is the Game File backup copy that has failed.
                    throw new IOException("Error creating file backup! " + e.getMessage(), e);
                }
                // Setup lineage
                fl = new FileLineage(
                        new ModFile(modFilePath,
                                HashUtil.computeFileHash(sourceDir.resolve(modFilePath)),
                                Files.size(sourceDir.resolve(modFilePath))),
                        FileVersion.GAME_OWNER); // initialize with Game Version
                fl.pushVersion(modId, modFile.getHash()); // Add the new Version
                // COPY
                copy = true;

            } else { // Else FileLineage exsists
                // read exsisting lineage.

                log.info(2, "✔ Exsisting Lineage found.");
                fl = (FileLineage) JsonIO.read(
                        GAME_ROOT_PATH.resolve(lineagePath).toFile(),
                        MapSerializable.ObjectTypes.FILE_LINEAGE);

                try {
                    // If it was top: COPY
                    if (fl.insertOrderedVersion(new FileVersion(modId, modFile.getHash()), gameState, loadOrder) == 0) {
                        copy = true;
                        log.info(1, "✔ Pushed as new owner in lineage.");

                    } else { // NO COPY. Is not the owner.
                        copy = false;
                        log.warning(2, "File is owned by higher prioirty Mod. Inserting as \"Wants to Own\"", null);

                        // This is a fallback check to handle when a mod is re-deployed after it's load
                        // order has been reduced.
                        if (!HashUtil.verifyFileIntegrity(game.getInstallDirectory().resolve(modFilePath),
                                fl.getStack().peek().getHash())) {
                            log.warning(2, "File is not what owner expects! Repairing...", null);
                            try {
                                Files.copy(
                                        game.getStoreDirectory().resolve(fl.getStack().peek().getModId(),
                                                modFilePath.toString()),
                                        targetDir.resolve(modFilePath));
                            } catch (IOException e) {
                                throw new IOException("Failed to restore file from owner.", e);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warning(3, "Did not re-insert Mod, skipping...", e);
                }
            }
            if (HashUtil.verifyFileIntegrity(targetDir, modFile.getHash(), modFile.getSize())) {
                // If the hashes match, then the files are identical.
                log.info(2, "Files are identical, no copy required.");
                copy = false;
            }
        } else { /// No conflict
            log.info(1, "⚪ No found File conflicts.");
            Files.createDirectories(targetDir.resolve(modFilePath).getParent());

            // Make lineage for new file.
            fl = new FileLineage(modFile, modId);
            // COPY
            copy = true;
        }

        // finishing up...
        log.info(2, "Creating Directories for lineage at: " + targetDir.resolve(lineagePath).getParent());
        Files.createDirectories(targetDir.resolve(lineagePath).getParent()); // won't exsist in temp.
        log.info(2, "Writing updated lineage at: " + targetDir.resolve(lineagePath));
        JsonIO.write(fl, targetDir.resolve(lineagePath).toFile()); // write new version in temp.

        if (copy) {
            Files.createDirectories(targetDir.resolve(modFilePath).getParent()); // won't exsist in temp.
            Files.copy(
                    sourceDir.resolve(modFilePath), targetDir.resolve(modFilePath),
                    StandardCopyOption.REPLACE_EXISTING);
            log.info(1, "✔ File copied from: " + sourceDir.resolve(modFilePath) + " to "
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
    private void restoreBackup(final Path modFilePath) throws Exception {
        final Path backup = GAME_ROOT_PATH.resolve(BACKUP_DIR.resolve(modFilePath + ".backup"));
        final Path gameModFile = GAME_ROOT_PATH.resolve(modFilePath);

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
        FileUtil.cleanDirectories(GAME_ROOT_PATH, BACKUP_DIR.resolve(modFilePath + ".backup").getParent());
    } // restoreBackup()

    /**
     * Replaces the exsisting Game-file by restoring a file from a Mod in storage.
     * 
     * @param modId       ModId to retreive matching file from.
     * @param modFilePath ModFile path in manifest to fetch.
     * @throws Exception Throws is Mod Storage file or manifest is missing.
     */
    private void restoreFromStorage(final String modId, final Path modFilePath) throws Exception {
        final Path source = game.getStoreDirectory().resolve(modId, modFilePath.toString());
        if (!Files.exists(source)) {
            throw new FileNotFoundException(
                    "Source file in storage not found: " + source.toString());
        }
        Files.copy(source, GAME_ROOT_PATH.resolve(modFilePath), StandardCopyOption.REPLACE_EXISTING);
    } // restoreFromManifest()

    /**
     * Makes a Diff-GameState that only contains the changes that need to be made to
     * the current instance GameState to make it the same as the passed new
     * GameState.<br>
     * <br>
     * Mods will have Enabled Flags, disabled entires are trash operations. Do not
     * trashAll().<br>
     * <br>
     * This instance is not for being written to a file.
     * 
     * @param newGs A new GameState with changes to apply.
     * @return A GameState with only the changes to apply relative to the current
     *         GameState, where Mods with a LoadOrder of -1 are to be removed.
     */
    private GameState mkGameStateDif(GameState newGs) {
        log.info(1, "Making GameState Diff.");

        /// 1. Go through current GameState
        for (Mod current : gameState.getDeployedMods()) {

            /// .1 Remove un-changed duplicates from newGs.
            if (newGs.containsMod(current.getId()) && newGs.getLoadOrder(current.getId()) == current.getLoadOrder()) {
                newGs.removeMod(current);
                continue;
            }
            /// .2 Add to newGs entries that were removed as disabled.
            else if (gameState.containsMod(current.getId()) && !newGs.containsMod(current.getId())) {
                current.setEnabled(false);
                newGs.addMod(current);
            }
        }

        log.info(1,
                "Finished GameState Diff.",
                "Finished GameState Diff: " + newGs.toString() + "\n");
        return newGs;
    } // mkGameStateDif()

    // #endregion
    /// /// /// Public Helpers /// /// ///
    // #region

    /**
     * Used to collet user data before compileMod is run. Pass it directly to
     * compileMod when using CLI.
     * 
     * @throws Exception
     */
    public static HashMap<String, Object> collectUserMetadata() throws Exception {
        final String[][] queryMatrix = {
                {
                        ModMetadata.Keys.NAME.key(),
                        ModMetadata.Keys.DESCRIPTION.key(),
                        ModMetadata.Keys.VERSION.key(),
                        ModMetadata.Keys.LOAD_ORDER.key(),
                        ModMetadata.Keys.DOWNLOAD_SOURCE.key(),
                        ModMetadata.Keys.DOWNLOAD_LINK.key()
                },
                {
                        "*Display Name",
                        "Description",
                        "*Version (default 1.0)",
                        "Load Order (default 1)",
                        "*Download Source",
                        "Download URL (Mod page)"
                }
        };
        return ScannerUtil.checklistConsole(queryMatrix);
    } // collectUserMetadata()

    /**
     * Get the instance of a Mod.
     * 
     * @param modId
     * @return Null if failed.
     */
    public Mod getModById(final String modId) throws Exception {
        Mod mod = new Mod();
        Path path = game.getStoreDirectory().resolve(modId, MANIFEST_DIR.toString(), modId + ".json");

        try {
            mod = (Mod) JsonIO.read(
                    path.toFile(),
                    MapSerializable.ObjectTypes.MOD_MANIFEST,
                    MapSerializable.ObjectTypes.MOD);
        } catch (InvalidObjectException e) {
            throw new Exception("Mod file does not exsists! " + path.toString(), e);
        } catch (Exception e) {
            throw new FileNotFoundException("Mod file does not exsists! " + path.toString());
        }
        return mod;
    } // getModById()

    /**
     * Get the instance of a ModManifest.
     * 
     * @param modId
     * @return
     */
    public ModManifest getModManifestById(final String modId) throws Exception {
        ModManifest mod = new ModManifest();
        Path path = game.getStoreDirectory().resolve(modId, MANIFEST_DIR.toString(), modId + ".json");

        try {
            mod = (ModManifest) JsonIO.read(
                    path.toFile(),
                    MapSerializable.ObjectTypes.MOD_MANIFEST);
        } catch (InvalidObjectException e) {
            throw new Exception("File is not a ModManifest. " + path.toString(), e);
        } catch (Exception e) {
            throw new FileNotFoundException("Mod manifest does not exsists! " + path.toString());
        }
        return mod;
    } // getModManifestById()

    /**
     * Reads all Mods for a Game in no specific order. Includes both deployed and
     * Stored Mods.<br>
     * <br>
     * Will set Enabled flags and use GameState LoadOrder values.
     * 
     * @return
     * @throws Exception
     */
    public List<Mod> getAllMods() throws Exception {
        List<Mod> allLs = new ArrayList<>();
        try (Stream<Path> paths = Files.list(game.getStoreDirectory())) {
            for (Path path : (Iterable<Path>) paths::iterator) {
                // Process each directory
                if (Files.isDirectory(path)) {
                    try {
                        path = path.resolve(config.getManifestDir().toString(), path.getFileName() + ".json");

                        if (!Files.exists(path)) {
                            log.warning("Mod folder " + path.getFileName() + " is missing a manifest!", null);
                            throw new InvalidObjectException(
                                    "Directory" + path.getFileName() + "is missing a Manifest.");
                        }
                        Mod mod = (Mod) JsonIO.read(
                                path.toFile(),
                                MapSerializable.ObjectTypes.MOD_MANIFEST,
                                MapSerializable.ObjectTypes.MOD);

                        if (gameState.containsMod(mod.getId())) {
                            mod.setLoadOrder(gameState.getLoadOrder(mod.getId()));
                            mod.setEnabled(true);
                        } else {
                            mod.setEnabled(false); // redundant but better be safe.
                        }
                        allLs.add(mod);

                    } catch (InvalidObjectException e) {
                        // Just skips silently. Other errors are caught outside to stop process.
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("Failed to read Storage mods: " + e.getMessage(), e);
        }
        return allLs;
    }

    /**
     * Trashes all deployed Mods, ordered to reduce total file system operations and
     * file restorations.
     * 
     * @throws Exception
     */
    public void disableAllMods() throws Exception {
        if (!LockManager.lockDirectory(game.getId(), game.getInstallDirectory()))
            throw new InaccessibleObjectException("Game directory is locked by another process");
        try {
            if (gameState.getDeployedMods() == null || gameState.getDeployedMods().isEmpty())
                return;

            while (gameState.getDeployedMods().size() > 0) {
                this.disableMod(gameState.getDeployedMods().getFirst().getId());
            }
        } finally {
            LockManager.unlockDirectory(game.getId());
        }
    } // trashAll()

    // #endregion
} // Class