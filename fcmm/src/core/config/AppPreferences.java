/**
 * Author Stephanos B
 * Date 23/01/2026
 */
package core.config;

import java.util.HashMap;
import java.util.Map;

import core.interfaces.MapSerializable;

/**
 * Used to store preferences. Simply has additional methods for ease of use and
 * to be distinct from AppConfig.<br>
 * <br>
 * This is only meant to be accessed through AppConfig to enforce it's singleton
 * maintain concurrency.
 */
public class AppPreferences implements MapSerializable {

    /**
     * Properties of all listed preferences.
     */
    public enum properties {
        FS_LOCKS("FS_LOCK", "File-System locks", false),

        NORMALISE_BY_GROUP("NORMALISE_BY_GROUP", "Normalise mods by groups", false),

        TRASH_SIZE_WARNING("TRASH_SIZE_WARNING", "Trash size limit warning", "off"),
        TRASH_SIZE_LIMIT("TRASH_SIZE_LIMIT", "Trash size limit", 100),
        TRASH_DAYS_OLD("TRASH_DAYS_OLD", "Trash days old limit", 30);

        ///

        private String key; // The key written to the Map
        private String name; // User-facing display name
        private Object defaultValue;

        private properties(String key, String name, Object defaultValue) {
            this.key = key;
            this.name = name;
            this.defaultValue = defaultValue;
        }

        public String key() {
            return this.key;
        }

        public String getName() {
            return this.name;
        }

        public Object getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String toString() {
            return this.name;
        }
    } // enum

    private Map<String, Object> preferences = new HashMap<>();

    public AppPreferences() {
    }

    public AppPreferences(Object rawValue) {
        this();

        if (rawValue != null && rawValue instanceof Map) {
            Map<?, ?> rawList = (Map<?, ?>) rawValue;
            HashMap<String, Object> map = new HashMap<>();
            for (Object key : rawList.keySet()) {
                if (key instanceof String) {
                    String keystr = (String) key;
                    map.put(keystr, rawList.get(keystr));
                }
            }
            this.setFromMap(map);
        }
    }

    /// /// /// Implements /// /// ///

    @Override
    public String getObjectType() {
        return MapSerializable.ObjectTypes.PREFERENCES;
    }

    @Override
    public MapSerializable setFromMap(Map<String, Object> map) {
        this.preferences = map;

        return this;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map = this.preferences;

        return map;
    }

    /// /// /// Getters and Setters /// /// ///

    public Object get(properties property) {
        Object value = preferences.get(property.key);
        return value instanceof Object ? value : property.defaultValue;
    }

    public Object get(String key, String defaultValue) {
        Object value = preferences.get(key);
        return value instanceof Object ? value : defaultValue;
    }

    public boolean is(properties property) {
        Object value = preferences.get(property.key);
        return value instanceof Boolean ? (boolean) value : (boolean) property.defaultValue;
    }

    public void set(String key, Object value) {
        preferences.put(key, value);
    }

    /**
     * If the key is linked to a String Object, then it will return that value.<br>
     * <br>
     * This is NOT a toString() converstion!
     */
    public String getAsString(String key, String defaultValue) {
        Object value = preferences.get(key);
        return value instanceof String ? (String) value : defaultValue;
    }

    public Number getAsNumber(properties property) {
        Object value = preferences.get(property.key);
        return value instanceof Number ? (Number) value : (Number) property.defaultValue;
    }

    public int getAsInt(properties property) {
        return getAsNumber(property).intValue();
    }

    ///

    Map<String, Object> getPreferences() {
        return this.preferences;
    }

    /// /// ///

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append("Preferences:\n");
        for (String key : this.preferences.keySet()) {
            str.append(String.format("\t%-15s : %s\n", key, preferences.get(key)));
        }

        return str.toString();
    } // toString()

} // Class