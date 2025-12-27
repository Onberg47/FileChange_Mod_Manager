/**
 * Author Stephanos B
 * Date 22/12/2025
 */
package core.objects;

import java.nio.file.Path;
import java.time.LocalDateTime;

import core.interfaces.JsonSerializable;
import core.io.JsonIO;

/**
 * Stores the previous versions of a file for roll-back tracking.
 * 
 * @author Stephanos B
 */
public class FileVersion {

    private String modId; // Path of the content file within the Mod
    private String hash; // Hexadecimal string, of file contents
    private LocalDateTime timestamp; // Timestamp of when the entry was created. (Mainly debug)

    public static final String GAME_OWNER = "GAME";

    /**
     * Used to ensure Json Keys are consistent.
     */
    public enum JsonFields {
        modId,
        hash,
        timestamp
    }

    public FileVersion() {
        modId = null;
        hash = null;
        timestamp = LocalDateTime.now();
    }

    public FileVersion(String modId, String hash) {
        this();
        this.modId = modId;
        this.hash = hash;
    }

    public FileVersion(String modId, String hash, LocalDateTime timestamp) {
        this(modId, hash);
        this.timestamp = timestamp;
    }

    /// /// /// Getters and Setters /// /// ///

    public String getModId() {
        return modId;
    }

    public void setModId(String modId) {
        this.modId = modId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /// /// /// Methods /// /// ///

    /**
     * This is deliberately NOT stored in the FileVersion.
     * Otherwise reordering mods would involve going to every FileLineage and
     * updating the LoadOrder for every file belonging to a Mod.
     * 
     * Instead, this will fetch the value from the ModManifest on demand to ensure
     * its correct.
     * 
     * @param MANIFEST_DIR The full path the current game is using to store it's
     *                     manifests.
     * @return int LoadOrder of the mod as it is CURRENTLY deployed. -1 for Game
     *         files or non-exsistent manifests.
     * @throws Exception
     */
    public int getLoadOrder(Path MANIFEST_DIR) throws Exception {
        if (this.modId.equalsIgnoreCase(GAME_OWNER))
            return -1;

        // Casting to a Mod because the full file details from the Manifest are not
        // needed.
        Mod mod = (Mod) JsonIO.read(
                MANIFEST_DIR.resolve(this.modId + ".json").toFile(),
                JsonSerializable.ObjectTypes.MOD_MANIFEST);
        return mod.getLoadOrder();
    } // getLoadOrder()

    @Override
    public String toString(){
        return String.format("File Version: Owner ModId: %s, Hash: %s", modId, hash);
    }
}// Class