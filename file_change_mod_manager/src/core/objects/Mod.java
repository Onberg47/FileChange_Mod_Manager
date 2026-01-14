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

    private Boolean enabled;

    /**
     * Empty constructor for Mod.
     */
    public Mod() {
        super();
    }

    /**
     * Essentials parameterized constructor for Mod.
     * 
     * @param gameId The ID of the Game this Mod is for.
     */
    public Mod(String gameId) {
        super(gameId);
    }

    protected Mod(String gameId,
            String id,
            String downloadSource,
            String version,
            String name,
            String description,
            int loadOrder,
            LocalDateTime downloadDate,
            String downloadLink,
            Boolean forceIdUpdate) {

        super(gameId,
                id,
                downloadSource,
                version,
                name,
                description,
                loadOrder,
                downloadDate,
                downloadLink);
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

    /// /// /// Getters and Setters /// /// ///

    public Boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /// /// /// Methods /// /// ///

    /**
     * A light, single-line print out of a Mod with essential information. Includes
     * the Enabled/Disabled Flag for debugging if set.
     */
    public String printLite() {
        return String.format("ID: %18s | Name: %-40s | Order : %-3d %s", getId(), getName(), getLoadOrder(),
                isEnabled() != null ? (" | " + this.isEnabled()) : "");
    }

} // Class
