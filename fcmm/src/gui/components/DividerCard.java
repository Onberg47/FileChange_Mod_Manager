/**
 * Author Stephanos B
 * Date 11/01/2026
 */
package gui.components;

import javax.swing.*;
import java.awt.*;

/**
 * A Divider Card for the ModList ScrollPane. These act as headings and interact
 * with the drag-to-order ModCards.
 * 
 * @author Stephanos B
 * @since v3
 */
public class DividerCard extends JPanel {
    private final String title;
    private final Color highlightColor;

    public DividerCard(String title, Color highlightColor) {
        this.title = title;
        this.highlightColor = highlightColor;
        initComponents();
    }

    private void initComponents() {
        JLabel titleLabel = new JLabel(title);

        // Style the label
        titleLabel.setFont(new Font("Noto Sans", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(highlightColor, 6),
                        BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED))));
        titleLabel.setToolTipText(title);

        // Panel setup
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        setPreferredSize(new Dimension(1000, 50));
        setMinimumSize(new Dimension(100, 40));
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        setBackground(new Color(245, 245, 245));

        // Layout
        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.CENTER);
    }

    // Factory methods for common dividers
    public static DividerCard createEnabledDivider() {
        return new DividerCard("Enabled Mods", new Color(0, 150, 0));
    }

    public static DividerCard createDisabledDivider() {
        return new DividerCard("Disabled Mods", Color.RED);
    }

    public static DividerCard createStoredDivider() {
        return new DividerCard("Available Mods (Not Installed)", new Color(70, 130, 180));
    }

    // Getter for draggging logic.
    public String getTitle() {
        return this.title;
    }
} // Class