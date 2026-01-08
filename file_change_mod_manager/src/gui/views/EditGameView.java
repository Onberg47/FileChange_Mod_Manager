/**
 * Author Stephanos B
 * Date 8/01/2026
 */
package gui.views;

import gui.forms.FormQuestion;
import gui.forms.QuestionDefinitions;
import gui.navigator.AppNavigator;
import gui.state.AppState;
import core.managers.GameManager;
import core.objects.Game;

import java.awt.Component;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * Read an exsisting Game.json to auto-populate data and allow editting.
 */
public class EditGameView extends FormView {
    private Game game;
    private HashMap<String, String> answers;

    public EditGameView(AppNavigator navigator, Map<String, Object> params) {
        super(navigator, params, "Edit Game");
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
        System.out.println("Loading data.");
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

        HashMap<String, String> gameData = game.toMap();

        // Special handling for paths - convert to absolute if relative
        if (gameData.containsKey("gameDirectory") && !gameData.get("gameDirectory").isEmpty()) {
            Path dir = Paths.get(gameData.get("gameDirectory"));
            if (!dir.isAbsolute()) {
                // Convert to absolute path for display
                dir = dir.toAbsolutePath();
                gameData.put("gameDirectory", dir.toString());
            }
        }

        // Special handling for mod count (if displaying)
        if (gameData.containsKey("modCount")) {
            // Could format as "X mods installed"
            int count = Integer.parseInt(gameData.get("modCount"));
            gameData.put("modCountDisplay", count + " mod" + (count != 1 ? "s" : ""));
        }

        formPanel.setAnswers(gameData);
        submitButton.setEnabled(true);
        setTitle("Edit Game: " + game.getName());
    } // loadExistingData()

    /**
     * Helper to update title
     * 
     * @param title New title.
     */
    private void setTitle(String title) {
        // Find the title label in the view
        for (Component comp : getComponents()) {
            if (comp instanceof JLabel && ((JLabel) comp).getHorizontalAlignment() == SwingConstants.CENTER) {
                ((JLabel) comp).setText(title);
                break;
            }
        }
    }

    @Override
    protected void onSubmit() {
        if (!validateAndCollect())
            return;

        answers = (HashMap<String, String>) formPanel.getAnswers();
        try {
            // remove while still the old instance
            // AppState.getInstance().removeFromCache(game.getId());

            // Update game from answers
            game.setFromMap(answers);
            GameManager.saveGame(game);
            // AppState.getInstance().cacheGame(game);

            // Notify other views
            // AppState.getInstance().fireGameUpdated(game);

            // Navigate back to library
            navigator.navigateTo("library");
        } catch (Exception e) {
            showError("Failed to update game: " + e.getMessage());
        }
    }
} // Class