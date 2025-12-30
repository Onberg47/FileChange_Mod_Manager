/*
 * Author: Stephanos B
 * Date: 15/12/2025
*/
package core.objects;

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
    private String installPath;
    /** Path where the Mods are stored. */
    private String modsPath;

    /**
     * Used to ensure Json Keys are consistent.
     */
    public enum JsonFields {
        id,
        releaseVersion,
        name,
        installPath,
        modsPath
    }

    public Game() {
        id = "unkown01";
        releaseVersion = "0.0.0";
        name = "Unkown Game";
        installPath = null; // Null because it must be set.
        modsPath = null;
    }

    /**
     * Parameterized constructor for Game.
     * 
     * @param id          The unique identifier for the Game. Used as the directory
     *                    name.
     * @param name        The user-friendly name of the Game.
     * @param installPath The installation path of the Game. (Should be absolute)
     * @param modsPath    The Mods storage path of the Game.
     */
    public Game(String id, String releaseVersion, String name, String installPath, String modsPath) {
        this.id = id;
        this.releaseVersion = releaseVersion;
        this.name = name;
        this.installPath = installPath;
        this.modsPath = modsPath;
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

    public String getInstallPath() {
        return installPath;
    }

    /**
     * 
     * @param installPath The installation path of the Game. (Should be absolute)
     */
    public void setInstallPath(String installPath) {
        this.installPath = installPath;
    }

    public String getModsPath() {
        return modsPath;
    }

    public void setModsPath(String modsPath) {
        this.modsPath = modsPath;
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
        return "Game details: \n\tID = " + id + "\n\tName = " + name + "\n\tInstall Path = " + installPath
                + "\n\tMods Path = " + modsPath;
    } // toString()

} // Class
