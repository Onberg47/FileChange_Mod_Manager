/*
 * Author: Stephanos B
 * Date: 19/12/2025
 */
package io;

import org.json.simple.*;

import objects.Game;

/**
 * Utility class to handle reading and writing Game JSON files.
 * 
 * @author Stephanos B
 * @apiNote See ModIO.java for docs.
 */
public class GameIO {

    /**
     * 
     * @param json
     * @return
     */
    static Game read(JSONObject json) {

        Game game = new Game();
        game.setId((String) json.get(Game.JsonFields.id.toString()));
        game.setName((String) json.get(Game.JsonFields.name.toString()));
        game.setInstallPath((String) json.get(Game.JsonFields.installPath.toString()));
        game.setModsPath((String) json.get(Game.JsonFields.modsPath.toString()));

        return game;
    } // read()

    /**
     * Populates a JSONObject from the given Class
     * 
     * @param obj Object to process.
     * @return JSONObject ready to be written to a JSON file.
     */
    @SuppressWarnings("unchecked")
    public static JSONObject write(Game obj) {
        JSONObject json = new JSONObject();

        json.put(Game.JsonFields.id, obj.getId());
        json.put(Game.JsonFields.name, obj.getName());
        json.put(Game.JsonFields.installPath, obj.getInstallPath());
        json.put(Game.JsonFields.modsPath, obj.getModsPath());

        return json;
    } // write()

} // Class