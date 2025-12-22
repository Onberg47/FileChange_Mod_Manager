/**
 * Author Stephanos B
 * Date 22/12/2025
 */
package objects;

import org.json.simple.JSONObject;

import interfaces.JsonSerializable;
import io.BackupManifestIO;

/**
 * A lightweight class for storing ModFiles with no further information.
 * 
 * @author Stephanos B
 */
public class BackupManifest implements JsonSerializable {

    private ModFile[] filesArr;

    public BackupManifest() {
    }

    public BackupManifest(ModFile[] filesArr) {
        this.filesArr = filesArr;
    }

    /// /// /// Implements /// /// ///

    @Override
    public String getObjectType() {
        return ObjectTypes.BACKUP_MANIFEST;
    }

    @Override
    public JSONObject toJsonObject() {
        return BackupManifestIO.write(this); // keeps IO operations seperate
    } // toJsonObject()

    /// /// /// Getters and Setters /// /// ///

    public ModFile[] getFilesArr() {
        return filesArr;
    }

    public void setFilesArr(ModFile[] filesArr) {
        this.filesArr = filesArr;
    }

} // Class
