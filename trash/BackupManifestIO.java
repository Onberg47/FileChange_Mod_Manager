/*
 * Author: Stephanos B
 * Date: 22/12/2025
 */
package io;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import interfaces.JsonSerializable;
import objects.BackupManifest;
import objects.ModManifest;

/**
 * IO class for BackupManifests.
 * 
 * @author Stephanos B
 * @apiNote See JsonIO.java for docs.
 */
public class BackupManifestIO {

    /**
     * 
     * @param file
     * @return
     * @throws Exception
     */
    public static BackupManifest read(JSONObject json) throws Exception {
        BackupManifest obj = new BackupManifest();

        JSONArray files = (JSONArray) json.get(ModManifest.JsonFields.files.toString());
        obj.setFilesArr(ModFileIO.readModFiles(files)); // Reuse the same method for reading ModFiles.

        return obj;
    } // read()

    /**
     * Populates a JSONObject from the given Class
     * 
     * @param obj Object to process.
     * @return JSONObject ready to be written to a JSON file.
     */
    @SuppressWarnings("unchecked")
    public static JSONObject write(BackupManifest obj) {
        JSONObject json = new JSONObject();
        json.put(JsonSerializable.ObjectTypeKey, obj.getObjectType()); // standard

        // Write ModManifest fields.
        JSONArray files = ModFileIO.writeModFiles(obj.getFilesArr());
        json.put(ModManifest.JsonFields.files, files);

        return json;
    } // write()

} // Class
