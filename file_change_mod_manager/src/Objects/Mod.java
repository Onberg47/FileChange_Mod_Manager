/*
 * Author: Stephanos B
 * Date: 15/12/2025
*/

package Objects;

import java.util.Date;

/**
 * Represents a Mod.JSON file for tracking contents and metadata of a Mod.
 * 
 * @author Stephanos B
 */
public class Mod {

    private String id; // Unique identifier for the Mod.
    private String name; // User-friendly name, doubles as the filename for the ModFile.
    private String version;
    private ModFile[] contentsArr; // Array of contents inside the ModFile.

    // non-essential fields
    private String gameId; // ID of the Game this Mod is for.
    private Date downloadDate; // Used for update checks.
    private String description;
    private String downloadLink;

    /**
     * Used to ensure Json Keys are consistent.
     */
    public enum JsonFields {
        id,
        name,
        version,
        description,
        files,
        gameId,
        downloadDate,
        downloadLink
    }

    /**
     * Empty constructor for Mod.
     */
    public Mod() {
        this.name = "Unnamed Mod";
        this.version = "0.0";
        this.downloadDate = new Date(0); // Epoch
        this.description = "No description provided.";
        this.downloadLink = "";
    }

    /**
     * Parameterized constructor for Mod without contents.
     * 
     * @param id          Unique identifier for the Mod.
     * @param name        User-friendly name, doubles as the filename for the Mod.
     * @param version     AUTO-GENERATED
     * @param description The description of the Mod.
     */
    public Mod(String id, String name, String description, String gameId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.gameId = gameId;
        this.version = "0.0";
    }

    /**
     * Parameterized constructor for Mod with contents.
     * 
     * @param name        User-friendly name, doubles as the filename for the Mod
     * @param version     The version of the Mod.
     * @param description The description of the Mod.
     * @param contentsArr The file contents array of the Mod.
     */
    public Mod(String id, String name, String description, String gameId, ModFile[] contentsArr) {
        this(id, name, description, gameId);
        this.contentsArr = contentsArr;
    }

    /// /// /// Getters and Setters /// /// ///

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameID) {
        this.gameId = gameID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(Date downloadDate) {
        this.downloadDate = downloadDate;
    }

    public ModFile[] getContentsArr() {
        return contentsArr;
    }

    public void setContentsArr(ModFile[] contentsArr) {
        this.contentsArr = contentsArr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
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

        String contents = printContents();

        return "Mod [name=" + name + ", version=" + version + ", downloadDate=" + downloadDate + ", description="
                + description + ", downloadLink=" + downloadLink + ",contents=\n" + contents + "]";
    } // toString()

} // Class
