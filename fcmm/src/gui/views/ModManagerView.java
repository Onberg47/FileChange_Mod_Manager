/**
 * Author Stephanos B
 * Date 11/01/2026
 */
package gui.views;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import gui.navigator.AppNavigator;
import gui.state.AppState;
import gui.util.IconLoader;
import gui.util.IconLoader.ICONS;
//import gui.util.ColorExtractor;
import gui.components.ModCard;
import gui.components.DividerCard;
import core.config.AppConfig;
import core.config.AppPreferences.properties;
import core.managers.ModManager;
import core.objects.GameState;
import core.objects.Mod;
import core.utils.Logger;

/**
 * This is the primary view responsible for displaying and managing Mods.
 * This includes all ModManager operations found in the CLI.
 * 
 * @author Stephanos B
 * @since v3
 */
public class ModManagerView extends BaseView {
    // Globals
    private final ModManager manager;

    // UI Components
    private JPanel utilityPanel;
    private JButton applyButton;
    private JButton goBackButton;
    private JButton compileNewButton;
    private JScrollPane modCardScrollPane;
    private JTextField filterNameTextField;
    private JComboBox<String> filterStatusComboBox;
    private JTextField filterTagsTextField;

    // Data
    private List<Mod> allMods;
    private List<Mod> enabledMods;
    private List<Mod> disabledMods;
    private JPanel modListPanel;
    // Dragging
    private Mod draggedMod = null;

    // private Color themeColor;
    // private Map<String, Color> colorPalette;

    public ModManagerView(AppNavigator navigator, Map<String, Object> params) {
        // this.game = AppState.getInstance().getCurrentGame();
        this.manager = new ModManager(AppState.getInstance().getCurrentGame());
        super(navigator, params);
    }

    @Override
    protected void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // themeColor = AppState.getInstance().getThemeColor();
        // colorPalette = ColorExtractor.createColorPalette(themeColor);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create a vertical panel for top components
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Add some spacing

        // Utility panel (top)
        utilityPanel = createUtilityPanel();

        topPanel.add(utilityPanel, BorderLayout.NORTH);

        // Filter panel
        JPanel filterPanel = createFilterPanel();
        topPanel.add(filterPanel, BorderLayout.CENTER); // This will be centered in the top panel

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Mod list scroll pane
        modListPanel = new JPanel();
        modListPanel.setLayout(new BoxLayout(modListPanel, BoxLayout.Y_AXIS));

        modCardScrollPane = new JScrollPane(modListPanel);
        modCardScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        modCardScrollPane.setBorder(BorderFactory.createEmptyBorder());
        modCardScrollPane.getVerticalScrollBar().setValue(0);

        mainPanel.add(modCardScrollPane, BorderLayout.CENTER);

        /// styling

        /*
         * mainPanel.setBackground(colorPalette.get("muted"));
         * utilityPanel.setBackground(themeColor);
         * filterPanel.setBackground(colorPalette.get("muted"));
         * topPanel.setBackground(colorPalette.get("muted"));
         * 
         * modCardScrollPane.setBackground(themeColor);
         * 
         * applyButton.setBackground(colorPalette.get("muted"));
         * compileNewButton.setBackground(colorPalette.get("muted"));
         * goBackButton.setBackground(colorPalette.get("muted"));
         */

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createUtilityPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        applyButton = new JButton("Apply Changes");
        applyButton.setToolTipText("Apply the current mod layout to the game");
        applyButton.setIcon(IconLoader.loadIcon(ICONS.SYNC, new Dimension(20, 20)));

        compileNewButton = new JButton("Compile New Mod");
        compileNewButton.setToolTipText("Create a new mod from files");
        compileNewButton.setIcon(IconLoader.loadIcon(ICONS.CREATE, new Dimension(20, 20)));

        // TODO Add a dropdown for GameState (Profile) Selector

        goBackButton = new JButton("← Back to Library");
        goBackButton.setToolTipText("Return to game library");
        goBackButton.setIcon(IconLoader.loadIcon(ICONS.LIBRARY, new Dimension(20, 20)));

        panel.add(applyButton);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(compileNewButton);
        panel.add(Box.createHorizontalGlue());
        panel.add(goBackButton);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Filters"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Status filter
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        filterStatusComboBox = new JComboBox<>(new String[] {
                "All", "Enabled", "Disabled"
        });
        filterStatusComboBox.setToolTipText("Filter by mod status");
        panel.add(filterStatusComboBox, gbc);

        // Name filter
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel.add(new JLabel("Tags:"), gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        filterTagsTextField = new JTextField();
        filterTagsTextField.setToolTipText("Comma-separated tags (e.g., weapons, textures)");
        panel.add(filterTagsTextField, gbc);

        // Tags filter
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        filterNameTextField = new JTextField();
        filterNameTextField.setToolTipText("🔍 Search in mod names and descriptions");
        panel.add(filterNameTextField, gbc);

        // Clear filters button
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearFilters());
        panel.add(clearButton, gbc);

        return panel;
    }

    @Override
    protected void initializeData() {
        scrollTo(0);
        loadMods();
        // updateModList();
    }

    @Override
    protected void setupEventHandlers() {
        // Navigation
        goBackButton.addActionListener(e -> navigator.navigateTo("library"));
        compileNewButton.addActionListener(e -> navigator.navigateTo("compileMod", Map.of("game", manager)));

        // Apply changes
        applyButton.addActionListener(e -> applyChanges());

        // Filter listeners
        filterStatusComboBox.addActionListener(e -> loadMods());

        // Setup document listeners for both text fields
        setupDocumentListener(filterNameTextField);
        setupDocumentListener(filterTagsTextField);
    }

    private void setupDocumentListener(JTextField textField) {
        textField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
                    public void changedUpdate(javax.swing.event.DocumentEvent e) {
                        loadMods();
                    }

                    public void removeUpdate(javax.swing.event.DocumentEvent e) {
                        loadMods();
                    }

                    public void insertUpdate(javax.swing.event.DocumentEvent e) {
                        loadMods();
                    }
                });
    }

    /// /// /// Mod List display and Logic /// /// ///

    /**
     * Load Mod data from Mod Storage with enabled/disabled flags and does
     * filtering.
     */
    private void loadMods() {
        // Load all mods for this game. No need to reload.
        if (allMods == null) {
            // Async / background loading of all Mods when needed.
            SwingWorker<Void, List<Mod>> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Logger.getInstance().info(0, null, "fetching all mods...");
                    allMods = manager.getAllMods();
                    publish(allMods);
                    return null;
                }

                @Override
                protected void process(List<List<Mod>> chunks) {
                    Logger.getInstance().info(0, null, "mods retrieved");
                    allMods.sort(Comparator.comparingInt(Mod::getLoadOrder));
                    loadMods();
                }
            };
            worker.execute();
            // allMods = null;
            modListPanel.add(new DividerCard("Loading...", Color.GRAY)); // show while loading.
            return; // don't procceed further because of loading
        }

        SwingWorker<Void, List<Mod>> worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    /// Filters
                    String statusFilter = (String) filterStatusComboBox.getSelectedItem();
                    String nameFilter = filterNameTextField.getText().toLowerCase();
                    String tagsFilter = filterTagsTextField.getText().toLowerCase();
                    // Parse tags
                    Set<String> tagFilters = Arrays.stream(tagsFilter.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toSet());

                    // Logging for future testing.
                    Logger.getInstance().info(0, null,
                            "Filteres aplied: \n\tStatus: " + statusFilter
                                    + "\n\tName: " + nameFilter
                                    + "\n\tTags: " + tagFilters.toString());

                    /// Separate enabled/disabled
                    enabledMods = allMods.stream()
                            .filter(Mod::isEnabled)
                            .sorted(Comparator.comparingInt(Mod::getLoadOrder))
                            .collect(Collectors.toList());

                    disabledMods = allMods.stream()
                            .filter(m -> !m.isEnabled())
                            .sorted(Comparator.comparing(Mod::getName))
                            .collect(Collectors.toList());

                    allMods.clear(); // recombine allMods to now be made of the pre-sorted sections.
                    allMods.addAll(enabledMods);
                    allMods.addAll(disabledMods);

                    /// Filter enabled mods
                    enabledMods = enabledMods.stream()
                            .filter(mod -> matchesStatus(mod, statusFilter))
                            .filter(mod -> matchesName(mod, nameFilter))
                            .filter(mod -> matchesTags(mod, tagFilters))
                            .collect(Collectors.toList());
                    disabledMods = disabledMods.stream()
                            .filter(mod -> matchesStatus(mod, statusFilter))
                            .filter(mod -> matchesName(mod, nameFilter))
                            .filter(mod -> matchesTags(mod, tagFilters))
                            .collect(Collectors.toList());

                    publish(allMods, enabledMods, disabledMods); // required!

                } catch (Exception e) {
                    showError("Failed to load mods: " + e.getMessage(), e);
                    enabledMods = new ArrayList<>();
                    disabledMods = new ArrayList<>();
                }
                return null;
            }

            @Override
            protected void process(List<List<Mod>> chunks) {
                displayModList();
            }

        };
        worker.execute();
    } // loadMods()

    /**
     * Displays the final mod lists.
     */
    private void displayModList() {
        int scroll = modCardScrollPane.getVerticalScrollBar().getValue();
        modListPanel.removeAll();

        // Add enabled mods section
        if (!enabledMods.isEmpty()) {
            modListPanel.add(DividerCard.createEnabledDivider());
            modListPanel.add(Box.createRigidArea(new Dimension(0, 5)));

            for (Mod mod : enabledMods) {
                ModCard card = createModCard(mod);
                modListPanel.add(card);
                modListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        // Add disabled mods section
        if (!disabledMods.isEmpty()) {
            modListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            modListPanel.add(DividerCard.createDisabledDivider());
            modListPanel.add(Box.createRigidArea(new Dimension(0, 5)));

            for (Mod mod : disabledMods) {
                ModCard card = createModCard(mod);
                modListPanel.add(card);
                modListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        // If no mods, show message
        if (enabledMods.isEmpty() && disabledMods.isEmpty()) {
            JLabel noModsLabel = new JLabel("No mods installed for this game.", SwingConstants.CENTER);
            noModsLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
            noModsLabel.setForeground(Color.GRAY);
            modListPanel.add(noModsLabel);
        }

        modListPanel.revalidate();
        modListPanel.repaint();
        scrollTo(scroll);
    }

    private ModCard createModCard(Mod mod) {
        return new ModCard(
                mod,
                this::toEditModPage,
                this::toggleMod,
                this::updateLoadOrder,
                this::handleDragStart, // New: drag start callback
                this::handleDragEnd // New: drag end callback
        );
    }

    /**
     * Sets the scroll to a set potition.
     * 
     * @param i number from 0, where 0 is the top.
     */
    private void scrollTo(int i) {
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = modCardScrollPane.getVerticalScrollBar();
            if (verticalBar != null) {
                verticalBar.setValue(i);
            }
        });
    }

    /**
     * Consumer<br>
     * <br>
     * This updates the Mods internal loadOrder value which is used to set the
     * spinner and reloads Mods.
     * 
     * @param mod
     */
    private void updateLoadOrder(Mod mod) {
        try {

            for (Mod modTemp : allMods) {
                if (modTemp.getId().equals(mod.getId())) {
                    this.allMods.add(allMods.indexOf(modTemp), mod);
                    this.allMods.remove(modTemp);
                    break;
                }
            }
            loadMods(); // Reload to get new order
        } catch (Exception e) {
            showError("Failed to update load order: " + e.getMessage(), e);
        }
    }

    /// /// Filters

    private boolean matchesStatus(Mod mod, String statusFilter) {
        if (statusFilter == null || "All".equals(statusFilter))
            return true;

        switch (statusFilter) {
            case "Enabled":
                return mod.isEnabled();
            case "Disabled":
                return !mod.isEnabled();
            default:
                return true;
        }
    }

    private boolean matchesName(Mod mod, String nameFilter) {
        if (nameFilter.isEmpty())
            return true;

        return mod.getName().toLowerCase().contains(nameFilter) ||
                mod.getDescription().toLowerCase().contains(nameFilter);
    }

    private boolean matchesTags(Mod mod, Set<String> tagFilters) {
        if (tagFilters.isEmpty())
            return true;

        Set<String> modTags = new HashSet<>(mod.getTagSet());
        return tagFilters.stream().anyMatch(modTags::contains);
    }

    private void clearFilters() {
        filterStatusComboBox.setSelectedIndex(0);
        filterNameTextField.setText("");
        filterTagsTextField.setText("");
        // updateModList(); // Show all mods again
        loadMods();
    }

    /// /// /// Dragging /// /// ///

    // Handle drag start
    private void handleDragStart(Mod mod) {
        // Logger.getInstance().logEntry(null, "Started dragging: " + mod.getId());
        draggedMod = mod;
    }

    // Handle drag end
    private void handleDragEnd(Mod mod) {
        // Logger.getInstance().logEntry(null, "Stopped dragging: " + mod.getId());

        if (draggedMod != null) {

            // Get mouse position
            Point mousePos = modListPanel.getMousePosition();
            if (mousePos == null)
                return;

            // Find which ModCard is at this position
            Mod targetMod = findModAtPosition(mousePos);
            if (targetMod == null)
                return;
            Logger.getInstance().info(null, "\tDropped on: " + targetMod.getName());

            if (targetMod != null && !targetMod.getId().equals(draggedMod.getId())) {
                moveDraggedMod(draggedMod, targetMod); // Move draggedMod to position before targetMod in allMods
                loadMods(); // Refresh display
            }
        }
        draggedMod = null;
    }

    /**
     * Find which Card (ModCard or DividerCard) is at a given position of the
     * point.<br>
     * <br>
     * If a DividerCard, returns a Mod with a matching Enabled Flag and a LoadOrder
     * of 0.
     * 
     * @param point Point on the window to check
     * @return Mod instance to determine where to move to.
     */
    private Mod findModAtPosition(Point point) {
        for (Component comp : modListPanel.getComponents()) {
            if (!comp.getBounds().contains(point))
                continue;

            if (comp instanceof ModCard) {
                ModCard card = (ModCard) comp;
                return card.getMod();
            } else if (comp instanceof DividerCard) {

                DividerCard divider = (DividerCard) comp;
                Mod tmp = new Mod(null);
                tmp.setLoadOrder(0);
                tmp.setEnabled(divider.getTitle().contains("Enabled"));
                return tmp;
            }
        }
        return null;
    }

    /**
     * Moves a Mod to replace the position of the target. If the target is from a
     * DividerCard, the Mod enters its category at the top.
     * 
     * @param modToMove Mod to Move.
     * @param targetMod Target Mod to determine where to move to.
     */
    private void moveDraggedMod(Mod modToMove, Mod targetMod) {
        int index = targetMod.getLoadOrder();
        modToMove.setEnabled(targetMod.isEnabled());

        if (!AppConfig.getInstance().preferences.is(properties.NORMALISE_BY_GROUP)) {

            allMods.remove(modToMove);
            index = allMods.indexOf(targetMod) + 1;
            if (index < modToMove.getLoadOrder() && index > 0) // makes the swapping behavior directional.
                index--;

            modToMove.setEnabled(targetMod.isEnabled());
            if (targetMod.isEnabled())
                modToMove.setLoadOrder(index);

            // ensure mod is added to the end is index is too large
            if (index > allMods.size())
                allMods.add(modToMove);
            else
                allMods.add(index, modToMove);
            normaliseLoadOrder();
        } else {
            if (targetMod.isEnabled())
                modToMove.setLoadOrder(index);
        }

        Logger.getInstance().info(0, null, "Dragger Mod " + modToMove.getId() + " to [" + modToMove.isEnabled()
                + "] : " + modToMove.getLoadOrder());
    }

    /**
     * Update all Enabled Mod loadOrders based on their current GUI ordering.
     * This will enforces load orders are sequential, from 1 and have no duplicates.
     */
    private void normaliseLoadOrder() {
        List<Mod> enabledModsList = allMods.stream()
                .filter(Mod::isEnabled)
                .collect(Collectors.toList());

        if (AppConfig.getInstance().preferences.is(properties.NORMALISE_BY_GROUP)) {
            // this allows duplicate loadorder values. All it does is ensure each group of
            // duplicates is sequential. (1,2,3...) to avoid oddly-high numbers (1,2,8...)
            int index = 0, groupCnt = 0;
            for (int i = 0; i < enabledModsList.size(); i++) {

                if (index != enabledModsList.get(i).getLoadOrder()) {
                    groupCnt++;
                    enabledModsList.get(i).setLoadOrder(groupCnt);
                    index = enabledModsList.get(i).getLoadOrder();
                } else
                    enabledModsList.get(i).setLoadOrder(groupCnt);
            }
        } else {
            // simply orders in sequence
            for (int i = 0; i < enabledModsList.size(); i++) {
                enabledModsList.get(i).setLoadOrder(i + 1);
            }
        }
    } // normaliseLoadOrder()

    /// /// /// Button Events /// /// ///

    private void toEditModPage(Mod mod) {
        AppState.getInstance().setCurrentMod(mod);
        navigator.navigateTo("editMod", Map.of("modId", mod.getId()));
    }

    private void toggleMod(Mod mod) {
        try {
            mod.setEnabled(!mod.isEnabled());
            loadMods(); // Reload to update.
        } catch (Exception e) {
            showError("Failed to toggle mod: " + e.getMessage(), e);
        }
    }

    /**
     * Apply all changes to the current GameState and rebuild.
     */
    private void applyChanges() {
        try {
            // Apply to currentGameState
            normaliseLoadOrder(); // clean up load order values

            GameState gameState = new GameState();
            gameState.setOrderedMods(allMods.stream()
                    .filter(Mod::isEnabled)
                    .collect(Collectors.toList()));

            // Apply to Game
            showConsole();
            SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception { // long-running task
                    manager.deployGameState(gameState);
                    return null;
                }

                @Override
                protected void done() { // Task completed - update GUI state
                    // forces a complete re-read
                    allMods = null; // do not use .clear().
                    // Need the distinction between empty and null!
                    loadMods();
                    finishConsole();
                }
            };
            worker.execute();

        } catch (Exception e) {
            showError("Failed to apply changes: " + e.getMessage(), e);
        }
    }
} // Class