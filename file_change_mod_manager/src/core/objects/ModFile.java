/*
 * Author: Stephanos B
 * Date: 15/12/2025
*/

package core.objects;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import core.interfaces.MapSerializable;

/**
 * Represents the File Contents of a Mod within the Mod.JSON file.
 * 
 * @author Stephanos B
 */
public class ModFile implements MapSerializable {

    private Path filePath; // Path of the content file within the Mod
    private String hash; // SHA-256 stored as a hexadecimal string, of file contents
    private long size = 0; // For info/validation

    /**
     * Used to ensure Json Keys are consistent.
     */
    public enum JsonFields {
        filePath,
        hash,
        size
    }

    public enum FileOperation {
        ADD, // File didn't exist before
        REPLACE, // Overwrote existing file
        DELETE // Removed vanilla file
    }

    public ModFile() {
        this.filePath = Path.of("");
        this.hash = "";
        this.size = 0;
    }

    /**
     * Standard parameterized constructor for ModContent.
     * 
     * @param filePath The path of the content file within the Mod.
     * @param hash     The SHA-256 hash of the file contents.
     */
    public ModFile(Path filePath, String hash, long size) {
        this();
        this.setFilePath(filePath);
        this.hash = hash.toLowerCase();
        this.size = size;
    }

    /// /// /// Implements /// /// ///

    @Override
    public String getObjectType() {
        // return ObjectTypes.MOD_FILE;
        return "ModFile (component)"; // This is never used as a standalone file, so this is never used.
    }

    @Override
    public ModFile setFromMap(Map<String, Object> map) {
        if (map.containsKey(JsonFields.filePath.toString()))
            this.setFilePath((String) map.get(JsonFields.filePath.toString()));

        if (map.containsKey(JsonFields.hash.toString()))
            this.setHash((String) map.get(JsonFields.hash.toString()));

        if (map.containsKey(JsonFields.size.toString()))
            this.setSize((Long) map.get(JsonFields.size.toString()));

        return this;
    } // setFromMap()

    @Override
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();

        map.put(JsonFields.filePath.toString(), this.getFilePath());
        map.put(JsonFields.hash.toString(), this.getHash());
        map.put(JsonFields.size.toString(), this.getSize());

        return map;
    } // toMap()

    /// /// /// Getters and Setters /// /// ///

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath != null ? filePath.normalize() : null;
    }

    public void setFilePath(String filePathStr) {
        this.filePath = filePathStr != null ? Paths.get(filePathStr).normalize() : null;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash.toLowerCase();
    }

    public long getSize() {
        return size;
    }

    public void setSize(long originalSize) {
        this.size = originalSize;
    }

    /// /// /// Methods /// /// ///

    /**
     * Returns a string representation of the ModContent.
     * 
     * @return A string representation of the ModContent.
     */
    @Override
    public String toString() {
        return String.format("filePath= %s, hash= %s, size= %d", filePath,
                hash.length() <= 6 ? hash : hash.substring(0, 6) + "...", size);
    } // toString()

} // Class
