/**
 * Author Stephanos B
 * Date 16/01/2026
 */
package gui.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @since v3.3.4
 */
public class ResourceLoader {

    /**
     * The base resource path in your project.
     * Change this if your resource structure changes.
     */
    private static final String RESOURCE_BASE = "resources/";

    public static String getResourceAsString(String resourcePath) {
        // The key: Use `getResourceAsStream` on the ClassLoader.
        // The path is relative to the root of your classpath, which includes 'src'.
        InputStream inputStream = ResourceLoader.class.getClassLoader()
                .getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IllegalArgumentException("Resource not found: " + resourcePath);
        }

        // Read the entire stream into a String
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").next(); // '\A' = start of stream
        }
    }

    /**
     * Returns a URL from a resource html file.<br>
     * <br>
     * Usage:
     * 
     * <pre>
     * <code>
     * URL helpUrl = ResourceLoader.getResourceUrl("help/gameLibrary.html");
     * myEditorPane.setPage(helpUrl);
     * 
     * <pre>
     * <code>
     * 
     * @param resourcePath Path from {@code resources/} to retrieve.
     */
    public static URL getResourceUrl(String resourcePath) {
        URL url = ResourceLoader.class.getClassLoader().getResource(resourcePath);
        if (url == null) {
            throw new IllegalArgumentException("Resource not found: " + resourcePath);
        }
        return url;
    }

    public static InputStream getResourceAsStream(String resourcePath) {
        // Ensure path starts with the resource base
        String fullPath = resourcePath.startsWith(RESOURCE_BASE)
                ? resourcePath
                : RESOURCE_BASE + resourcePath;

        InputStream is = ResourceLoader.class.getClassLoader()
                .getResourceAsStream(fullPath);

        if (is == null) {
            // Helpful debug: try to list what's available
            debugResourceNotFound(fullPath);
            throw new IllegalArgumentException("Resource not found: " + fullPath);
        }

        return is;
    }

    /**
     * Convenience method for icons.
     */
    public static InputStream getIconStream(String iconName) {
        return getResourceAsStream("icons/" + iconName + ".png");
    }

    /**
     * Convenience method for help files.
     */
    public static InputStream getHelpStream(String helpFileName) {
        return getResourceAsStream("help/" + helpFileName + ".html");
    }

    /// /// /// Debug / Testing /// /// ///

    /**
     * Debug helper when a resource isn't found.
     */
    private static void debugResourceNotFound(String path) {
        System.err.println("=== RESOURCE DEBUG ===");
        System.err.println("Looking for: " + path);

        // Try to find what IS available
        try {
            // List contents of the parent directory
            int lastSlash = path.lastIndexOf('/');
            String parentDir = lastSlash > 0 ? path.substring(0, lastSlash) : "";

            if (!parentDir.isEmpty()) {
                System.err.println("\nAttempting to list: " + parentDir);
                // This is a hacky way to list resources - works for simple cases
                URL dirUrl = ResourceLoader.class.getClassLoader().getResource(parentDir);
                if (dirUrl != null) {
                    System.err.println("Directory exists at: " + dirUrl);

                    // For file-based resources (not in JAR), we can list them
                    if ("file".equals(dirUrl.getProtocol())) {
                        File dir = new File(dirUrl.toURI());
                        if (dir.exists() && dir.isDirectory()) {
                            String[] files = dir.list();
                            if (files != null) {
                                System.err.println("Contents:");
                                for (String f : files) {
                                    System.err.println("  - " + f);
                                }
                            }
                        }
                    }
                } else {
                    System.err.println("Parent directory not found either!");
                }
            }
        } catch (Exception e) {
            // Ignore debug errors
        }

        System.err.println("=== END DEBUG ===\n");
    }

    /**
     * Runs tests.
     * 
     */
    public static void main(String[] args) {
        String[] testPaths = {
                "icons/ic_exit.png",
                "/icons/ic_exit.png",
                "resources/icons/ic_exit.png",
                "/resources/icons/ic_exit.png"
        };

        for (String path : testPaths) {
            try (InputStream test = ResourceLoader.class.getClassLoader().getResourceAsStream(path)) {

                System.out.println("Trying '" + path + "': " + (test != null ? "FOUND" : "null"));
                if (test != null) {
                    System.out.println("SUCCESS");
                    break;
                }
            } catch (IOException e) {
                System.out.println("FAIL");
                e.printStackTrace();
                break;
            }
        }
    }

} // Class