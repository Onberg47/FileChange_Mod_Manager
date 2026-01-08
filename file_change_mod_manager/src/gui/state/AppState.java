package gui.state;

import core.objects.Game;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AppState {
    private static AppState instance;

    private Game currentGame;
    private Map<String, Game> gameCache = new ConcurrentHashMap<>();
    private List<Game> allGames; // Cached game list

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    // === Game Cache Management ===

    public void cacheGame(Game game) {
        if (game != null && game.getId() != null) {
            gameCache.put(game.getId(), game);
            pcs.firePropertyChange("gameCached", null, game.getId());
        }
    }

    public void cacheGames(List<Game> games) {
        if (games != null) {
            allGames = new ArrayList<>(games);
            for (Game game : games) {
                cacheGame(game);
            }
            pcs.firePropertyChange("gamesCached", null, games.size());
        }
    }

    public Game getCachedGame(String gameId) {
        return gameCache.get(gameId);
    }

    public List<Game> getCachedGames() {
        return allGames != null ? new ArrayList<>(allGames) : new ArrayList<>();
    }

    public void clearGameCache() {
        gameCache.clear();
        allGames = null;
        pcs.firePropertyChange("gameCacheCleared", null, true);
    }

    public void removeFromCache(String gameId) {
        gameCache.remove(gameId);
        if (allGames != null) {
            allGames.removeIf(game -> gameId.equals(game.getId()));
        }
        pcs.firePropertyChange("gameRemovedFromCache", gameId, null);
    }

    public void fireGameUpdated(Game updatedGame) {
        // Update cache
        cacheGame(updatedGame);

        // Update current game if it's the same
        if (currentGame != null && currentGame.getId().equals(updatedGame.getId())) {
            setCurrentGame(updatedGame);
        }

        // Fire event for listeners
        pcs.firePropertyChange("gameUpdated", null, updatedGame);
    }

    // === Current Game Management ===

    public Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game game) {
        Game old = this.currentGame;
        this.currentGame = game;
        cacheGame(game); // Also cache it
        pcs.firePropertyChange("currentGame", old, game);
    }

    public void setCurrentGameById(String gameId) {
        Game game = getCachedGame(gameId);
        if (game == null) {
            // Try to load from file
            game = loadGameFromFile(gameId);
            if (game != null) {
                cacheGame(game);
            }
        }
        setCurrentGame(game);
    }

    private Game loadGameFromFile(String gameId) {
        // Call your GameManager to load from file
        try {
            // return GameManager.getInstance().loadGame(gameId);
            return null; // Replace with actual call
        } catch (Exception e) {
            return null;
        }
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