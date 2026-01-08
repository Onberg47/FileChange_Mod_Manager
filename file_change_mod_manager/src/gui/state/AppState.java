package gui.state;

import core.objects.Game;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AppState {
    private static AppState instance;

    private Game currentGame;

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