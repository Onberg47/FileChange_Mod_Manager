/**
 * Author Stephanos B
 * Date 8/01/2026
 */
package gui.views;

import gui.navigator.AppNavigator;
import gui.state.AppState;
import gui.util.IconLoader;
import gui.components.GameTile;
import gui.layouts.ResponsiveGridLayout;
import core.managers.GameManager;
import core.objects.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;
import java.util.List;

/**
 * A simple View that displays GameTiles, where the first tile is for adding a
 * new game.
 * 
 * @author Stephanos B
 * @since v2
 */
public class GameLibraryView extends BaseView {
    // UI Components
    private JPanel contentPanel;
    private JScrollPane scrollPane;
    private JPanel gameTilesPanel;

    public GameLibraryView(AppNavigator navigator, Map<String, Object> params) {
        super(navigator, params);
    }

    @Override
    protected void initializeUI() {
        setLayout(new BorderLayout(0, 0));

        // Create main content panel with some padding
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        contentPanel.setBackground(Color.WHITE);

        // Create title
        JLabel titleLabel = new JLabel("Games", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        // Create the responsive grid panel
        gameTilesPanel = new JPanel();
        ResponsiveGridLayout layout = new ResponsiveGridLayout(200, 200, 25, 25) {
            @Override
            public void layoutContainer(Container parent) {
                super.layoutContainer(parent);

                // Optional: Add subtle drop shadows to tiles
                for (Component comp : parent.getComponents()) {
                    if (comp instanceof GameTile) {
                        ((JComponent) comp).setBorder(
                                BorderFactory.createLineBorder(new Color(220, 220, 220), 2));
                    }
                } // for comp
            }
        };

        gameTilesPanel.setLayout(layout);
        gameTilesPanel.setBackground(Color.WHITE);

        // Wrap in scroll pane with no horizontal scroll
        scrollPane = new JScrollPane(gameTilesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling

        // Add resize listener for responsive updates
        scrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                gameTilesPanel.revalidate();
            }
        });

        // addSearchBar();
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }

    // Optional: Add search/filter bar
    @SuppressWarnings("unused")
    private void addSearchBar() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JTextField searchField = new JTextField(25);
        searchField.setToolTipText("Search games...");

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> filterGames(searchField.getText()));

        searchPanel.add(new JLabel("🔍"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        contentPanel.add(searchPanel, BorderLayout.SOUTH);
    }

    private void filterGames(String query) {
        for (Component comp : gameTilesPanel.getComponents()) {
            if (comp instanceof GameTile) {
                GameTile tile = (GameTile) comp;
                boolean matches = query.isEmpty() ||
                        tile.getGameName().toLowerCase().contains(query.toLowerCase());
                tile.setVisible(matches);
            }
        }
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

} // Class