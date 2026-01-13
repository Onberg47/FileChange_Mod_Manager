/*
 * Author Stephanos B
 * Date: 8/01/2026
 */
package gui.state;

import core.objects.Game;
import core.objects.Mod;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AppState {
    private static AppState instance;

    private Game currentGame;
    private Mod currentMod;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    // === Current Game Management ===

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

    // === Property Change Support ===

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(property, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    // Singleton pattern
    private AppState() {
    }

    public static synchronized AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }
} // Class