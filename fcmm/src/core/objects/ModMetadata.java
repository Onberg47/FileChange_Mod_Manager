package core.objects;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import core.interfaces.MapSerializable;
import core.utils.Logger;

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

    protected Set<String> tagSet;

    private boolean forceIdUpdate = false; // When true the ID will be regenerated on get(Id)

    /**
     * Used to ensure Json Keys are consistent.
     * 
     * @author Stephanos B
     */
    public enum Keys {
        ID("id"),
        GAME_ID("gameId"),
        VERSION("version"),
        LOAD_ORDER("loadOrder"),
        NAME("name"),
        DESCRIPTION("description"),
        DOWNLOAD_SOURCE("downloadSource"),
        DOWNLOAD_DATE("downloadDate"),
        DOWNLOAD_LINK("downloadLink"),
        TAGS("tags");

        private final String key;

        private Keys(String key) {
            this.key = key;
        }

        public String key() {
            return this.key;
        }
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
        this.tagSet = new HashSet<>();
    }

    /**
     * Essentials parameterized constructor for Mod.
     * 
     * @param gameId      The ID of the Game this Mod is for.
     * @param source      The source of the Mod, used for ID generation.
     * @param NAME        User-friendly name, doubles as the filename for the Mod.
     * @param DESCRIPTION The description of the Mod.
     */
    public ModMetadata(String gameId) {
        this();
        this.gameId = gameId;
    }

    /**
     * For childeren casting.
     * 
     * @param gameId
     * @param id
     * @param downloadSource
     * @param version
     * @param name
     * @param description
     * @param loadOrder
     * @param downloadDate
     * @param downloadLink
     * @param forceIdUpdate
     */
    protected ModMetadata(String gameId,
            String id,
            String downloadSource,
            String version,
            String name,
            String description,
            int loadOrder,
            LocalDateTime downloadDate,
            String downloadLink) {

        this.gameId = gameId;
        this.id = id;
        this.downloadSource = downloadSource;
        this.version = version;
        this.name = name;
        this.description = description;
        this.loadOrder = loadOrder;
        this.downloadDate = downloadDate;
        this.downloadLink = downloadLink;
        this.forceIdUpdate = true;
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
        if (map.containsKey(Keys.ID.key))
            this.setId((String) map.get(Keys.ID.key));

        if (map.containsKey(Keys.GAME_ID.key))
            this.setGameId((String) map.get(Keys.GAME_ID.key));

        if (map.containsKey(Keys.VERSION.key))
            this.setVersion((String) map.get(Keys.VERSION.key));

        if (map.containsKey(Keys.LOAD_ORDER.key)) {
            try {
                this.setLoadOrder(Integer.parseInt(map.get(Mod.Keys.LOAD_ORDER.key).toString()));
            } catch (ClassCastException e) {
                Logger.getInstance().error("Casting error, failed to set LoadOrder", e);
            }
        }
        if (map.containsKey(Keys.NAME.key))
            this.setName((String) map.get(Keys.NAME.key));

        if (map.containsKey(Keys.DESCRIPTION.key))
            this.setDescription((String) map.get(Keys.DESCRIPTION.key));

        if (map.containsKey(Keys.DOWNLOAD_SOURCE.key))
            this.setDownloadSource((String) map.get(Keys.DOWNLOAD_SOURCE.key));

        if (map.containsKey(Keys.DOWNLOAD_LINK.key))
            this.setDownloadLink((String) map.get(Keys.DOWNLOAD_LINK.key).toString());

        if (map.containsKey(Keys.DOWNLOAD_DATE.key))
            this.setDownloadDate(LocalDateTime.parse(map.get(Keys.DOWNLOAD_DATE.key).toString()));

        if (map.containsKey(Keys.TAGS.key)) {
            try {
                this.setTagSet(TagParser.parseTags(map.get(Keys.TAGS.key)));
            } catch (ClassCastException e) {
                Logger.getInstance().error("Parsing error, failed to set Tags", e);
            }
        }

        return this;
    } // setFromMap()

    @Override
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();

        map.put(Keys.ID.key, this.getId());
        map.put(Keys.GAME_ID.key, this.getGameId());
        map.put(Keys.VERSION.key, this.getVersion());
        map.put(Keys.LOAD_ORDER.key, this.getLoadOrder());

        map.put(Keys.NAME.key, this.getName());
        map.put(Keys.DESCRIPTION.key, this.getDescription());

        map.put(Keys.DOWNLOAD_DATE.key, this.getDownloadDate().toString());
        map.put(Keys.DOWNLOAD_SOURCE.key, this.getDownloadSource());
        map.put(Keys.DOWNLOAD_LINK.key, this.getDownloadLink());

        map.put(Keys.TAGS.key, this.getTagSet());

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
    protected void setId(String id) {
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

    public Set<String> getTagSet() {
        return this.tagSet;
    }

    public void setTagSet(Set<String> tagSet) {
        this.tagSet = tagSet;
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
     * For parsing Tag input
     */
    private class TagParser {

        public static Set<String> parseTags(Object input) {
            if (input == null) {
                return Collections.emptySet();
            }

            String inputString = input.toString().trim().toLowerCase();

            // Handle empty input
            if (inputString.isEmpty()) {
                return Collections.emptySet();
            }

            // Remove surrounding brackets if present
            if (inputString.startsWith("[") && inputString.endsWith("]")) {
                inputString = inputString.substring(1, inputString.length() - 1);
            }

            // Split by comma and process each tag
            return Arrays.stream(inputString.split(","))
                    .map(String::trim) // Remove leading/trailing whitespace from each tag
                    .filter(tag -> !tag.isEmpty()) // Remove empty tags
                    .map(TagParser::normalizeTag) // Remove internal spaces
                    .collect(Collectors.toSet());
        }

        private static String normalizeTag(String tag) {
            // Remove all internal whitespace characters
            return tag.replaceAll("\\s+", "_");
        }
    }

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
                "Mod Details:\nID: %s | Game ID: %s\n\tVersion: %s\n\tDownload Source: %s\n\tName: %s | Description: %s\n\tLoad Order: %d\n\tDownload Date: %s | Download Link: %s\n\tTags: %s",
                getId(), gameId, version, downloadSource, name, description, loadOrder,
                downloadDate.toString(), downloadLink, tagSet.toString());
    } // toString()

} // Class
