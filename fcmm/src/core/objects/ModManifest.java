/*
 * Author: Stephanos B
 * Date: 15/12/2025
*/
package core.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a Mod.JSON file for tracking contents and metadata of a Mod.
 * 
 * @author Stephanos B
 */
public class ModManifest extends ModMetadata {

    private ModFile[] contentsArr; // Array of contents inside the ModFile.

    /**
     * Used to ensure Json Keys are consistent.
     */
    public enum Keys {
        FILES("files");

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
    public ModManifest() {
        super();
    }

    /**
     * Initializes from an exsiting Mod parent.
     * 
     * @param mod Complete parent Mod.
     */
    public ModManifest(Mod mod) {
        super(mod.getGameId(),
                mod.getId(),
                mod.getDownloadSource(),
                mod.getVersion(),
                mod.getName(),
                mod.getDescription(),
                mod.getLoadOrder(),
                mod.getDownloadDate(),
                mod.getDownloadLink());
    }

    /**
     * Essentials parameterized constructor for Mod WITHOUT contents.
     * 
     * @param gameId The ID of the Game this Mod is for.
     * @param source The source of the Mod, used for ID generation.
     * @param NAME   User-friendly name, doubles as the filename for the Mod.
     */
    public ModManifest(String gameId) {
        this();
        this.gameId = gameId;
    }

    public Mod getAsMod() {
        Mod mod = new Mod();
        mod.setId(id);
        mod.setLoadOrder(loadOrder);

        mod.setName(name);
        mod.setDescription(description);
        mod.setGameId(gameId);
        mod.setVersion(version);

        mod.setDownloadDate(downloadDate);
        mod.setDownloadLink(downloadLink);
        mod.setDownloadSource(downloadSource);

        return mod;
    }

    /// /// /// Implements /// /// ///

    @Override
    public String getObjectType() {
        return ObjectTypes.MOD_MANIFEST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ModManifest setFromMap(Map<String, Object> map) {
        super.setFromMap(map);

        if (map.containsKey(Keys.FILES.key)) {
            Object rawValue = map.get(Keys.FILES.key);
            if (rawValue instanceof List) {
                List<?> rawList = (List<?>) rawValue;
                List<ModFile> ls = new ArrayList<>();
                for (Object item : rawList) {
                    if (item instanceof Map) {
                        Map<String, Object> itemMap = (Map<String, Object>) item;
                        ls.add(new ModFile().setFromMap(itemMap));
                    }
                }
                this.setContentsArr(ls.toArray(new ModFile[0]));
            }
        }
        return this;
    } // setFromMap()

    @Override
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = super.toMap();

        ArrayList<HashMap<String, Object>> arrLs = new ArrayList<>();
        /// Get map of each modFile stored.
        for (ModFile tmp : this.getContentsArr()) {
            arrLs.add((HashMap<String, Object>) tmp.toMap());
        }
        map.put(Keys.FILES.toString(), arrLs);

        return map;
    } // toMap()

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
        return (super.toString() + "\nContents: " + printContents());
    } // toString()

} // Class
