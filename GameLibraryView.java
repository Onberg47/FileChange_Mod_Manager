/**
 * Author Stephanos B
 * Date 8/01/2026
 */
package gui.views;

import gui.navigator.AppNavigator;
import gui.state.AppState;
import gui.util.IconLoader;
import gui.components.GameTile;
import core.managers.GameManager;
import core.objects.Game;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * A simple View that displays GameTiles, where the first tile is for adding a
 * new game.
 * 
 * @author Stephanos B
 * @since v2
 */
public class GameLibraryView extends BaseView {
    // UI Components (from your JForm design)
    private JScrollPane scrollPane;

    // Custom components
    private JPanel gameTilesPanel; // Will be inside scrollPane

    public GameLibraryView(AppNavigator navigator, Map<String, Object> params) {
        super(navigator, params);
    }

    @Override
    protected void initializeUI() {
        // Initialize JForm components
        initComponents();

        // Use the designed layout from JForm
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        // Create panel for game tiles (inside scroll pane)
        gameTilesPanel = new JPanel(new GridBagLayout());
        gameTilesPanel.setBackground(Color.WHITE);

        // Use GridBagLayout with dynamic column calculation
        scrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                refreshLayout();
            }
        });

        scrollPane.setViewportView(gameTilesPanel);
    }

    private void refreshLayout() {
        gameTilesPanel.removeAll();

        int tileSize = 200;
        int gap = 20;
        int panelWidth = scrollPane.getViewport().getWidth();
        int columns = Math.max(1, (panelWidth + gap) / (tileSize + gap));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(gap, gap, gap, gap);
        gbc.fill = GridBagConstraints.NONE;

        int row = 0;
        int col = 0;

        // Add "Add Game" tile first
        gbc.gridx = col;
        gbc.gridy = row;
        gameTilesPanel.add(createAddTile(), gbc);
        col++;

        // Add game tiles
        for (Game game : GameManager.getAllGames()) {
            if (col >= columns) {
                col = 0;
                row++;
            }

            gbc.gridx = col;
            gbc.gridy = row;
            gameTilesPanel.add(createGameTile(game), gbc);
            col++;
        }

        // Add filler to push tiles to the left
        gbc.gridx = columns;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gameTilesPanel.add(Box.createHorizontalGlue(), gbc);

        gameTilesPanel.revalidate();
        gameTilesPanel.repaint();
    }

    @Override
    protected void initializeData() {
        loadGameTiles();
    }

    private void loadGameTiles() {
        gameTilesPanel.removeAll();

        // 1. ADD "ADD GAME" TILE (always first)
        gameTilesPanel.add(createAddTile());

        // 2. Try to get games
        List<Game> games;
        try {
            games = GameManager.getAllGames();
        } catch (Exception e) {
            showError("Failed to load games: " + e.getMessage(), e);
            games = addSampleGames(); // Fallback
        }
        // Add game tiles from whatever populated the game list
        for (Game game : games) {
            // addGameTile(game);
            gameTilesPanel.add(createGameTile(game));
        }

        // Refresh display
        gameTilesPanel.revalidate();
        gameTilesPanel.repaint();
    }

    /// /// /// Helpers /// /// ///

    private GameTile createAddTile() {
        GameTile addTile = new GameTile(); // Constructor with no args = add button
        addTile.addTileClickListener(e -> {
            navigator.navigateTo("addGame");
        });
        return addTile;
    }

    private GameTile createGameTile(Game game) {
        GameTile tile = new GameTile(game.getId(), game.getName());

        // Click on tile -> set current game and navigate
        tile.addTileClickListener(e -> {
            AppState.getInstance().setCurrentGame(game);
            AppState.getInstance().setThemeColor(
                    IconLoader.extractThemeColor(
                            tile.getIcon(),
                            0.45f));

            navigator.navigateTo("modManager",
                    Map.of("gameId", game.getId()));
        });

        // Edit button -> set current game and navigate to editor
        tile.addEditButtonListener(e -> {
            AppState.getInstance().setCurrentGame(game);
            navigator.navigateTo("editGame",
                    Map.of("gameId", game.getId()));
        });

        return tile;
    } // createGameTile()

    /// /// /// Fallback data /// /// ///

    private List<Game> addSampleGames() {
        // Remove this in production - just for demo
        return Arrays.asList(
                createSampleGame("ghost-recon", "Ghost Recon Breakpoint"),
                createSampleGame("cyberpunk2077", "Cyberpunk 2077"),
                createSampleGame("skyrim-se", "Skyrim Special Edition"),
                createSampleGame("witcher3", "The Witcher 3"),
                createSampleGame("fallout4", "Fallout 4"));
    }

    private Game createSampleGame(String id, String name) {
        // Create a simple game object for demo
        // Replace with your actual Game constructor
        Game game = new Game();
        game.setId(id);
        game.setName(name);
        return game;
    }

    /**
     * JForm-generated initialization.
     * COPY FROM YOUR GameLibraryPanel.java
     */
    private void initComponents() {
        // ======== Generated by JFormDesigner ========
        scrollPane = new JScrollPane();

        // ======== this ========
        setMinimumSize(new Dimension(200, 250));
        setPreferredSize(new Dimension(400, 400));

        // ======== scrollPane ========
        {
            scrollPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            scrollPane.setMinimumSize(new Dimension(80, 80));
        }
    }
} // Class