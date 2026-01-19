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

    private final String AppVersion = "4.0.1"; // Program's version
    
    private static final Path configPath = defaultConfig.CONFIG_FILE_PATH;

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
    private final Path DEFAULT_MOD_DIR;

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

    /**
     * Builds an always complete instance with every final value from AppConfig.
     */
    private static class Builder {
        private String appVersion;
        private Path gameDir;
        private Path tempDir;
        private Path trashDir;

        private Path logDir;
        private Path defaultModDir;

        private Path managerDir;

        public Builder fromMap(HashMap<String, String> hMap) {
            this.appVersion = hMap.getOrDefault("APP_VERSION", "0.0");
            this.gameDir = Path.of(hMap.getOrDefault("GAME_DIR", defaultConfig.GAME_DIR.toString())).normalize();
            this.tempDir = Path.of(hMap.getOrDefault("TEMP_DIR", defaultConfig.TEMP_DIR.toString())).normalize();
            this.trashDir = Path.of(hMap.getOrDefault("TRASH_DIR", defaultConfig.TRASH_DIR.toString())).normalize();

            this.logDir = Path.of(hMap.getOrDefault("LOG_DIR", defaultConfig.LOG_DIR.toString())).normalize();
            this.defaultModDir = Path
                    .of(hMap.getOrDefault("DEFAULT_MOD_DIR", defaultConfig.DEFAULT_MOD_DIR.toString()))
                    .normalize();

            this.managerDir = Path.of(hMap.getOrDefault("MANAGER_DIR", defaultConfig.MANAGER_DIR.toString()))
                    .normalize();
            return this;
        }

        public Builder fromConfigFile(Path configIn) {
            HashMap<String, String> hMap;
            try {
                hMap = JsonIO.readHashMap(configIn.toFile());
            } catch (Exception e) {
                System.err.println("❌ Failed to initialize config! " + e.getMessage());
                hMap = defaultConfig.getDefaultMap(); // Use hard-coded fallback HashMap of the default config file.
                e.printStackTrace();
            }
            this.fromMap(hMap);
            return this;
        } // fromConfigFile()

    } // Class Builder

    /**
     * Assign all fields from the Builder
     * 
     * @param builder
     */
    private AppConfig(Builder builder) {
        // Cannot have initializeFromBuilder(), compiler won't allow because values are
        // final, only constructor must set!
        this.GAME_DIR = builder.gameDir;
        this.TEMP_DIR = builder.tempDir;
        this.TRASH_DIR = builder.trashDir;

        this.LOG_DIR = builder.logDir;
        this.DEFAULT_MOD_DIR = builder.defaultModDir;

        this.MANAGER_DIR = builder.managerDir;

        // constants
        this.LINEAGE_DIR = MANAGER_DIR.resolve("lineages").normalize();
        this.BACKUP_DIR = MANAGER_DIR.resolve("backups").normalize();
        this.MANIFEST_DIR = MANAGER_DIR.resolve("manifests").normalize();

        // Config file version is not the same, re-write
        if (!builder.appVersion.equalsIgnoreCase(this.AppVersion)) {
            try {
                System.out.println("Version mismatch.");
                if (instance == null)
                    instance = this;
                this.saveConfig();
            } catch (Exception e) {
                System.err.println("Could not repair Config file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Initialise using the default config file Path.
     */
    private AppConfig() {
        this(new Builder().fromConfigFile(configPath));

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

        } catch (IOException e) {
            System.err.println("❌ Failed to create program directories! Aborting...");
            e.printStackTrace();
            System.exit(1);
        }
    } // AppConfig(Path)

    public AppConfig(HashMap<String, String> hMap) {
        this(new Builder().fromMap(hMap));
    }

    /// /// /// Singleton Pattern /// /// ///

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

    /**
     * Overrides the instance with a fresh constructor call, respects final.
     * 
     * @param hMap new values
     * @return New instance
     */
    private AppConfig getInstance(HashMap<String, String> hMap) {
        synchronized (AppConfig.class) {
            instance = new AppConfig(hMap);
        }
        return instance;
    }

    /// /// /// Mappings /// /// ///

    /**
     * Saves the current Config to the default config location
     */
    public void saveConfig() throws Exception {
        // Ensure parent directories exist
        Path parent = configPath.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        JsonIO.writeHashMap(configPath.toFile(), instance.toMap());
    }

    /**
     * Reload configuration from file.
     */
    public void reloadConfig() throws Exception {
        HashMap<String, String> loadedMap = JsonIO.readHashMap(configPath.toFile());
        this.getInstance(loadedMap);
    }

    /**
     * Update config with new values and save immediately.
     */
    public void updateAndSaveConfig(HashMap<String, String> newValues) throws Exception {
        this.getInstance(newValues);
        saveConfig();
    }

    /**
     * @return A hashMap of all set-able config values.
     */
    public HashMap<String, String> toMap() {
        HashMap<String, String> hMap = new HashMap<String, String>();

        hMap.put("APP_VERSION", AppVersion);
        hMap.put("GAME_DIR", GAME_DIR.toString());
        hMap.put("TEMP_DIR", TEMP_DIR.toString());
        hMap.put("TRASH_DIR", TRASH_DIR.toString());
        hMap.put("LOG_DIR", LOG_DIR.toString());
        hMap.put("DEFAULT_MOD_DIR", DEFAULT_MOD_DIR.toString());
        hMap.put("MANAGER_DIR", MANAGER_DIR.toString());

        return hMap;
    }

    /// /// /// Getters /// /// ///
    // #region

    /**
     * {@code /home/game_root/}
     * <br>
     * <br>
     * Path within Program working directory.
     * 
     */
    public Path getGameDir() {
        return GAME_DIR;
    }

    /**
     * {@code ~Progam/} {@code .temp/}
     * <br>
     * <br>
     * Path within Program working directory.
     * 
     */
    public Path getTempDir() {
        return TEMP_DIR;
    }

    /**
     * {@code ~Progam/} {@code .temp/trash/}
     * <br>
     * <br>
     * Path within Program working directory.
     */
    public Path getTrashDir() {
        return TRASH_DIR;
    }

    /**
     * {@code ~game_root/} {@code manager/}
     * <br>
     * <br>
     * Path from Game_root to where all Mod Manager data is contained.
     */
    public Path getManagerDir() {
        return MANAGER_DIR;
    }

    /**
     * {@code ~game_root/manager/} {@code manifests/}
     * <br>
     * <br>
     * Path from Game_root to mod manifests.
     */
    public Path getManifestDir() {
        return MANIFEST_DIR;
    }

    /**
     * {@code ~game_root/manager/} {@code backup/}
     * <br>
     * <br>
     * Path from Game_root where backups are.
     */
    public Path getBackupDir() {
        return BACKUP_DIR;
    }

    /**
     * {@code ~game_root/manager/} {@code lineage/}
     * <br>
     * <br>
     * Path from Game_root where all file lineages are.
     */
    public Path getLineageDir() {
        return LINEAGE_DIR;
    }

    /**
     * {@code ~Program/} {@code logs/}
     * <br>
     * <br>
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

    /**
     * {@code ~Program/mods/} {add the game_id}
     * <br>
     * <br>
     * Default location to store Mods for each game.
     */
    public Path getDefaultModStorage() {
        return DEFAULT_MOD_DIR;
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