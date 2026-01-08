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
        /// Games
        navigator.registerView("library", params -> new GameLibraryView(navigator, params));
        navigator.registerView("addGame", params -> new AddGameView(navigator, params));
        navigator.registerView("editGame", params -> new EditGameView(navigator, params));

        /// Mods
        //navigator.registerView("modManager", params -> new ModManagerView(navigator, params));
        //navigator.registerView("editMod", params -> new EditModView(navigator, params));
        //navigator.registerView("compileMod", params -> new CompileModView(navigator, params));

        /// Other
        //navigator.registerView("settings", params -> new SettingsView(navigator, params));

        // Add more views as needed
    }

    /**
     * Create initial view for app startup.
     */
    public JPanel createInitialView() {
        return new GameLibraryView(navigator, new java.util.HashMap<>());
    }
} // Class