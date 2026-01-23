/**
 * Author Stephanos B
 * Date 23/01/2026
 */
package gui.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * @since v4.0.2
 */
public class ToastNotification {
    private static final int DISPLAY_TIME = 1000; // 1 seconds
    private static final int FADE_DURATION = 32;

    public static void showNotification(JFrame parent, String message) {
        JWindow toast = new JWindow(parent);
        toast.setAlwaysOnTop(true);

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        label.setBackground(new Color(255, 255, 220)); // Light yellow
        label.setOpaque(true);

        toast.add(label);
        toast.pack();

        // Position at bottom right
        Point parentLocation = parent.getLocationOnScreen();
        Dimension parentSize = parent.getSize();
        Dimension toastSize = toast.getSize();

        toast.setLocation(
                parentLocation.x + parentSize.width - toastSize.width - 10,
                parentLocation.y + parentSize.height - toastSize.height - 10);

        toast.setVisible(true);

        new Timer(DISPLAY_TIME, e -> {

            // Fade begins
            new Timer(FADE_DURATION, f -> {
                float currentOpacity = toast.getOpacity();
                if (currentOpacity > 0.05f) {
                    toast.setOpacity(currentOpacity - 0.05f);
                } else
                    toast.dispose();
            }).start();
        }).start();
    }

} // Class