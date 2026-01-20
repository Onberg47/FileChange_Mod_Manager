/**
 * Author Stephanos B
 * Date 8/01/2026
 */
package gui.views;

import gui.forms.FormQuestion;
import gui.forms.QuestionDefinitions;
import gui.navigator.AppNavigator;
import gui.state.AppState;
import gui.util.IconLoader;
import gui.util.IconLoader.ICONS;
import core.managers.GameManager;

import java.awt.Dimension;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays a FormView. Add a new Game from scratch and write a JSON file.
 * 
 * @author Stephanos B
 * @since v2
 */
public class AddGameView extends FormView {
    public AddGameView(AppNavigator navigator, Map<String, Object> params) {
        super(navigator, params, "Add New Game");
        this.submitButton.setIcon(IconLoader.loadIcon(ICONS.ADD, new Dimension(20, 20)));
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

        try {
            // Update game from answers
            HashMap<String, Object> answers = (HashMap<String, Object>) formPanel.getAnswers();
            AppState.getInstance().setCurrentGame(GameManager.addGame(answers));

            // Try add a new icon
            if (answers.containsKey("iconFile")) {
                IconLoader.fetchGameIcon(Path.of((String) answers.get("iconFile")));
                IconLoader.clearCache();
            }

            // Navigate back
            navigator.goBack();
        } catch (Exception e) {
            showError("Failed to add game: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onDelete() {
        // Does nothing in this case.
    }
} // Class