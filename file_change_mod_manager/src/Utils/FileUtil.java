/*
 * Author Stephanos B
 * Date: 16/12/2025
 */

package Utils;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Stream;

import Objects.ModFile;

/**
 * Provides utility methods for scanning files and directories.
 * 
 * @author Stephanos B
 */
public class FileUtil {

    /**
     * Recursively scans a directory and prints all files and directories with
     * indentation based on depth.
     * 
     * MAXIMUM DEPTH: 10 by default to prevent infinite recursion!
     * 
     * @param dirPath  The root path to scan from.
     * @param relative Relative root to be removed from the final paths.
     *                 (relativize)
     * 
     * @return A list of ModFile objects representing the files found.
     */
    public static List<ModFile> getDirectoryFiles(String dirPath, String relative) {
        return getDirectoryFiles(dirPath, relative, "", 0, 10);
    } // getDirectoryFiles()

    /**
     * Recursively scans a directory and prints all files and directories with
     * indentation based on depth.
     * 
     * @param dirPath  The root path to scan from.
     * @param relative Relative root to be removed from the final paths.
     *                 (relativize)
     * @param maxDepth The maximum depth to recurse into directories. (default 10)
     * 
     * @return A list of ModFile objects representing the files found.
     */
    public static List<ModFile> getDirectoryFiles(String dirPath, String relative, int maxDepth) {
        return getDirectoryFiles(dirPath, relative, "", 0, maxDepth);
    } // getDirectoryFiles()

    /**
     * Internal recursive method to scan directories.
     * 
     * @param dirPath  The path of the directory to scan.
     * @param relative Relative root to be removed from the final paths.
     *                 (relativize)
     * @param prefix   The prefix path for indentation.
     * @param depth    The current depth of recursion counting and depth-based
     *                 indentation.
     * @param maxDepth The maximum depth to recurse into directories.
     * 
     * @return A list of ModFile objects representing the files found.
     */
    private static List<ModFile> getDirectoryFiles(String dirPath, String relative, String prefix, int depth,
            int maxDepth) {
        Path directoryPath = Paths.get(dirPath);

        try (Stream<Path> paths = Files.list(directoryPath)) {
            List<ModFile> list = new java.util.ArrayList<ModFile>();

            for (Path path : (Iterable<Path>) paths::iterator) {
                // Process each file

                if (Files.isRegularFile(path)) {
                    list.add(new ModFile(Path.of(relative).relativize(path).toString(), HashUtil.computeFileHash(path),
                            Files.size(path)));
                    System.out.println(
                            String.format("%sFound File: %s", " ".repeat(depth * 3), prefix + path.getFileName()));

                    // Process each directory
                } else if (Files.isDirectory(path)) {
                    // Using a temporary prefix to avoid modifying the original prefix for sibling
                    // directories
                    String tmpPrefix = prefix + path.getFileName().toString() + "/";
                    System.out.println(String.format("%sFound directory: %s", " ".repeat(depth * 3), tmpPrefix));

                    if (depth >= maxDepth) {
                        // Prevents infinite recursion past reasonable depth.
                        System.err.println("❗ Maximum depth reached, stopping recursion.");
                        // TODO remove debug with better logging
                        break;
                    }

                    list.addAll(getDirectoryFiles(path.toString(), relative, tmpPrefix, depth + 1, maxDepth));
                    // Recursive call
                }
            } // for
            return list;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // Hashing exception
            e.printStackTrace();
        }
        return null;
    } // private getDirectoryFiles()

    /**
     * Uses {@code walkFileTree} to delete a populated directory.
     * 
     * @param rootPath Directory to delete. Acts as root as all child paths are
     *                 deleted.
     * @reurn Silently returns if directory does not exsist.
     * @throws IOException
     */
    public static void deleteDirectory(Path rootPath) throws IOException {
        if (!Files.exists(rootPath))
            return;

        Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file); // Delete individual files first
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException {
                if (exc != null)
                    throw exc; // Propagate any errors from child processing
                Files.delete(dir); // Delete now-empty directory
                return FileVisitResult.CONTINUE;
            }
        });
    } // deleteDirectory()

    public static void copyDirectoryContents(Path rootDir, Path targetDir) throws IOException {
        if (!Files.exists(rootDir) || !Files.exists(targetDir))
            return;

        Files.list(rootDir)
                .filter(Files::isDirectory)
                //.filter(path -> path.toFile().getName().startsWith("pak_") && path.toFile().getName().endsWith("_"))
                .forEach(sourceDir -> {
                    try (Stream<Path> files = Files.walk(sourceDir).filter(Files::isRegularFile)) {
                        files.forEach(file -> {
                            try {
                                // Get relative path from source directory
                                String relPath = file.toAbsolutePath()
                                        .toFile().getCanonicalPath()
                                        .substring(sourceDir.toFile().getCanonicalPath().length() + 1);

                                // Construct target path (output/relative-path)
                                Path target = targetDir.resolve(relPath);

                                // Copy the file (creates intermediate dirs if needed)
                                Files.copy(file, target);
                            } catch (IOException e) {
                                System.err.println("Error copying file: " + e.getMessage() + "\n\tContinuing...");
                            }
                        });
                    } catch (IOException e) {
                        System.err.println("Fatal Error copying files! " + e.getStackTrace());
                        return;
                    }
                });
    } // copyDirectoryContents()

    /**
     * Attempts to delete all directories listed in the given path if they are
     * empty, tracking how many are removed.
     * This can handle the Path pointing to a non-directory, simply ignoring it (if
     * the file does not exists)
     * 
     * @param relative Path that leads to the working Path but is NOT checked for
     *                 cleaning.
     * 
     * @param working  Path to clean through. (Will not branch off this path, simply
     *                 walks back)
     * @return Int that counts how many empty directories were removed. -1 if path
     *         is invalid.
     */
    public static int cleanDirectories(Path relative, Path working) {
        // The file might not exsist by the path to it might.
        if (!Files.exists(relative.resolve(working)) && working.getParent() == null) {
            System.err.println("❌ Invalid path! [realtive] -> [working] : " + relative + " -> " + working);
            return -1;
        }

        int i = 0; // counts how many directories are removed.
        boolean last = false; // is last itteration?
        do {
            if (working.getParent() == null) {
                last = true;
            }

            // System.out.println("Trying to delete: " + relative.resolve(working)); // TODO
            try {
                Files.deleteIfExists(relative.resolve(working));
                i++;
            } catch (DirectoryNotEmptyException e) {
                // If the directory is no empty, returns because all subsequent parents are
                // thereby not empty either.
                System.err.println("❌ Directory " + relative + working + " is not empty.");
                return i;
            } catch (IOException e) {
                // Other error
                e.printStackTrace();
                return -1;
            }

            working = working.getParent();
            // System.out.println(" Now at: " + working); // TODO

        } while (!last);

        return i;
    } // cleanDirectories()

} // Class
