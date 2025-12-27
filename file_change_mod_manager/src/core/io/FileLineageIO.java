/*
 * Author: Stephanos B
 * Date: 24/12/2025
 */
package core.io;

import java.util.Stack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import core.objects.FileLineage;
import core.objects.FileVersion;

/**
 * Utility class to handle reading and writing JSON files.
 * 
 * @author Stephanos B
 * @apiNote See JsonIO.java for docs.
 */
public class FileLineageIO {

    /**
     * Populate a the Object from a JSONObject.
     * 
     * @param json JSONObject to read from.
     * @return The populated Object if successful.
     */
    static FileLineage read(JSONObject json) {
        FileLineage fl = new FileLineage();

        JSONArray stackArr = (JSONArray) json.get(FileLineage.JsonFields.stack.toString());
        Stack<FileVersion> tmp = new Stack<>();

        // Read elements in order - first element in array becomes bottom of stack
        for (Object obj : stackArr) {
            JSONObject fileObj = (JSONObject) obj;
            FileVersion fileV = FileVersionIO.read(fileObj);
            tmp.push(fileV); // Push to maintain order
        }

        // Reverse the stack to get the correct order
        Stack<FileVersion> correctOrderStack = new Stack<>();
        while (!tmp.isEmpty()) {
            correctOrderStack.push(tmp.pop());
        }

        fl.setStack(correctOrderStack);
        return fl;
    } // read()

    /**
     * Populates a JSONObject from the given Class
     * 
     * @param obj Object to process.
     * @return JSONObject ready to be written to a JSON file.
     */
    @SuppressWarnings("unchecked") // type-safe warning comes from how JSONObject works internally!
    public static JSONObject write(FileLineage obj) {
        JSONObject json = new JSONObject();

        JSONArray files = new JSONArray();
        Stack<FileVersion> stack = obj.getStack();

        // Write elements from bottom to top (this preserves the stack order when read
        // back)
        // We need to iterate through the stack in reverse order
        for (int i = stack.size() - 1; i >= 0; i--) {
            FileVersion fileV = stack.get(i); // Get from top down
            JSONObject fileObj = FileVersionIO.write(fileV);
            files.add(fileObj);
        }

        json.put(FileLineage.JsonFields.stack, files);
        return json;
    } // write()

} // Class
