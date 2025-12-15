import java.io.File;

import Objects.Mod;
import Objects.ModFile;

/**
 * Author: Stephanos B
 * Date: 15/12/2025
 */

public class ModManager {

    public static void main(String[] args) throws Exception {
        // Create a test Mod with files
        Mod mod = new Mod("Test Mod", "1.0");
        mod.addFile(new ModFile("data/test.txt", "abc123"));

        // Write to JSON
        ModIO.writeMod(mod, new File("test.json"));
        System.out.println("Written!");

        // Read it back
        Mod readMod = ModIO.readMod(new File("test.json"));
        System.out.println("Read: " + readMod.getName());
    }

    /**
     * Deploys the given Mod to the game directory.
     * 
     * The mod must be unpacked into ./temp/ then the Mod.JSON is read
     * 
     * @param Mod The Mod Object to deploy.
     */
    public void deployMod(Mod Mod) {
        // TODO
    } // deployMod()

    /**
     * Removes the given Mod from the game directory.
     * Follows the Mod.JSON data to safely remove files.
     */
    public void removeMod(Mod Mod) {
        // TODO
    } // removeMod()

    /**
     * Creates a pak/archive Mod for a given set of files and creats a Mod.JSON to
     * allow deployment.
     * The raw mod is expanded/copied into ./temp/{modname} before being packed and
     * a Mod.JSON created.
     */
    public void pakMod() {
        // TODO
    } // pakMod()

    /// /// /// Util Methods /// /// ///

    /**
     * Retrieves a Mod by its path from the Mod.
     * 
     * @param path The path of the Mod to retrieve.
     * @return The Mod object if found, otherwise null.
     */
    public Mod getModByPath(String path) {
        // TODO
        return null;
    } // getModByPath()

} // Class
