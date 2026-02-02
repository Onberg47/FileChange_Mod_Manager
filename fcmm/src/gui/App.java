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

import core.config.AppConfig;
import core.config.AppPreferences.properties;
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

    public void start() {
        // Set look and feel
        GUIUtils.setLookAndFeel();

        // Create main window
        mainFrame = new JFrame("Mod Manager");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setPreferredSize(new Dimension(1200, 800));
        mainFrame.setIconImage(IconLoader.loadIconImage(ICONS.LOGO, new Dimension(40, 40)));

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
        exitItem.setIcon(IconLoader.loadIcon(ICONS.EXIT, new Dimension(20, 20)));
        fileMenu.add(exitItem);

        // Navigation menu
        JMenu navMenu = new JMenu("Navigation");
        JMenuItem backItem = new JMenuItem("Back");
        backItem.addActionListener(e -> navigator.goBack());
        backItem.setIcon(IconLoader.loadIcon(ICONS.BACK, new Dimension(20, 20)));
        navMenu.add(backItem);

        // Settings menu
        JMenu settingsMenu = new JMenu("Settings");
        JMenuItem settingsItem = new JMenuItem("Open Settings");
        settingsItem.addActionListener(e -> navigator.navigateTo("settings"));
        settingsItem.setIcon(IconLoader.loadIcon(ICONS.SETTINGS, new Dimension(20, 20)));
        settingsMenu.add(settingsItem);

        JMenuItem emptyTrashItem = new JMenuItem("Empty Trash");
        emptyTrashItem.addActionListener(e -> quickCleanTrash());
        emptyTrashItem.setToolTipText(
                "Shortcut to clean trash using last saved settings");
        emptyTrashItem.setIcon(IconLoader.loadIcon(ICONS.TRASH, new Dimension(20, 20)));
        settingsMenu.addSeparator();
        settingsMenu.add(emptyTrashItem);

        JMenu helpMenu = new JMenu("Help");

        JMenuItem helpItem = new JMenuItem("Get help");
        helpItem.addActionListener(e -> new HelpPopup(mainFrame, navigator.getViewId()).show());
        helpItem.setIcon(IconLoader.loadIcon(ICONS.HELP, new Dimension(22, 22)));

        JMenuItem helpBrowserItem = new JMenuItem("Help in Browser");
        helpBrowserItem.setIcon(IconLoader.loadIcon(ICONS.REDIRECT, new Dimension(20, 20)));
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
        int daysOld = AppConfig.getInstance().preferences.getAsInt(properties.TRASH_DAYS_OLD);
        int trashLimit = AppConfig.getInstance().preferences.getAsInt(properties.TRASH_SIZE_LIMIT);

        int result = JOptionPane.showConfirmDialog(
                mainFrame,
                "Permanently delete all files in trash older than "
                        + daysOld
                        + " days and trim to under "
                        + trashLimit
                        + "MB?",
                "Clean Trash",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            // Call your consolePopup or show progress
            try {
                ConsolePopup console = ConsolePopup.getInstance(mainFrame);
                console.show();

                SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                    @Override
                    protected Void doInBackground() throws Exception { // long-running task
                        TrashUtil.cleanTrash(trashLimit, LocalDate.now().minusDays(daysOld));
                        return null;
                    }

                    @Override
                    protected void done() { // Task completed - update GUI state
                        console.setDone();
                    }
                };
                worker.execute();

            } catch (Exception f) {
                Logger.getInstance().error("Failed to clean", f);
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