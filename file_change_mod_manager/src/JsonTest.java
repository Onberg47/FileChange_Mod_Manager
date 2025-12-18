/*
 * Author: Stephanos B
 * Date: 15/12/2025
 */

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import Objects.Game;
import Objects.Mod;
import Objects.ModFile;
import Objects.Mod.ModSource;
import Utils.FileUtil;
import Utils.ModIO;

/**
 * Test class for Mod JSON read/write functionality.
 * 
 * @author Stephanos B
 */
public class JsonTest {

    public static final Game game = new Game("game_1", "Test Game", "test/game_root", "test/mod_storage/game_1");

    public static void main(String[] args) throws Exception {

        Mod mod = new Mod(game.getId(), ModSource.NEXUS, "Test Mod");
        mod.setVersion("2.0");
        mod.setLoadOrder(3);
        mod.setDownloadSource("steam workshop");
        mod.setDescription("A mod for testing purposes.");
        mod.addFile(new ModFile("data/test.txt", "abc123"));
        mod.addFile(new ModFile("data/test_img.png", "jwjhchf3sisjsw12fde3fcsfbw2"));
        // System.out.println(mod.toString());

        // ModIO.writeMod(mod, new File("dummy.json"));
        // System.out.println("\nWritten:\n" + mod.toString());
        // Read it back
        // Mod readMod = ModIO.readMod(new File("dummy.json"));
        // System.out.println("\nRead: " + readMod.toString());

        // sampleModTest();
        // moveFromTempTest();

        ModManager modManager = new ModManager(game);
        // modManager.compileNewMod("sample");
        modManager.modTrash(ModIO.readMod(new File("temp/mod.json")));

        /*
         * Path temp = Path.of("temp/sample");
         * Path src = Path.of("temp/sample/data/example_file_6.txt");
         * System.out.println(temp.relativize(src));
         */

        // Mod readMod = ModIO.readMod(new
        // File("test/mod_storage/game_1/nexus-sample-51449/mod.json"));
        // System.out.println("Read: " + readMod.getName());

        /*
         * Path storagePath = Path.of(game.getModsPath(), mod.getId());
         * System.out.println(storagePath);
         * 
         * ModManager modManager = new ModManager(game);
         * List<ModFile> files = FileUtil.getDirectoryFiles("temp/sample_mod_2");
         * System.out.println("\nDone.\n\n" + files.toString());
         */
    } // psvm()

    /**
     * Creates a sample Game and Mod to perfrom a full Mod JSON creation test.
     */
    private static void sampleModTest() {

        // Create a sample Mod with multiple files
        Mod sampleMod = new Mod(game.getId(), ModSource.OTHER, "Sample Mod");
        sampleMod.setDescription("This is a sample mod for testing the Mod JSON creation.");
        sampleMod.addFile(new ModFile("example_file_1.txt", "checksum1"));
        sampleMod.addFile(new ModFile("data/example_file_2.txt", "checksum2"));
        sampleMod.addFile(new ModFile("data/example_file_3.txt", "checksum3"));

        ModManager modManager = new ModManager(game);
        Path storagePath = Path.of(game.getModsPath(), sampleMod.getId());

        System.out.println(modManager.createModJson(sampleMod, storagePath.resolve("mod.json")));

        // modManager.createModJson(sampleMod, Path.of(game.getModsPath()));

    } // sampleModTest()

    /**
     * Tests moving a Mod's data out from the Temp directory to its correct storage
     * location.
     * Will delete previous data if the mod's directory is already present.
     */
    private static void moveFromTempTest() {

        Mod mod = new Mod(game.getId(), ModSource.NEXUS, "Test Mod");
        mod.setVersion("2.0");
        mod.setDownloadSource("steam workshop");
        mod.setDescription("A mod for testing purposes.");
        mod.addFile(new ModFile("data/test.txt", "abc123"));
        mod.addFile(new ModFile("data/test_img.png", "jwjhchf3sisjsw12fde3fcsfbw2"));

        Path tempDir = Path.of("temp/", "mod_a");
        Path storagePath = Path.of(game.getModsPath(), mod.getId());

        try {
            if (Files.exists(storagePath)) {
                System.out.println("File found!");
                FileUtil.deleteDirectory(storagePath);
            }
            Files.move(tempDir, storagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

    } // moveFromTempTest()

} // Class
