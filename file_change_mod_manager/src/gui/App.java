/*
 * Author Stephanos B
 * Date: 8/01/2026
 */
package gui;

import gui.navigator.AppNavigator;
import gui.navigator.ViewFactory;
import gui.state.AppState;
import gui.util.GUIUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Main GUI application entry point.
 */
public class App {
    private JFrame mainFrame;
    private AppNavigator navigator;

    public void start() {
        // Set look and feel
        GUIUtils.setLookAndFeel();

        // Create main window
        mainFrame = new JFrame("Mod Manager");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setPreferredSize(new Dimension(1200, 800));

        // Initialize navigation
        navigator = new AppNavigator(mainFrame);
        ViewFactory viewFactory = new ViewFactory(navigator);
        viewFactory.registerAllViews();

        // Set up menu bar
        mainFrame.setJMenuBar(createMenuBar());

        // Show initial view
        navigator.navigateTo("library");

        // Finalize window
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null); // Center on screen
        mainFrame.setVisible(true);

        // Load initial state
        AppState.getInstance(); // Initialize
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // Navigation menu
        JMenu navMenu = new JMenu("Navigation");
        JMenuItem backItem = new JMenuItem("Back");
        backItem.addActionListener(e -> navigator.goBack());
        navMenu.add(backItem);

        // Settings menu
        JMenu settingsMenu = new JMenu("Settings");
        JMenuItem settingsItem = new JMenuItem("Open Settings");
        settingsItem.addActionListener(e -> navigator.navigateTo("settings"));
        settingsMenu.add(settingsItem);

        menuBar.add(fileMenu);
        menuBar.add(navMenu);
        menuBar.add(settingsMenu);

        return menuBar;
    }

    public static void main(String[] args) {
        // Run on EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            new App().start();
        });
    }
} // Class