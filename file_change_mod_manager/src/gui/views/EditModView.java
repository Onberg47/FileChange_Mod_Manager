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
import core.managers.ModManager;
import core.objects.Mod;

import java.nio.file.Path;
import java.util.*;

import javax.swing.JComponent;
import javax.swing.JToggleButton;

/**
 * Read an exsisting Mod.json to auto-populate data and allow editting.
 */
public class EditModView extends FormView {
    private Mod mod;
    private ModManager manager;
    private JToggleButton modeToggleButton;

    private static boolean isUpdate = false;

    public EditModView(AppNavigator navigator, Map<String, Object> params) {
        if (params.containsKey("isUpdate") && params.get("isUpdate") == "true")
            isUpdate = true;

        super(navigator, params, "Edit Mod");
        deleteButton.setVisible(true);
    }

    @Override
    protected List<FormQuestion> getQuestions() {
        // Get questions
        if (isUpdate)
            return QuestionDefinitions.getModQuestions();
        else
            return QuestionDefinitions.getModEditQuestions();
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

    /// /// /// Buttons

    @Override
    protected JComponent customButton() {
        modeToggleButton = new JToggleButton("Update Mode", false);
        if (isUpdate)
            modeToggleButton.setSelected(true);

        modeToggleButton.addActionListener(e -> {
            if (modeToggleButton.isSelected()) {
                isUpdate = true;
                navigator.replace("editMod", params);
            } else {
                isUpdate = false;
                navigator.replace("editMod", params);
            }
        });
        return modeToggleButton;
    }

    /**
     * Edit or Update the current mod.
     */
    @Override
    protected void onSubmit() {
        if (!validateAndCollect())
            return;

        try {
            ModManager manager = new ModManager(AppState.getInstance().getCurrentGame());

            if (isUpdate) { // Update mode
                try {
                    showConsole();

                    Path files = null;
                    if (formPanel.getAnswers().containsKey("pathToFiles"))
                        files = Path.of(formPanel.getAnswers().get("pathToFiles").toString());

                    manager.updateMod(mod.getId(), files, (HashMap<String, Object>) formPanel.getAnswers());
                } finally {
                    super.consolePopup.setDone();
                }
            } else { // Edit mode
                System.out.println("Saving mod with edits: " + formPanel.getAnswers().toString());
                manager.editMod(mod.getId(), (HashMap<String, Object>) formPanel.getAnswers());
            }
            // Navigate back
            navigator.goBack();
        } catch (Exception e) {
            showError("Failed to compile Mod: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onDelete() {
        if (!confirm("Are you sure you want to delete Mod: " + mod.getName() + "?"))
            return;
        try {
            manager.deleteMod(mod.getId());

        } catch (Exception e) {
            showError("Could not delete Mod " + mod.getId() + " -> " + e.getMessage(), e);
        } finally {
            AppState.getInstance().setCurrentMod(null);
            navigator.goBack();
        }
    }

} // Class