/*
 * Author Stephanos B
 * Date: 27/12/2025
 */
package core.config;

import java.nio.file.Path;

/**
 * Contains configuration settings for AppConfig.
 * Using this publically is idential to getting values from AppConfig, so this
 * can be used for testing and later switched for ease of testing.
 * 
 * @author Stephanos B
 */
public abstract class defaultConfig {

    // All variables are package-private to allow direct access for other Config
    // classes but ensures all outside usage matches the AppConfig usage pattern for
    // ease of transitioning to it.

    static final Path CONFIG_FILE = Path.of("mod_manager/config.json");

    // Program:
    // static final Path WORKING_DIR = Path.of("mod_manager/");
    static final Path GAME_DIR = Path.of("mod_manager/games/");
    static final Path TEMP_DIR = Path.of("mod_manager/.temp/");
    static final Path TRASH_DIR = Path.of("mod_manager/.temp/trash/");

    // Mod Manager:
    // static final Path TEMP_DIR = Path.of("mod_manager/.temp/");
    // static final Path TRASH_DIR = Path.of("mod_manager/.temp/trash/");
    static final Path MANAGER_DIR = Path.of(".mod_manager/");
    static final Path MANIFEST_DIR = MANAGER_DIR.resolve("manifests/");
    static final Path BACKUP_DIR = MANAGER_DIR.resolve("backups/");
    static final Path LINEAGE_DIR = MANAGER_DIR.resolve("lineages/");

    static final Path LOG_DIR = MANAGER_DIR.resolve("logs/");

    /// /// /// Getters /// /// ///
    // #region

    /**
     * @deprecated
     *             Marked to ensure any calls are replaced later.
     */
    public static Path getGameDir() {
        return GAME_DIR;
    }

    /**
     * @deprecated
     *             Marked to ensure any calls are replaced later.
     */
    public static Path getConfigFile() {
        return CONFIG_FILE;
    }

    /**
     * @deprecated
     *             Marked to ensure any calls are replaced later.
     */
    public static Path getTempDir() {
        return TEMP_DIR;
    }

    /**
     * @deprecated
     *             Marked to ensure any calls are replaced later.
     */
    public static Path getTrashDir() {
        return TRASH_DIR;
    }

    /**
     * @deprecated
     *             Marked to ensure any calls are replaced later.
     */
    public static Path getManagerDir() {
        return MANAGER_DIR;
    }

    /**
     * @deprecated
     *             Marked to ensure any calls are replaced later.
     */
    public static Path getManifestDir() {
        return MANIFEST_DIR;
    }

    /**
     * @deprecated
     *             Marked to ensure any calls are replaced later.
     */
    public static Path getBackupDir() {
        return BACKUP_DIR;
    }

    /**
     * @deprecated
     *             Marked to ensure any calls are replaced later.
     */
    public static Path getLineageDir() {
        return LINEAGE_DIR;
    }
    // #endregion
} // Class
