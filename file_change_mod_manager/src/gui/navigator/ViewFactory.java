// src/gui/navigator/ViewFactory.java
package gui.navigator;

import gui.views.*;
import javax.swing.*;
import java.util.Map;

/**
 * Factory for creating views. Decouples navigator from view implementations.
 */
public class ViewFactory {

    @FunctionalInterface
    public interface ViewCreator {
        JPanel create(Map<String, Object> params);
    }

    private final AppNavigator navigator;

    public ViewFactory(AppNavigator navigator) {
        this.navigator = navigator;
    }

    /**
     * Register all application views with the navigator.
     */
    public void registerAllViews() {
        navigator.registerView("library", params -> new GameLibraryView(navigator, params));

        navigator.registerView("modManager", params -> new ModManagerView(navigator, params));

        navigator.registerView("settings", params -> new SettingsView(navigator, params));

        // Add more views as needed
    }

    /**
     * Create initial view for app startup.
     */
    public JPanel createInitialView() {
        return new GameLibraryView(navigator, new java.util.HashMap<>());
    }
} // Class