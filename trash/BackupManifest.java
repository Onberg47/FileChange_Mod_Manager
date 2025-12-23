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
 * Used to track what files are backed-up.
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

    /// /// /// Methods /// /// ///

    /**
     * Adds a ModFile of a file to the ModFile array.
     * 
     * @param content The ModFile to add.
     */
    public void addFile(ModFile content) {
        if (this.filesArr == null) {
            this.filesArr = new ModFile[] { content };
        } else {
            ModFile[] newArr = new ModFile[this.filesArr.length + 1]; // initialize array if needed
            System.arraycopy(this.filesArr, 0, newArr, 0, this.filesArr.length);
            newArr[newArr.length - 1] = content;
            this.filesArr = newArr;
        }
    } // addFile()

    /**
     * Prints the contents of the ModFile array.
     * 
     * @return A string representation of the contents of the Mod.
     */
    public String printContents() {
        String contents = "";
        if (filesArr != null) {
            for (ModFile content : filesArr) {
                contents += content.toString() + "\n";
            }
        } else {
            contents = "No contents available.";
        }
        return contents;
    } // printContents()

} // Class
