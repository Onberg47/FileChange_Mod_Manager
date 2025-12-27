package core.io;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InvalidObjectException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import core.interfaces.JsonSerializable;

public class JsonIO {

    /**
     * 
     * @param file        {@code File.json} to attempt to read. Can handle missing
     *                    extension.
     * @param type_string String defining the type of object expected in the file.
     *                    If Null it will auto-decide.
     * @return
     * @throws IllegalArgumentException When the type_string is unkown
     * @throws InvalidObjectException   The provided File does not match the
     *                                  specified type.
     * @throws Exception                An error with the Json process or invalid
     *                                  file (path or extension)
     */
    public static JsonSerializable read(File file, String type_string) throws Exception {
        if (!file.exists()) {
            throw new Exception("File path is not a valid .json path: " + file.getAbsolutePath());
        }
        if (!file.isFile()) { // Check if it's actually a file (not a directory)
            throw new IllegalArgumentException("Path is not a file: " + file.getAbsolutePath());
        }
        if (file.length() == 0) { // Check if file has content (optional, depending on your needs)
            throw new IllegalArgumentException("File is empty: " + file.getAbsolutePath());
        }
        if (file.getName().toLowerCase().endsWith(".json") == false) { // appends the `.json` extension if needed

            file = file.toPath().getParent().resolve(
                    (file.toPath().getFileName() + ".json")).toFile();
        }
        // end of checks...

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(new FileReader(file));

        String fileType = ((String) json.get(JsonSerializable.ObjectTypeKey));
        if (type_string != null && !fileType.equals(type_string))
            throw new InvalidObjectException("The file does not store the desired Object!");

        // Queries the actual file type to allow auto-detection
        switch (fileType) {
            case JsonSerializable.ObjectTypes.MOD:
                return ModIO.read(json);
            case JsonSerializable.ObjectTypes.MOD_MANIFEST:
                return ModManifestIO.read(json);

            case JsonSerializable.ObjectTypes.GAME:
                return GameIO.read(json);
            case JsonSerializable.ObjectTypes.GAME_STATE:
                return GameStateIO.read(json);

            case JsonSerializable.ObjectTypes.FILE_LINEAGE:
                return FileLineageIO.read(json);

            default:
                throw new IllegalArgumentException("Unknown object type: " + type_string);
        }
    } // read()

    /**
     * Writes a JSON file to the given location of the given Object.
     * 
     * @param object Any JsonSerializable implmenting object.
     * @param file   File with Filename to write to. Can handle missing extension.
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void write(JsonSerializable object, File file) throws Exception {
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

        JSONObject json = object.toJsonObject(); // Create JSONObject from the object
        json.put(JsonSerializable.ObjectTypeKey, object.getObjectType()); // always add the file-type written.

        // Write to file
        try (
                FileWriter writer = new FileWriter(file)) {
            json.writeJSONString(writer);
        }
    } // write()

} // Class
