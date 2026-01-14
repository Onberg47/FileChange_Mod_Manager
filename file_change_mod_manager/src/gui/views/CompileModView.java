/**
 * Author Stephanos B
 * Date 8/01/2026
 */
package gui.views;

import gui.forms.FormQuestion;
import gui.forms.QuestionDefinitions;
import gui.navigator.AppNavigator;
import gui.state.AppState;

import core.managers.ModManager;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Add a new Game from scratch and write a JSON file.
 */
public class CompileModView extends FormView {
    public CompileModView(AppNavigator navigator, Map<String, Object> params) {
        super(navigator, params, "Compile New Mod");
    }

    @Override
    protected List<FormQuestion> getQuestions() {
        List<FormQuestion> question = QuestionDefinitions.getModQuestions();
        question.getLast().setRequired(true); // sets the las (mod files) to required
        return question;
    }

    @Override
    protected String getSubmitButtonText() {
        return "Compile Mod";
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

        ModManager manager = new ModManager(AppState.getInstance().getCurrentGame());
        try {
            /// Compile Mod
            Path files = Path.of(formPanel.getAnswers().get("pathToFiles").toString());
            showConsole();
            manager.compileMod(files, (HashMap<String, Object>) formPanel.getAnswers());

            // Navigate back
            navigator.goBack();
        } catch (Exception e) {
            showError("Failed to compile Mod: " + e.getMessage(), e);
        } finally {
            super.consolePopup.setDone();
        }
    }

    @Override
    protected void onDelete() {
        // Does nothing in this case.
    }
} // Class