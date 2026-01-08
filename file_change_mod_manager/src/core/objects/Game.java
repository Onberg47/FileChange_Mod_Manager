/*
 * Author: Stephanos B
 * Date: 15/12/2025
*/
package core.objects;

import java.util.HashMap;

import org.json.simple.JSONObject;

import core.interfaces.JsonSerializable;
import core.io.GameIO;

/**
 * Represents a Game. This sets the general parameters for the Mod deployment.
 * 
 * @author Stephanos B
 */
public class Game implements JsonSerializable {

    /** Unique identifier for the Game. Used as the directory name. */
    private String id;
    /** ReleaseVersion of the game for ID creation and debugging */
    private String releaseVersion;
    /** User-friendly name of the Game for interfaces. */
    private String name;
    /** Path where the Game is installed. (Should be absolute) */
    private String installDirectory;
    /** Path where the Mods are stored. */
    private String storeDirectory;

    /**
     * Used to ensure Json Keys are consistent.
     */
    public enum JsonFields {
        id,
        releaseVersion,
        name,
        installDirectory,
        storeDirectory
    }

    public Game() {
        id = "unkown01";
        releaseVersion = "0.0.0";
        name = "Unkown Game";
        installDirectory = null; // Null because it must be set.
        storeDirectory = null;
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
    public Game(String id, String releaseVersion, String name, String installDirectory, String storeDirectory) {
        this.id = id;
        this.releaseVersion = releaseVersion;
        this.name = name;
        this.installDirectory = installDirectory;
        this.storeDirectory = storeDirectory;
    }

    /// /// /// Implements /// /// ///

    @Override
    public String getObjectType() {
        return ObjectTypes.GAME;
    }

    @Override
    public JSONObject toJsonObject() {
        return GameIO.write(this); // keeps IO operations seperate
    } // toJsonObject()

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

    public String getInstallDirectory() {
        return installDirectory;
    }

    /**
     * 
     * @param installDirectory The installation path of the Game. (Should be
     *                         absolute)
     */
    public void setInstallDirectory(String installDirectory) {
        this.installDirectory = installDirectory;
    }

    public String getStoreDirectory() {
        return storeDirectory;
    }

    public void setStoreDirectory(String storeDirectory) {
        this.storeDirectory = storeDirectory;
    }

    // #endregion
    /// /// /// Methods /// /// ///

    /**
     * For GUI.
     * Sets all values of the current Game from the passed Map<> and won't override
     * missing values, allowing for updating instances.
     * 
     * @param metaMap Complete or patially complete Meta Map to read values from.
     */
    public void setFromMap(HashMap<String, String> metaMap) {

        if (metaMap.containsKey("id")) // This prevents missing values being set to null, allowing updates.
            this.setId(metaMap.get("id"));

        if (metaMap.containsKey("releaseVersion"))
            this.setReleaseVersion(metaMap.get("releaseVersion"));

        if (metaMap.containsKey("name"))
            this.setName(metaMap.get("name"));

        if (metaMap.containsKey("installDirectory")) {
            this.setInstallDirectory(metaMap.get("installDirectory"));
        }

        if (metaMap.containsKey("storeDirectory"))
            this.setStoreDirectory(metaMap.get("storeDirectory"));

    } // setFromMap()

    /**
     * For GUI.
     * 
     * @return HashMap<String, String> of all the instance's fields.
     */
    public HashMap<String, String> toMap() {
        HashMap<String, String> metaMap = new HashMap<>();

        metaMap.put("id", this.getId());
        metaMap.put("name", this.getName());
        metaMap.put("releaseVersion", this.getReleaseVersion());
        metaMap.put("installDirectory", this.getInstallDirectory());
        metaMap.put("storeDirectory", this.getStoreDirectory());

        return metaMap;
    } // toMap()

    /**
     * Overrides toString() to provide a string representation of the Game object.
     * 
     * @return A string representation of the Game object.
     */
    @Override
    public String toString() {
        return "Game details: \n\tID = " + id + "\n\tName = " + name + "\n\tInstall Path = " + installDirectory
                + "\n\tMods Path = " + storeDirectory;
    } // toString()

} // Class
