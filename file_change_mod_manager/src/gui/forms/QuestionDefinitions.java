/**
 * Author Stephanos B
 * Date 8/01/2026
 */
package gui.forms;

import java.util.Arrays;
import java.util.List;

import core.config.AppConfig;
import core.objects.Game;
import core.objects.ModMetadata;

/**
 * Pre-define the questions as FormQUestion instances. Add/modify form layouts
 * for FormPanel.java here.
 */
public class QuestionDefinitions {

        // === GAME QUESTIONS ===
        public static List<FormQuestion> getGameQuestions() {
                return Arrays.asList(
                                FormQuestion.builder(Game.Keys.id.toString(), "Game ID")
                                                .required()
                                                .defaultValue("game-name")
                                                .tooltip("Unique identifier only interacted with by the user in CLI (no spaces, e.g., 'ghost-recon')")
                                                .build(),

                                FormQuestion.builder(Game.Keys.name.toString(), "Game Name")
                                                .required()
                                                .tooltip("The display name of the game")
                                                .build(),

                                FormQuestion.builder(Game.Keys.releaseVersion.toString(), "Release Version")
                                                .required()
                                                .tooltip("Used for version tracking and compatability (use the build-ID from steam-properties)")
                                                .build(),

                                FormQuestion.builder(Game.Keys.installDirectory.toString(), "Game Install Directory")
                                                .required()
                                                .type(FormQuestion.QuestionType.DIRECTORY_CHOOSER)
                                                .tooltip("Absolute Path to Root directory to install mods")
                                                .build(),

                                FormQuestion.builder(Game.Keys.storeDirectory.toString(), "Mod Storage Directory")
                                                .required()
                                                .defaultValue(AppConfig.getInstance().getDefaultModStorage()
                                                                .resolve("game-name").toString())
                                                .type(FormQuestion.QuestionType.DIRECTORY_CHOOSER)
                                                .tooltip("Absolute Path to store non-deployed Mods")
                                                .build(),

                                FormQuestion.builder("iconFile", "Icon File")
                                                .type(FormQuestion.QuestionType.FILE_CHOOSER)
                                                .tooltip("Pick a file to be copied to the manager files as the icon ('.png' / '.jpg' only)")
                                                .build());
        } // getGameQuestions()

        // === MOD QUESTIONS ===
        public static List<FormQuestion> getModQuestions() {
                return Arrays.asList(
                                FormQuestion.builder(ModMetadata.Keys.name.toString(), "Mod Name")
                                                .required()
                                                .tooltip("Display name for the mod")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.description.toString(), "Description")
                                                .type(FormQuestion.QuestionType.TEXT_AREA)
                                                .tooltip("Description of what the mod does")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.version.toString(), "Version")
                                                .required()
                                                .defaultValue("1.0")
                                                .tooltip("Mod version (semantic versioning recommended)")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.loadOrder.toString(), "Default load Order")
                                                .required()
                                                .tooltip("The default load order from 0 to apply to this mod")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.downloadSource.toString(), "Download Source")
                                                .defaultValue("local")
                                                .tooltip("Referance name of where the mod came from (be consistent)")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.downloadLink.toString(), "Download URL")
                                                .tooltip("Any link for the mod")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.tags.toString(), "Tags")
                                                .tooltip("Comma-separated tags (weapons, textures, gameplay)")
                                                .build(),

                                FormQuestion.builder("pathToFiles", "Mod Files Directory")
                                                .type(FormQuestion.QuestionType.DIRECTORY_CHOOSER)
                                                .tooltip("Path to mod directory, pre-format according to install instructions (this will override all data when updating)")
                                                .build());
        } // getModQuestions()

        public static List<FormQuestion> getModEditQuestions() {
                return Arrays.asList(
                                FormQuestion.builder(ModMetadata.Keys.description.toString(), "Description")
                                                .type(FormQuestion.QuestionType.TEXT_AREA)
                                                .tooltip("Description of what the mod does")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.loadOrder.toString(), "Default load Order")
                                                .required()
                                                .tooltip("The default load order from 0 to apply to this mod")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.downloadSource.toString(), "Download Source")
                                                .defaultValue("local")
                                                .tooltip("Referance name of where the mod came from (be consistent)")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.downloadLink.toString(), "Download URL")
                                                .tooltip("Any link for the mod")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.tags.toString(), "Tags")
                                                .tooltip("Comma-separated tags (weapons, textures, gameplay)")
                                                .build());

        } // getModEditQuestions()
} // Class