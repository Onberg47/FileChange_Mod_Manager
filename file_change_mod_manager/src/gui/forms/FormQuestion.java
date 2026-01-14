/**
 * Author Stephanos B
 * Date 8/01/2026
 */
package gui.forms;

/**
 * Object for storing Form questions for a reusable form mad of Questions.
 * 
 * @author Stephanos B
 */
public class FormQuestion {
    private final String key;
    private final String label;
    private final String tooltip;
    private boolean required; // not final for cntextual changes.
    private final QuestionType type;
    private final Object defaultValue;
    private boolean enabled = true;

    public enum QuestionType {
        TEXT_FIELD,
        TEXT_AREA,
        FILE_CHOOSER, // For paths
        DIRECTORY_CHOOSER,
        COMBO_BOX, // For dropdowns
        CHECKBOX
    }

    public FormQuestion(String key, String label, String tooltip,
            boolean required, QuestionType type, Object defaultValue, boolean enabled) {
        this.key = key;
        this.label = label;
        this.tooltip = tooltip;
        this.required = required;
        this.type = type;
        this.defaultValue = defaultValue;
        this.enabled = enabled;
    }

    /// /// /// Getters and Setters /// /// ///

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public String getTooltip() {
        return tooltip;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public QuestionType getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /// /// /// Builder /// /// ///

    // Builder pattern for easy creation
    public static Builder builder(String key, String label) {
        return new Builder(key, label);
    }

    public static class Builder {
        private final String key;
        private final String label;
        private String tooltip = "";
        private boolean required = false;
        private QuestionType type = QuestionType.TEXT_FIELD;
        private Object defaultValue = null;
        private boolean enabled = true;

        public Builder(String key, String label) {
            this.key = key;
            this.label = label;
        }

        public Builder tooltip(String tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder required() {
            this.required = true;
            return this;
        }

        public Builder type(QuestionType type) {
            this.type = type;
            return this;
        }

        public Builder defaultValue(Object value) {
            this.defaultValue = value;
            return this;
        }

        public Builder disabled() {
            this.enabled = false;
            return this;
        }

        public FormQuestion build() {
            return new FormQuestion(key, label, tooltip, required, type, defaultValue, enabled);
        }
    } // Builder
} // Class