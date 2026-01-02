/*
 * Author: Stephanos B
 * Date: 15/12/2025
 */

package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.zip.DataFormatException;

import core.config.defaultConfig;
import core.interfaces.JsonSerializable;
import core.io.JsonIO;
import core.io.ModIO;
import core.managers.GameManager;
import core.managers.ModManager;
import core.objects.FileLineage;
import core.objects.FileVersion;
import core.objects.Game;
import core.objects.Mod;
import core.objects.ModFile;
import core.objects.ModManifest;
import core.utils.FileUtil;

/**
 * Test class for Mod JSON read/write functionality.
 * 
 * @author Stephanos B
 */
@SuppressWarnings("unused")
public class JsonTest {

    public static final Game game = new Game("game_1", "0.0.0", "Test Game", "tst_fs/game_root",
            "mod_manager/mod_storage/game_1");

    public static void main(String[] args) throws Exception {

        ModManifest mod = new ModManifest(game.getId(), "Local", "Test Mod");
        mod.setVersion("2.0");
        mod.setLoadOrder(3);
        mod.setDownloadSource("steam workshop");
        mod.setDescription("A mod for testing purposes.");
        mod.addFile(new ModFile("data/test.txt", "abc123", 111));
        mod.addFile(new ModFile("data/test_img.png", "jwjhchf3sisjsw12fde3fcsfbw2", 23214));
        // System.out.println(mod.toString());

        // sampleModTest();
        // moveFromTempTest();

        ModManager modManager = new ModManager(game);
        GameManager gm = new GameManager();

        /// Test something...

    } // psvm()

    private static void TestGame() throws Exception {
        GameManager gm = new GameManager();
        HashMap<String, String> tMap = new HashMap<>();
        tMap.put("id", "example_id");
        tMap.put("name", "Example Game 1");
        tMap.put("releaseVersion", "1.27.4a");
        tMap.put("installPath", "/home/hdd700/Programming/java/JarProjects/FileChange_Mod_Manager/tst_fs/game_root/");
        tMap.put("modsPath",
                "/home/hdd700/Programming/java/JarProjects/FileChange_Mod_Manager/mod_manager/mod_storage/game_1/");

        // gm.addGame(tMap);
        System.out.println(FileUtil.printGames());
        // gm.removeGame("game_id");
        // System.out.println(GameManager.getGameById("game_id").toString());u
    }

    /**
     * Creates a sample Game and Mod to perfrom a full Mod JSON creation test.
     */
    private static void sampleModTest() {

        // Create a sample Mod with multiple files
        ModManifest sampleMod = new ModManifest(game.getId(), "local", "Sample Mod");
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
    private static void moveFromTempTest() {

        ModManifest mod = new ModManifest(game.getId(), "local", "Test Mod");
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

    private static void checkStackRemoveAll() {
        Stack<Integer> stack = new Stack<>();
        stack.add(0);
        stack.add(1);
        stack.add(0);
        stack.add(2);
        stack.add(0);
        stack.add(0);
        stack.add(3);

        System.out.println("Original:\n\tTop element: " + stack.peek() + "\n\tAll: " + stack.toString());

        Stack<Integer> tmpStack = new Stack<>();
        Integer tmpFV;

        while (!stack.empty()) {
            tmpFV = stack.pop();
            System.out.print("tmpFV: " + tmpFV);

            if (tmpFV != 0) {
                tmpStack.addFirst(tmpFV);
            }
            System.out.println(" --> ✔");
        }
        stack = tmpStack;

        System.out.println("RemovedAll 0's:\n\tTop element: " + stack.peek() + "\n\tAll: " + stack.toString());
    }

    private static void makeDefaultConfig() {
        Path path = Path.of("mod_manager/config.json");
        // JsonIO.readHashMap(path.toFile());

        HashMap<String, String> hMap = new HashMap<>();
        hMap.put("BACKUP_DIR", defaultConfig.getBackupDir().toString());
        hMap.put("LINEAGE_DIR", defaultConfig.getLineageDir().toString());
        hMap.put("MANAGER_DIR", defaultConfig.getManagerDir().toString());
        hMap.put("MANIFEST_DIR", defaultConfig.getManifestDir().toString());
        hMap.put("TEMP_DIR", defaultConfig.getTempDir().toString());
        hMap.put("TRASH_DIR", defaultConfig.getTrashDir().toString());
        try {
            JsonIO.writeHasMap(path.toFile(), hMap);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

} // Class
