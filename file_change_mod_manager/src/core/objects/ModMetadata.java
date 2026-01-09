package core.objects;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import core.interfaces.MapSerializable;

/**
 * Represents a Mod.JSON file for tracking contents and metadata of a Mod.
 * 
 * @author Stephanos B
 */
public abstract class ModMetadata implements MapSerializable {

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
    public enum Keys {
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
    public ModMetadata() {
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
    public ModMetadata(String gameId, String downloadSource, String name) {
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
    protected ModMetadata(String gameId, String id, String downloadSource, String version, String name,
            String description,
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
    public ModMetadata setFromMap(Map<String, Object> map) {
        // if a key is missing, don't set it.
        // Values will be left from constructor default
        if (map.containsKey(Keys.id.toString()))
            this.setId((String) map.get(Keys.id.toString()));

        if (map.containsKey(Keys.gameId.toString()))
            this.setGameId((String) map.get(Keys.gameId.toString()));

        if (map.containsKey(Keys.name.toString()))
            this.setName((String) map.get(Keys.name.toString()));

        if (map.containsKey(Keys.description.toString()))
            this.setDescription((String) map.get(Keys.description.toString()));

        if (map.containsKey(Keys.version.toString()))
            this.setVersion((String) map.get(Keys.version.toString()));

        if (map.containsKey(Keys.downloadSource.toString()))
            this.setDownloadSource((String) map.get(Keys.downloadSource.toString()));

        if (map.containsKey(Keys.downloadLink.toString()))
            this.setDownloadLink((String) map.get(Keys.downloadLink.toString()));

        if (map.containsKey(Keys.downloadDate.toString()))
            this.setDownloadDate(LocalDateTime.parse(map.get(Keys.downloadDate.toString()).toString()));

        if (map.containsKey(Keys.loadOrder.toString())) {
            try {
                this.setLoadOrder(((Long) map.get(Mod.Keys.loadOrder.toString())).intValue());
            } catch (ClassCastException e) {
                System.err.println("Casting error, failed to set LoadOrder. " + e.getMessage());
            }
        }
        return this;
    } // setFromMap()

    @Override
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();

        map.put(Keys.id.toString(), this.getId());
        map.put(Keys.gameId.toString(), this.getGameId());
        map.put(Keys.version.toString(), this.getVersion());
        map.put(Keys.loadOrder.toString(), this.getLoadOrder());

        map.put(Keys.gameId.toString(), this.getName());
        map.put(Keys.gameId.toString(), this.getDescription());

        map.put(Keys.downloadDate.toString(), this.getDownloadDate().toString());
        map.put(Keys.downloadSource.toString(), this.getDownloadSource());
        map.put(Keys.downloadLink.toString(), this.getDownloadLink());

        return map;
    } // toMap()

    /// /// /// Getters and Setters /// /// ///

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        forceIdUpdate = true;
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
        this.id = shortName + "-" +
                String.format("%05d", tmp.hashCode() & 0xffff) + "-" +
                String.format("%05d", version.hashCode() & 0xffff);
        return this.id;
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
