/**
 * Author Stephanos B
 * Date 8/01/2026
 */
package gui.views;

import gui.forms.FormPanel;
import gui.forms.FormQuestion;
import gui.navigator.AppNavigator;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Reusable element for viewing a FormPanel.
 */
public abstract class FormView extends BaseView {
    protected FormPanel formPanel;
    protected JButton submitButton;
    protected JButton cancelButton;
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

        // Form panel (subclass provides questions)
        formPanel = new FormPanel(getQuestions());
        add(new JScrollPane(formPanel), BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        cancelButton = new JButton("Cancel");
        submitButton = new JButton(getSubmitButtonText());
        submitButton.setEnabled(false);

        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load existing data if editing
        loadExistingData();
    }

    @Override
    protected void setupEventHandlers() {
        cancelButton.addActionListener(e -> onCancel());
        submitButton.addActionListener(e -> onSubmit());

        // Enable submit when form is valid
        // You'd need to add validation listeners to each field
        // For simplicity, we'll validate on submit
    }

    protected abstract List<FormQuestion> getQuestions();

    protected abstract String getSubmitButtonText();

    protected abstract void onSubmit();

    protected abstract void loadExistingData();

    protected void onCancel() {
        navigator.goBack();
    }

    protected boolean validateAndCollect() {
        if (!formPanel.validateForm()) {
            showError("Please fill in all required fields.");
            return false;
        }
        return true;
    }
} // Class