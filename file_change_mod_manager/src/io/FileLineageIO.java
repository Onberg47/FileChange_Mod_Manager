/*
 * Author: Stephanos B
 * Date: 24/12/2025
 */
package io;

import java.util.Stack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import objects.FileLineage;
import objects.FileVersion;

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

        // TODO THIS PROBABLY DOES NOT RETAIN THE ORDER OF THE STACK!
        JSONArray stackArr = (JSONArray) json.get(FileLineage.JsonFields.stack.toString());
        Stack<FileVersion> tmp = new Stack<>();

        for (Object obj : stackArr) {
            JSONObject fileObj = (JSONObject) obj;

            FileVersion fileV = FileVersionIO.read(fileObj);
            tmp.add(fileV);
        }
        fl.setStack(tmp);

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

        // TODO THIS PROBABLY DOES NOT RETAIN THE ORDER OF THE STACK!
        JSONArray files = new JSONArray();
        Stack<FileVersion> tmp = obj.getStack(); // make a temp we can pop.

        // write each deployed Mod.
        for (int i = tmp.size() - 1; i >= 0; i--) {
            FileVersion fileV = tmp.pop();
            tmp.insertElementAt(fileV, i);

            JSONObject fileObj = new JSONObject();
            fileObj = FileVersionIO.write(fileV);
            files.add(fileObj);
        }
        json.put(FileLineage.JsonFields.stack, files);

        return json;
    } // write()

} // Class
