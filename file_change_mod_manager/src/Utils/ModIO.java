/*
 * Author: Stephanos B
 * Date: 15/12/2025
 */
package Utils;

import Objects.Mod;
import Objects.ModFile;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;

import org.json.simple.*;
//import org.json.simple.parser.*;
import org.json.simple.parser.JSONParser;

/**
 * Utility class to handle reading and writing Mod JSON files.
 */
public class ModIO {

    /**
     * Reads the given Mod.JSON file and returns the Mod object.
     * 
     * @param file The Mod.JSON file to read.
     * @return The Mod object if successful.
     * @throws Exception
     */
    public static Mod readMod(File file) throws Exception {
        if (!file.exists()) {
            throw new Exception("❌ File path is invalid: " + file.getAbsolutePath());
        }

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(new FileReader(file));

        Mod mod = new Mod();
        mod.setGameId((String) json.get(Mod.JsonFields.gameId.toString()));
        mod.setVersion((String) json.get(Mod.JsonFields.version.toString()));
        mod.setDownloadSource((String) json.get(Mod.JsonFields.downloadSource.toString()));
        mod.setName((String) json.get(Mod.JsonFields.name.toString()));
        mod.setDescription((String) json.get(Mod.JsonFields.description.toString()));
        mod.setLoadOrder(((Long) json.get(Mod.JsonFields.loadOrder.toString())).intValue());
        // mod.setDownloadDate(); //TODO
        mod.setDownloadLink((String) json.get(Mod.JsonFields.downloadLink.toString()));

        JSONArray files = (JSONArray) json.get(Mod.JsonFields.files.toString());
        for (Object obj : files) {
            JSONObject fileObj = (JSONObject) obj;
            ModFile modFile = new ModFile();
            modFile.setFilePath((String) fileObj.get("filePath"));
            modFile.setHash((String) fileObj.get("hash"));
            mod.addFile(modFile);
        }

        mod.generateModId(); // Do this last for saftey.
        return mod;
    } // readMod()

    /**
     * Writes the given Mod object to a Mod.JSON file.
     * 
     * @param mod  Complete Mod object to write.
     * @param file The file to write the Mod.JSON to.
     * @throws Exception
     */
    @SuppressWarnings("unchecked") // type-safe warning comes from how JSONObject works internally!
    public static void writeMod(Mod mod, File file) throws Exception {
        JSONObject json = new JSONObject();

        json.put(Mod.JsonFields.id, mod.getId());
        json.put(Mod.JsonFields.gameId, mod.getGameId());
        json.put(Mod.JsonFields.version, mod.getVersion());
        json.put(Mod.JsonFields.downloadSource, mod.getDownloadSource().getCode());
        json.put(Mod.JsonFields.loadOrder, mod.getLoadOrder());
        json.put(Mod.JsonFields.name, mod.getName());
        json.put(Mod.JsonFields.description, mod.getDescription());
        json.put(Mod.JsonFields.downloadDate, mod.getDownloadDate().toString());
        json.put(Mod.JsonFields.downloadLink, mod.getDownloadLink());

        JSONArray files = new JSONArray();
        for (ModFile modFile : mod.getContentsArr()) {
            JSONObject fileObj = new JSONObject();
            fileObj.put(ModFile.JsonFields.filePath, modFile.getFilePath());
            fileObj.put(ModFile.JsonFields.hash, modFile.getHash());
            files.add(fileObj);
        }
        json.put(Mod.JsonFields.files, files);

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(json.toJSONString());
            // Jsoner.prettyPrint(new StringReader(json.toJSONString()),fw," ", "\n"); //
            // Requires another Lib to print pritty.
        }
    } // writeMod()

} // Class