import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import core.io.JsonIO;
import core.managers.ModManager;
import core.objects.FileLineage;
import core.objects.FileVersion;
import core.objects.Game;
import core.objects.GameState;
import core.objects.Mod;
import core.objects.ModFile;
import core.objects.ModManifest;

@SuppressWarnings("unused")
public class JsonTest {

    public static final Game game = new Game("game_1",
            "0.0.0",
            "Test Game",
            Path.of("tst_fs/game_root"),
            Path.of("mod_manager/mod_storage/game_1"));

    public static void main(String[] args) throws Exception {

        Mod mod = new Mod();
        mod.setGameId(game.getId());
        mod.setName("Test Moddie");
        mod.setDescription("A tiny Mod instace for testing override and updates.");
        mod.setLoadOrder(11);
        mod.setVersion("X.0.12");

        ModManifest manifest = new ModManifest(game.getId());
        manifest.setName("Test Mod");
        manifest.setVersion("2.0");
        manifest.setLoadOrder(3);
        manifest.setDownloadSource("steam workshop");
        manifest.setDescription("A mod for testing purposes.");
        manifest.addFile(new ModFile(Path.of("data/test.txt"), "abc123", 111));
        manifest.addFile(new ModFile(Path.of("data/test_img.png"), "jwjhchf3sisjsw12fde3fcsfbw2", 23214));

        ModManager modManager = new ModManager(game);

        /// /// ///

        /// TODO Make this into a block-test
        Path manPath = Path.of("fcmm/test/files/manifest.json");
        Path flPath = Path.of("fcmm/test/files/fileLineage.json");
        Path gsPath = Path.of("fcmm/test/files/game_state.json");

        ModManifest man = (ModManifest) JsonIO.read(manPath.toFile(), null);
        System.out.println("Manifest: " + man.toString());

        man.setFromMap(mod.toMap());
        System.out.println("\nModded Manifest: " + man.toString());

        FileLineage fileLineage = (FileLineage) JsonIO.read(flPath.toFile(), null);
        GameState gameState = (GameState) JsonIO.read(gsPath.toFile(), null);

        System.out.println("\nTest Read: " + fileLineage.toString() + "\nPeek: " + fileLineage.peek().toString());
        System.out.println("\nTest Read: " + gameState.toString());
        System.out.println("InsertOrdered: "
                + fileLineage.insertOrderedVersion(new FileVersion("lineage_insert_test", "hash-value"), gameState, 2));

        gameState.appendModOnly(manifest.getAsMod());
        System.out.println("\nTest Sorted: " + gameState.toString());
        JsonIO.write(fileLineage, manPath.getParent().resolve("fileLineage_result").toFile());

        ///

        Set<String> tags = new HashSet<>();
        tags.add("test");
        tags.add("manifest");
        tags.add("fcmm");
        man.setTagSet(tags);
        System.out.println("\nUpdated manifest: " + man.toString());
        JsonIO.write(man, manPath.getParent().resolve("manifest_result").toFile());

        ///

        List<Mod> modLs = modManager.getAllMods();
        for (Mod mod2 : modLs) {
            if (mod2.isEnabled())
                System.out.println("+ " + mod2.printLite());
            else
                System.out.println("- " + mod2.printLite());
        }
    }
} // Class
