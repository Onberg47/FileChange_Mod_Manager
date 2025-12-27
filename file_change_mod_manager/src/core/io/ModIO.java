/*
 * Author: Stephanos B
 * Date: 15/12/2025
 */
package core.io;

import java.time.LocalDateTime;

import org.json.simple.*;

import core.objects.FileVersion;
import core.objects.Mod;
import core.objects.ModManifest;

/**
 * Utility class to handle reading and writing Mod JSON files.
 * 
 * @author Stephanos B
 * @apiNote See JsonIO.java for docs.
 */
public class ModIO {

    /**
     * Populate a the Object from a JSONObject.
     * 
     * @param json JSONObject to read from.
     * @return The populated Object if successful.
     */
    static Mod read(JSONObject json) {
        ModManifest mod = new ModManifest();

        mod.setGameId((String) json.get(Mod.JsonFields.gameId.toString()));
        mod.setVersion((String) json.get(Mod.JsonFields.version.toString()));
        mod.setDownloadSource((String) json.get(Mod.JsonFields.downloadSource.toString()));
        mod.setName((String) json.get(Mod.JsonFields.name.toString()));
        mod.setDescription((String) json.get(Mod.JsonFields.description.toString()));
        mod.setDownloadLink((String) json.get(Mod.JsonFields.downloadLink.toString()));

        mod.setLoadOrder(((Long) json.get(Mod.JsonFields.loadOrder.toString())).intValue());

        Object dateObj = json.get(FileVersion.JsonFields.timestamp.toString()); // Fixed field name
        if (dateObj != null) { // Add null check
            try {
                String dateString = (String) dateObj;
                // Use a specific formatter to match what you wrote
                LocalDateTime lDate = LocalDateTime.parse(dateString);
                mod.setDownloadDate(lDate);
            } catch (Exception e) {
                System.err.println("❗ Warning: Could not parse timestamp: " + dateObj.toString());
                // Will be left at default: .now()
            }
        }

        mod.generateModId(); // Do this last for saftey.
        return mod;
    } // read()

    /**
     * Populates a JSONObject from the given Class
     * 
     * @param obj Object to process.
     * @return JSONObject ready to be written to a JSON file.
     */
    @SuppressWarnings("unchecked")
    public static JSONObject write(Mod obj) {
        JSONObject json = new JSONObject();

        json.put(Mod.JsonFields.id, obj.getId());
        json.put(Mod.JsonFields.gameId, obj.getGameId());
        json.put(Mod.JsonFields.version, obj.getVersion());
        json.put(Mod.JsonFields.name, obj.getName());
        json.put(Mod.JsonFields.description, obj.getDescription());
        json.put(Mod.JsonFields.downloadLink, obj.getDownloadLink());

        json.put(Mod.JsonFields.downloadSource, obj.getDownloadSource().getCode());
        json.put(Mod.JsonFields.loadOrder, obj.getLoadOrder());

        json.put(FileVersion.JsonFields.timestamp, obj.getDownloadDate().toString());

        return json;
    } // write()

} // Class