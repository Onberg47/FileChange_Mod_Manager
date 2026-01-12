/**
 * Author Stephanos B
 * Date 11/01/2026
 */
package gui.views;

import gui.navigator.AppNavigator;
import gui.state.AppState;
import gui.components.ModCard;
import gui.components.DividerCard;
import core.managers.ModManager;
import core.managers.GameManager;
import core.objects.Game;
import core.objects.GameState;
import core.objects.Mod;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ModManagerView extends BaseView {
    private final Game game;
    private final ModManager manager;
    private GameState gameState;

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

    public ModManagerView(AppNavigator navigator, Map<String, Object> params) {
        super(navigator, params);
        this.game = AppState.getInstance().getCurrentGame();
        this.manager = new ModManager(game);
        this.gameState = new GameState();
    }

    @Override
    protected void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Utility panel (top)
        utilityPanel = createUtilityPanel();
        mainPanel.add(utilityPanel, BorderLayout.NORTH);

        // Filter panel
        JPanel filterPanel = createFilterPanel();
        mainPanel.add(filterPanel, BorderLayout.CENTER);

        // Mod list scroll pane
        modListPanel = new JPanel();
        modListPanel.setLayout(new BoxLayout(modListPanel, BoxLayout.Y_AXIS));

        modCardScrollPane = new JScrollPane(modListPanel);
        modCardScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        modCardScrollPane.setBorder(BorderFactory.createEmptyBorder());

        mainPanel.add(modCardScrollPane, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createUtilityPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        applyButton = new JButton("Apply Changes");
        applyButton.setToolTipText("Apply the current mod layout to the game");

        compileNewButton = new JButton("Compile New Mod");
        compileNewButton.setToolTipText("Create a new mod from files");

        goBackButton = new JButton("← Back to Library");
        goBackButton.setToolTipText("Return to game library");

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
                "All", "Enabled", "Disabled", "Has Conflicts", "Needs Update"
        });
        filterStatusComboBox.setToolTipText("Filter by mod status");
        panel.add(filterStatusComboBox, gbc);

        // Name filter
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel.add(new JLabel("Search:"), gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        filterNameTextField = new JTextField();
        filterNameTextField.setToolTipText("Search in mod names and descriptions");
        panel.add(filterNameTextField, gbc);

        // Tags filter
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Tags:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        filterTagsTextField = new JTextField();
        filterTagsTextField.setToolTipText("Comma-separated tags (e.g., weapons, textures)");
        panel.add(filterTagsTextField, gbc);

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
        loadMods();
        updateModList();
    }

    @Override
    protected void setupEventHandlers() {
        // Navigation
        goBackButton.addActionListener(e -> navigator.navigateTo("library"));
        compileNewButton.addActionListener(e -> navigator.navigateTo("compileMod", Map.of("game", manager)));

        // Apply changes
        applyButton.addActionListener(e -> applyChanges());

        // Filter listeners
        filterStatusComboBox.addActionListener(e -> filterMods());
        filterNameTextField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
                    public void changedUpdate(javax.swing.event.DocumentEvent e) {
                        filterMods();
                    }

                    public void removeUpdate(javax.swing.event.DocumentEvent e) {
                        filterMods();
                    }

                    public void insertUpdate(javax.swing.event.DocumentEvent e) {
                        filterMods();
                    }
                });
        filterTagsTextField.getDocument().addDocumentListener(
                (javax.swing.event.DocumentListener) filterNameTextField.getDocument()
                        .getListeners(javax.swing.event.DocumentListener.class)[0]);
    }

    private void loadMods() {
        try {
            // Load deployed mods for this game
            allMods = ModManager.getInstance().getDeployedMods(manager);

            // Separate enabled/disabled
            enabledMods = allMods.stream()
                    .filter(Mod::isEnabled)
                    .sorted(Comparator.comparingInt(Mod::getLoadOrder))
                    .collect(Collectors.toList());

            disabledMods = allMods.stream()
                    .filter(m -> !m.isEnabled())
                    .sorted(Comparator.comparing(Mod::getName))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            showError("Failed to load mods: " + e.getMessage());
            enabledMods = new ArrayList<>();
            disabledMods = new ArrayList<>();
        }
    }

    private void updateModList() {
        modListPanel.removeAll();

        // Add enabled mods section
        if (!enabledMods.isEmpty()) {
            modListPanel.add(new DividerCard("Enabled Mods", new Color(0, 150, 0)));
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
            modListPanel.add(new DividerCard("Disabled Mods", Color.GRAY));
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
            noModsLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
            noModsLabel.setForeground(Color.GRAY);
            modListPanel.add(noModsLabel);
        }

        modListPanel.revalidate();
        modListPanel.repaint();
    }

    private ModCard createModCard(Mod mod) {
        return new ModCard(
                mod,
                this::editMod,
                this::toggleMod,
                this::updateLoadOrder);
    }

    private void editMod(Mod mod) {
        navigator.navigateTo("editMod", Map.of(
                "game", manager,
                "modId", mod.getId()));
    }

    private void toggleMod(Mod mod) {
        try {
            mod.setEnabled(!mod.isEnabled());
            //ModManager.getInstance().updateModState(manager, mod);
            gameState.toggleMod(mod);
            loadMods(); // Reload to re-categorize
            filterMods(); // Re-apply filters
        } catch (Exception e) {
            showError("Failed to toggle mod: " + e.getMessage());
        }
    }

    private void updateLoadOrder(Mod mod) {
        try {
            //ModManager.getInstance().updateModOrder(manager, mod);
            gameState.update(mod);
            loadMods(); // Reload to get new order
            filterMods();
        } catch (Exception e) {
            showError("Failed to update load order: " + e.getMessage());
        }
    }

    private void filterMods() {
        String statusFilter = (String) filterStatusComboBox.getSelectedItem();
        String nameFilter = filterNameTextField.getText().toLowerCase();
        String tagsFilter = filterTagsTextField.getText().toLowerCase();

        // Parse tags
        Set<String> tagFilters = Arrays.stream(tagsFilter.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        // Filter enabled mods
        List<Mod> filteredEnabled = enabledMods.stream()
                .filter(mod -> matchesStatus(mod, statusFilter))
                .filter(mod -> matchesName(mod, nameFilter))
                .filter(mod -> matchesTags(mod, tagFilters))
                .collect(Collectors.toList());

        // Filter disabled mods
        List<Mod> filteredDisabled = disabledMods.stream()
                .filter(mod -> matchesStatus(mod, statusFilter))
                .filter(mod -> matchesName(mod, nameFilter))
                .filter(mod -> matchesTags(mod, tagFilters))
                .collect(Collectors.toList());

        // Update display with filtered lists
        displayFilteredMods(filteredEnabled, filteredDisabled);
    }

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

    private void displayFilteredMods(List<Mod> filteredEnabled,
            List<Mod> filteredDisabled) {
        modListPanel.removeAll();

        if (!filteredEnabled.isEmpty()) {
            modListPanel.add(new DividerCard("Enabled Mods", new Color(0, 150, 0)));
            modListPanel.add(Box.createRigidArea(new Dimension(0, 5)));

            for (Mod mod : filteredEnabled) {
                modListPanel.add(createModCard(mod));
                modListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        if (!filteredDisabled.isEmpty()) {
            modListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            modListPanel.add(new DividerCard("Disabled Mods", Color.GRAY));
            modListPanel.add(Box.createRigidArea(new Dimension(0, 5)));

            for (Mod mod : filteredDisabled) {
                modListPanel.add(createModCard(mod));
                modListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        // Show "no results" message if all filters return empty
        if (filteredEnabled.isEmpty() && filteredDisabled.isEmpty()) {
            JLabel noResults = new JLabel("No mods match your filters.", SwingConstants.CENTER);
            noResults.setFont(new Font("SansSerif", Font.ITALIC, 14));
            noResults.setForeground(Color.GRAY);
            modListPanel.add(noResults);
        }

        modListPanel.revalidate();
        modListPanel.repaint();
    }

    private void clearFilters() {
        filterStatusComboBox.setSelectedIndex(0);
        filterNameTextField.setText("");
        filterTagsTextField.setText("");
        updateModList(); // Show all mods again
    }

    private void applyChanges() {
        try {
            // Save all mod states and load order
            //ModManager.saveModConfiguration(manager, allMods);

            // Apply to game files
            gameState.setDeployedMods(enabledMods);
            gameState.saveToFile();
            manager.deployGameState(gameState);

            JOptionPane.showMessageDialog(this,
                    "Mod changes applied successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            showError("Failed to apply changes: " + e.getMessage());
        }
    }
} // Class