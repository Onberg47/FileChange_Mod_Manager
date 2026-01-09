/*
 * Author: Stephanos B
 * Date: 09/01/2026
*/
package core.objects;

import java.util.HashMap;
import java.util.Map;

/**
 * Use this to Force not getting a ModManifest.
 */
public class ModLight extends Mod {
    public ModLight() {
        super();
    }

    /// /// /// Implements /// /// ///

    @Override
    public String getObjectType() {
        return ObjectTypes.MOD;
    }

    @Override
    public ModLight setFromMap(Map<String, Object> map) {
        super.setFromMap(map);
        return this;
    } // setFromMap()

    @Override
    public HashMap<String, Object> toMap() {
        return super.toMap();
    } // toMap()

} // Class
