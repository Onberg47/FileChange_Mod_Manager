/*
 * Author: Stephanos B
 * Date: 15/12/2025
*/
package core.objects;

import java.time.LocalDateTime;

import org.json.simple.JSONObject;

import core.interfaces.JsonSerializable;
import core.io.ModIO;

/**
 * Represents a Mod.JSON file for tracking contents and metadata of a Mod.
 * 
 * @author Stephanos B
 */
public class Mod implements JsonSerializable {

    protected String gameId; // ID of the Game this Mod is for.
    protected String id; // Unique identifier for the Mod.
                         // Convention: source(enum)-name(first 6 chars)-0000(AUTO INCREMENT)
    protected String downloadSource;
    protected String version;

    protected String name; // User-friendly name.
    protected String description;
    protected int loadOrder; // Load order priority. The higher the number, the later it loads. (default: 1)

    protected LocalDateTime downloadDate; // Used for update checks.
    protected String downloadLink;

    private Boolean forceIdUpdate = false; // When true the ID will be regenerated on get(Id)

    /**
     * Used to ensure Json Keys are consistent.
     * 
     * @author Stephanos B
     */
    public enum JsonFields {
        id,
        gameId,
        version,
        loadOrder,
        name,
        description,
        downloadSource,
        downloadDate,
        downloadLink
    } // JsonFields enum

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
        this.downloadSource = "unkown";
        this.downloadDate = LocalDateTime.now(); // Set to current date/time
        this.downloadLink = "No link provided.";
    }

    /**
     * Essentials parameterized constructor for Mod.
     * 
     * @param gameId      The ID of the Game this Mod is for.
     * @param source      The source of the Mod, used for ID generation.
     * @param name        User-friendly name, doubles as the filename for the Mod.
     * @param description The description of the Mod.
     */
    public Mod(String gameId, String downloadSource, String name) {
        this();
        this.name = name;
        this.gameId = gameId;
        this.downloadSource = downloadSource.toLowerCase();
        this.id = generateModId(); // NB: Do this last to ensure using input data!
    }

    /**
     * Complete constructor with ALL fields, including the Auto-generated. Meant for
     * Mod -> Child types initializing.
     * 
     * @param gameIdThe      ID of the Game this Mod is for.
     * @param id             ID of the mod. Only copied to reduce overhead of
     *                       regeneration.
     * @param downloadSource
     * @param version
     * @param name
     * @param description
     * @param loadOrder
     * @param downloadDate
     * @param downloadLink
     */
    protected Mod(String gameId, String id, String downloadSource, String version, String name, String description,
            int loadOrder, LocalDateTime downloadDate, String downloadLink) {
        this.gameId = gameId;
        this.id = id;
        this.downloadSource = downloadSource.toLowerCase();
        this.version = version;
        this.name = name;
        this.description = description;
        this.loadOrder = loadOrder;
        this.downloadDate = downloadDate;
        this.downloadLink = downloadLink;
    }

    /// /// /// Implements /// /// ///

    @Override
    public String getObjectType() {
        return ObjectTypes.MOD;
    }

    @Override
    public JSONObject toJsonObject() {
        return ModIO.write(this); // keeps IO operations seperate
    } // toJsonObject()

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
        if (forceIdUpdate)
            return generateModId();
        else
            return id;
    }

    /**
     * Private to prevent explicit setting of the ID which may break naming rules.
     */
    private void setId(String id) {
        this.id = id;
    }

    /**
     * Cannot explicity set Mod Id. Use `generateModId()` to force Id regeneration.
     * This method is only a reminder.
     * 
     * @deprecated
     */
    public void setId() {
        // does nothing by design.
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameID) {
        this.gameId = gameID;
        forceIdUpdate = true;
    }

    public void setName(String name) {
        this.name = name;
        forceIdUpdate = true;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
        forceIdUpdate = true;
    }

    public String getDownloadSource() {
        return downloadSource;
    }

    public void setDownloadSource(String downloadSource) {
        this.downloadSource = downloadSource.toLowerCase();
        forceIdUpdate = true;
    }

    public LocalDateTime getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(LocalDateTime downloadDate) {
        this.downloadDate = downloadDate;
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
     * Generates a unique Mod ID based on the Mod's key meta-data
     * Protected for internal use.
     * 
     * @param source The source of the Mod from the ModSource enum.
     * @return The generated Mod ID.
     */
    protected String generateModId(String source) {
        forceIdUpdate = false;
        String tmp = this.getName().toLowerCase().replaceAll("[^a-z0-9]", "_");
        String shortName = tmp.length() <= 6 ? tmp : tmp.substring(0, 6);

        // return source + "-" + shortName + "-" + String.format("%04d",
        // version.hashCode() & 0xffff);
        return shortName + "-" +
                String.format("%05d", tmp.hashCode() & 0xffff) + "-" +
                String.format("%05d", version.hashCode() & 0xffff);
    } // generateModId()

    /**
     * Uses the Mod's Name, and Version (hashcode) Ensure all feilds are
     * defined first otherwise default values are used.
     * 
     * @apiNote Manually calling this is optional! If fields that affect the ModID
     *          are
     *          ever changed, the id is forcefully updated when next fetched.
     * 
     * @return The generated Mod Id.
     */
    public String generateModId() {
        this.setId(generateModId(this.getDownloadSource()));
        return this.getId();
    } // generateModId()

    public String printLite() {
        return String.format("ID: %s | Name: %-20s | Order : %-3d", getId(), getName(), getLoadOrder());
    }

    /**
     * Overrides toString() to provide a string representation of the Mod object.
     * 
     * @return A string representation of the Mod object.
     */
    @Override
    public String toString() {
        return String.format(
                "Mod Details:\nID: %s | Game ID: %s\nVersion: %s\n Download Source: %s\nName: %s | Description: %s\nLoad Order: %d\nDownload Date: %s | Download Link: %s",
                getId(), gameId, version, downloadSource, name, description, loadOrder,
                downloadDate.toString(), downloadLink);
    } // toString()

} // Class
