/**
 * Author Stephanos B
 * Date 14/01/2026
 */
package gui.components;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.*;

/**
 * Creates a popup window that listens to the console.
 * 
 * @author Stephanos B
 * @since v3
 */
public class ConsolePopup {
    private JTextArea consoleArea;
    private JScrollPane scrollPane;
    private JFrame frame;
    private JFrame parentFrame; // Store parent reference
    private JButton closeButton;

    public ConsolePopup(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setupGUI();
        redirectSystemOut();
    }

    private void setupGUI() {
        frame = new JFrame("Installation Progress");
        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        // Use a font that supports emojis
        Font font = new Font("Monospaced", Font.PLAIN, 12);
        consoleArea.setFont(font);

        scrollPane = new JScrollPane(consoleArea);

        // Create close button
        closeButton = new JButton("Busy");
        closeButton.setEnabled(false);
        closeButton.addActionListener(e -> frame.dispose());

        // Create a panel for the button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);

        // Create main layout
        BorderLayout layout = new BorderLayout();
        frame.setLayout(layout);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Set popup size relative to parent
        if (parentFrame != null) {
            Dimension parentSize = parentFrame.getSize();
            int width = Math.min(1200, (int) (parentSize.width * 0.8));
            int height = Math.min(800, (int) (parentSize.height * 0.8));
            frame.setSize(width, height);
            frame.setLocationRelativeTo(parentFrame);
        } else {
            frame.setSize(1200, 800);
        }
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    // Courtesy of Qwen3.
    private void redirectSystemOut() {
        // Create a custom PrintStream that writes to both System.out and your GUI
        PrintStream consoleStream = new PrintStream(new OutputStream() {
            private StringBuilder buffer = new StringBuilder();

            @Override
            public void write(int b) throws IOException {
                // Handle single byte
                buffer.append((char) b);
                flushIfNeeded();
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                // Handle byte array
                buffer.append(new String(b, off, len, "UTF-8"));
                flushIfNeeded();
            }

            private void flushIfNeeded() {
                // Only update UI when we have enough content or encounter newline
                if (buffer.length() > 0) {
                    SwingUtilities.invokeLater(() -> {
                        consoleArea.append(buffer.toString());
                        consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
                        buffer.setLength(0); // Clear buffer
                    });
                }
            }

            @Override
            public void flush() throws IOException {
                if (buffer.length() > 0) {
                    SwingUtilities.invokeLater(() -> {
                        consoleArea.append(buffer.toString());
                        consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
                        buffer.setLength(0);
                    });
                }
            }
        });

        // Redirect System.out and System.err
        System.setOut(consoleStream);
        System.setErr(consoleStream);
    }

    public void show() {
        frame.setVisible(true);
    }

    public void hide() {
        frame.setVisible(false);
    }

    public void clear() {
        SwingUtilities.invokeLater(() -> {
            consoleArea.setText("");
        });
    }

    // Operations enable this once they are complete.
    public void setDone() {
        SwingUtilities.invokeLater(() -> {
            closeButton.setText("Done");
            closeButton.setEnabled(true);
        });
    }

    // Method to reset button state (if needed)
    public void setBusy() {
        SwingUtilities.invokeLater(() -> {
            closeButton.setText("Busy");
            closeButton.setEnabled(false);
        });
    }
} // Class