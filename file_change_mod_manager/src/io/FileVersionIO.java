/*
 * Author: Stephanos B
 * Date: 24/12/2025
 */
package io;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.simple.JSONObject;
import objects.FileVersion;
import objects.Mod;

/**
 * Utility class to handle reading and writing JSON files.
 * 
 * @author Stephanos B
 * @apiNote See JsonIO.java for docs.
 */
public class FileVersionIO {

    /**
     * Populate a the Object from a JSONObject.
     * 
     * @param json JSONObject to read from.
     * @return The populated Object if successful.
     */
    static FileVersion read(JSONObject json) {
        FileVersion fileV = new FileVersion();

        fileV.setModId((String) json.get(FileVersion.JsonFields.modId.toString()));
        fileV.setHash((String) json.get(FileVersion.JsonFields.hash.toString()));

        Object dateObj = json.get(FileVersion.JsonFields.timestamp.toString()); // Fixed field name
        if (dateObj != null) { // Add null check
            try {
                String dateString = (String) dateObj;
                // Use a specific formatter to match what you wrote
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                LocalDateTime lDate = LocalDateTime.parse(dateString, formatter);
                fileV.setTimestamp(lDate);
            } catch (Exception e) {
                System.err.println("❗ Warning: Could not parse timestamp: " + dateObj.toString());
                // FileVersion timeStamp will be left at default: .now()
            }
        }
        return fileV;
    } // read()

    /**
     * Populates a JSONObject from the given Class
     * 
     * @param obj Object to process.
     * @return JSONObject ready to be written to a JSON file.
     */
    @SuppressWarnings("unchecked") // type-safe warning comes from how JSONObject works internally!
    public static JSONObject write(FileVersion obj) {
        JSONObject json = new JSONObject();

        json.put(FileVersion.JsonFields.modId, obj.getModId());
        json.put(FileVersion.JsonFields.hash, obj.getHash());
        // Use a consistent format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        json.put(FileVersion.JsonFields.timestamp, obj.getTimestamp().format(formatter));

        return json;
    } // write()

} // Class
