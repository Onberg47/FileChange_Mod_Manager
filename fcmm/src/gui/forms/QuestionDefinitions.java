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
 * 
 * @since v2
 */
public class QuestionDefinitions {

        /**
         * Full questions for a Game.
         */
        public static List<FormQuestion> getGameQuestions() {
                return Arrays.asList(
                                FormQuestion.builder(Game.Keys.NAME.key(), "Game Name")
                                                .required()
                                                .tooltip("The display name of the game")
                                                .build(),

                                FormQuestion.builder(Game.Keys.ID.key(), "Game ID")
                                                .required()
                                                .defaultValue("game-name")
                                                .tooltip("Unique identifier only interacted with by the user in CLI (no spaces, e.g., 'ghost-recon')")
                                                .build(),

                                FormQuestion.builder(Game.Keys.RELEASE_VERSION.key(), "Release Version")
                                                .tooltip("Used for version tracking and compatability (use the build-ID from steam-properties)")
                                                .build(),

                                FormQuestion.builder(Game.Keys.INSTALL_DIR.key(), "Game Install Directory")
                                                .required()
                                                .type(FormQuestion.QuestionType.DIRECTORY_CHOOSER)
                                                .tooltip("Absolute Path to Root directory to install mods")
                                                .build(),

                                FormQuestion.builder(Game.Keys.STORE_DIR.key(), "Mod Storage Directory")
                                                .required()
                                                .defaultValue(AppConfig.getInstance().getDefaultModStorage()
                                                                .resolve("game-name").toString())
                                                .type(FormQuestion.QuestionType.DIRECTORY_CHOOSER)
                                                .tooltip("Absolute Path to store non-deployed Mods")
                                                .build(),

                                FormQuestion.builder("iconFile", "Icon File")
                                                .type(FormQuestion.QuestionType.FILE_CHOOSER)
                                                .tooltip("Pick a file to be copied to the manager files as the icon ('.png' / '.jpg' only)")
                                                .build()

                );
        } // getGameQuestions()

        /**
         * Full ModManifest questions. This is for compiling mods.
         */
        public static List<FormQuestion> getModQuestions() {
                return Arrays.asList(
                                FormQuestion.builder(ModMetadata.Keys.NAME.key(), "Mod Name")
                                                .required()
                                                .tooltip("Display name for the mod")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.DESCRIPTION.key(), "Description")
                                                .type(FormQuestion.QuestionType.TEXT_AREA)
                                                .tooltip("Description of what the mod does")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.VERSION.key(), "Version")
                                                .required()
                                                .defaultValue("1.0")
                                                .tooltip("Mod version (semantic versioning recommended)")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.LOAD_ORDER.key(), "Default load order")
                                                .defaultValue(1)
                                                .required()
                                                .tooltip("The default load order from 0 to apply to this mod")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.DOWNLOAD_SOURCE.key(), "Download Source")
                                                .defaultValue("local")
                                                .tooltip("Referance name of where the mod came from (be consistent)")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.DOWNLOAD_LINK.key(), "Download URL")
                                                .tooltip("Any link for the mod")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.TAGS.key(), "Tags")
                                                .tooltip("Comma-separated tags (weapons, textures, gameplay)")
                                                .build(),

                                FormQuestion.builder("pathToFiles", "Mod Files Directory")
                                                .type(FormQuestion.QuestionType.DIRECTORY_CHOOSER)
                                                .tooltip("Path to mod directory, pre-format according to install instructions (this will override all data when updating)")
                                                .build()

                );
        } // getModQuestions()

        /**
         * This is only non-essential fields that can be re-written without compiling.
         */
        public static List<FormQuestion> getModEditQuestions() {
                return Arrays.asList(
                                FormQuestion.builder(ModMetadata.Keys.DESCRIPTION.key(), "Description")
                                                .type(FormQuestion.QuestionType.TEXT_AREA)
                                                .tooltip("Description of what the mod does")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.LOAD_ORDER.key(), "Default load order")
                                                .required()
                                                .tooltip("The default load order from 0 to apply to this mod")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.DOWNLOAD_SOURCE.key(), "Download Source")
                                                .defaultValue("local")
                                                .tooltip("Referance name of where the mod came from (be consistent)")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.DOWNLOAD_LINK.key(), "Download URL")
                                                .tooltip("Any link for the mod")
                                                .build(),

                                FormQuestion.builder(ModMetadata.Keys.TAGS.key(), "Tags")
                                                .tooltip("Comma-separated tags (weapons, textures, gameplay)")
                                                .build()

                );
        } // getModEditQuestions()

        /**
         * All settings for primary config settings.
         */
        public static List<FormQuestion> getSettingQuestions() {
                return Arrays.asList(

                                FormQuestion.builder("DEFAULT_MOD_DIR", "(Normal) Default Mod storage Direcotry")
                                                .type(FormQuestion.QuestionType.DIRECTORY_CHOOSER)
                                                .tooltip("Where to set store mods per a game by default")
                                                .build(),

                                FormQuestion.builder("MANAGER_DIR", "(Advanced) Deployment Manager Direcotry")
                                                .type(FormQuestion.QuestionType.DIRECTORY_CHOOSER)
                                                .tooltip("Where the manager stores its files within a Game")
                                                .build(),

                                FormQuestion.builder("GAME_DIR", "(Expert) Games Direcotry")
                                                .type(FormQuestion.QuestionType.DIRECTORY_CHOOSER)
                                                .tooltip("Where games are stored in the manager.")
                                                .build(),

                                FormQuestion.builder("TEMP_DIR", "(Expert) Tempory Direcotry")
                                                .type(FormQuestion.QuestionType.DIRECTORY_CHOOSER)
                                                .tooltip("Where tempory operations take place")
                                                .build(),

                                FormQuestion.builder("TRASH_DIR", "(Expert) Trash Direcotry")
                                                .type(FormQuestion.QuestionType.DIRECTORY_CHOOSER)
                                                .tooltip("Where trashed files go")
                                                .build(),

                                FormQuestion.builder("LOG_DIR", "(Expert) Logs Direcotry")
                                                .type(FormQuestion.QuestionType.DIRECTORY_CHOOSER)
                                                .tooltip("Where Log files are stored.")
                                                .build()

                );
        } // getModEditQuestions()
} // Class