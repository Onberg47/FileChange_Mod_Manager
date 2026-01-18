/**
 * Author Stephanos B
 * Date 8/01/2026
 */
package gui.components;

import gui.forms.FormQuestion;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.util.ArrayList;

/**
 * Displays a FormQuestion instance graphically.
 * 
 * @author Stephanos B
 * @since v2
 */
public class QuestionCard extends JPanel {
    private final FormQuestion question;
    private JComponent inputComponent;

    public QuestionCard(FormQuestion question) {
        this.question = question;
        initComponents();
    }

        private void initComponents() {
        // Use BorderLayout
        setLayout(new BorderLayout(15, 0));
        
        // Left: Label with required indicator
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel label = new JLabel(question.getLabel());
        
        if (question.isRequired()) {
            label.setText(label.getText() + " *");
            label.setForeground(new Color(200, 0, 0));
        }
        
        if (!question.getTooltip().isEmpty()) {
            label.setToolTipText(question.getTooltip());
        }
        
        labelPanel.add(label);
        add(labelPanel, BorderLayout.WEST);
        
        // Right: Input component with proper sizing
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputComponent = createInputComponent();
        configureInputSizing(inputComponent);
        inputPanel.add(inputComponent, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.CENTER);
        
        // Set preferred size based on input type
        setPreferredSize(getPreferredCardSize());
    }
    
    private Dimension getPreferredCardSize() {
        int height;
        switch (question.getType()) {
            case TEXT_AREA:
                height = 100; // Initial height for text areas
                break;
            case FILE_CHOOSER:
            case DIRECTORY_CHOOSER:
                height = 40;
                break;
            default:
                height = 35; // Standard height for other inputs
        }
        return new Dimension(600, height); // Width will be stretched
    }
    
    private void configureInputSizing(JComponent input) {
        // Let the input component decide its own preferred size
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        if (input instanceof JScrollPane && 
            ((JScrollPane) input).getViewport().getView() instanceof JTextArea) {
            JTextArea textArea = (JTextArea) ((JScrollPane) input).getViewport().getView();
            textArea.setRows(3);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            
            JScrollPane scrollPane = (JScrollPane) input;
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        }
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

        // Add drag-and-drop support
        pathField.setTransferHandler(new FileTransferHandler(pathField));
        pathField.setDragEnabled(true);
        pathField.setToolTipText("Drop something here!");

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

    /// /// /// Layout /// /// ///


    
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

    /// /// /// File Transfer /// /// ///

    /**
     * Custom TransferHandler for file/directory dropping.
     */
    private class FileTransferHandler extends TransferHandler {
        private final JTextField textField;

        public FileTransferHandler(JTextField textField) {
            this.textField = textField;
        }

        @Override
        public boolean canImport(TransferSupport support) {
            // Check if it's a file transfer
            if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false;
            }

            // Check if we're dropping into the correct component
            return support.getComponent() == textField;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            try {
                // Get the dropped files
                ArrayList<File> files = new ArrayList<File>();
                files = (ArrayList<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                if (files.isEmpty()) {
                    return false;
                }

                File file = files.get(0); // Take the first file/directory

                // Handle based on question type
                if (question.getType() == FormQuestion.QuestionType.DIRECTORY_CHOOSER) {
                    // For directory chooser, only accept directories
                    if (file.isDirectory()) {
                        textField.setText(file.getAbsolutePath());
                    } else {
                        // If it's a file, use its parent directory
                        textField.setText(file.getParentFile().getAbsolutePath());
                    }
                } else {
                    // For file chooser, accept files
                    textField.setText(file.getAbsolutePath());
                }

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }
    } // FileTransferHandler

} // Class