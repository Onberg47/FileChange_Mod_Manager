/*
 * Author: Stephanos B
 * Date: 15/12/2025
 */
package tests;

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
import core.interfaces.MapSerializable;
import core.io.JsonIO;
import core.managers.GameManager;
import core.managers.ModManager;
import core.objects.FileLineage;
import core.objects.FileVersion;
import core.objects.Game;
import core.objects.GameState;
import core.objects.Mod;
import core.objects.ModFile;
import core.objects.ModManifest;
import core.objects.ModMetadata;
import core.utils.FileUtil;
import core.utils.HashUtil;

/**
 * Test class for Mod JSON read/write functionality.
 * 
 * @author Stephanos B
 */
@SuppressWarnings("unused")
public class JsonTest {

    public static final Game game = new Game("game_1",
            "0.0.0",
            "Test Game",
            Path.of("tst_fs/game_root"),
            Path.of("mod_manager/mod_storage/game_1"));

    public static void main(String[] args) throws Exception {

        ModManifest manifest = new ModManifest(game.getId());
        manifest.setName("Test Mod");
        manifest.setVersion("2.0");
        manifest.setLoadOrder(3);
        manifest.setDownloadSource("steam workshop");
        manifest.setDescription("A mod for testing purposes.");
        manifest.addFile(new ModFile(Path.of("data/test.txt"), "abc123", 111));
        manifest.addFile(new ModFile(Path.of("data/test_img.png"), "jwjhchf3sisjsw12fde3fcsfbw2", 23214));

        Mod mod = new Mod();
        mod.setGameId(game.getId());
        mod.setName("Test Moddie");
        mod.setDescription("A tiny Mod instace for testing override and updates.");
        mod.setLoadOrder(11);
        mod.setVersion("X.0.12");
        // System.out.println(mod.toString());

        // sampleModTest();
        // moveFromTempTest();

        ModManager modManager = new ModManager(game);
        // GameManager gm = new GameManager();

        /// Test something...

        Path path = Path.of("file_change_mod_manager/src/tests/files/manifest.json");
        ModManifest man = (ModManifest) JsonIO.read(path.toFile(), null);
        System.out.println("Manifest: " + man.toString());

        man.setFromMap(mod.toMap());
        System.out.println("\nModded Manifest: " + man.toString());

        /// ///

        Path manPath = Path.of("file_change_mod_manager/src/tests/files/manifest.json");
        Path flPath = Path.of("file_change_mod_manager/src/tests/files/fileLineage.json");
        Path gsPath = Path.of("file_change_mod_manager/src/tests/files/game_state.json");

        FileLineage fileLineage = (FileLineage) JsonIO.read(flPath.toFile(), null);
        GameState gameState = (GameState) JsonIO.read(gsPath.toFile(), null);

        System.out.println("\nTest Read: " + fileLineage.toString() + "\nPeek: " + fileLineage.peek().toString());
        System.out.println("\nTest Read: " + gameState.toString());
        System.out.println("InsertOrdered: "
                + fileLineage.insertOrderedVersion(new FileVersion("lineage_tst", null), gameState, 7));

        gameState.appendModOnly(manifest.getAsMod());
        System.out.println("\nTest Sorted: " + gameState.toString());
        // JsonIO.write(temp, path.getParent().resolve("tmp").toFile());

        List<Mod> modLs = modManager.getAllMods();
        for (Mod mod2 : modLs) {
            if (mod2.isEnabled())
                System.out.println("+ " + mod2.printLite());
            else
                System.out.println("- " + mod2.printLite());
        }

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
        ModManifest sampleMod = new ModManifest(game.getId());
        sampleMod.setName("Sample Mod");
        sampleMod.setDescription("This is a sample mod for testing the Mod JSON creation.");
        sampleMod.addFile(new ModFile(Path.of("example_file_1.txt"), "checksum1", 111));
        sampleMod.addFile(new ModFile(Path.of("data/example_file_2.txt"), "checksum2", 222));
        sampleMod.addFile(new ModFile(Path.of("data/example_file_3.txt"), "checksum3", 333));

        @SuppressWarnings("unused")
        ModManager modManager = new ModManager(game);
        Path storagePath = game.getStoreDirectory().resolve(sampleMod.getId());

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

        ModManifest mod = new ModManifest(game.getId());
        mod.setName("Test Mod");
        mod.setVersion("2.0");
        mod.setDownloadSource("steam workshop");
        mod.setDescription("A mod for testing purposes.");
        mod.addFile(new ModFile(Path.of("data/test.txt"), "abc123", 111));
        mod.addFile(new ModFile(Path.of("data/test_img.png"), "jwjhchf3sisjsw12fde3fcsfbw2", 23214));

        Path tempDir = Path.of("mod_manager/.temp/", "mod_a");
        Path storagePath = game.getStoreDirectory().resolve(mod.getId());

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
            JsonIO.writeHashMap(path.toFile(), hMap);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void lineageTest() {

        Stack<Integer> stack = new Stack<Integer>();
        stack.push(0);
        stack.push(2);
        stack.push(3);

        System.out.println("Stack: " + stack.toString());

        int loadOrder = -1;

        // Find insertion point
        int insertIndex = 0; // default to end
        for (int i = stack.size() - 1; i >= 0; i--) {
            System.out.println("i: " + i + " if c: " + loadOrder + " >= stack: " + stack.get(i).intValue());

            if (loadOrder >= stack.get(i).intValue()) {
                insertIndex = i + 1;
                break;
            }
        }
        stack.insertElementAt(loadOrder, insertIndex);
        System.out.println("Result: " + (stack.size() - 1 - insertIndex));

        System.out.println("New Stack: " + stack.toString());
    }

} // Class
