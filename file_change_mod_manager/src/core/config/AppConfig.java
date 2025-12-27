/*
 * Author Stephanos B
 * Date: 27/12/2025
 */
package core.config;

import java.nio.file.Path;
import java.util.HashMap;

import core.io.JsonIO;

/**
 * Immutable configuration snapshot
 */
public final class AppConfig {
    /// Directories:
    // #region
    /**
     * From game_root: Where ModFile Linage.jsons are stored.
     */
    private Path LINEAGE_DIR;

    /**
     * From game_root: Where original game files are backed up if to be overridden.
     */
    private Path BACKUP_DIR;

    /**
     * Temporary directory for mod operations.
     */
    public Path TEMP_DIR;

    /**
     * Trash root directory.
     */
    private Path TRASH_DIR;

    /**
     * Where in the game_root are manifests
     */
    private Path MANAGER_DIR;

    /**
     * From game_root: Where are manifests stored.
     */
    private Path MANIFEST_DIR;

    // #endregion

    /// GUI:
    // private int fontSize;

    public AppConfig() {
        this(defaultConfig.CONFIG_FILE);
    }

    public AppConfig(Path configIn) {
        try {
            HashMap<String, String> hMap = JsonIO.readHashMap(configIn.toFile());
            this.BACKUP_DIR = Path.of(hMap.get("BACKUP_DIR"));
            this.LINEAGE_DIR = Path.of(hMap.get("LINEAGE_DIR"));
            this.MANAGER_DIR = Path.of(hMap.get("MANAGER_DIR"));
            this.MANIFEST_DIR = Path.of(hMap.get("MANIFEST_DIR"));
            this.TEMP_DIR = Path.of(hMap.get("TEMP_DIR"));
            this.TRASH_DIR = Path.of(hMap.get("TRASH_DIR"));

        } catch (Exception e) {
            // Rather do this instead of .getOrDefault() to ensure ALL get assigned.
            BACKUP_DIR = defaultConfig.BACKUP_DIR;
            LINEAGE_DIR = defaultConfig.LINEAGE_DIR;
            MANAGER_DIR = defaultConfig.MANAGER_DIR;
            MANIFEST_DIR = defaultConfig.MANIFEST_DIR;
            TEMP_DIR = defaultConfig.TEMP_DIR;
            TRASH_DIR = defaultConfig.TRASH_DIR;

            System.err.println("Failed to initialize config! " + e.getMessage());
            e.printStackTrace();
            return;
        }
    } // AppConfig()

    /// /// /// Getters /// /// ///

    public Path getTempDir() {
        return TEMP_DIR;
    }

    public Path getTrashDir() {
        return TRASH_DIR;
    }

    public Path getManagerDir() {
        return MANAGER_DIR;
    }

    public Path getManifestDir() {
        return MANIFEST_DIR;
    }

    public Path getBackupDir() {
        return BACKUP_DIR;
    }

    public Path getLineageDir() {
        return LINEAGE_DIR;
    }

} // Class