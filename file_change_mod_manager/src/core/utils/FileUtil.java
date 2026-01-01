/*
 * Author Stephanos B
 * Date: 16/12/2025
 */
package core.utils;

import java.io.IOException;
import java.io.InvalidObjectException;
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

import core.config.AppConfig;
import core.interfaces.JsonSerializable;
import core.io.JsonIO;
import core.objects.Game;
import core.objects.GameState;
import core.objects.Mod;
import core.objects.ModFile;

/**
 * Provides utility methods for scanning files and directories and read-only
 * file operations.
 * 
 * @author Stephanos B
 */
public class FileUtil {
    private static AppConfig config = AppConfig.getInstance();

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
    private static List<ModFile> getDirectoryModFiles(Path dirPath, Path relative, String prefix, int depth,
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
                                String.format("%s🗒  Found File: %s", " ".repeat(depth * 3),
                                        prefix + path.getFileName()));
                        return Stream.of(modFile);
                    } else if (Files.isDirectory(path)) {
                        // Create ModFile for directory (if needed) or recurse
                        String tmpPrefix = prefix + path.getFileName().toString() + "/";
                        System.out
                                .println(String.format("%s🗂  Found directory: %s", " ".repeat(depth * 3), tmpPrefix));

                        if (depth >= maxDepth) {
                            System.err.println("❗ Maximum depth reached, stopping recursion.");
                            return Stream.empty();
                        }

                        // Recursively process subdirectory
                        return getDirectoryModFiles(path, relative, tmpPrefix, depth + 1,
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

    /// /// /// File Print Utils /// /// ///

    /**
     * For CLI and debug use. Creates a String to print out what Mods are installed
     * according to the GameState.
     * 
     * @param game
     * @return String to display
     * @throws Exception
     */
    public static String printGameState(Game game) throws Exception {
        Path managerPath = config.getManagerDir();
        GameState gState;
        Path GsPath = Path.of(game.getInstallPath(), managerPath.toString(), GameState.FILE_NAME);

        if (!Files.exists(GsPath))
            System.err.println("❗ No mods installed, could not find " + GameState.FILE_NAME);
        try {
            gState = (GameState) JsonIO.read(GsPath.toFile(), JsonSerializable.ObjectTypes.GAME_STATE);
            return gState.toString();

        } catch (Exception e) {
            throw new Exception("❌ Failed to add Mod to GameState", e);
        }
    } // readGameState()

    /**
     * For CLI use. Creates a String to print out what Mods actually exsist in Mod
     * Storage based on exsiting ModManifests.
     * 
     * @param game
     * @param all  whether to display all mods or only mods that are not deployed
     *             (when false)
     * @return String to display.
     * @throws Exception
     */
    public static String printStoredMods(Game game, Boolean all) throws Exception {
        Path manifestPath = config.getManifestDir();
        StringBuilder sb = new StringBuilder();

        Path GsPath = Path.of(game.getInstallPath(), config.getManagerDir().toString(), GameState.FILE_NAME);
        GameState gState;
        if (!Files.exists(GsPath))
            System.err.println("❗ No mods installed, could not find " + GameState.FILE_NAME);
        try {
            gState = (GameState) JsonIO.read(GsPath.toFile(), JsonSerializable.ObjectTypes.GAME_STATE);

        } catch (Exception e) {
            gState = new GameState();
        }

        if (all) {
            sb.append("📦 All available Mods:\n\t Game: " + game.getName());
        } else
            sb.append("📦 Non-deployed only Mods:\n\t Game: " + game.getName());

        Path storeDir = Path.of(game.getModsPath());
        try (Stream<Path> paths = Files.list(storeDir)) {
            // List<ModFile> list = new java.util.ArrayList<ModFile>();

            for (Path path : (Iterable<Path>) paths::iterator) {
                // Process each file

                if (Files.isDirectory(path)) {
                    try {
                        Mod mod = (Mod) JsonIO.read(
                                storeDir.resolve(path.getFileName().toString(), manifestPath.toString(),
                                        path.getFileName() + ".json").toFile(),
                                JsonSerializable.ObjectTypes.MOD_MANIFEST);

                        if (gState.containsMod(mod.getId())) {
                            if (all)
                                sb.append("\n\t\t⚫ " + mod.printLite());
                        } else {
                            sb.append("\n\t\t⚪ " + mod.printLite());
                        }

                    } catch (InvalidObjectException e) {
                        // Just skips silently. Other errors are caught outside to stop process.
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("❌ Failed to read Storage mods: ", e);
        }

        return sb.toString();
    } // printStoredMods()

    /**
     * For CLI and debug use. Creates a String to print from reading what valid
     * Game.json files are exsist.
     * 
     * @return
     * @throws Exception
     */
    public static String printGames() throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append("📦 Known Games:");

        Path gameDir = config.getGameDir();
        try (Stream<Path> paths = Files.list(gameDir)) {
            // List<ModFile> list = new java.util.ArrayList<ModFile>();

            for (Path path : (Iterable<Path>) paths::iterator) {
                // Process each file

                if (Files.isRegularFile(path)) {
                    try {
                        Game game = (Game) JsonIO.read(
                                path.toFile(),
                                JsonSerializable.ObjectTypes.GAME);

                        sb.append("\n\t\t⚪ " + game.getName() + " [id: " + game.getId() + "]");

                    } catch (InvalidObjectException e) {
                        // Just skips silently. Other errors are caught outside to stop process.
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("Failed to read Games: ", e);
        }
        return sb.toString();
    } // printGames()

    /// /// /// Directory Utils /// /// ///

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
        if (working == null) {
            return -1;
        }
        Path resolvedPath = relative.resolve(working);

        // Validate that we're working with a valid path
        if (!Files.exists(resolvedPath)) {
            System.err.println("❌ Invalid path! [relative] -> [working] : " + relative + " -> " + working);
            return -1;
        }

        int count = 0;
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

        System.out.println("✔ Cleaned [" + count + "] empty directories in: [relative] -> [working] : " + relative
                + " -> " + working);
        return count;
    } // cleanDirectories()

} // Class
