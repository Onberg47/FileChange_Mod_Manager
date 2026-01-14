/**
 * Author Stephanos B
 * Date 8/01/2026
 */
package gui.components;

import gui.forms.FormQuestion;
import javax.swing.*;
import java.awt.*;

/**
 * Displays a FormQuestion instance graphically.
 */
public class QuestionCard extends JPanel {
    private final FormQuestion question;
    private JComponent inputComponent;

    public QuestionCard(FormQuestion question) {
        this.question = question;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 0));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Left: Label with required indicator
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel label = new JLabel(question.getLabel());
        if (question.isRequired()) {
            label.setText(label.getText() + " *");
            label.setForeground(new Color(200, 0, 0)); // Red for required
        }

        if (!question.getTooltip().isEmpty()) {
            label.setToolTipText(question.getTooltip());
        }

        labelPanel.add(label);
        add(labelPanel, BorderLayout.WEST);

        // Right: Input component
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputComponent = createInputComponent();
        inputPanel.add(inputComponent, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.CENTER);
    }

    private JComponent createInputComponent() {
        switch (question.getType()) {
            case TEXT_FIELD:
                JTextField textField = new JTextField(20);
                if (question.getDefaultValue() != null) {
                    textField.setText(question.getDefaultValue().toString());
                }
                return textField;

            case TEXT_AREA:
                JTextArea textArea = new JTextArea(3, 20);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                if (question.getDefaultValue() != null) {
                    textArea.setText(question.getDefaultValue().toString());
                }
                return new JScrollPane(textArea);

            case FILE_CHOOSER:
            case DIRECTORY_CHOOSER:
                return createFileChooserPanel();

            case COMBO_BOX:
                JComboBox<String> combo = new JComboBox<>();
                if (question.getDefaultValue() instanceof String[]) {
                    for (String item : (String[]) question.getDefaultValue()) {
                        combo.addItem(item);
                    }
                }
                return combo;

            case CHECKBOX:
                JCheckBox checkBox = new JCheckBox();
                if (question.getDefaultValue() instanceof Boolean) {
                    checkBox.setSelected((Boolean) question.getDefaultValue());
                }
                return checkBox;

            default:
                return new JTextField(20);
        }
    }

    private JPanel createFileChooserPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        JTextField pathField = new JTextField();
        JButton browseButton = new JButton("Browse...");

        if (question.getDefaultValue() != null) {
            pathField.setText(question.getDefaultValue().toString());
        }

        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();

            if (question.getType() == FormQuestion.QuestionType.DIRECTORY_CHOOSER) {
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setDialogTitle("Select Directory");
            } else {
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setDialogTitle("Select File");
            }

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                pathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        panel.add(pathField, BorderLayout.CENTER);
        panel.add(browseButton, BorderLayout.EAST);

        return panel;
    }

    public boolean validateInput() {
        if (!question.isRequired()) {
            return true;
        }

        String value = getValue();
        boolean isValid = !value.isEmpty();

        // Visual feedback
        if (inputComponent instanceof JTextField) {
            ((JTextField) inputComponent).setBackground(
                    isValid ? Color.WHITE : new Color(255, 230, 230));
        }

        return isValid;
    }

    /// /// /// Getters /// /// ///

    public String getValue() {
        if (inputComponent instanceof JTextField) {
            return ((JTextField) inputComponent).getText().trim();
        } else if (inputComponent instanceof JScrollPane) {
            JViewport viewport = ((JScrollPane) inputComponent).getViewport();
            if (viewport.getView() instanceof JTextArea) {
                return ((JTextArea) viewport.getView()).getText().trim();
            }
        } else if (inputComponent instanceof JPanel) {
            // File chooser panel
            for (Component comp : ((JPanel) inputComponent).getComponents()) {
                if (comp instanceof JTextField) {
                    return ((JTextField) comp).getText().trim();
                }
            }
        } else if (inputComponent instanceof JComboBox) {
            return ((JComboBox<?>) inputComponent).getSelectedItem().toString();
        } else if (inputComponent instanceof JCheckBox) {
            return Boolean.toString(((JCheckBox) inputComponent).isSelected());
        }
        return "";
    }

    public String getKey() {
        return question.getKey();
    }

    /**
     * For auto-populating answer data.
     * 
     * @return
     */
    public JComponent getInputComponent() {
        return inputComponent;
    }

    /**
     * Set the value displayed in this card. Handles different component types.
     */
    public void setValue(String value) {
        if (value == null)
            return;

        if (inputComponent instanceof JTextField) {
            ((JTextField) inputComponent).setText(value);

        } else if (inputComponent instanceof JScrollPane) {
            JViewport viewport = ((JScrollPane) inputComponent).getViewport();
            if (viewport.getView() instanceof JTextArea) {
                ((JTextArea) viewport.getView()).setText(value);
            }

        } else if (inputComponent instanceof JPanel) {
            // File chooser panel - find the text field
            for (Component comp : ((JPanel) inputComponent).getComponents()) {
                if (comp instanceof JTextField) {
                    ((JTextField) comp).setText(value);
                    break;
                }
            }

        } else if (inputComponent instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) inputComponent;
            // Try to select matching item
            for (int i = 0; i < combo.getItemCount(); i++) {
                if (combo.getItemAt(i).toString().equals(value)) {
                    combo.setSelectedIndex(i);
                    return;
                }
            }
            // If not found, try to add it
            try {
                setComboBoxValue(combo, value);
                combo.setSelectedItem(value);
            } catch (Exception e) {
                // Fallback to first item
                if (combo.getItemCount() > 0) {
                    combo.setSelectedIndex(0);
                }
            }

        } else if (inputComponent instanceof JCheckBox) {
            JCheckBox checkBox = (JCheckBox) inputComponent;
            // Handle various truthy strings
            String lowerValue = value.toLowerCase();
            checkBox.setSelected(
                    lowerValue.equals("true") ||
                            lowerValue.equals("yes") ||
                            lowerValue.equals("on") ||
                            lowerValue.equals("1") ||
                            lowerValue.equals("enabled"));
        }
    } // setValue()

    /**
     * Clear the input field.
     */
    public void clear() {
        setValue("");
        if (inputComponent instanceof JCheckBox) {
            ((JCheckBox) inputComponent).setSelected(false);
        }
    }

    /**
     * Set combobox value (select matching item).
     */
    private void setComboBoxValue(JComboBox<?> combo, String value) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            if (item.toString().equalsIgnoreCase(value)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        // If not found, add it or select first
        if (combo.getItemCount() > 0) {
            combo.setSelectedIndex(0);
        }
    }
} // Class