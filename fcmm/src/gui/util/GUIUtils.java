/**
 * Author Stephanos B
 * Date 08/01/2026
 */
package gui.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import core.objects.Mod;

/**
 * Contains general helper utilites for the GUI.
 * 
 * @author Stephanos B
 * @since v2
 */
public class GUIUtils {

    public static void setLookAndFeel() {
        // TODO
    }

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
    public static Map<String, Object> toGenericMap(Map<String, Object> map) {
        HashMap<String, Object> hMap = new HashMap<>();
        for (String key : map.keySet()) {
            hMap.put(key, (Object) map.get(key));
        }
        return hMap;
    }

    /// /// /// ModManager /// /// ///

    public class ModFilter {

        public static List<Mod> filterMods(List<Mod> mods,
                String statusFilter,
                String nameFilter,
                String tagsFilter) {
            // Parse tags
            Set<String> tagFilters = parseTags(tagsFilter);

            return mods.stream()
                    .filter(mod -> matchesStatus(mod, statusFilter))
                    .filter(mod -> matchesName(mod, nameFilter))
                    .filter(mod -> matchesTags(mod, tagFilters))
                    .collect(Collectors.toList());
        }

        private static Set<String> parseTags(String tagsFilter) {
            if (tagsFilter == null || tagsFilter.trim().isEmpty()) {
                return Collections.emptySet();
            }

            return Arrays.stream(tagsFilter.split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
        }

        private static boolean matchesStatus(Mod mod, String statusFilter) {
            if (statusFilter == null || statusFilter.equals("All")) {
                return true;
            }

            switch (statusFilter) {
                case "Enabled":
                    return true; // TODO mod.isEnabled();
                case "Disabled":
                    return false; // TODO !mod.isEnabled();
                // case "Has Conflicts": return mod.hasConflicts(); // examples for future use
                // case "Needs Update": return mod.needsUpdate();
                default:
                    return true;
            }
        }

        private static boolean matchesName(Mod mod, String nameFilter) {
            if (nameFilter == null || nameFilter.trim().isEmpty()) {
                return true;
            }

            String filter = nameFilter.toLowerCase();
            return mod.getName().toLowerCase().contains(filter) ||
                    mod.getDescription().toLowerCase().contains(filter);
        }

        private static boolean matchesTags(Mod mod, Set<String> tagFilters) {
            if (tagFilters.isEmpty()) {
                return true;
            }

            Set<String> modTags = mod.getTagSet().stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());

            return tagFilters.stream().anyMatch(modTags::contains);
        }
    }
} // Class
