/**
 * Author Stephanos B
 * Date 22/12/2025
 */
package interfaces;

import org.json.simple.JSONObject;

/**
 * An interface for all my Json objects to implement.
 * Ensures nesessary methods exsist for json handling.
 * 
 * @author Stephanos B
 */
public interface JsonSerializable {
    // to be implemented.
    JSONObject toJsonObject(); // This method should return the JSON representation. but only redirects to the
                               // correct IO method, to avoid importing JSON into Object classes.

    String getObjectType(); // To determin the type of each object.

    public String ObjectTypeKey = "ObjectType"; // The key String for Json operations.

    public abstract class ObjectTypes {
        public static final String MOD = "Mod";
        public static final String MOD_MANIFEST = "ModManifest";
        public static final String BACKUP_MANIFEST = "BackupManifest";
        public static final String GAME = "Game";
        public static final String GAME_STATE = "GameState";
        public static final String FILE_LINEAGE = "FileLineage";
    }

} // Class
