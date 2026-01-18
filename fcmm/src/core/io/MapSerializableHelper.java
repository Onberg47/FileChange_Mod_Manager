package core.io;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import core.interfaces.MapSerializable;
import core.objects.FileLineage;
import core.objects.Game;
import core.objects.GameState;
import core.objects.Mod;
import core.objects.ModManifest;

/**
 * A general helper for MapSerializable objects to use when mapping.
 */
public class MapSerializableHelper {

    /**
     * 
     * @param map Map to read
     * @param key Key for the nested objects.
     * @return List of whiche
     */
    public List<MapSerializable> setFromNestedMap(Map<String, Object> map, String key, String cast_type) {
        Object rawValue = map.get(key);
        if (rawValue instanceof List) {
            List<?> rawList = (List<?>) rawValue;
            List<MapSerializable> ls = new ArrayList<>();
            for (Object item : rawList) {
                if (item instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> modMap = (Map<String, Object>) item;

                    switch (cast_type) {
                        case MapSerializable.ObjectTypes.MOD:
                            ls.add(new Mod().setFromMap(modMap));
                        case MapSerializable.ObjectTypes.MOD_MANIFEST:
                            ls.add(new ModManifest().setFromMap(modMap));

                        case MapSerializable.ObjectTypes.GAME:
                            ls.add(new Game().setFromMap(modMap));
                        case MapSerializable.ObjectTypes.GAME_STATE:
                            ls.add(new GameState().setFromMap(modMap));

                        case MapSerializable.ObjectTypes.FILE_LINEAGE:
                            ls.add(new FileLineage().setFromMap(modMap));

                        default:
                            throw new IllegalArgumentException("Unknown object type: " + cast_type);
                    } // switch
                }
            }
            return ls;
        }
        return null;
    }

    /**
     * 
     * @param map
     * @param key
     * @return
     */
    public static Integer getInt(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null)
            return null;
        if (value instanceof Number)
            return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static LocalDateTime getDateTime(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null)
            return null;
        try {
            return LocalDateTime.parse(value.toString());
        } catch (DateTimeParseException e) {
            return null;
        }
    }
} // Class