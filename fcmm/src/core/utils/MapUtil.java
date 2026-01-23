package core.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {

    /**
     * Converts a Map<String, Object> into Map<String, String>
     * 
     * @param map
     * @return
     */
    public static Map<String, String> toStringOnlyMap(Map<String, Object> map) {
        HashMap<String, String> hMap = new HashMap<>();
        for (String key : map.keySet()) {
            try {
                hMap.put(key, (String) map.get(key));
            } catch (Exception e) {
                hMap.put(key, map.get(key).toString()); // fallback for non-directly castable methods
            }
        }
        return hMap;
    }

    /**
     * Converts a Map<String, String> into Map<String, Object>
     * 
     * @param map
     * @return
     */
    public static Map<String, Object> toGenericMap(Map<String, String> map) {
        HashMap<String, Object> hMap = new HashMap<>();
        for (String key : map.keySet()) {
            hMap.put(key, (Object) map.get(key));
        }
        return hMap;
    }

} // Class