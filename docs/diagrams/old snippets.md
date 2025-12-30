```java
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
```