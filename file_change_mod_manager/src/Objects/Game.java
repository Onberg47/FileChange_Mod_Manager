/*
 * Author: Stephanos B
 * Date: 15/12/2025
*/
package Objects;

/**
 * Represents a Game. This sets the general parameters for the Mod deployment.
 * 
 * @author Stephanos B
 */
public class Game {

    private String id; // Unique identifier for the Game. Used as the directory name.
    private String name; // User-friendly name of the Game for interfaces.
    private String installPath; // Path where the Game is installed. (Should be absolute)
    private String modsPath; // Path where the Mods are stored.

    /**
     * Used to ensure Json Keys are consistent.
     */
    public enum JsonFields {
        id,
        name,
        installPath,
        modsPath
    }

    public Game() {
        id = "unkown01";
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
    public Game(String id, String name, String installPath, String modsPath) {
        this.id = id;
        this.name = name;
        this.installPath = installPath;
        this.modsPath = modsPath;
    }

    /// /// /// Getters and Setters /// /// ///

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    /// /// /// Methods /// /// ///

    /**
     * Overrides toString() to provide a string representation of the Game object.
     * 
     * @return A string representation of the Game object.
     */
    @Override
    public String toString() {
        return "Game [id=" + id + ", name=" + name + ", installPath=" + installPath + ", modsPath=" + modsPath + "]";
    } // toString()

} // Class
