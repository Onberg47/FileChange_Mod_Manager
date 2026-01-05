
package gui.state;

import core.objects.Game;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.file.Path;
import java.util.prefs.Preferences;

/**
 * Global application state. Observable for UI updates.
 */
public class AppState {
    private static AppState instance;

    private Game currentGame;
    private Path lastUsedDirectory;
    private String theme = "dark";
    private boolean verboseLogging = false;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final Preferences prefs = Preferences.userNodeForPackage(AppState.class);

    private AppState() {
        loadPreferences();
    }

    public static synchronized AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }

    // === Current Game ===
    public Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game game) {
        Game old = this.currentGame;
        this.currentGame = game;
        pcs.firePropertyChange("currentGame", old, game);
        savePreferences();
    }

    // === Theme ===
    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        String old = this.theme;
        this.theme = theme;
        pcs.firePropertyChange("theme", old, theme);
        savePreferences();
    }

    // === Last Used Directory ===
    public Path getLastUsedDirectory() {
        return lastUsedDirectory;
    }

    public void setLastUsedDirectory(Path path) {
        Path old = this.lastUsedDirectory;
        this.lastUsedDirectory = path;
        pcs.firePropertyChange("lastUsedDirectory", old, path);
        savePreferences();
    }

    // === Property Change Support ===
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    // === Preferences ===
    private void loadPreferences() {
        theme = prefs.get("theme", "dark");
        String lastDir = prefs.get("lastUsedDirectory", null);
        if (lastDir != null) {
            lastUsedDirectory = Path.of(lastDir);
        }
        verboseLogging = prefs.getBoolean("verboseLogging", false);
    }

    private void savePreferences() {
        prefs.put("theme", theme);
        if (lastUsedDirectory != null) {
            prefs.put("lastUsedDirectory", lastUsedDirectory.toString());
        }
        prefs.putBoolean("verboseLogging", verboseLogging);
    }
} // Class