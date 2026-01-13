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
import core.managers.ModManager;
import core.objects.Mod;

import java.nio.file.Path;
import java.util.*;

/**
 * Read an exsisting Mod.json to auto-populate data and allow editting.
 */
public class EditModView extends FormView {
    private Mod mod;
    private ModManager manager;
    private HashMap<String, String> answers;

    public EditModView(AppNavigator navigator, Map<String, Object> params) {
        super(navigator, params, "Edit Mod");
        deleteButton.setVisible(true);
    }

    @Override
    protected List<FormQuestion> getQuestions() {
        // Get questions
        return QuestionDefinitions.getModQuestions();
    }

    @Override
    protected String getSubmitButtonText() {
        return "Save Changes";
    }

    @Override
    protected void loadExistingData() {
        String modId = (String) params.get("modId");
        this.mod = AppState.getInstance().getCurrentMod();
        this.manager = new ModManager(AppState.getInstance().getCurrentGame());

        // If not in cache, try to load
        if (this.mod == null) {
            try {
                this.mod = manager.getModById(modId);
            } catch (Exception e) {
                showError("Could not find Mod: " + modId, e);
                mod = null;
            }
        }

        HashMap<String, String> modData = (HashMap<String, String>) GUIUtils.toStringOnlyMap(mod.toMap());
        formPanel.setAnswers(modData);
        submitButton.setEnabled(true);
        setTitle("Edit Mod: " + mod.getName());
    } // loadExistingData()

    @Override
    protected void onSubmit() {
        if (!validateAndCollect())
            return;

        answers = (HashMap<String, String>) GUIUtils.toStringOnlyMap(formPanel.getAnswers());
        try {
            // Update game from answers
            mod.setFromMap(formPanel.getAnswers());
            // manager.updateMod();

            // Try add a new icon
            if (answers.containsKey("iconFile")) {
                IconLoader.fetchIcon(Path.of(answers.get("iconFile")));
                IconLoader.clearCache();
            }

            // Navigate back to library
            AppState.getInstance().setCurrentGame(null);
            navigator.navigateTo("library");
        } catch (Exception e) {
            showError("Failed to update Mod: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onDelete() {
        if (!confirm("Are you sure you want to delete Mod: " + mod.getName() + "?"))
            return;
        try {
            manager.deleteMod(mod.getId());
            AppState.getInstance().setCurrentMod(null);

        } catch (Exception e) {
            System.err.println("Could not delete Mod " + mod.getId() + " -> " + e.getMessage());
            e.printStackTrace();
        } finally {
            navigator.navigateTo("modManager");
        }
    }
} // Class