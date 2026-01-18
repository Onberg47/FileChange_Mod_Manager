/**
 * Date 17/01/2026
 */
package gui.layouts;

import java.awt.*;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import gui.components.QuestionCard;

/**
 * Layout manager for form cards that:
 * - Keeps cards at preferred height
 * - Allows text areas to expand with content (up to max)
 * - Horizontally stretches input fields
 * - Leaves empty space at bottom when needed
 */
public class FormCardLayout implements LayoutManager2 {
    private final int vGap; // Vertical gap between cards
    private final int maxTextHeight; // Maximum height for text areas before scrolling

    public FormCardLayout(int vGap, int maxTextHeight) {
        this.vGap = vGap;
        this.maxTextHeight = maxTextHeight;
    }

    public FormCardLayout() {
        this(10, 200); // Default: 10px gap, max 200px for text areas
    }

    @Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int availableWidth = parent.getWidth() - insets.left - insets.right;
            int x = insets.left;
            int y = insets.top;

            for (Component comp : parent.getComponents()) {
                if (!comp.isVisible())
                    continue;

                Dimension pref = comp.getPreferredSize();
                int height = pref.height;

                // Special handling for text areas - they can grow with content
                if (hasTextArea(comp)) {
                    // Get the actual text area component
                    JTextArea textArea = findTextArea(comp);
                    if (textArea != null) {
                        // Calculate preferred height based on content
                        int contentHeight = textArea.getPreferredSize().height;
                        // Clamp between minimum and maximum
                        height = Math.min(Math.max(pref.height, contentHeight), maxTextHeight);
                    }
                }

                // Set bounds: full width, calculated height
                comp.setBounds(x, y, availableWidth, height);
                y += height + vGap;
            }
        }
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return calculateSize(parent, false);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return calculateSize(parent, true);
    }

    @Override
    public Dimension maximumLayoutSize(Container parent) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0.5f;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0.5f;
    }

    @Override
    public void invalidateLayout(Container target) {
    }

    private Dimension calculateSize(Container parent, boolean minimum) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int width = 0;
            int height = 0;

            for (Component comp : parent.getComponents()) {
                if (!comp.isVisible())
                    continue;

                Dimension size = minimum ? comp.getMinimumSize() : comp.getPreferredSize();
                width = Math.max(width, size.width);
                height += size.height;

                // Add gap after each component except the last
                if (comp != parent.getComponents()[parent.getComponentCount() - 1]) {
                    height += vGap;
                }
            }

            return new Dimension(
                    width + insets.left + insets.right,
                    height + insets.top + insets.bottom);
        }
    }

    private boolean hasTextArea(Component comp) {
        if (comp instanceof QuestionCard) {
            JComponent input = ((QuestionCard) comp).getInputComponent();
            return input instanceof JScrollPane &&
                    ((JScrollPane) input).getViewport().getView() instanceof JTextArea;
        }
        return false;
    }

    private JTextArea findTextArea(Component comp) {
        if (comp instanceof QuestionCard) {
            JComponent input = ((QuestionCard) comp).getInputComponent();
            if (input instanceof JScrollPane) {
                Component view = ((JScrollPane) input).getViewport().getView();
                if (view instanceof JTextArea) {
                    return (JTextArea) view;
                }
            }
        }
        return null;
    }
}