/*
 * Author: Stephanos B
 * Date: 15/12/2025
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.zip.DataFormatException;

import interfaces.JsonSerializable;
import io.JsonIO;
import io.ModIO;
import managers.ModManager;
import objects.FileVersion;
import objects.Game;
import objects.Mod;
import objects.ModFile;
import objects.ModManifest;
import objects.Mod.ModSource;
import utils.FileUtil;

/**
 * Test class for Mod JSON read/write functionality.
 * 
 * @author Stephanos B
 */
@SuppressWarnings("unused")
public class JsonTest {

    public static final Game game = new Game("game_1", "Test Game", "tst_fs/game_root", "mod_manager/mod_storage/game_1");

    public static void main(String[] args) throws Exception {

        ModManifest mod = new ModManifest(game.getId(), ModSource.NEXUS, "Test Mod");
        mod.setVersion("2.0");
        mod.setLoadOrder(3);
        mod.setDownloadSource("steam workshop");
        mod.setDescription("A mod for testing purposes.");
        mod.addFile(new ModFile("data/test.txt", "abc123", 111));
        mod.addFile(new ModFile("data/test_img.png", "jwjhchf3sisjsw12fde3fcsfbw2", 23214));
        // System.out.println(mod.toString());

        // ModIO.writeMod(mod, new File("dummy.json"));
        // System.out.println("\nWritten:\n" + mod.toString());
        // Read it back
        // Mod readMod = ModIO.readMod(new File("dummy.json"));
        // System.out.println("\nRead: " + readMod.toString());

        // sampleModTest();
        // moveFromTempTest();

        ModManager modManager = new ModManager(game);
        //modManager.modCompileNew("sample");
        modManager.deployMod("other-basemo-10808", true); // LoadOrder 0
        //modManager.deployMod("other-mega_s-51449", true); // LoadOrder 3

        //modManager.modTrash("other-basemo-10808");
        // modManager.modTrash("other-mega_s-51449");
        
        /*
         * Path temp = Path.of("temp/sample");
         * Path src = Path.of("temp/sample/data/example_file_6.txt");
         * System.out.println(temp.relativize(src));
         */

        // ModManifest readMod = (ModManifest) JsonIO.read(new
        // File("mod_manager/mod_storage/game_1/other-base_m-8888/.mod_manifests/other-base_m-8888.json"),
        // null);
        // System.out.println("Read contents:\n" + readMod.printContents());

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
    @SuppressWarnings("unused")
    private static void sampleModTest() {

        // Create a sample Mod with multiple files
        ModManifest sampleMod = new ModManifest(game.getId(), ModSource.OTHER, "Sample Mod");
        sampleMod.setDescription("This is a sample mod for testing the Mod JSON creation.");
        sampleMod.addFile(new ModFile("example_file_1.txt", "checksum1", 111));
        sampleMod.addFile(new ModFile("data/example_file_2.txt", "checksum2", 222));
        sampleMod.addFile(new ModFile("data/example_file_3.txt", "checksum3", 333));

        @SuppressWarnings("unused")
        ModManager modManager = new ModManager(game);
        Path storagePath = Path.of(game.getModsPath(), sampleMod.getId());

        try {
            // Write to JSON
            JsonIO.write(sampleMod, new File(storagePath.resolve("mod.json").toString()));
            System.out.println("✔ Written!"); // Debug
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // modManager.createModJson(sampleMod, Path.of(game.getModsPath()));

    } // sampleModTest()

    /**
     * Tests moving a Mod's data out from the Temp directory to its correct storage
     * location.
     * Will delete previous data if the mod's directory is already present.
     */
    @SuppressWarnings("unused")
    private static void moveFromTempTest() {

        ModManifest mod = new ModManifest(game.getId(), ModSource.NEXUS, "Test Mod");
        mod.setVersion("2.0");
        mod.setDownloadSource("steam workshop");
        mod.setDescription("A mod for testing purposes.");
        mod.addFile(new ModFile("data/test.txt", "abc123", 111));
        mod.addFile(new ModFile("data/test_img.png", "jwjhchf3sisjsw12fde3fcsfbw2", 23214));

        Path tempDir = Path.of("mod_manager/.temp/", "mod_a");
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

    private static void checkExtensionAdd() {
        File file = Path.of("path/to/file/my_file").toFile();
        if (file.getName().endsWith(".json") == false) {
            // appends the `.json` extension if needed
            file = file.toPath().getParent().resolve(
                    (file.toPath().getFileName() + ".json")).toFile();
        }
        System.out.println("file: " + file.toString());
    }

} // Class
