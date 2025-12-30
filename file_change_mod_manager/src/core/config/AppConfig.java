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
 * 
 * @author Stephanos B
 */
public final class AppConfig {
    /// Directories:
    // #region

    /// Program Paths:
    /**
     * From program's location: Where Games are stored.
     */
    private final Path GAME_DIR;
    /**
     * From program's location: Temporary directory for mod operations.
     */
    private final Path TEMP_DIR;
    /**
     * From program's location: Trash root directory.
     */
    private final Path TRASH_DIR;

    /// Mod Mangaer Paths:
    /**
     * From game_root: Where ModFile Linage.jsons are stored.
     */
    private final Path LINEAGE_DIR;
    /**
     * From game_root: Where original game files are backed up if to be overridden.
     */
    private final Path BACKUP_DIR;
    /**
     * From game_root: Where the mod_manager stores active deployment data.
     */
    private final Path MANAGER_DIR;
    /**
     * From game_root: Where are manifests stored.
     */
    private final Path MANIFEST_DIR;

    /// Other
    private final Path LOG_DIR;

    // #endregion

    /// GUI values:
    // #region
    // private int fontSize; // example field.
    // #endregion

    /**
     * Builds an always complete instance with every final value from AppConfig.
     */
    private static class Builder {
        private Path gameDir;
        private Path tempDir;
        private Path trashDir;

        private Path lineageDir;
        private Path backupDir;
        private Path managerDir;
        private Path manifestDir;

        private Path logDir;

        public Builder fromConfigFile(Path configIn) {
            try {
                HashMap<String, String> hMap = JsonIO.readHashMap(configIn.toFile());

                this.gameDir = Path.of(hMap.getOrDefault("GAME_DIR", defaultConfig.GAME_DIR.toString()));
                this.tempDir = Path.of(hMap.getOrDefault("TEMP_DIR", defaultConfig.TEMP_DIR.toString()));
                this.trashDir = Path.of(hMap.getOrDefault("TRASH_DIR", defaultConfig.TRASH_DIR.toString()));

                this.lineageDir = Path.of(hMap.getOrDefault("LINEAGE_DIR", defaultConfig.LINEAGE_DIR.toString()));
                this.backupDir = Path.of(hMap.getOrDefault("BACKUP_DIR", defaultConfig.BACKUP_DIR.toString()));
                this.managerDir = Path.of(hMap.getOrDefault("MANAGER_DIR", defaultConfig.MANAGER_DIR.toString()));
                this.manifestDir = Path.of(hMap.getOrDefault("MANIFEST_DIR", defaultConfig.MANIFEST_DIR.toString()));

                this.logDir = Path.of(hMap.getOrDefault("LOG_DIR", defaultConfig.LOG_DIR.toString()));

            } catch (Exception e) {
                System.err.println("Failed to initialize config! " + e.getMessage());
                e.printStackTrace();
            }
            return this;
        } // fromConfigFile()
    } // Class Builder

    /**
     * Assign all fields from the Builder
     * 
     * @param builder
     */
    private AppConfig(Builder builder) {
        this.GAME_DIR = builder.gameDir;
        this.TEMP_DIR = builder.tempDir;
        this.TRASH_DIR = builder.trashDir;

        this.LINEAGE_DIR = builder.lineageDir;
        this.BACKUP_DIR = builder.backupDir;
        this.MANAGER_DIR = builder.managerDir;
        this.MANIFEST_DIR = builder.manifestDir;

        this.LOG_DIR = builder.logDir;
    }

    /**
     * initialise using the default config file Path.
     */
    public AppConfig() {
        this(defaultConfig.CONFIG_FILE);
    }

    /**
     * initialise using a specified config file Path. Will use default values as a
     * fallback.
     * 
     * @param configIn Path to desired config file.
     */
    private AppConfig(Path configIn) {
        this(new Builder().fromConfigFile(configIn));
    }

    /// /// /// Singleton /// /// ///

    // Singleton instance
    private static volatile AppConfig instance;

    /**
     * Get the config instance. Double-checks locking for better performance.
     * 
     * @return
     */
    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }

    /// /// /// Getters /// /// ///
    // #region

    public Path getGameDir() {
        return GAME_DIR;
    }

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

    public Path getLogDir() {
        return this.LOG_DIR;
    }

    // #endregion

} // Class