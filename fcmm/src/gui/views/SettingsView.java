/**
 * Author Stephanos B
 * Date 14/01/2026
 */
package gui.views;

import gui.forms.FormQuestion;
import gui.forms.QuestionDefinitions;
import gui.navigator.AppNavigator;
import gui.state.AppState;
import gui.util.GUIUtils;
import gui.util.IconLoader;
import gui.util.IconLoader.ICONS;
import core.config.AppConfig;
import core.utils.Logger;
import core.utils.TrashUtil;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;

/**
 * Read an exsisting Game.json to auto-populate data and allow editting.
 * 
 * @author Stephanos B
 * @since v2
 */
public class SettingsView extends FormView {
    private AppConfig config;

    public SettingsView(AppNavigator navigator, Map<String, Object> params) {
        super(navigator, params, "Edit Settings");
        this.submitButton.setIcon(IconLoader.loadIcon(ICONS.SAVE, new Dimension(20, 20)));
        updateTrashSize();
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
            HashMap<String, String> map = (HashMap<String, String>) GUIUtils.toStringOnlyMap(formPanel.getAnswers());
            System.out.println(formPanel.getAnswers());
            config.updateAndSaveConfig(map);

            // Navigate back to library
            AppState.getInstance().setCurrentGame(null);
            loadExistingData();
        } catch (Exception e) {
            showError("Failed to update config: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onDelete() {
        // nothing to do.
    }

    /// /// /// Custom section /// /// ///

    /// /// /// Trash utility /// /// ///

    @Override
    protected JComponent createCustomContent() {
        return createTrashPanel(); // Returns the trash panel from earlier
    }

    private JLabel trashSizeLabel;
    private JProgressBar trashUsageBar;
    private JSpinner maxSizeSpinner;
    private JSpinner daysToKeepSpinner;

    private JComponent createTrashPanel() {
        JPanel trashPanel = new JPanel(new GridBagLayout());
        trashPanel.setBorder(BorderFactory.createTitledBorder("Trash Management"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Current size display
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        trashSizeLabel = new JLabel("Calculating trash size...");
        trashPanel.add(trashSizeLabel, gbc);

        gbc.gridy = 1;
        trashUsageBar = new JProgressBar(0, 100);
        trashUsageBar.setStringPainted(true);
        trashPanel.add(trashUsageBar, gbc);

        // Configuration
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        trashPanel.add(new JLabel("Max trash size (MB):"), gbc);

        gbc.gridx = 1;
        maxSizeSpinner = new JSpinner(new SpinnerNumberModel(100, 10, 10000, 10));
        trashPanel.add(maxSizeSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        trashPanel.add(new JLabel("Keep files for (days):"), gbc);

        gbc.gridx = 1;
        daysToKeepSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 365, 1));
        trashPanel.add(daysToKeepSpinner, gbc);

        // Action buttons
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton cleanNowButton = new JButton("Clean Trash Now");
        cleanNowButton.addActionListener(e -> cleanTrash());

        JButton emptyNowButton = new JButton("Empty Trash Now");
        emptyNowButton.setForeground(Color.RED);
        emptyNowButton.addActionListener(e -> emptyTrash());

        JButton openFolderButton = new JButton("Open Trash Folder");
        openFolderButton.addActionListener(e -> openTrashFolder());

        buttonPanel.add(cleanNowButton);
        buttonPanel.add(emptyNowButton);
        buttonPanel.add(openFolderButton);

        trashPanel.add(buttonPanel, gbc);

        updateTrashSize();
        return trashPanel;
    }

    private void updateTrashSize() {
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                Logger.getInstance().info(0, null, "trash size requested");
                long sizeBytes = TrashUtil.getDiskSize(config.getTrashDir());
                float sizeMB = sizeBytes / (1024f * 1024f);

                publish(String.format("Trash size: %.2f MB", sizeMB));
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                Logger.getInstance().info(0, null, "trash size returned");
                trashSizeLabel.setText(chunks.get(chunks.size() - 1));
            }
        };
        worker.execute();
    }

    /// /// /// Button logic /// /// ///

    private void cleanTrash() {
        try {
            showConsole();
            int maxMB = (Integer) maxSizeSpinner.getValue();
            int days = (Integer) daysToKeepSpinner.getValue();
            LocalDate cutoff = LocalDate.now().minusDays(days);

            SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception { // long-running task
                    TrashUtil.cleanTrash(maxMB, cutoff);
                    return null;
                }

                @Override
                protected void done() { // Task completed - update GUI state
                    updateTrashSize();
                    finishConsole();
                }
            };
            worker.execute();

        } catch (Exception e) {
            showError("Failed to compile Mod: " + e.getMessage(), e);
        } finally {
            finishConsole();
        }
    }

    private void emptyTrash() {
        showConsole();
        try {
            TrashUtil.emptyTrash();
        } catch (Exception e) {
            showError("Failed to compile Mod: " + e.getMessage(), e);
        } finally {
            finishConsole();
        }
    }

    private void openTrashFolder() {
        try {
            Desktop.getDesktop().open(config.getTrashDir().toFile());
        } catch (IOException e) {
            showError("Could not open file", e);
        }
    }

} // Class