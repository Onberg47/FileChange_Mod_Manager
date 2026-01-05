/*
 * Author: Stephanos B
 * Date: 19/12/2025
*/
package core.objects;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.simple.JSONObject;

import core.interfaces.JsonSerializable;
import core.io.GameStateIO;

/**
 * Object that is stored within the Game's Manifest directory. Used for
 * quick-access data for both the ModManager and GUI.
 * 
 * @author Stephanos B
 */
public class GameState implements JsonSerializable {

    public enum JsonFields {
        lastModified,
        deployedMods
    }

    /**
     * File name used for all gameState json files to ensure consistency.
     */
    public static final String FILE_NAME = "game_state.json";

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

    @Override
    public JSONObject toJsonObject() {
        return GameStateIO.write(this); // keeps IO operations seperate
    } // toJsonObject()

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
    }

    /// /// ///

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
            System.err.println("Index: " + index + " is out of bounds.");
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

    /// /// /// Public Utils /// /// ///

    /**
     * Checks if a Mod with the given Id is within the GameState's deployed mods.
     * 
     * @param modId
     * @return
     */
    public Boolean containsMod(String modId) {
        for (Mod mod : deployedMods) {
            if (mod.getId().equals(modId))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("🗂  Game State:\n\tLast Modified: %s\n\tDeployed Mods:%s",
                this.getLastModified().toString(),
                printDeployedMods());
    }
} // Class
