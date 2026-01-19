/*
 * Author: Stephanos B
 * Date: 15/12/2025
*/
package core.objects;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import core.interfaces.MapSerializable;

/**
 * Represents a Game. This sets the general parameters for the Mod deployment.
 * 
 * @author Stephanos B
 */
public class Game implements MapSerializable {

    /** Unique identifier for the Game. Used as the directory name. */
    private String id;
    /** ReleaseVersion of the game for ID creation and debugging */
    private String releaseVersion;
    /** User-friendly name of the Game for interfaces. */
    private String name;
    /** Path where the Game is installed. (Should be absolute) */
    private Path installDirectory;
    /** Path where the Mods are stored. */
    private Path storeDirectory;

    /**
     * Used to ensure Json Keys are consistent.
     */
    public enum Keys {
        ID("id"),
        RELEASE_VERSION("releaseVersion"),
        NAME("name"),
        INSTALL_DIR("installDirectory"),
        STORE_DIR("storeDirectory");

        private final String key;

        private Keys(String key) {
            this.key = key;
        }

        public String key(){
            return this.key;
        }
    }

    public Game() {
        id = "unkown01";
        releaseVersion = "0.0.0";
        name = "Unkown Game";
        installDirectory = Path.of(""); // Null because it must be set.
        storeDirectory = Path.of("");
    }

    /**
     * Parameterized constructor for Game.
     * 
     * @param id               The unique identifier for the Game. Used as the
     *                         directory
     *                         name.
     * @param name             The user-friendly name of the Game.
     * @param installDirectory The installation path of the Game. (Should be
     *                         absolute)
     * @param storeDirectory   The Mods storage path of the Game.
     */
    public Game(String id, String releaseVersion, String name, Path installDirectory, Path storeDirectory) {
        this.id = id;
        this.releaseVersion = releaseVersion;
        this.name = name;
        this.setInstallDirectory(installDirectory);
        this.setStoreDirectory(storeDirectory);
    }

    /// /// /// Implements /// /// ///

    @Override
    public String getObjectType() {
        return ObjectTypes.GAME;
    }

    @Override
    public Game setFromMap(Map<String, Object> map) {
        if (map.containsKey(Keys.ID.key))
            this.setId((String) map.get(Keys.ID.key));

        if (map.containsKey(Keys.NAME.key))
            this.setName((String) map.get(Keys.NAME.key));

        if (map.containsKey(Keys.RELEASE_VERSION.key))
            this.setReleaseVersion((String) map.get(Keys.RELEASE_VERSION.key));

        if (map.containsKey(Keys.INSTALL_DIR.key))
            this.setInstallDirectory((String) map.get(Keys.INSTALL_DIR.key));

        if (map.containsKey(Keys.STORE_DIR.key))
            this.setStoreDirectory((String) map.get(Keys.STORE_DIR.key));

        return this;
    } // setFromMap()

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put(Keys.ID.key, this.getId());
        map.put(Keys.NAME.key, this.getName());
        map.put(Keys.RELEASE_VERSION.key, this.getReleaseVersion());
        map.put(Keys.INSTALL_DIR.key, this.getInstallDirectory().toAbsolutePath().toString());
        map.put(Keys.STORE_DIR.key, this.getStoreDirectory().toAbsolutePath().toString());

        return map;
    } // toMap()

    /// /// /// Getters and Setters /// /// ///
    // #region

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReleaseVersion() {
        return releaseVersion;
    }

    public void setReleaseVersion(String releaseVersion) {
        this.releaseVersion = releaseVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@code /home/game_root/mods/}
     * <br>
     * <br>
     * Absolute Path where a Game's Mods are installed.
     */
    public Path getInstallDirectory() {
        return installDirectory;
    }

    /**
     * 
     * @param installDirectory The installation path of the Game. (Should be
     *                         absolute)
     */
    public void setInstallDirectory(Path installDirectory) {
        this.installDirectory = installDirectory != null ? installDirectory.normalize() : null;
    }

    public void setInstallDirectory(String installDirectoryStr) {
        this.installDirectory = installDirectoryStr != null ? Path.of(installDirectoryStr).normalize() : null;
    }

    /**
     * {@code /home/mods/game/}
     * <br>
     * <br>
     * Absolute Path where a Game's Mods are stored.
     */
    public Path getStoreDirectory() {
        return storeDirectory;
    }

    public void setStoreDirectory(Path storeDirectory) {
        this.storeDirectory = storeDirectory != null ? storeDirectory.normalize() : null;
    }

    public void setStoreDirectory(String storeDirectoryStr) {
        this.storeDirectory = storeDirectoryStr != null ? Path.of(storeDirectoryStr).normalize() : null;
    }

    // #endregion
    /// /// /// Methods /// /// ///

    /**
     * Overrides toString() to provide a string representation of the Game object.
     * 
     * @return A string representation of the Game object.
     */
    @Override
    public String toString() {
        return "Game details: \n\tID = " + id
                + "\n\tName = " + name
                + "\n\tRelease Version: " + releaseVersion
                + "\n\tInstall Path = " + installDirectory
                + "\n\tMods Path = " + storeDirectory;
    } // toString()

} // Class
