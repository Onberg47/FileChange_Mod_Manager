/*
 * Author: Stephanos B
 * Date: 15/12/2025
 */
package io;

import java.time.DateTimeException;
import java.time.Instant;
import java.util.Date;

import org.json.simple.*;

import interfaces.JsonSerializable;
import objects.Mod;
import objects.ModManifest;

/**
 * Utility class to handle reading and writing Mod JSON files.
 * 
 * @author Stephanos B
 * @apiNote See JsonIO.java for docs.
 */
public class ModIO {

    /**
     * @param json
     * @return
     * @throws Exception
     */
    static Mod read(JSONObject json) throws Exception {
        ModManifest mod = new ModManifest();
        mod.setGameId((String) json.get(Mod.JsonFields.gameId.toString()));
        mod.setVersion((String) json.get(Mod.JsonFields.version.toString()));
        mod.setDownloadSource((String) json.get(Mod.JsonFields.downloadSource.toString()));
        mod.setName((String) json.get(Mod.JsonFields.name.toString()));
        mod.setDescription((String) json.get(Mod.JsonFields.description.toString()));
        mod.setDownloadLink((String) json.get(Mod.JsonFields.downloadLink.toString()));

        mod.setLoadOrder(((Long) json.get(Mod.JsonFields.loadOrder.toString())).intValue());

        Object dateObj = json.get(Mod.JsonFields.downloadDate.toString());
        try {
            String dateString = (String) dateObj;
            Instant instant = Instant.parse(dateString);
            mod.setDownloadDate(Date.from(instant));
        } catch (Exception e) {
            System.err.println("❗ Warning: Could not parse download date: " + dateObj);
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
        json.put(JsonSerializable.ObjectTypeKey, obj.getObjectType()); // standard

        json.put(Mod.JsonFields.id, obj.getId());
        json.put(Mod.JsonFields.gameId, obj.getGameId());
        json.put(Mod.JsonFields.version, obj.getVersion());
        json.put(Mod.JsonFields.name, obj.getName());
        json.put(Mod.JsonFields.description, obj.getDescription());
        json.put(Mod.JsonFields.downloadLink, obj.getDownloadLink());

        json.put(Mod.JsonFields.downloadSource, obj.getDownloadSource().getCode());
        json.put(Mod.JsonFields.loadOrder, obj.getLoadOrder());

        try {
            String dateString = obj.getDownloadDate().toInstant().toString();
            json.put(Mod.JsonFields.downloadDate.toString(), dateString);
        } catch (DateTimeException e) {
            System.err.println("❗ Warning: Could not format date!");
        }
        return json;
    } // write()

} // Class