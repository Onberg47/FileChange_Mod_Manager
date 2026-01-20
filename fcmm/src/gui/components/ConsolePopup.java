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

import javax.swing.*;

import gui.util.IconLoader;
import gui.util.IconLoader.ICONS;

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

    private static PrintStream originalOut = System.out;
    private static PrintStream originalErr = System.err;
    private static boolean isRedirecting = false;
    private static ConsolePopup instance = null;

    /// /// /// Singleton pattern /// /// ///

    public static synchronized ConsolePopup getInstance(JFrame parentFrame) {
        if (instance == null) {
            instance = new ConsolePopup(parentFrame);
        } else if (!instance.isVisible()) {
            // If instance exists but is not visible, recreate it
            instance.dispose();
            instance = new ConsolePopup(parentFrame);
        }
        return instance;
    }

    public static synchronized ConsolePopup getInstance() {
        return instance;
    }

    public ConsolePopup(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setupGUI();
        if (!isRedirecting) {
            redirectSystemOut();
        }
        instance = this;
    }

    /// /// ///

    private void setupGUI() {
        frame = new JFrame("FCMM Console");
        frame.setIconImage(parentFrame.getIconImage());

        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        // Use a font that supports emojis
        Font font = new Font("Monospaced", Font.PLAIN, 12);
        consoleArea.setFont(font);

        scrollPane = new JScrollPane(consoleArea);

        // Create close button
        closeButton = new JButton("Busy");
        this.setButtonBusy();
        closeButton.addActionListener(e -> frame.dispose());

        JButton copyButton = new JButton("Copy");
        copyButton.setIcon(IconLoader.loadIcon(ICONS.COPY_ALL, new Dimension(16, 16)));
        copyButton.setToolTipText("Copy all to clipboard");
        copyButton.addActionListener(e -> copyToClipboard());

        // Create a panel for the button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        buttonPanel.add(copyButton);

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

        // Add window listener to properly dispose when closed
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                dispose();
            }
        });
    }

    private void setButtonBusy() {
        closeButton.setText("Busy");
        closeButton.setIcon(IconLoader.loadIcon(ICONS.BUSY, new Dimension(24, 24)));
        closeButton.setToolTipText(null);
        closeButton.setEnabled(false);
    }

    /// /// /// Logic /// /// ///

    // Courtesy of DeepSeekV3.

    private void redirectSystemOut() {
        PrintStream consoleStream = new PrintStream(new OutputStream() {
            private final StringBuilder buffer = new StringBuilder();

            @Override
            public void write(int b) throws IOException {
                buffer.append((char) b);
                // Flush on newline or when buffer is large
                if (buffer.indexOf("\n") >= 0 || buffer.length() > 500) {
                    flushBuffer();
                }
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                buffer.append(new String(b, off, len, "UTF-8"));
                flushBuffer(); // Always flush batch writes
            }

            private void flushBuffer() {
                if (buffer.length() > 0) {
                    final String content = buffer.toString();
                    buffer.setLength(0);

                    SwingUtilities.invokeLater(() -> {
                        consoleArea.append(content);
                        consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
                    });
                }
            }

            @Override
            public void flush() throws IOException {
                flushBuffer();
            }

            @Override
            public void close() throws IOException {
                flushBuffer();
            }
        });

        System.setOut(consoleStream);
        System.setErr(consoleStream);
    }

    public void dispose() {
        if (instance == this) {
            // Restore original streams only if we're the active redirector
            if (isRedirecting) {
                System.setOut(originalOut);
                System.setErr(originalErr);
                isRedirecting = false;
            }
            if (frame != null) {
                frame.dispose();
            }
            instance = null;
        }
    }

    // Add this method to properly handle console state
    public static void ensureConsoleClosed() {
        if (instance != null) {
            instance.dispose();
        }
    }

    // Add this method to check if console is active
    public static boolean isConsoleActive() {
        return instance != null && instance.isVisible();
    }

    /// /// ///

    private void copyToClipboard() {
        String text = consoleArea.getText();
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(text), null);
        JOptionPane.showMessageDialog(parentFrame, "Console content copied to clipboard");
    }

    ///

    public void hide() {
        if (frame != null) {
            frame.setVisible(false);
        }
    }

    public void show() {
        if (frame != null) {
            frame.setVisible(true);
        }
        setBusy();
    }

    public boolean isVisible() {
        return frame.isVisible();
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
            closeButton.setIcon(IconLoader.loadIcon(ICONS.DONE, new Dimension(24, 24)));
            closeButton.setToolTipText("Click to close");
            closeButton.setEnabled(true);
        });
    }

    // Method to reset button state (if needed)
    public void setBusy() {
        SwingUtilities.invokeLater(() -> {
            this.setButtonBusy();
        });
    }

} // Class