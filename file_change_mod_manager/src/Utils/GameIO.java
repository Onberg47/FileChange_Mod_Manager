/*
 * Author: Stephanos B
 * Date: 19/12/2025
 */
package Utils;

import Objects.Game;
import Objects.GameState;
import Objects.Mod;
import Objects.ModManifest;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

/**
 * Utility class to handle reading and writing Game JSON files.
 * 
 * @apiNote See ModIO.java for docs.
 */
public class GameIO {

    public static Game readGame(File file) throws Exception {
        if (!file.exists()) {
            throw new Exception("❌ File path is invalid: " + file.getAbsolutePath());
        }

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(new FileReader(file));

        Game game = new Game();
        game.setId((String) json.get(Game.JsonFields.id.toString()));
        game.setName((String) json.get(Game.JsonFields.name.toString()));
        game.setInstallPath((String) json.get(Game.JsonFields.installPath.toString()));
        game.setModsPath((String) json.get(Game.JsonFields.modsPath.toString()));

        return game;
    } // readGame()

    /**
     * Writes the given Game object to a game.JSON file.
     * 
     * @param game Complete Game object to write.
     * @param file The file to write the Game.JSON to.
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void writeGame(Game game, File file) throws Exception {
        JSONObject json = new JSONObject();

        json.put(Game.JsonFields.id, game.getId());
        json.put(Game.JsonFields.name, game.getName());
        json.put(Game.JsonFields.installPath, game.getInstallPath());
        json.put(Game.JsonFields.modsPath, game.getModsPath());

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(json.toJSONString());
        }
    } // writeGame()

    /// /// /// GameState /// /// ///

    public static GameState readGameState(File file) throws Exception {
        if (!file.exists()) {
            throw new Exception("❌ File path is invalid: " + file.getAbsolutePath());
        }

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(new FileReader(file));

        GameState gState = new GameState();
        // game.setLastModified((String)
        // json.get(GameState.JsonFields.lastModified.toString()));

        JSONArray files = (JSONArray) json.get(GameState.JsonFields.deployedMods.toString());
        for (Object obj : files) {
            JSONObject fileObj = (JSONObject) obj;

            ModManifest mod = new ModManifest();
            mod.setGameId((String) fileObj.get(Mod.JsonFields.gameId.toString()));
            mod.setVersion((String) fileObj.get(Mod.JsonFields.version.toString()));
            mod.setDownloadSource((String) fileObj.get(Mod.JsonFields.downloadSource.toString()));
            mod.setName((String) fileObj.get(Mod.JsonFields.name.toString()));
            mod.setDescription((String) fileObj.get(Mod.JsonFields.description.toString()));
            mod.setLoadOrder(((Long) fileObj.get(Mod.JsonFields.loadOrder.toString())).intValue());
            // mod.setDownloadDate(); //TODO
            mod.setDownloadLink((String) fileObj.get(Mod.JsonFields.downloadLink.toString()));
            mod.generateModId(); // Do this last for saftey.

            gState.addDeployedMod(mod);
        }

        return gState;
    } // readGame()

    /**
     * Writes the given Game object to a game.JSON file.
     * 
     * @param gState Complete Game object to write.
     * @param file   The file to write the Game.JSON to.
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void writeGame(GameState gState, File file) throws Exception {
        JSONObject json = new JSONObject();

        json.put(GameState.JsonFields.lastModified, gState.getLastModified());

        JSONArray files = new JSONArray();
        for (Mod mod : gState.getDeployedMods()) {
            JSONObject fileObj = new JSONObject();

            fileObj.put(Mod.JsonFields.id, mod.getId());
            fileObj.put(Mod.JsonFields.gameId, mod.getGameId());
            fileObj.put(Mod.JsonFields.version, mod.getVersion());
            fileObj.put(Mod.JsonFields.downloadSource, mod.getDownloadSource().getCode());
            fileObj.put(Mod.JsonFields.loadOrder, mod.getLoadOrder());
            fileObj.put(Mod.JsonFields.name, mod.getName());
            fileObj.put(Mod.JsonFields.description, mod.getDescription());
            fileObj.put(Mod.JsonFields.downloadDate, mod.getDownloadDate().toString());
            fileObj.put(Mod.JsonFields.downloadLink, mod.getDownloadLink());

            files.add(fileObj);
        }
        json.put(GameState.JsonFields.deployedMods, files);

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(json.toJSONString());
        }
    } // writeGame()

} // Class