import java.io.File;

import Objects.Game;
import Objects.Mod;
import Objects.ModFile;

public class JsonTest {
    public static void main(String[] args) throws Exception {
        // basicTest();
        sampleModTest();
    }

    /**
     * Basic test to write and read a Mod JSON file.
     * 
     * @throws Exception
     */
    private static void basicTest() throws Exception {
        // Create a test Mod with files
        Mod mod = new Mod("unknown-001", "Test Mod", "1.0");
        mod.addFile(new ModFile("data/test.txt", "abc123"));

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
        Game game = new Game("testG", "Test Game", "test/game_root", "test/mod_storage/test01");

        // Create a sample Mod with multiple files
        Mod sampleMod = new Mod("testG-01", "Sample Mod", "Simply for testing.");
        sampleMod.addFile(new ModFile("example_file_1.txt", "checksum1"));
        sampleMod.addFile(new ModFile("data/example_file_2.txt", "checksum2"));
        sampleMod.addFile(new ModFile("data/example_file_3.txt", "checksum3"));

        ModManager modManager = new ModManager(game);
        modManager.createModJson(sampleMod);

    } // sampleModTest()

}
// Class
