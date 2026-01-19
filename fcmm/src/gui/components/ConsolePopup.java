/**
 * Author Stephanos B
 * Date 14/01/2026
 */
package gui.components;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

import javax.swing.*;

import core.utils.Logger;
import gui.util.ConsoleRedirector;

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

    /// /// /// Singleton pattern /// /// ///

    private static ConsolePopup instance;

    public static synchronized ConsolePopup getInstance(JFrame parentFrame) {
        if (instance == null) {
            instance = new ConsolePopup(parentFrame);
        }

        if (!instance.isVisible())
            instance = new ConsolePopup(parentFrame);

        return instance;
    }

    public ConsolePopup(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setupGUI();
        redirectSystemOut();
        instance = this;
    }

    /// /// ///

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

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearConsole());

        JButton copyButton = new JButton("Copy");
        copyButton.addActionListener(e -> copyToClipboard());

        // Create a panel for the button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(clearButton);

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
        PrintStream consoleStream;
        try {
            consoleStream = new PrintStream(new OutputStream() {
                private final StringBuilder buffer = new StringBuilder();
                private volatile boolean scheduled = false;

                @Override
                public void write(int b) throws IOException {
                    buffer.append((char) b);
                    scheduleFlush();
                }

                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    buffer.append(new String(b, off, len, StandardCharsets.UTF_8));
                    scheduleFlush();
                }

                private synchronized void scheduleFlush() {
                    // Only schedule once if not already scheduled
                    if (!scheduled && buffer.length() > 0) {
                        scheduled = true;
                        SwingUtilities.invokeLater(this::flushToGUI);
                    }
                }

                private void flushToGUI() {
                    synchronized (this) {
                        if (buffer.length() == 0) {
                            scheduled = false;
                            return;
                        }

                        String textToAppend = buffer.toString();
                        buffer.setLength(0);
                        scheduled = false;

                        // Now update the GUI
                        consoleArea.append(textToAppend);

                        // Scroll to bottom (but not on every update for performance)
                        if (textToAppend.contains("\n")) {
                            consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
                        }
                    }
                }

                @Override
                public void flush() throws IOException {
                    // Force immediate flush
                    try {
                        SwingUtilities.invokeAndWait(() -> {
                            synchronized (this) {
                                if (buffer.length() > 0) {
                                    consoleArea.append(buffer.toString());
                                    consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
                                    buffer.setLength(0);
                                    scheduled = false;
                                }
                            }
                        });
                    } catch (InvocationTargetException | InterruptedException e) {
                        Logger.getInstance().error("Failed to flush stream", e);
                    }
                }
            }, true, StandardCharsets.UTF_8.name());

            // Redirect System.out and System.err
            System.setOut(consoleStream);
            System.setErr(consoleStream);

        } catch (UnsupportedEncodingException e) {
            Logger.getInstance().error("Failed to create console stream", e);
        }
    }

    /// /// ///

    private void clearConsole() {
        ConsoleRedirector.getInstance().clear();
    }
    
    private void copyToClipboard() {
        String text = ConsoleRedirector.getInstance().getText();
        Toolkit.getDefaultToolkit().getSystemClipboard()
            .setContents(new StringSelection(text), null);
        JOptionPane.showMessageDialog(parentFrame, "Console content copied to clipboard");
    }

    ///

    public void show() {
        frame.setVisible(true);
    }

    public boolean isVisible() {
        return frame.isVisible();
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