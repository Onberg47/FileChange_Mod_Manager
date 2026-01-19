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
import core.managers.ModManager;

import java.awt.Dimension;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingWorker;

/**
 * Displays the FormView. Gathers Mod information for compiling and compiles on
 * submit.
 * 
 * @author Stephanos B
 * @since v3
 */
public class CompileModView extends FormView {
    public CompileModView(AppNavigator navigator, Map<String, Object> params) {
        super(navigator, params, "Compile New Mod");
        this.submitButton.setIcon(IconLoader.loadResourceIcon(ICONS.CREATE, new Dimension(20, 20)));
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
            SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception { // long-running task
                    manager.compileMod(files, (HashMap<String, Object>) formPanel.getAnswers());
                    return null;
                }

                @Override
                protected void done() { // Task completed - update GUI state
                    finishConsole();
                    navigator.goBack();
                }
            };
            worker.execute();

        } catch (Exception e) {
            showError("Failed to compile Mod: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onDelete() {
        // Does nothing in this case.
    }
} // Class