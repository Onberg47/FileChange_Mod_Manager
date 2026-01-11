/*
 * Author Stephanos B
 * Date: 27/12/2025
 */
package core.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import core.io.JsonIO;

/**
 * Immutable configuration snapshot
 * 
 * @author Stephanos B
 */
public final class AppConfig {
    /// Core:
    // #region

    private static final String AppVersion = "3.0"; // Program's version

    /// Program Paths:
    /**
     * From program's location: Where Games are stored. FROM CONFIG
     */
    private final Path GAME_DIR;
    /**
     * From program's location: Temporary directory for mod operations. FROM CONFIG
     */
    private final Path TEMP_DIR;
    /**
     * From program's location: Trash root directory. FROM CONFIG
     */
    private final Path TRASH_DIR;

    private final Path LOG_DIR;

    /// Mod Mangaer Paths:
    /**
     * From game_root: Where the mod_manager stores active deployment data. FROM
     * CONFIG
     */
    private final Path MANAGER_DIR;
    /**
     * From game_root: Where ModFile Linage.jsons are stored.
     */
    private final Path LINEAGE_DIR;
    /**
     * From game_root: Where original game files are backed up if to be overridden.
     */
    private final Path BACKUP_DIR;
    /**
     * From game_root: Where are manifests stored.
     */
    private final Path MANIFEST_DIR;

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
        private Path logDir;
        private Path managerDir;

        public Builder fromConfigFile(Path configIn) {
            try {
                HashMap<String, String> hMap = JsonIO.readHashMap(configIn.toFile());
                // If the file is missing, this will be null and default values will be used.

                this.gameDir = Path.of(hMap.getOrDefault("GAME_DIR", defaultConfig.GAME_DIR.toString())).normalize();
                this.tempDir = Path.of(hMap.getOrDefault("TEMP_DIR", defaultConfig.TEMP_DIR.toString())).normalize();
                this.trashDir = Path.of(hMap.getOrDefault("TRASH_DIR", defaultConfig.TRASH_DIR.toString())).normalize();
                this.logDir = Path.of(hMap.getOrDefault("LOG_DIR", defaultConfig.LOG_DIR.toString())).normalize();
                this.managerDir = Path.of(hMap.getOrDefault("MANAGER_DIR", defaultConfig.MANAGER_DIR.toString()))
                        .normalize();

            } catch (Exception e) {
                System.err.println("❌ Failed to initialize config! " + e.getMessage());
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
        this.LOG_DIR = builder.logDir;
        this.MANAGER_DIR = builder.managerDir;

        this.LINEAGE_DIR = MANAGER_DIR.resolve("lineages/").normalize();
        this.BACKUP_DIR = MANAGER_DIR.resolve("backups/").normalize();
        this.MANIFEST_DIR = MANAGER_DIR.resolve("manifests/").normalize();
    }

    /**
     * Initialise using the default config file Path.
     */
    public AppConfig() {
        this(defaultConfig.CONFIG_FILE);
    }

    /**
     * Initialise using a specified config file Path. Will use default values as a
     * fallback.
     * 
     * @param configIn Path to desired config file.
     */
    private AppConfig(Path configIn) {

        if (!Files.exists(configIn)) {
            System.err.println("❗ Could not find config file. Creating a new one...");
            try {
                if (configIn.getParent() != null && !Files.exists(configIn.getParent()))
                    Files.createDirectories(configIn.getParent());

                JsonIO.writeHasMap(configIn.toFile(), defaultConfig.getDefaultMap());
            } catch (Exception e) {
                System.err.println("❌ Failed! Aborting...");
                e.printStackTrace();
                System.exit(3);
            }
        }
        this(new Builder().fromConfigFile(configIn));

        /// Create program directories.
        try {
            if (!Files.exists(this.GAME_DIR)) {
                // Files.createDirectories(this.GAME_DIR);
                Files.createDirectories(this.GAME_DIR.resolve("icons"));
            }

            if (!Files.exists(this.TEMP_DIR))
                Files.createDirectories(this.TEMP_DIR);

            if (!Files.exists(this.TRASH_DIR))
                Files.createDirectories(this.TRASH_DIR);

            if (!Files.exists(this.LOG_DIR))
                Files.createDirectories(this.LOG_DIR);

        } catch (

        IOException e) {
            System.err.println("❌ Failed to create program directories! Aborting...");
            e.printStackTrace();
            System.exit(1);
        }
    } // AppConfig(Path)

    /// /// /// Singleton /// /// ///

    // Singleton instance
    private static volatile AppConfig instance;

    /**
     * Get the config instance. Double-checks locking for better performance.
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

    /**
     * Path within Program working directory.
     * 
     */
    public Path getGameDir() {
        return GAME_DIR;
    }

    /**
     * Path within Program working directory.
     * 
     */
    public Path getTempDir() {
        return TEMP_DIR;
    }

    /**
     * Path within Program working directory.
     * 
     */
    public Path getTrashDir() {
        return TRASH_DIR;
    }

    /**
     * Path from Game_root to where all Mod Manager data is contained.
     */
    public Path getManagerDir() {
        return MANAGER_DIR;
    }

    /**
     * Path from Game_root to mod manifests.
     */
    public Path getManifestDir() {
        return MANIFEST_DIR;
    }

    /**
     * Path from Game_root where backups are.
     */
    public Path getBackupDir() {
        return BACKUP_DIR;
    }

    /**
     * Path from Game_root where all file lineages are.
     */
    public Path getLineageDir() {
        return LINEAGE_DIR;
    }

    /**
     * Path within the program owrking directory for storing Logs.
     */
    public Path getLogDir() {
        return this.LOG_DIR;
    }

    /**
     * The current version of the application.
     */
    public String getAppVersion() {
        return AppVersion;
    }

    // #endregion

    /**
     * Get information about th current config.
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append(String.format("Current configuration\n\tVersion: %s\nManager:\n", AppVersion));

        str.append(String.format("\t%-15s : %s\n", "Game data dir", GAME_DIR));
        str.append(String.format("\t%-15s : %s\n", "Temp dir", TEMP_DIR));
        str.append(String.format("\t%-15s : %s\n", "Trash dir", TRASH_DIR));
        str.append(String.format("\t%-15s : %s\n", "logging dir", LOG_DIR));

        str.append(String.format("Game Structure:\n\t%-15s : %s\n", "Manager data dir", MANAGER_DIR));

        return str.toString();
    } // toString()

} // Class