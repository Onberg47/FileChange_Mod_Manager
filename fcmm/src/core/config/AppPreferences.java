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

    public Object get(String key) {
        return preferences.get(key);
    }

    public void set(String key, Object value) {
        preferences.put(key, value);
    }

    public boolean getAsBoolean(String key, boolean defaultValue) {
        Object value = preferences.get(key);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }

    public String getAsString(String key, String defaultValue) {
        Object value = preferences.get(key);
        return value instanceof String ? (String) value : defaultValue;
    }

    public int getAsInt(String key, int defaultValue) {
        Object value = preferences.get(key);
        return value instanceof Number ? ((Number) value).intValue() : defaultValue;
    }

    public Number getAsNumber(String key, Number defaultValue) {
        Object value = preferences.get(key);
        return value instanceof Number ? (Number) value : defaultValue;
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