/*
 * Author: Stephanos B
 * Date: 15/12/2025
 */

import java.io.File;
import java.nio.file.Path;

import Objects.Game;
import Objects.Mod;
import Objects.ModFile;
import Objects.Mod.ModSource;
import Utils.ModIO;

/**
 * Test class for Mod JSON read/write functionality.
 * 
 * @author Stephanos B
 */
public class JsonTest {

    public static final Game game = new Game("game_1", "Test Game", "test/game_root", "test/mod_storage/game_1");

    private static Mod mod = new Mod(game.getId(), ModSource.NEXUS, "Test Mod");

    public static void main(String[] args) throws Exception {

        mod.setDescription("A mod for testing purposes.");
        mod.addFile(new ModFile("data/test.txt", "abc123"));
        mod.addFile(new ModFile("data/test_img.png", "jwjhchf3sisjsw12fde3fcsfbw2"));
        //System.out.println(mod.toString());

        // basicTest();
        //sampleModTest();

        Path storagePath = Path.of(game.getModsPath(), mod.getId());
        System.out.println(storagePath);

        // ModManager modManager = new ModManager(game);
        // List<ModFile> files = FileUtil.getDirectoryFiles("temp/sample_mod_2");
        // System.out.println("\nDone.\n\n" + files.toString());
    } // psvm()

    /**
     * Basic test to write and read a Mod JSON file.
     * 
     * @throws Exception
     */
    private static void basicTest() throws Exception {

        // Write to JSON
        ModIO.writeMod(mod, new File("sample_mod.json"));
        System.out.println("Written!");

        // Read it back
        Mod readMod = ModIO.readMod(new File("test.json"));
        System.out.println("Read: " + readMod.getName());
    } // basicTest()

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
        modManager.createModJson(sampleMod, Path.of(game.getModsPath()));

    } // sampleModTest()

} // Class
