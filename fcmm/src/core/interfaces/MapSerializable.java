/**
 * Author Stephanos B
 * Date 22/12/2025
 */
package core.interfaces;

import java.util.Map;

/**
 * An interface for all my Json objects to implement.
 * Ensures nesessary methods exsist for json handling.
 * 
 * @author Stephanos B
 */
public interface MapSerializable {

    /**
     * To determin the type of each object.
     * 
     * @return The String key-value used to define what type of JsonSerializable is
     *         stored in the Json file.
     */
    String getObjectType();

    /**
     * Sets all values of the current Game from the passed Map<> and won't override
     * missing values, allowing for updating instances.
     * 
     * @param map Complete or patially complete Meta Map to read values from.
     * @return returns itself after being set from the Map.
     */
    public MapSerializable setFromMap(Map<String, Object> map);

    /**
     * Uses a Map to allow easy casting to any of it's childeren.
     * 
     * @return Map<String, String> of all the instance's fields.
     */
    public Map<String, Object> toMap();

    /// /// ///

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
        public static final String PREFERENCES = "Preferences";
    }

} // Class
