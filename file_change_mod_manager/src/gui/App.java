/*
 * Author Stephanos B
 * Date: 8/01/2026
 */
package gui;

import gui.components.ConsolePopup;
import gui.components.HelpPopup;
import gui.navigator.AppNavigator;
import gui.navigator.ViewFactory;
import gui.state.AppState;
import gui.util.GUIUtils;
import gui.util.IconLoader;
import gui.util.IconLoader.ICONS;

import javax.swing.*;

import core.utils.Logger;
import core.utils.TrashUtil;

import java.awt.*;
import java.time.LocalDate;

/**
 * Main GUI application entry point.
 */
public class App {
    private JFrame mainFrame;
    private AppNavigator navigator;
    private ConsolePopup console;

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

        console = new ConsolePopup(mainFrame);

        // Load initial state
        AppState.getInstance(); // Initialize

    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        exitItem.setIcon(IconLoader.loadResourceIcon(ICONS.EXIT, new Dimension(20, 20)));
        fileMenu.add(exitItem);

        JMenuItem emptyTrashItem = new JMenuItem("Empty Trash");
        emptyTrashItem.addActionListener(e -> quickCleanTrash());
        emptyTrashItem.setToolTipText(
                "Shortcut to clean trash to within 30 days and under 2Gib (go to settings for more control)");
        emptyTrashItem.setIcon(IconLoader.loadResourceIcon(ICONS.TRASH, new Dimension(20, 20)));
        fileMenu.add(emptyTrashItem);

        // Navigation menu
        JMenu navMenu = new JMenu("Navigation");
        JMenuItem backItem = new JMenuItem("Back");
        backItem.addActionListener(e -> navigator.goBack());
        backItem.setIcon(IconLoader.loadResourceIcon(ICONS.BACK, new Dimension(20, 20)));
        navMenu.add(backItem);

        // Settings menu
        JMenu settingsMenu = new JMenu("Settings");
        JMenuItem settingsItem = new JMenuItem("Open Settings");
        settingsItem.addActionListener(e -> navigator.navigateTo("settings"));
        settingsItem.setIcon(IconLoader.loadResourceIcon(ICONS.SETTINGS, new Dimension(20, 20)));
        settingsMenu.add(settingsItem);

        JMenu helpMenu = new JMenu("Help");

        JMenuItem helpItem = new JMenuItem("Get help");
        helpItem.addActionListener(e -> new HelpPopup(mainFrame, navigator.getViewId()).show());
        helpItem.setIcon(IconLoader.loadResourceIcon(ICONS.HELP, new Dimension(22, 22)));

        JMenuItem helpBrowserItem = new JMenuItem("Help in Browser");
        helpBrowserItem.setIcon(IconLoader.loadResourceIcon(ICONS.REDIRECT, new Dimension(20, 20)));
        helpBrowserItem.addActionListener(e -> {
            HelpPopup.openHelpInBrowser();
        });
        helpMenu.add(helpItem);
        helpMenu.addSeparator();
        helpMenu.add(helpBrowserItem);
        menuBar.add(helpMenu);

        menuBar.add(fileMenu);
        menuBar.add(navMenu);
        menuBar.add(settingsMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    ///

    private void quickCleanTrash() {
        int result = JOptionPane.showConfirmDialog(
                mainFrame,
                "Permanently delete all files in trash older than 30 days?",
                "Clean Trash",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            // Call your consolePopup or show progress
            try {
                console = ConsolePopup.getInstance(mainFrame);
                console.show();
                console.setBusy();
                TrashUtil.cleanTrash(2000, LocalDate.now().minusDays(30));
                console.setDone();

            } catch (Exception f) {
                Logger.getInstance().logError("Failed to clean", f);
            }
        }
    }

    ///

    public static void main(String[] args) {
        // Run on EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            new App().start();
        });
    }
} // Class