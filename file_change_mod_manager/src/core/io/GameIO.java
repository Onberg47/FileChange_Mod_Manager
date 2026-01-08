/*
 * Author: Stephanos B
 * Date: 19/12/2025
 */
package core.io;

import org.json.simple.*;

import core.objects.Game;

/**
 * Utility class to handle reading and writing Game JSON files.
 * 
 * @author Stephanos B
 * @apiNote See ModIO.java for docs.
 */
public class GameIO {

    /**
     * Populate a the Object from a JSONObject.
     * 
     * @param json JSONObject to read from.
     * @return The populated Object if successful.
     */
    static Game read(JSONObject json) {
        Game game = new Game();

        game.setId((String) json.get(Game.JsonFields.id.toString()));
        game.setName((String) json.get(Game.JsonFields.name.toString()));
        game.setInstallDirectory((String) json.get(Game.JsonFields.installDirectory.toString()));
        game.setStoreDirectory((String) json.get(Game.JsonFields.storeDirectory.toString()));

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
        json.put(Game.JsonFields.installDirectory, obj.getInstallDirectory());
        json.put(Game.JsonFields.storeDirectory, obj.getStoreDirectory());

        return json;
    } // write()

} // Class