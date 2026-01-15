/**
 * Author Stephanos B
 * Date 14/01/2026
 */
package gui.views;

import gui.forms.FormQuestion;
import gui.forms.QuestionDefinitions;
import gui.navigator.AppNavigator;
import gui.state.AppState;
import core.config.AppConfig;

import java.util.*;

/**
 * Read an exsisting Game.json to auto-populate data and allow editting.
 */
public class SettingsView extends FormView {
    private AppConfig config;

    public SettingsView(AppNavigator navigator, Map<String, Object> params) {
        super(navigator, params, "Edit Settings");
    }

    @Override
    protected List<FormQuestion> getQuestions() {
        // Get questions
        return QuestionDefinitions.getSettingQuestions();
    }

    @Override
    protected String getSubmitButtonText() {
        return "Save Changes";
    }

    @Override
    protected void loadExistingData() {
        this.config = AppConfig.getInstance();

        HashMap<String, String> data = config.toMap();

        formPanel.setAnswers(data);
        submitButton.setEnabled(true);
        setTitle("Edit Settings");
    } // loadExistingData()

    @Override
    protected void onSubmit() {
        if (!validateAndCollect())
            return;

        try {
            // Update game from answers
            //config.setFromMap(formPanel.getAnswers());
            // TODO write file...

            // Navigate back to library
            AppState.getInstance().setCurrentGame(null);
            navigator.goBack();
        } catch (Exception e) {
            showError("Failed to update game: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onDelete() {
        // nothing to do.
    }
} // Class