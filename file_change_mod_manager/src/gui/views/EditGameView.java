/**
 * Author Stephanos B
 * Date 8/01/2026
 */
package gui.views;

import gui.forms.FormQuestion;
import gui.forms.QuestionDefinitions;
import gui.navigator.AppNavigator;
import gui.state.AppState;
import gui.util.GUIUtils;
import gui.util.IconLoader;
import core.managers.GameManager;
import core.objects.Game;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Read an exsisting Game.json to auto-populate data and allow editting.
 */
public class EditGameView extends FormView {
    private Game game;
    private HashMap<String, String> answers;

    public EditGameView(AppNavigator navigator, Map<String, Object> params) {
        super(navigator, params, "Edit Game");
        deleteButton.setVisible(true);
    }

    @Override
    protected List<FormQuestion> getQuestions() {
        // Get questions
        return QuestionDefinitions.getGameQuestions();
    }

    @Override
    protected String getSubmitButtonText() {
        return "Save Changes";
    }

    @Override
    protected void loadExistingData() {
        String gameId = (String) params.get("gameId");
        this.game = AppState.getInstance().getCurrentGame();

        // If not in cache, try to load
        if (this.game == null) {
            try {
                this.game = GameManager.getGameById(gameId);
            } catch (Exception e) {
                System.err.println("Could not find game!");
                game = null;
            }
        }

        HashMap<String, String> gameData = (HashMap<String, String>) GUIUtils.toStringOnlyMap(game.toMap());

        // Special handling for paths - convert to absolute if relative
        if (gameData.containsKey("installDirectory") && !gameData.get("installDirectory").isEmpty()) {
            Path dir = Paths.get(gameData.get("installDirectory"));
            if (!dir.isAbsolute()) {
                System.err.println("Making absolute...");
                // Convert to absolute path for display
                dir = dir.toAbsolutePath();
                gameData.put("installDirectory", dir.toString());
            }
        }

        formPanel.setAnswers(gameData);
        submitButton.setEnabled(true);
        setTitle("Edit Game: " + game.getName());
    } // loadExistingData()

    @Override
    protected void onSubmit() {
        if (!validateAndCollect())
            return;

        answers = (HashMap<String, String>) GUIUtils.toStringOnlyMap(formPanel.getAnswers());
        try {
            // Update game from answers
            game.setFromMap(formPanel.getAnswers());
            GameManager.saveGame(game);

            // Try add a new icon
            if (answers.containsKey("iconFile")) {
                IconLoader.fetchIcon(Path.of(answers.get("iconFile")));
                IconLoader.clearCache();
            }

            // Navigate back to library
            AppState.getInstance().setCurrentGame(null);
            navigator.navigateTo("library");
        } catch (Exception e) {
            showError("Failed to update game: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onDelete() {
        if (!confirm("Are you sure you want to delete Game: " + game.getName() + "?"))
            return;

        try {
            GameManager.removeGame(game.getId());
            AppState.getInstance().setCurrentGame(null);

        } catch (Exception e) {
            System.err.println("Could not delete Game " + game.getId() + " -> " + e.getMessage());
            e.printStackTrace();
        } finally {
            navigator.navigateTo("library");
        }
    }
} // Class