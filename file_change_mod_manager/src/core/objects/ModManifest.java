/*
 * Author: Stephanos B
 * Date: 15/12/2025
*/

package core.objects;

import org.json.simple.JSONObject;

import core.io.ModManifestIO;

/**
 * Represents a Mod.JSON file for tracking contents and metadata of a Mod.
 * 
 * @author Stephanos B
 */
public class ModManifest extends Mod {

    private ModFile[] contentsArr; // Array of contents inside the ModFile.

    /**
     * Used to ensure Json Keys are consistent.
     */
    public enum JsonFields {
        files
    } // JsonFields enum

    /**
     * Empty constructor for Mod.
     */
    public ModManifest() {
        super();
    }

    /**
     * Initializes from an exsiting Mod parent.
     * 
     * @param mod Complete parent Mod.
     */
    public ModManifest(Mod mod) {
        super(mod.getGameId(), mod.getId(), mod.getDownloadSource(), mod.getVersion(), mod.getName(),
                mod.getDescription(), mod.getLoadOrder(), mod.getDownloadDate(), mod.getDownloadLink());
    }

    /**
     * Essentials parameterized constructor for Mod WITHOUT contents.
     * 
     * @param gameId The ID of the Game this Mod is for.
     * @param source The source of the Mod, used for ID generation.
     * @param name   User-friendly name, doubles as the filename for the Mod.
     */
    public ModManifest(String gameId, String source, String name) {
        this();
        this.name = name;
        this.gameId = gameId;
        this.downloadSource = source;
        this.id = super.generateModId(source); // NB: do this last to ensure using input data!
    }

    /**
     * Essentials parameterized constructor for Mod WITH contents.
     * 
     * @param gameId      The ID of the Game this Mod is for.
     * @param source      The source of the Mod, used for ID generation.
     * @param name        User-friendly name, doubles as the filename for the Mod.
     * @param contentsArr The file contents array of the Mod.
     */
    public ModManifest(String gameId, String source, String name, ModFile[] contentsArr) {
        this(gameId, source, name);
        this.contentsArr = contentsArr;
    }

    /// /// /// Implements /// /// ///

    @Override
    public String getObjectType() {
        return ObjectTypes.MOD_MANIFEST;
    }

    @Override
    public JSONObject toJsonObject() {
        return ModManifestIO.write(this); // keeps IO operations seperate
    } // toJsonObject()

    /// /// /// Getters and Setters /// /// ///

    /**
     * 
     * @return Array of ModFiles.
     */
    public ModFile[] getContentsArr() {
        return contentsArr;
    }

    public void setContentsArr(ModFile[] contentsArr) {
        this.contentsArr = contentsArr;
    }

    /// /// /// Methods /// /// ///

    /**
     * Adds a ModContent of a file to the contents array.
     * 
     * @param content The ModContent to add.
     */
    public void addFile(ModFile content) {
        if (this.contentsArr == null) {
            this.contentsArr = new ModFile[] { content };
        } else {
            ModFile[] newArr = new ModFile[this.contentsArr.length + 1]; // initialize array if needed
            System.arraycopy(this.contentsArr, 0, newArr, 0, this.contentsArr.length);
            newArr[newArr.length - 1] = content;
            this.contentsArr = newArr;
        }
    } // addContent()

    /**
     * Prints the contents of the Mod.
     * 
     * @return A string representation of the contents of the Mod.
     */
    public String printContents() {
        String contents = "";
        if (contentsArr != null) {
            for (ModFile content : contentsArr) {
                contents += content.toString() + "\n";
            }
        } else {
            contents = "No contents available.";
        }
        return contents;
    } // printContents()

    /**
     * Overrides toString() to provide a string representation of the Mod object.
     * 
     * @return A string representation of the Mod object.
     */
    @Override
    public String toString() {
        return String.format(
                "Mod Details:\nID: %s | Game ID: %s\nVersion: %s\n Download Source: %s\nName: %s | Description: %s\nLoad Order: %d\nDownload Date: %s | Download Link: %s\nContents:\n%s",
                id, gameId, version, downloadSource, name, description, loadOrder, downloadDate.toString(),
                downloadLink, printContents());
    } // toString()

} // Class
