/**
 * Author Stephanos B
 * Date 22/12/2025
 */
package core.objects;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import core.interfaces.MapSerializable;
import core.utils.Logger;

/**
 * Object for keeping a Stack of FileVersions. Reads/Writes to a Json, so it
 * must implement my JsonSerializable.
 * 
 * @author Stephanos B
 */
public class FileLineage implements MapSerializable {

    private Stack<FileVersion> stack = new Stack<FileVersion>();

    /**
     * Used to ensure Json Keys are consistent.
     */
    public enum Keys {
        STACK("stack");

        private final String key;

        private Keys(String key) {
            this.key = key;
        }

        public String key() {
            return this.key;
        }
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

    @SuppressWarnings("unchecked")
    @Override
    public FileLineage setFromMap(Map<String, Object> map) {

        /// stack
        if (map.containsKey(Keys.STACK.key)) {
            Object rawValue = map.get(Keys.STACK.key);
            if (rawValue instanceof List) {
                List<?> rawList = (List<?>) rawValue;
                Stack<FileVersion> ls = new Stack<>();
                for (Object item : rawList) {
                    if (item instanceof Map) {
                        Map<String, Object> modMap = (Map<String, Object>) item;
                        ls.add(new FileVersion().setFromMap(modMap));
                    }
                }
                this.setStack(ls);
            }
        }
        return this;
    } // setFromMap()

    @Override
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();

        ArrayList<HashMap<String, Object>> arrLs = new ArrayList<>();
        /// Get map of each modFile stored.
        for (FileVersion tmp : this.getStack()) {
            arrLs.add((HashMap<String, Object>) tmp.toMap());
        }
        map.put(Keys.STACK.key, arrLs);

        return map;
    } // toMap()

    /// /// /// Getters and Setters /// /// ///

    public Stack<FileVersion> getStack() {
        return stack;
    }

    public void setStack(Stack<FileVersion> stack) {
        this.stack = stack;
    }

    /// /// /// Core Methods /// /// ///

    /**
     * PUSH: Add a new owner to top without any checks.
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
     * @return The index from the top inserted at. Where 0 is the top.
     * @throws Exception Throws if it cannot determine where to insert the mod.
     */
    public int insertOrderedVersion(FileVersion fVersion, Path manifestPath, int loadOrder) throws Exception {
        // Check for duplicate mod ID.
        // Cannot just use stack.contains() because Hashes or timestamps could differ.
        for (FileVersion existing : stack) {
            if (existing.getModId().equals(fVersion.getModId())) {
                Logger.getInstance().warning("Mod already present in Lineage. Removing first...", null);
                this.removeAllOf(fVersion.getModId());
                break;
            }
        }

        try {
            // Find insertion point
            int insertIndex = 0; // default to end
            for (int i = stack.size() - 1; i >= 0; i--) {
                if (loadOrder >= stack.get(i).getLoadOrder(manifestPath)) {
                    insertIndex = i + 1;
                    break;
                }
            }
            stack.insertElementAt(fVersion, insertIndex); // Insert at correct position
            return (stack.size() - 1 - insertIndex); // make it so 0 is top.

        } catch (Exception e) {
            throw new Exception("Could not determine load order! " + e.getMessage(), e);
        }
    } // insertOrderedVersion()

    /**
     * Attempts to insert a new FileVersion into the stack while respecting Load
     * Ordering.
     * 
     * @param fVersion  The FileVersion to be inserted where appropriate.
     * @param gameState Current GameState to use for loadOrder checks.
     * @return The index from the top inserted at. Where 0 is the top.
     * @throws Exception Throws if it cannot determine where to insert the mod.
     */
    public int insertOrderedVersion(FileVersion fVersion, GameState gameState, int loadOrder) throws Exception {
        // Check for duplicate mod ID.
        // Cannot just use stack.contains() because Hashes or timestamps could differ.
        for (FileVersion existing : stack) {
            if (existing.getModId().equals(fVersion.getModId())) {
                Logger.getInstance().warning("Mod already present in Lineage. Removing first...", null);
                this.removeAllOf(fVersion.getModId());
                break;
            }
        }

        try {
            // Find insertion point
            int insertIndex = 0; // default to end
            for (int i = stack.size() - 1; i >= 0; i--) {
                // System.out.printf("if l:%d >= g:%d\n", loadOrder,
                // gameState.getLoadOrder(stack.get(i).getModId())); // TODO remove debug
                if (loadOrder >= gameState.getLoadOrder(stack.get(i).getModId())) {
                    insertIndex = i + 1;
                    break;
                }
            }
            stack.insertElementAt(fVersion, insertIndex); // Insert at correct position
            return (stack.size() - 1 - insertIndex); // make it so 0 is top.

        } catch (Exception e) {
            throw new Exception("Could not determine load order! " + e.getMessage(), e);
        }
    }

    /**
     * Removes all occurances of Versions that belong to the given ID without
     * effecting the order of other instances.
     * 
     * @param ModId Mod ID to find occurances to remove.
     */
    public void removeAllOf(String ModId) {
        Stack<FileVersion> tmpStack = new Stack<FileVersion>();
        FileVersion tmpFV;

        while (!this.stack.isEmpty()) {
            tmpFV = stack.pop();
            if (!tmpFV.getModId().equals(ModId)) {
                tmpStack.addFirst(tmpFV);
            }
        }
        this.stack = tmpStack;
    } // removeAllOf()

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append("File Lineage:\n");
        for (FileVersion fileVersion : stack) {
            str.append(" - " + fileVersion.toString() + "\n");
        }

        return str.toString();
    }

} // Class