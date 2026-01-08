/**
 * Author Stephanos B
 * Date 8/01/2026
 */
package gui.forms;

import java.util.Arrays;
import java.util.List;

import core.objects.Game;

/**
 * Pre-define the questions as FormQUestion instances. Add/modify form layouts
 * for FormPanel.java here.
 */
public class QuestionDefinitions {

        // === GAME QUESTIONS ===
        public static List<FormQuestion> getGameQuestions() {
                return Arrays.asList(
                                FormQuestion.builder("id", "Game ID")
                                                .required()
                                                .tooltip("Unique identifier (no spaces, e.g., 'ghost-recon')")
                                                .build(),

                                FormQuestion.builder("name", "Game Name")
                                                .required()
                                                .tooltip("The display name of the game")
                                                .build(),

                                FormQuestion.builder("releaseVersion", "Release Version")
                                                .required()
                                                .tooltip("Used for version tracking and compatability")
                                                .build(),

                                FormQuestion.builder("installDirectory", "Game Install Directory")
                                                .required()
                                                .type(FormQuestion.QuestionType.DIRECTORY_CHOOSER)
                                                .tooltip("Absolute Path to Root directory to install mods")
                                                .build(),

                                FormQuestion.builder("storeDirectory", "Mod Storage Directory")
                                                .required()
                                                .type(FormQuestion.QuestionType.DIRECTORY_CHOOSER)
                                                .tooltip("Absolute Path to store non-deployed Mods")
                                                .build(),

                                FormQuestion.builder("iconFile", "Icon File")
                                                .type(FormQuestion.QuestionType.FILE_CHOOSER)
                                                .tooltip("Pick a file to be copied to the manager files")
                                                .build());
        } // getGameQuestions()

        public static List<FormQuestion> getGameQuestionsWithDefaults(Game game) {
                List<FormQuestion> questions = getGameQuestions();
                // In practice, you'd create new questions with defaults set
                // For now, we'll handle defaults in the View
                return questions;
        }

        // === MOD QUESTIONS ===
        public static List<FormQuestion> getModQuestions() {
                return Arrays.asList(
                                FormQuestion.builder("name", "Mod Name")
                                                .required()
                                                .tooltip("Display name for the mod")
                                                .build(),

                                FormQuestion.builder("modId", "Mod ID")
                                                .required()
                                                .tooltip("Unique ID (e.g., 'nexus-12345' or 'custom-weapons')")
                                                .build(),

                                FormQuestion.builder("version", "Version")
                                                .required()
                                                .defaultValue("1.0.0")
                                                .tooltip("Mod version (semantic versioning recommended)")
                                                .build(),

                                FormQuestion.builder("author", "Author")
                                                .tooltip("Mod author/creator")
                                                .build(),

                                FormQuestion.builder("description", "Description")
                                                .type(FormQuestion.QuestionType.TEXT_AREA)
                                                .tooltip("Description of what the mod does")
                                                .build(),

                                FormQuestion.builder("source", "Source")
                                                .type(FormQuestion.QuestionType.COMBO_BOX)
                                                .defaultValue(new String[] { "Nexus Mods", "Steam Workshop", "Manual",
                                                                "Custom" })
                                                .tooltip("Where the mod is from")
                                                .build(),

                                FormQuestion.builder("sourceId", "Source ID")
                                                .tooltip("ID on the source platform (e.g., Nexus mod ID)")
                                                .build(),

                                FormQuestion.builder("tags", "Tags")
                                                .tooltip("Comma-separated tags (weapons, textures, gameplay)")
                                                .build());
        } // getModQuestions()
} // Class