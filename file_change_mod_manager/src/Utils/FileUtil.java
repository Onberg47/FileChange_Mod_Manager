/*
 * Author Stephanos B
 * Date: 16/12/2025
 */

package Utils;

import java.io.File;
import java.io.IOException;
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
     * @param dirPath The root path to scan from.
     * 
     * @return A list of ModFile objects representing the files found.
     */
    public static List<ModFile> getDirectoryFiles(String dirPath) {
        return getDirectoryFiles(dirPath, "", 0, 10);
    } // getDirectoryFiles()

    /**
     * Recursively scans a directory and prints all files and directories with
     * indentation based on depth.
     * 
     * @param dirPath  The root path to scan from.
     * @param maxDepth The maximum depth to recurse into directories. (default 10)
     * 
     * @return A list of ModFile objects representing the files found.
     */
    public static List<ModFile> getDirectoryFiles(String dirPath, int maxDepth) {
        return getDirectoryFiles(dirPath, "", 0, maxDepth);
    } // getDirectoryFiles()

    /**
     * Internal recursive method to scan directories.
     * 
     * @param dirPath  The path of the directory to scan.
     * @param prefix   The prefix path for indentation.
     * @param depth    The current depth of recursion counting and depth-based
     *                 indentation.
     * @param maxDepth The maximum depth to recurse into directories.
     * 
     * @return A list of ModFile objects representing the files found.
     */
    private static List<ModFile> getDirectoryFiles(String dirPath, String prefix, int depth, int maxDepth) {
        Path directoryPath = Paths.get(dirPath);

        try (Stream<Path> paths = Files.list(directoryPath)) {
            List<ModFile> list = new java.util.ArrayList<ModFile>();

            for (Path path : (Iterable<Path>) paths::iterator) {
                // Process each file
                if (Files.isRegularFile(path)) {

                    list.add(new ModFile(path.toString(), HashUtil.computeFileHash(path)));
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
                        System.err.println("Maximum depth reached, stopping recursion.");
                        // TODO remove debug with better logging
                        break;
                    }

                    list.addAll(getDirectoryFiles(path.toString(), tmpPrefix, depth + 1, maxDepth));
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
     * 
     * @param rootPath
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

} // Class
