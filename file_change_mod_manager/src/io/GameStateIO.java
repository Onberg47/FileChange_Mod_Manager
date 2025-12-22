/*
 * Author: Stephanos B
 * Date: 22/12/2025
 */
package io;

import java.time.Instant;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import interfaces.JsonSerializable;
import objects.GameState;
import objects.Mod;
import objects.ModManifest;

/**
 * 
 * @author Stephanos B
 */
public class GameStateIO {

    /**
     * 
     * @param file
     * @return
     * @throws Exception
     */
    public static GameState read(JSONObject json) throws Exception {
        GameState gState = new GameState();

        Object dateObj = json.get(Mod.JsonFields.downloadDate.toString());
        try {
            String dateString = (String) dateObj;
            Instant instant = Instant.parse(dateString);
            gState.setLastModified(Date.from(instant));
        } catch (Exception e) {
            System.err.println("❗ Warning: Could not parse download date: " + dateObj);
        }

        JSONArray files = (JSONArray) json.get(GameState.JsonFields.deployedMods.toString());
        for (Object obj : files) {
            JSONObject fileObj = (JSONObject) obj;
            gState.addDeployedMod(new ModManifest(ModIO.read(fileObj)));
        } // for each

        return gState;
    } // read()

    /**
     * 
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    public static JSONObject write(GameState obj) {
        JSONObject json = new JSONObject();
        json.put(JsonSerializable.ObjectTypeKey, obj.getObjectType()); // standard

        json.put(GameState.JsonFields.lastModified, obj.getLastModified());
        
        JSONArray files = new JSONArray();
        // write each deployed Mod.
        for (Mod mod : obj.getDeployedMods()) {
            JSONObject fileObj = new JSONObject();
            fileObj = ModIO.write(mod); // Use the exsisting method for Mods
            files.add(fileObj);
        }
        json.put(GameState.JsonFields.deployedMods, files);

        return json;
    } // write()

} // Class