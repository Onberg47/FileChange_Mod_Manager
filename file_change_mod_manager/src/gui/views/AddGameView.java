/**
 * Author Stephanos B
 * Date 8/01/2026
 */
package gui.views;

import gui.forms.FormQuestion;
import gui.forms.QuestionDefinitions;
import gui.navigator.AppNavigator;
//import gui.state.AppState;
//import core.objects.Game;
import core.managers.GameManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Add a new Game from scratch and write a JSON file.
 */
public class AddGameView extends FormView {
    public AddGameView(AppNavigator navigator, Map<String, Object> params) {
        super(navigator, params, "Add New Game");
    }

    @Override
    protected List<FormQuestion> getQuestions() {
        return QuestionDefinitions.getGameQuestions();
    }

    @Override
    protected String getSubmitButtonText() {
        return "Add Game";
    }

    @Override
    protected void loadExistingData() {
        // Nothing to load for add
        submitButton.setEnabled(true); // Simple validation
    }

    @Override
    protected void onSubmit() {
        if (!validateAndCollect())
            return;

        HashMap<String, String> answers = (HashMap<String, String>) formPanel.getAnswers();
        try {
            // Update game from answers
            GameManager gm = new GameManager();
            // Game game =
            gm.addGame(answers);

            // if (AppState.getInstance().getCachedGame(game.getId()) != null)
            // throw new Exception("Game ID already exsists! Do not use AddGame for
            // updating.");
            // AppState.getInstance().cacheGame(game);

            // Notify other views
            // AppState.getInstance().fireGameUpdated(game);

            // Navigate back
            navigator.goBack();
        } catch (Exception e) {
            showError("Failed to add game: " + e.getMessage());
        }
    }

    @Override
    protected void onDelete() {
        // Does nothing in this case.
    }
} // Class