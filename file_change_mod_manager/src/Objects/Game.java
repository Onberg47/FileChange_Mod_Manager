package Objects;

/**
 * Represents a Game. This sets the general parameters for the Mod deployment.
 * 
 * @author Stephanos B
 */
public class Game {

    private String name; // User-friendly name of the Game
    private String installPath; // Path where the Game is installed
    private String modsPath; // Path where the Mods are stored

    /**
     * Parameterized constructor for Game.
     * 
     * @param name        The user-friendly name of the Game.
     * @param installPath The installation path of the Game.
     * @param modsPath    The Mods storage path of the Game.
     */
    public Game(String name, String installPath, String modsPath) {
        this.name = name;
        this.installPath = installPath;
        this.modsPath = modsPath;
    }

} // Class
