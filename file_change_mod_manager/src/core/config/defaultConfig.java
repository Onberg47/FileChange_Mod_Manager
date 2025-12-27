/*
 * Author Stephanos B
 * Date: 27/12/2025
 */
package core.config;

import java.nio.file.Path;

/**
 * Contains configuration settings for the entrie Core code to share.
 * 
 * @author Stephanos B
 */
public abstract class defaultConfig {

    static final Path CONFIG_FILE = Path.of("mod_manager/config.json");

    /// /// /// Values /// /// ///

    /**
     * Temporary directory for mod operations.
     */
    public static final Path TEMP_DIR = Path.of("mod_manager/.temp/");

    /**
     * Trash root directory.
     */
    public static final Path TRASH_DIR = Path.of("mod_manager/.temp/trash/");

    /**
     * Where in the game_root are manifests
     */
    public static final Path MANAGER_DIR = Path.of(".mod_manager/");

    /**
     * From game_root: Where are manifests stored.
     */
    public static final Path MANIFEST_DIR = MANAGER_DIR.resolve("manifests/");

    /**
     * From game_root: Where original game files are backed up if to be overridden.
     */
    public static final Path BACKUP_DIR = MANAGER_DIR.resolve("backups/");

    /**
     * From game_root: Where ModFile Linage.jsons are stored.
     */
    public static final Path LINEAGE_DIR = MANAGER_DIR.resolve("lineages/");

} // Class
