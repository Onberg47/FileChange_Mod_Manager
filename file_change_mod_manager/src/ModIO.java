/*
 * Author: Stephanos B
 * Date: 15/12/2025
 */

import Objects.Mod;
import Objects.ModFile;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.json.simple.*;
//import org.json.simple.parser.*;
import org.json.simple.parser.JSONParser;

public class ModIO {
    // READING
    public static Mod readMod(File file) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(new FileReader(file));

        Mod mod = new Mod();
        mod.setName((String) json.get("name"));
        mod.setVersion((String) json.get("version"));

        JSONArray files = (JSONArray) json.get("files");
        for (Object obj : files) {
            JSONObject fileObj = (JSONObject) obj;
            ModFile modFile = new ModFile();
            modFile.setFilePath((String) fileObj.get("path"));
            modFile.setHash((String) fileObj.get("hash"));
            mod.addFile(modFile);
        }

        return mod;
    } // readMod()

    // WRITING
    public static void writeMod(Mod mod, File file) throws Exception {
        JSONObject json = new JSONObject();
        json.put("id", mod.getId());
        json.put("name", mod.getName());
        json.put("version", mod.getVersion());
        json.put("description", mod.getDescription());

        JSONArray files = new JSONArray();
        for (ModFile modFile : mod.getContentsArr()) {
            JSONObject fileObj = new JSONObject();
            fileObj.put("path", modFile.getFilePath());
            fileObj.put("hash", modFile.getHash());
            files.add(fileObj);
        }
        json.put("files", files);

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(json.toJSONString());
        }
    } // writeMod()
} // Class