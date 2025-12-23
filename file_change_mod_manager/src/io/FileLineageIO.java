package io;

import org.json.simple.JSONObject;

import interfaces.JsonSerializable;
import objects.FileLineage;

/**
 * Utility class to handle reading and writing JSON files.
 * 
 * @author Stephanos B
 * @apiNote See JsonIO.java for docs.
 */
public class FileLineageIO {

    /**
     * 
     * 
     * @param file The ModManifest file to read.
     * @return The ModManifest object if successful.
     * @throws Exception
     */
    static FileLineage read(JSONObject json) throws Exception {

        FileLineage fl = new FileLineage();

        // TODO

        return fl;
    } // read()

    /**
     * Populates a JSONObject from the given Class
     * 
     * @param obj Object to process.
     * @return JSONObject ready to be written to a JSON file.
     */
    @SuppressWarnings("unchecked") // type-safe warning comes from how JSONObject works internally!
    public static JSONObject write(FileLineage obj) {
        JSONObject json = new JSONObject();
        json.put(JsonSerializable.ObjectTypeKey, obj.getObjectType()); // standard

        // TODO

        return json;
    } // write()

} // Class
