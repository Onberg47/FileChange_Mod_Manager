/*
 * Author: Stephanos B
 * Date: 22/12/2025
 */
package io;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import interfaces.JsonSerializable;
import objects.ModManifest;

/**
 * 
 * @author Stephanos B
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
     * Writes the given ModManifest object to a Mod.JSON file.
     * 
     * @param obj  Complete ModManifest object to write.
     * @param file The file to write the Mod.JSON to.
     * @throws Exception
     */
    @SuppressWarnings("unchecked") // type-safe warning comes from how JSONObject works internally!
    public static JSONObject write(ModManifest obj) {
        JSONObject json = new JSONObject();
        json.put(JsonSerializable.ObjectTypeKey, obj.getObjectType()); // standard

        // Write standard mod fields.
        json = ModIO.write(obj);

        // Write ModManifest fields.
        JSONArray files = ModFileIO.writeModFiles(obj.getContentsArr());
        json.put(ModManifest.JsonFields.files, files);

        return json;
    } // write()

} // Class
