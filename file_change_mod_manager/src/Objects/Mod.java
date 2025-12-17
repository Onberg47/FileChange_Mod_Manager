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

    private String gameId; // ID of the Game this Mod is for.
    private String id; // Unique identifier for the Mod.
                       // Convention: source(enum)-name(first 6 chars)-0000(AUTO INCREMENT)
    private int loadOrder; // Load order priority. The higher the number, the later it loads. (default: 1)
    private ModFile[] contentsArr; // Array of contents inside the ModFile.
    private String version;

    private String name; // User-friendly name.
    private String description;

    private ModSource downloadSource;
    private Date downloadDate; // Used for update checks.
    private String downloadLink;

    /**
     * Used to ensure Json Keys are consistent.
     */
    public enum JsonFields {
        id,
        gameId,
        version,
        loadOrder,
        name,
        description,
        files,
        downloadSource,
        downloadDate,
        downloadLink
    } // JsonFields enum

    public enum ModSource {
        NEXUS("nexus"),
        MODDB("moddb"),
        STEAMWORKS("steam"),
        OTHER("other"),
        default_("unkown");

        private final String name;

        ModSource(String string) {
            this.name = string;
        }

        public String getName() {
            return name;
        }

    } // ModSource enum

    /**
     * Empty constructor for Mod.
     */
    public Mod() {

        this.gameId = "unknown_game";
        this.name = "Unnamed Mod";
        this.id = "unknown-unnamed-0000";
        // this.id = generateModId(ModSource.default_, 0);
        this.loadOrder = 1;
        this.version = "1.0";

        this.description = "No description provided.";
        this.downloadSource = ModSource.default_;
        this.downloadDate = new Date(); // Set to current date/time
        this.downloadLink = "No link provided.";
    }

    /**
     * Essentials parameterized constructor for Mod WITHOUT contents.
     * 
     * @param gameId      The ID of the Game this Mod is for.
     * @param source      The source of the Mod, used for ID generation.
     * @param name        User-friendly name, doubles as the filename for the Mod.
     * @param description The description of the Mod.
     */
    public Mod(String gameId, ModSource source, String name) {
        this();
        this.name = name;
        this.gameId = gameId;
        this.downloadSource = source;
        this.id = generateModId(source); // NB: do this last to ensure using input data!
    }

    /**
     * Essentials parameterized constructor for Mod WITH contents.
     * 
     * @param gameId      The ID of the Game this Mod is for.
     * @param source      The source of the Mod, used for ID generation.
     * @param name        User-friendly name, doubles as the filename for the Mod.
     * @param contentsArr The file contents array of the Mod.
     */
    public Mod(String gameId, ModSource source, String name, ModFile[] contentsArr) {
        this(gameId, source, name);
        this.contentsArr = contentsArr;
    }

    /// /// /// Getters and Setters /// /// ///

    public String getName() {
        return name;
    }

    public int getLoadOrder() {
        return loadOrder;
    }

    public void setLoadOrder(int loadOrder) {
        this.loadOrder = loadOrder;
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

    public ModSource getDownloadSource() {
        return downloadSource;
    }

    public void setDownloadSource(ModSource downloadSource) {
        this.downloadSource = downloadSource;
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
     * Generates a unique Mod ID based on the Mod's source, name, and version.
     * 
     * @param source The source of the Mod from the ModSource enum.
     * @return The generated Mod ID.
     */
    private String generateModId(ModSource source) {
        String tmp = this.getName().toLowerCase().replaceAll("[^a-z0-9]", "_");
        String shortName = tmp.length() <= 6 ? tmp : tmp.substring(0, 6);

        return source.getName() + "-" + shortName + "-" + String.format("%04d", version.hashCode() & 0xffff);
    } // generateModId()

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
                "Mod Details:\nID: %s | Game ID: %s\nName: %s | Description: %s\nLoad Order: %d\nDownload Date: %s | Download Link: %s\nContents:\n%s",
                id, gameId, name, description, loadOrder, downloadDate.toString(), downloadLink, printContents());
    } // toString()

} // Class
