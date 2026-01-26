/*
 * Author Stephanos B
 * Date: 8/01/2026
 */
package gui.state;

import core.objects.Game;
import core.objects.Mod;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.file.Path;

/**
 * GUI state instance.
 * 
 * @since v2.0
 */
public class AppState {
    private static AppState instance;

    // Essential
    private Game currentGame;
    private Mod currentMod;

    // Non-essential
    private Color themeColor; // for dynamic themes (not in use)
    private Path lastPickedDir = null; // For file/directory pickers to remember the last picked directory.

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /// /// /// Getters and Setters /// /// ///

    public Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game game) {
        Game old = this.currentGame;
        this.currentGame = game;
        pcs.firePropertyChange("currentGame", old, game);
    }

    public Mod getCurrentMod() {
        return currentMod;
    }

    public void setCurrentMod(Mod mod) {
        Mod old = this.currentMod;
        this.currentMod = mod;
        pcs.firePropertyChange("currentMod", old, mod);
    }

    /// Non-essnetial

    public Color getThemeColor() {
        return themeColor;
    }

    public void setThemeColor(Color colorPalette) {
        Color old = this.themeColor;
        this.themeColor = colorPalette;
        pcs.firePropertyChange("colorPalette", old, colorPalette);
    }

    public Path getLastPickedDir() {
        return lastPickedDir;
    }

    public void setLastPickedDir(Path lastPickedDir) {
        Path old = this.lastPickedDir;
        this.lastPickedDir = lastPickedDir;
        pcs.firePropertyChange("lastPickedDir", old, lastPickedDir);
    }

    /// /// /// Property Change Support /// /// ///

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(property, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    /// /// /// Singleton pattern /// /// ///
    private AppState() {
    }

    public static synchronized AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }
} // Class