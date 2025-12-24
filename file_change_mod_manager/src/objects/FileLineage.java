/**
 * Author Stephanos B
 * Date 22/12/2025
 */
package objects;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Stack;

import org.json.simple.JSONObject;

import interfaces.JsonSerializable;
import io.FileLineageIO;

/**
 * Object for keeping a Stack of FileVersions. Reads/Writes to a Json, so it
 * must implement my JsonSerializable.
 * 
 * @author Stephanos B
 */
public class FileLineage implements JsonSerializable {

    private Stack<FileVersion> stack = new Stack<FileVersion>();

    /**
     * Used to ensure Json Keys are consistent.
     */
    public enum JsonFields {
        stack
    }

    public FileLineage() {
    }

    /**
     * For initial creation of a FileLineage, when the initial entry is created.
     * 
     * @param mFile
     */
    public FileLineage(ModFile mFile, String modId) {
        this.stack.add(new FileVersion(modId, mFile.getHash()));
    }

    /// /// /// Implements /// /// ///

    @Override
    public String getObjectType() {
        return ObjectTypes.FILE_LINEAGE;
    }

    @Override
    public JSONObject toJsonObject() {
        return FileLineageIO.write(this); // keeps IO operations seperate
    } // toJsonObject()

    /// /// /// Getters and Setters /// /// ///

    public Stack<FileVersion> getStack() {
        return stack;
    }

    public void setStack(Stack<FileVersion> stack) {
        this.stack = stack;
    }

    /// /// /// Core Methods /// /// ///

    /**
     * PUSH: Add a new owner to top
     * 
     * @param modId
     * @param hash
     */
    public void pushVersion(String modId, String hash) {
        stack.push(new FileVersion(modId, hash, LocalDateTime.now()));
    }

    /**
     * POP_UNTIL: When a mod is removed
     * 
     * @param modId
     * @return
     */
    public FileVersion popUntil(String modId) {
        // Find version owned by ModId
        int index = -1;
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (modId.equals(stack.get(i).getModId())) {
                index = i;
                break;
            }
        } // popUntil()

        if (index == -1)
            return null; // We don't own this file

        // Remove our version and any newer versions (shouldn't exist if load order
        // respected)
        while (stack.size() > index) {
            stack.pop();
        }

        // Return what should now be current (top of stack)
        return stack.isEmpty() ? null : stack.peek();
    } // popUntil()

    /**
     * PEEK: Get current owner
     * 
     * @return
     */
    public FileVersion peek() {
        return stack.isEmpty() ? null : stack.peek();
    } // peek()

    /// /// /// Methods /// /// ///

    /**
     * Attempts to insert a new FileVersion into the stack while respecting Load
     * Ordering.
     * 
     * @param fVersion     The FileVersion to be inserted where appropriate.
     * @param manifestPath Path required to determine where to fetch deployed
     *                     Manifests for LoadOrder checking.
     */
    public int insertOrderedVersion(FileVersion fVersion, Path manifestPath) throws Exception {
        // Check for duplicate mod ID
        for (FileVersion existing : stack) {
            if (existing.getModId().equals(fVersion.getModId())) {
                System.err.println("Error: Mod already has a version!");
                return -1;
            }
        }

        try {
            int loadOrder = fVersion.getLoadOrder(manifestPath);

            // Find insertion point
            int insertIndex = stack.size(); // default to end
            for (int i = 0; i < stack.size(); i++) {
                if (loadOrder < stack.get(i).getLoadOrder(manifestPath)) {
                    insertIndex = i;
                    break;
                }
            }
            stack.insertElementAt(fVersion, insertIndex); // Insert at correct position
            return insertIndex;

        } catch (Exception e) {
            throw new Exception("Could not determine load order! ", e);
        }
    } // insertOrderedVersion()

} // Class