/*
 * Author: Stephanos B
 * Date: 22/12/2025
 */
package io;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import objects.ModManifest;

/**
 * 
 * @author Stephanos B
 * @apiNote See JsonIO.java for docs.
 */
public class ModManifestIO {

    /**
     * Reads the given {@code ModManifest.json} file and returns the Mod object.
     * 
     * @param file The ModManifest file to read.
     * @return The ModManifest object if successful.
     * @throws Exception
     */
    static ModManifest read(JSONObject json) throws Exception {

        ModManifest mod = new ModManifest(ModIO.read(json)); // use the Mod reader for simplicity.

        JSONArray files = (JSONArray) json.get(ModManifest.JsonFields.files.toString());
        mod.setContentsArr(ModFileIO.readModFiles(files));

        mod.generateModId(); // Do this last for saftey.
        return mod;
    } // read()

    /**
     * Populates a JSONObject from the given Class
     * 
     * @param obj Object to process.
     * @return JSONObject ready to be written to a JSON file.
     */
    @SuppressWarnings("unchecked") // type-safe warning comes from how JSONObject works internally!
    public static JSONObject write(ModManifest obj) {
        JSONObject json = new JSONObject();

        // Write standard mod fields.
        json = ModIO.write(obj);

        // Write ModManifest fields.
        JSONArray files = ModFileIO.writeModFiles(obj.getContentsArr());
        json.put(ModManifest.JsonFields.files, files);

        return json;
    } // write()

} // Class
