/*
 * Author: Stephanos B
 * Date: 22/12/2025
 */
package io;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import objects.ModFile;

/**
 * @author Stephanos B
 */
public class ModFileIO {

    /**
     * 
     * @param files JSONArray of files to read through.
     * @return An Array of ModFiles from the provided Json files.
     */
    static ModFile[] readModFiles(JSONArray files) {
        ModFile[] tmp = new ModFile[files.size()];
        int i = 0;

        for (Object obj : files) {
            JSONObject fileObj = (JSONObject) obj;
            ModFile modFile = new ModFile();
            modFile.setFilePath((String) fileObj.get(ModFile.JsonFields.filePath.toString()));
            modFile.setHash((String) fileObj.get(ModFile.JsonFields.hash.toString()));
            modFile.setSize((long) fileObj.get(ModFile.JsonFields.size.toString()));
            tmp[i] = modFile;
            i++;
        }
        return tmp;
    } // getModFiles()

    /**
     * 
     * @param modFiles
     * @return
     */
    @SuppressWarnings("unchecked") // type-safe warning comes from how JSONObject works internally!
    static JSONArray writeModFiles(ModFile[] modFiles) {
        JSONArray files = new JSONArray();

        for (ModFile modFile : modFiles) {
            JSONObject fileObj = new JSONObject();
            fileObj.put(ModFile.JsonFields.filePath, modFile.getFilePath());
            fileObj.put(ModFile.JsonFields.hash, modFile.getHash());
            fileObj.put(ModFile.JsonFields.size, modFile.getSize());
            files.add(fileObj);
        }
        return files;
    } //

} // Class