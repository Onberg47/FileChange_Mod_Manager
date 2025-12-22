/*
 * Author Stephanos B
 * Date: 16/12/2025
 */

package utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import objects.ModFile;

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
    public static List<ModFile> getDirectoryModFiles(Path dirPath, Path relative) {
        return getDirectoryModFiles(dirPath, relative, "", 0, 10);
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
    public static List<ModFile> getDirectoryModFiles(Path dirPath, Path relative, int maxDepth) {
        return getDirectoryModFiles(dirPath, relative, "", 0, maxDepth);
    } // getDirectoryFiles()

    /**
     * Internal recursive method to scan directories.
     * My original imperative approach.
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
    private static List<ModFile> getDirectoryModFiles(Path dirPath, Path relative, String prefix, int depth,
            int maxDepth) {
        try (Stream<Path> paths = Files.list(dirPath)) {
            List<ModFile> list = new java.util.ArrayList<ModFile>();

            for (Path path : (Iterable<Path>) paths::iterator) {
                // Process each file

                if (Files.isRegularFile(path)) {
                    list.add(new ModFile(
                            relative.relativize(path).toString(),
                            HashUtil.computeFileHash(path),
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

                    list.addAll(getDirectoryModFiles(path, relative, tmpPrefix, depth + 1, maxDepth));
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
        return new ArrayList<>(); // only reached after a catch
    } // private getDirectoryModFiles()

    /**
     * Internal recursive method to scan directories.
     * A more advanced version that uses lambda and Steams.
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
    @SuppressWarnings("unused")
    private static List<ModFile> getDirectoryModFilesStream(Path dirPath, Path relative, String prefix, int depth,
            int maxDepth) {
        try (Stream<Path> paths = Files.list(dirPath)) {
            return paths.flatMap(path -> {
                try {
                    if (Files.isRegularFile(path)) {
                        // Create ModFile for regular file
                        ModFile modFile = new ModFile(
                                relative.relativize(path).toString(),
                                HashUtil.computeFileHash(path),
                                Files.size(path));
                        System.out.println(
                                String.format("%sFound File: %s", " ".repeat(depth * 3), prefix + path.getFileName()));
                        return Stream.of(modFile);
                    } else if (Files.isDirectory(path)) {
                        // Create ModFile for directory (if needed) or recurse
                        String tmpPrefix = prefix + path.getFileName().toString() + "/";
                        System.out.println(String.format("%sFound directory: %s", " ".repeat(depth * 3), tmpPrefix));

                        if (depth >= maxDepth) {
                            System.err.println("❗ Maximum depth reached, stopping recursion.");
                            return Stream.empty();
                        }

                        // Recursively process subdirectory
                        return getDirectoryModFilesStream(path, relative, tmpPrefix, depth + 1,
                                maxDepth).stream();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return Stream.empty();
                }
                return Stream.empty();
            }).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    } // getDirectoryModFilesStream()

    /// ///

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

    /**
     * Copies all contents of a rootDirectory into another directory.
     * 
     * @param rootDir   Directory who's contents are to be copied.
     * @param targetDir Directory contents are copied into.
     * @throws IOException
     * 
     * @author Qwen3 Coder 30B
     */
    public static void copyDirectoryContents(Path rootDir, Path targetDir, StandardCopyOption copyOption)
            throws IOException {
        // Create target directory if it doesn't exist
        Files.createDirectories(targetDir);

        // Walk through all files and directories in rootDir
        try (Stream<Path> paths = Files.walk(rootDir)) {
            paths.forEach(sourcePath -> {
                try {
                    // Skip the root directory itself
                    if (sourcePath.equals(rootDir)) {
                        return;
                    }

                    // Calculate the relative path from rootDir
                    Path relativePath = rootDir.relativize(sourcePath);

                    // Create the corresponding target path
                    Path targetPath = targetDir.resolve(relativePath);

                    // If it's a directory, create it
                    if (Files.isDirectory(sourcePath)) {
                        Files.createDirectories(targetPath);
                    } else {
                        // If it's a file, copy it
                        Files.copy(sourcePath, targetPath, copyOption);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
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
        Path resolvedPath = relative.resolve(working);

        // Validate that we're working with a valid path
        if (!Files.exists(resolvedPath)) {
            System.err.println("❌ Invalid path! [relative] -> [working] : " + relative + " -> " + working);
            return -1;
        }

        int count = 0;

        // /a/b/c
        // Walk up the directory tree from working path
        while (working != null) {
            Path currentPath = relative.resolve(working);

            try {
                // Check if it's a directory and empty
                if (Files.isDirectory(currentPath)) {
                    // Check if directory is empty before attempting deletion
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentPath)) {
                        if (!stream.iterator().hasNext()) {
                            // Directory is empty, delete it
                            Files.delete(currentPath);
                            count++;
                        } else {
                            // Directory not empty, stop here
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("❌ Error processing directory " + currentPath + ": " + e.getMessage());
                return -1;
            }

            // Move to parent directory
            working = working.getParent();
        } // while()

        return count;
    } // cleanDirectories()

} // Class
