/**
 * Author Stephanos B
 * Date 22/12/2025
 */
package core.interfaces;

import org.json.simple.JSONObject;

/**
 * An interface for all my Json objects to implement.
 * Ensures nesessary methods exsist for json handling.
 * 
 * @author Stephanos B
 */
public interface JsonSerializable {

    /**
     * This method should return the JSON representation. but only redirects to the
     * correct IO method, to avoid importing JSON into Object classes.
     * 
     * @return JSONObject derived from {@code this}
     */
    JSONObject toJsonObject();

    /**
     * To determin the type of each object.
     * 
     * @return The String key-value used to define what type of JsonSerializable is
     *         stored in the Json file.
     */
    String getObjectType();

    ///

    /**
     * The key-field used to to store the ObjectType in a Json.
     */
    public String ObjectTypeKey = "ObjectType"; // The key String for Json operations.

    /**
     * A set of constants for each possible type of JsonSerializable Object.
     */
    public abstract class ObjectTypes {
        public static final String MOD = "Mod";
        public static final String MOD_MANIFEST = "ModManifest";
        public static final String GAME = "Game";
        public static final String GAME_STATE = "GameState";
        public static final String FILE_LINEAGE = "FileLineage";
    }

} // Class
