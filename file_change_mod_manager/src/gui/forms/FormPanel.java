/**
 * Author Stephanos B
 * Date 8/01/2026
 */
package gui.forms;

import gui.components.QuestionCard;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * A standard panel that implements FormCards to display modular questions and
 * retreive answers.
 * 
 * @author Stephanos B
 */
public class FormPanel extends JPanel {
    private final List<QuestionCard> questionCards = new ArrayList<>();
    private final Map<String, Object> answers = new HashMap<>();

    public FormPanel(List<FormQuestion> questions) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (FormQuestion question : questions) {
            QuestionCard card = new QuestionCard(question);
            questionCards.add(card);
            add(card);
            add(Box.createRigidArea(new Dimension(0, 10))); // Spacing
        }
    }

    /**
     * Empty/Blank fields are not added, the key will not be present.
     * 
     * @return Map<String, String> of answers to each question-key.
     */
    public Map<String, Object> getAnswers() {
        answers.clear();
        for (QuestionCard card : questionCards) {
            if (!card.getValue().isBlank())
                answers.put(card.getKey(), card.getValue());
        }
        return new HashMap<>(answers);
    }

    public boolean validateForm() {
        boolean allValid = true;
        for (QuestionCard card : questionCards) {
            if (!card.validateInput()) {
                allValid = false;
            }
        }
        return allValid;
    }

    /**
     * Populate form with existing values.
     * Handles all question types appropriately.
     */
    public void setAnswers(Map<String, String> values) {
        for (QuestionCard card : questionCards) {
            try {
                String key = card.getKey();
                if (values.containsKey(key)) {
                    String value = values.get(key);
                    if (value != null) {
                        card.setValue(value);
                    } else {
                        card.clear();
                    }
                } else {
                    card.clear();
                }
            } catch (Exception e) {
                // Log but don't crash
                System.err.println("Failed to set value for " + card.getKey() + ": " + e.getMessage());
                card.clear();
            }
        }
    }

} // Class