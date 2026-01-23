/**
 * Author Stephanos B
 * Date 16/12/2025
 */
package core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InvalidObjectException;
import java.util.HashMap;
import java.util.Map;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import com.github.cliftonlabs.json_simple.JsonException;

import core.interfaces.MapSerializable;
import core.objects.FileLineage;
import core.objects.Game;
import core.objects.GameState;
import core.objects.Mod;
import core.objects.ModManifest;

/**
 * Updated version
 */
public class JsonIO {

    /**
     * 
     * @param file        File to try read from. Can handle missing {@code .json}
     *                    extension.
     * @param type_string String defining the type of object expected in the file.
     *                    If Null it will auto-decide.
     * @return
     * @throws IllegalArgumentException When the type_string is unkown
     * @throws InvalidObjectException   The provided File does not match the
     *                                  specified type.
     * @throws FileNotFoundException    The provided file does not exsist.
     * @throws Exception                An error with the Json process or invalid
     *                                  file (path or extension)
     */
    public static MapSerializable read(File file, String type_string) throws Exception {
        return read(file, type_string, null);
    }

    /**
     * 
     * @param file        File to try read from. Can handle missing {@code .json}
     *                    extension.
     * @param type_string String defining the type of object expected in the file.
     *                    If Null it will auto-decide.
     * @param cast_type   Override what object to return. This is for overriding
     *                    child types. (Mod / ModManifest)
     * @return
     * @throws IllegalArgumentException When the type_string is unkown
     * @throws InvalidObjectException   The provided File does not match the
     *                                  specified type.
     * @throws FileNotFoundException    The provided file does not exsist.
     * @throws Exception                An error with the Json process or invalid
     *                                  file (path or extension)
     */
    public static MapSerializable read(File file, String type_string, String cast_type) throws Exception {
        checkReadFile(file);

        JsonObject json;
        try (FileReader fileReader = new FileReader(file)) {
            json = (JsonObject) Jsoner.deserialize(fileReader);
        } catch (JsonException e) {
            throw new Exception("Failed to parse JSON: " + e.getMessage(), e);
        }

        String fileType = (String) json.get(MapSerializable.ObjectTypeKey);
        if (type_string != null && !fileType.equals(type_string))
            throw new InvalidObjectException("The file does not store the desired Object!");
        if (cast_type == null)
            cast_type = fileType;

        // Queries the actual file type to allow auto-detection
        switch (cast_type) {
            case MapSerializable.ObjectTypes.MOD:
                return new Mod().setFromMap(json);
            case MapSerializable.ObjectTypes.MOD_MANIFEST:
                return new ModManifest().setFromMap(json);

            case MapSerializable.ObjectTypes.GAME:
                return new Game().setFromMap(json);
            case MapSerializable.ObjectTypes.GAME_STATE:
                return new GameState().setFromMap(json);

            case MapSerializable.ObjectTypes.FILE_LINEAGE:
                return new FileLineage().setFromMap(json);

            default:
                throw new IllegalArgumentException("Unknown object type: " + type_string);
        }
    } // read()

    /**
     * Writes a JSON file to the given location of the given Object. Type is
     * implicit, no need to specify.
     * 
     * @param object Any JsonSerializable implmenting object.
     * @param file   File with Filename to write to. Can handle missing
     *               {@code .json} extension.
     * @throws Exception
     */
    public static void write(MapSerializable object, File file) throws Exception {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        if (file.getName().toLowerCase().endsWith(".json") == false) { // appends the `.json` extension if needed
            file = file.toPath().getParent().resolve(
                    (file.toPath().getFileName() + ".json")).toFile();
        }
        // end of checks...

        Map<String, Object> map = object.toMap();
        map.put(MapSerializable.ObjectTypeKey, object.getObjectType());
        JsonObject json = new JsonObject(map);

        // Create parent directories if they don't exist
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(file)) {
            Jsoner.serialize(json, writer);
        }
    } // write()

    /// /// /// Helper /// /// ///

    /**
     * A Utility method that handles reading and parsing the JSON, returning a
     * Keyed-value HashMap of the Json file.
     * This is for simple (non-nested) Json files for non-JsonSerializable objects.
     * 
     * @param file File to read. Can handle missing {@code .json} extension.
     * @return HashMap<String, Object>
     * @throws IllegalArgumentException If the file provided is not a regualr file
     *                                  or is empty.
     * @throws FileNotFoundException    The provided file does not exsist.
     * @throws Exception                Fatal Json errors.
     */
    public static HashMap<String, Object> readHashMap(File file) throws Exception {
        checkReadFile(file); // using a helper to make this cleaner

        HashMap<String, Object> hMap = new HashMap<>();

        JsonObject json;
        try (FileReader fileReader = new FileReader(file)) {
            json = (JsonObject) Jsoner.deserialize(fileReader);
        } catch (JsonException e) {
            throw new Exception("Failed to parse JSON: " + e.getMessage(), e);
        }

        for (Map.Entry<String, Object> entry : json.entrySet()) {
            hMap.put(entry.getKey(), entry.getValue());
        }
        return hMap;
    } // readHashMap()

    /**
     * A Utility method that handles writing to a JSON file from a Keyed-value
     * HashMap.
     * This is for simple (non-nested) Json files for non-JsonSerializable objects.
     * 
     * @param file File to write. Can handle missing {@code .json} extension. Will
     *             create the file.
     * @param hMap A HashMap<String, Object> to write to Json.
     * @throws IllegalArgumentException If hMap or file is empty/null
     * @throws Exception                If the file directories do not exsist.
     */
    public static void writeHashMap(File file, HashMap<String, Object> hMap) throws Exception {
        if (hMap == null || hMap.isEmpty()) {
            throw new IllegalArgumentException("HashMap cannot be null or empty");
        }
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        if (!file.getName().toLowerCase().endsWith(".json")) {
            file = file.toPath().getParent().resolve(
                    file.toPath().getFileName() + ".json").toFile();
        }
        // end of checks...

        JsonObject json = new JsonObject();
        for (Map.Entry<String, Object> entry : hMap.entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }

        // Create parent directories if they don't exist
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(file)) {
            Jsoner.serialize(json, writer);
        }
    } // writeHasMap()

    /// /// /// Helpers

    private static void checkReadFile(File file) throws Exception {
        if (!file.exists()) {
            throw new InvalidObjectException("File path is not a valid .json path: " + file.toPath().toString());
        }
        if (!file.isFile()) { // Check if it's actually a file (not a directory)
            throw new IllegalArgumentException("Path is not a file: " + file.getAbsolutePath());
        }
        if (file.length() == 0) { // Check if file has content (optional, depending on your needs)
            throw new IllegalArgumentException("File is empty: " + file.getAbsolutePath());
        }
        if (!file.getName().toLowerCase().endsWith(".json")) { // appends the `.json` extension if needed
            file = file.toPath().getParent().resolve(
                    file.toPath().getFileName() + ".json").toFile();
        }
    }
}
// Class
