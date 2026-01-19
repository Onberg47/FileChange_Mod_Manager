package gui.util;

import javax.swing.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * High-performance console output redirector for Swing GUI.
 * Uses a producer-consumer pattern to avoid blocking the EDT.
 */
public class ConsoleRedirector {
    private final JTextArea consoleArea;
    private final BlockingQueue<String> messageQueue;
    private volatile boolean running;
    private Thread consumerThread;

    // Singleton instance
    private static ConsoleRedirector instance;

    /**
     * Initialize the console redirector with a text area.
     * Call this once during GUI setup.
     */
    public static void initialize(JTextArea consoleArea) {
        if (instance != null) {
            throw new IllegalStateException("ConsoleRedirector already initialized");
        }
        instance = new ConsoleRedirector(consoleArea);
    }

    /**
     * Get the singleton instance.
     */
    public static ConsoleRedirector getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ConsoleRedirector not initialized");
        }
        return instance;
    }

    /**
     * Shutdown the redirector (call on application exit).
     */
    public static void shutdown() {
        if (instance != null) {
            instance.stop();
            instance = null;
        }
    }

    private ConsoleRedirector(JTextArea consoleArea) {
        this.consoleArea = consoleArea;
        this.messageQueue = new LinkedBlockingQueue<>(1000); // Bounded queue
        this.running = true;
        startConsumer();
    }

    private void startConsumer() {
        consumerThread = new Thread(() -> {
            StringBuilder batch = new StringBuilder(4096); // 4KB buffer

            while (running || !messageQueue.isEmpty()) {
                try {
                    // Wait for first message
                    String message = messageQueue.poll(100, TimeUnit.MILLISECONDS);

                    if (message != null) {
                        batch.append(message);

                        // Try to gather more messages without waiting
                        int drained = messageQueue.drainTo(new java.util.ArrayList<>() {
                            @Override
                            public boolean add(String msg) {
                                batch.append(msg);
                                return true;
                            }
                        }, 99); // Get up to 99 more

                        // Update GUI in batches (better performance)
                        if (batch.length() > 0) {
                            final String textToAppend = batch.toString();
                            SwingUtilities.invokeLater(() -> {
                                consoleArea.append(textToAppend);

                                // Smart scrolling: only if near bottom
                                if (shouldAutoScroll()) {
                                    consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
                                }
                            });
                            batch.setLength(0); // Clear buffer
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            // Flush any remaining messages
            if (batch.length() > 0) {
                final String finalText = batch.toString();
                SwingUtilities.invokeLater(() -> {
                    consoleArea.append(finalText);
                    consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
                });
            }
        });

        consumerThread.setName("Console-Redirector-Consumer");
        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    /**
     * Smart scroll detection: only auto-scroll if user hasn't manually scrolled up.
     */
    private boolean shouldAutoScroll() {
        JScrollPane scrollPane = (JScrollPane) consoleArea.getParent().getParent();
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();

        // Check if scrollbar is at or near bottom
        int max = verticalBar.getMaximum();
        int extent = verticalBar.getVisibleAmount();
        int value = verticalBar.getValue();

        // Allow small "slop" area near bottom (50 pixels)
        return (value + extent) >= (max - 50);
    }

    /**
     * Print a message to the console (without newline).
     */
    public void print(String message) {
        if (!running)
            return;

        // Non-blocking offer (returns false if queue full)
        if (!messageQueue.offer(message)) {
            // Queue full - could log to file or ignore
            System.err.println("Console queue full, dropping message: " +
                    (message.length() > 50 ? message.substring(0, 50) + "..." : message));
        }
    }

    /**
     * Print a message to the console with newline.
     */
    public void println(String message) {
        print(message + "\n");
    }

    /**
     * Clear the console.
     */
    public void clear() {
        SwingUtilities.invokeLater(() -> {
            consoleArea.setText("");
        });
    }

    /**
     * Get the current text in the console.
     */
    public String getText() {
        return consoleArea.getText();
    }

    /**
     * Stop the redirector and cleanup.
     */
    private void stop() {
        running = false;
        if (consumerThread != null) {
            consumerThread.interrupt();
            try {
                consumerThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
} // Class