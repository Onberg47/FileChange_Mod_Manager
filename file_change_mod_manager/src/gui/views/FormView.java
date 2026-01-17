/**
 * Author Stephanos B
 * Date 8/01/2026
 */
package gui.views;

import gui.forms.FormPanel;
import gui.forms.FormQuestion;
import gui.navigator.AppNavigator;
import gui.util.IconLoader;
import gui.util.IconLoader.ICONS;

import javax.swing.*;
import java.awt.*;
import java.io.NotActiveException;
import java.util.List;
import java.util.Map;

/**
 * Reusable element for viewing a FormPanel.
 * 
 * @author Stephanos B
 * @since v2
 */
public abstract class FormView extends BaseView {
    protected FormPanel formPanel;
    protected JButton submitButton;
    protected JButton cancelButton;
    protected JButton deleteButton;
    protected String formTitle;

    public FormView(AppNavigator navigator, Map<String, Object> params, String title) {
        super(navigator, params);
        this.formTitle = title;
    }

    @Override
    protected void initializeUI() {
        setLayout(new BorderLayout(20, 20));

        // Title
        JLabel titleLabel = new JLabel(formTitle, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());

        // Form panel
        formPanel = new FormPanel(getQuestions());
        contentPanel.add(formPanel, BorderLayout.NORTH);

        // Custom content (if any)
        JComponent customContent = createCustomContent();
        if (customContent != null) {
            contentPanel.add(customContent, BorderLayout.CENTER);
        }

        add(new JScrollPane(contentPanel), BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        cancelButton = new JButton("Cancel");

        submitButton = new JButton(getSubmitButtonText());
        submitButton.setEnabled(false);
        // icon is set by implmenting View.

        deleteButton = new JButton("Delete");
        deleteButton.setVisible(false);
        this.deleteButton.setIcon(IconLoader.loadResourceIcon(ICONS.TRASH, new Dimension(18, 18)));

        try {
            buttonPanel.add(customButton());
        } catch (NotActiveException e) {
        }
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load existing data if editing
        loadExistingData();
    }

    /**
     * Override this with a custom button.
     * 
     * @throws NotActiveException when no Custom button is set.
     */
    protected JComponent customButton() throws NotActiveException {
        throw new NotActiveException();
    }

    /**
     * Accepts a self-contained custom section to fill beneath the FormQuestions
     * 
     * @return
     */
    protected JComponent createCustomContent() {
        return null; // Subclasses can override to add custom panels
    }

    @Override
    protected void setupEventHandlers() {
        deleteButton.addActionListener(e -> onDelete());
        cancelButton.addActionListener(e -> onCancel());
        submitButton.addActionListener(e -> onSubmit());

        // Enable submit when form is valid
        // You'd need to add validation listeners to each field
        // For simplicity, only validates on submit
    }

    protected abstract List<FormQuestion> getQuestions();

    protected abstract String getSubmitButtonText();

    protected abstract void onSubmit();

    protected abstract void loadExistingData();

    protected void onCancel() {
        navigator.goBack();
    }

    protected abstract void onDelete();

    protected boolean validateAndCollect() {
        if (!formPanel.validateForm()) {
            showError("Please fill in all required fields.");
            return false;
        }
        return true;
    }

    /**
     * Helper to update title
     * 
     * @param title New title.
     */
    protected void setTitle(String title) {
        // Find the title label in the view
        for (Component comp : getComponents()) {
            if (comp instanceof JLabel && ((JLabel) comp).getHorizontalAlignment() == SwingConstants.CENTER) {
                ((JLabel) comp).setText(title);
                break;
            }
        }
    }
} // Class