/*
 * Author: Stephanos B
 * Date: 19/12/2025
*/
package core.objects;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import core.interfaces.MapSerializable;
import core.io.JsonIO;
import core.utils.Logger;

/**
 * Object that is stored within the Game's Manifest directory. Used for
 * quick-access data for both the ModManager and GUI.
 * 
 * @author Stephanos B
 */
public class GameState implements MapSerializable {

    public enum Keys {
        LAST_MODIFIED("lastModified"),
        DEPLOYED_MODS("deployedMods");

        private final String key;

        private Keys(String key) {
            this.key = key;
        }

        public String key() {
            return this.key;
        }
    }

    /**
     * File name used for all gameState json files to ensure consistency.
     */
    public static final String FILE_NAME = "game_state.json";

    // fields
    private LocalDateTime lastModified;
    private List<Mod> deployedMods;

    public GameState() {
        lastModified = LocalDateTime.now();
        deployedMods = new ArrayList<>();
    }

    /// /// /// Implements /// /// ///

    @Override
    public String getObjectType() {
        return ObjectTypes.GAME_STATE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public GameState setFromMap(Map<String, Object> map) {
        /// single fields
        if (map.containsKey(Keys.LAST_MODIFIED.key))
            this.setLastModified(LocalDateTime.parse(map.get(Keys.LAST_MODIFIED.key).toString()));

        /// deployed Mods
        if (map.containsKey(Keys.DEPLOYED_MODS.key)) {
            Object rawValue = map.get(Keys.DEPLOYED_MODS.key);
            if (rawValue instanceof List) {
                List<?> rawList = (List<?>) rawValue;
                List<Mod> ls = new ArrayList<>();
                for (Object item : rawList) {
                    if (item instanceof Map) {
                        Map<String, Object> modMap = (Map<String, Object>) item;
                        ls.add(new Mod().setFromMap(modMap));
                    }
                }
                this.setDeployedMods(ls);
            }
        }
        return this;
    } // setFromMap()

    @Override
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();

        /// single fields
        map.put(Keys.LAST_MODIFIED.key, this.getLastModified().toString());

        /// deployed Mods
        ArrayList<HashMap<String, Object>> arrLs = new ArrayList<>();
        // Get map of each.
        for (Mod tmp : this.getDeployedMods()) {
            arrLs.add((HashMap<String, Object>) tmp.toMap());
        }
        map.put(Keys.DEPLOYED_MODS.key, arrLs);

        return map;
    } // toMap()

    /// /// /// Getters and Setters /// /// ///

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime createdAt) {
        this.lastModified = createdAt;
    }

    private void updateModified() {
        this.lastModified = LocalDateTime.now();
    }

    public List<Mod> getDeployedMods() {
        return deployedMods;
    }

    public void setDeployedMods(List<Mod> deployedMods) {
        this.deployedMods = deployedMods;
        this.updateModified();
    }

    /**
     * Set the deployed Mods from a pre-sorted List by LoadOrder. This will update
     * the loadOrder values of each Mod.
     * 
     * @param deployedMods List<Mod> pre-sorted.
     */
    public void setOrderedMods(List<Mod> deployedMods) {
        this.deployedMods = deployedMods;

        // set the load order of each mod to match it's position in the List
        IntStream.range(0, deployedMods.size())
                .forEach(index -> deployedMods.get(index).setLoadOrder(index));

        // If I want to keep reading the Manifests for loadOrder, then all manifests
        // must be updated here. Instead, using GameState to avoid that and reduce
        // overhead for FileLineage checking
        this.updateModified();
    }

    /// /// /// File I/O Methods /// /// ///

    /**
     * 
     * @param filePath
     * @return
     * @throws Exception
     */
    public static GameState loadFromFile(Path filePath) throws Exception {
        Logger.getInstance().logEntry(null, "Loading GameState from: " + filePath);
        return (GameState) JsonIO.read(filePath.toFile(), MapSerializable.ObjectTypes.GAME_STATE);
    }

    /**
     * Saves the current GameState to a JSON file.
     * 
     * @param filePath
     * @throws Exception
     */
    public void saveToFile(Path filePath) throws Exception {
        Logger.getInstance().logEntry("Saving GameState to: " + filePath);
        try {
            // If removed Mod was last, delete file.
            if (this.deployedMods.isEmpty())
                Files.delete(filePath);
            else
                JsonIO.write(this, filePath.toFile());

        } catch (Exception e) {
            throw new Exception("Failed to save GameState : " + e.getMessage(), e);
        }
    } // saveToFile()

    /// /// /// Methods /// /// ///

    /**
     * Adds a mod without auto-sorting or updating last modified. For reading
     * pre-sorted from JSON. Does not check for duplicates.
     * 
     * @param mod
     */
    public void addMod(Mod mod) {
        deployedMods.add(mod);
        updateModified();
    }

    /**
     * Adds a Mod and auto sorts by load order.
     * Updates LastModified.
     * 
     * @param mod
     */
    public void appendMod(Mod mod) {
        if (this.deployedMods.isEmpty())
            deployedMods.add(mod);
        else {
            deployedMods.add(mod);
            deployedMods.sort(Comparator.comparingInt(Mod::getLoadOrder));
        }
        updateModified();
    }

    /**
     * Adds a Mod and auto sorts by load order and will remove all instances first
     * if they exsist.
     * Updates LastModified.
     * 
     * @param mod
     */
    public void appendModOnly(Mod mod) {
        if (this.deployedMods.isEmpty())
            deployedMods.add(mod);
        else {
            for (int i = 0; i < deployedMods.size(); i++) {
                if (deployedMods.get(i).getId().equals(mod.getId())) {
                    deployedMods.remove(i);
                    i--;
                }
            }
            deployedMods.add(mod);
            deployedMods.sort(Comparator.comparingInt(Mod::getLoadOrder));
        }
        updateModified();
    } // appendModOnly()

    /**
     * Removes the entry at the index. If the index is out of bounds, prints a
     * warning and continues.
     * Updates LastModified.
     * 
     * @param index
     */
    public void removeMod(int index) {
        try {
            this.deployedMods.remove(index);
            updateModified();
        } catch (IndexOutOfBoundsException e) {
            Logger.getInstance().logWarning("Index: " + index + " is out of bounds.", e);
        }
    }

    /**
     * Updates LastModified.
     * 
     * @param mod
     */
    public void removeMod(Mod mod) {
        for (int i = 0; i < deployedMods.size(); i++) {
            if (deployedMods.get(i).getId().equals(mod.getId())) {
                deployedMods.remove(i);
                break;
            }
        } // for i
        updateModified();
    } // removeMod()

    /// /// /// Helper Methods /// /// ///

    /**
     * Sorts Mods Acending by LoadOrder. (Order for deployment)
     * Updates last modified.
     */
    public void sortDeployedMods() {
        Collections.sort(deployedMods, Comparator.comparingInt(Mod::getLoadOrder));
        updateModified();
    } // sortDeployedMods()

    /**
     * Checks if a Mod with the given Id is within the GameState's deployed mods.
     * 
     * @param modId
     * @return
     */
    public boolean containsMod(String modId) {
        for (Mod mod : deployedMods) {
            if (mod.getId().equals(modId))
                return true;
        }
        return false;
    }

    /**
     * Reads the LoadOrder of the modId from within the GameState.
     * 
     * @param modId modId to find.
     * @return The load order of the mod or -404 if the mod was not found.
     */
    public int getLoadOrder(String modId) {
        for (Mod mod : deployedMods) {
            if (mod.getId().equals(modId))
                return mod.getLoadOrder();
        }
        return -404;
    }

    /**
     * Creates a String for printing a single-line display of each mod in the
     * GameState.
     * For CLI use.
     * 
     * @return
     */
    private String printDeployedMods() {
        StringBuilder sb = new StringBuilder();
        for (Mod mod : this.getDeployedMods()) {
            sb.append("\n\t\t⚫ " + mod.printLite());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("🗂  Game State:\n\tLast Modified: %s\n\tDeployed Mods:%s",
                this.getLastModified().toString(),
                printDeployedMods());
    }
} // Class
