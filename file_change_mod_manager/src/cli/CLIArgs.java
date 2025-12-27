/*
 * Author Stephanos B
 * Date: 26/12/2025
 */
package cli;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingFormatArgumentException;

/**
 * Handles processing of Args from CLI
 */
public class CLIArgs {
    private Map<String, String> params = new HashMap<>();

    public CLIArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                String key = args[i].substring(2);
                String value = (i + 1 < args.length && !args[i + 1].startsWith("--"))
                        ? args[++i]
                        : "true"; // Flag without value
                params.put(key, value);
            }
        }
    }

    public String getString(String key, String defaultValue) {
        return params.getOrDefault(key, defaultValue);
    }

    /**
     * Throws if the key is missing to force interuption.
     * 
     * @param key
     * @return Value of the key, only on success
     * @throws MissingFormatArgumentException The required key is missing.
     */
    public String getRequired(String key) throws MissingFormatArgumentException {
        if (!params.containsKey(key)) {
            throw new MissingFormatArgumentException("Missing required argument: --" + key); // CLIException
        }
        return params.get(key);
    }

    /**
     * Check if an Argument key exsists.
     * 
     * @param key
     * @return true if key exsists.
     */
    public boolean hasFlag(String key) {
        return params.containsKey(key);
    }
} // Class
