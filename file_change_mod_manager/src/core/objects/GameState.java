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
     * pre-sorted from JSON.
     * 
     * @param mod
     */
    public void addMod(Mod mod) {
        deployedMods.add(mod);
    }

    /**
     * Adds a Mod and auto sorts by load order. Updates LastModified.
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
     * Updates LastModified.
     * 
     * @param index
     */
    public void removeMod(int index) {
        this.deployedMods.remove(index);
        updateModified();
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

    /// /// /// Methods /// /// ///

    /**
     * Sorts Mods Acending by LoadOrder. (Order for deployment)
     * Uses the simplest bubble sort.
     * Updates last modified.
     */
    public void sortDeployedMods() {
        Collections.sort(deployedMods, Comparator.comparingInt(Mod::getLoadOrder));
        updateModified();
    } // sortDeployedMods()

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
