/*
 * Author: Stephanos B
 * Date: 15/12/2025
*/
package core.objects;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Mod.JSON file for tracking contents and metadata of a Mod.
 * 
 * @author Stephanos B
 */
public class Mod extends ModMetadata {

    /**
     * Empty constructor for Mod.
     */
    public Mod() {
        super();
    }

    /**
     * Essentials parameterized constructor for Mod.
     * 
     * @param gameId      The ID of the Game this Mod is for.
     * @param source      The source of the Mod, used for ID generation.
     * @param name        User-friendly name, doubles as the filename for the Mod.
     * @param description The description of the Mod.
     */
    public Mod(String gameId, String downloadSource, String name) {
        super(gameId, downloadSource, name);
    }

    /**
     * Complete constructor with ALL fields, including the Auto-generated. Meant for
     * Mod -> Child types initializing.
     * 
     * @param gameIdThe      ID of the Game this Mod is for.
     * @param id             ID of the mod. Only copied to reduce overhead of
     *                       regeneration.
     * @param downloadSource
     * @param version
     * @param name
     * @param description
     * @param loadOrder
     * @param downloadDate
     * @param downloadLink
     */
    protected Mod(String gameId, String id, String downloadSource, String version, String name, String description,
            int loadOrder, LocalDateTime downloadDate, String downloadLink) {

        super(gameId, id, downloadSource, version, name, description, loadOrder, downloadDate, downloadLink);
    }

    /// /// /// Implements /// /// ///

    @Override
    public String getObjectType() {
        return ObjectTypes.MOD;
    }

    @Override
    public Mod setFromMap(Map<String, Object> map) {
        super.setFromMap(map);
        return this;
    } // setFromMap()

    @Override
    public HashMap<String, Object> toMap() {
        return super.toMap();
    } // toMap()

    /// /// /// Methods /// /// ///

    public String printLite() {
        return String.format("ID: %s | Name: %-20s | Order : %-3d", getId(), getName(), getLoadOrder());
    }

} // Class
