/*
 * Author: Stephanos B
 * Date: 15/12/2025
*/

package core.objects;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import core.interfaces.MapSerializable;

/**
 * Represents the File Contents of a Mod within the Mod.JSON file.
 * 
 * @author Stephanos B
 */
public class ModFile implements MapSerializable {

    /**
     * {@code ~mod_id/} {@code data/file.txt}
     * <br>
     * <br>
     * Relative Path from the Mod home to the file with all path elements.
     */
    private Path filePath;
    private String hash; // SHA-256 stored as a hexadecimal string, of file contents
    private long size = 0; // For info/validation

    /**
     * Used to ensure Json Keys are consistent.
     */
    public enum Keys {
        FILE_PATH("filePath"),
        HASH("hash"),
        SIZE("size");

        private final String key;

        private Keys(String key) {
            this.key = key;
        }

        public String key() {
            return this.key;
        }
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
        if (map.containsKey(Keys.FILE_PATH.key))
            this.setFilePath((String) map.get(Keys.FILE_PATH.key));

        if (map.containsKey(Keys.HASH.key))
            this.setHash((String) map.get(Keys.HASH.key));

        if (map.containsKey(Keys.SIZE.key))
            this.setSize(Long.parseLong(map.get(Keys.SIZE.key).toString()));

        return this;
    } // setFromMap()

    @Override
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();

        map.put(Keys.FILE_PATH.key, this.getFilePath().toString());
        map.put(Keys.HASH.key, this.getHash());
        map.put(Keys.SIZE.key, this.getSize());

        return map;
    } // toMap()

    /// /// /// Getters and Setters /// /// ///

    /**
     * {@code ~mod_id/} {@code data/file.txt}
     * <br>
     * <br>
     * Relative Path from the Mod home to the file with all path elements.
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Normalises and sets the Path.<br>
     * <br>
     * ONLY use this when the path has no risk of being cross-OS.
     * 
     * @param filePathStr The relative path to the ModFile.
     */
    public void setFilePath(Path filePath) {
        this.filePath = filePath != null ? filePath.normalize() : null;
    }

    /**
     * Creates a normalised and sanitised Path from a String.<br>
     * <br>
     * ALWAYS use this when there is risk of cross-OS paths.
     * 
     * @param filePathStr The relative path to the ModFile.
     */
    public void setFilePath(String filePathStr) {
        for (String sep : new String[] { "/", "\\" }) {
            if (!File.pathSeparator.equals(sep))
                filePathStr = filePathStr.replace(sep, File.separator);
        }

        this.filePath = filePathStr != null ? Path.of(filePathStr).normalize() : null;
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
