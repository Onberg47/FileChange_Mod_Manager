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
import gui.components.ModCard;
import gui.components.DividerCard;
import core.managers.ModManager;
import core.objects.GameState;
import core.objects.Mod;
import core.utils.Logger;

public class ModManagerView extends BaseView {
    // Globals
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
        // this.game = AppState.getInstance().getCurrentGame();
        this.manager = new ModManager(AppState.getInstance().getCurrentGame());
        super(navigator, params);
    }

    @Override
    protected void initializeUI() {
        setLayout(new BorderLayout(10, 10));

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
        try {
            // Load all mods for this game. No need to reload
            if (allMods == null || allMods.isEmpty())
                allMods = manager.getAllMods();

            String statusFilter = (String) filterStatusComboBox.getSelectedItem();
            String nameFilter = filterNameTextField.getText().toLowerCase();
            String tagsFilter = filterTagsTextField.getText().toLowerCase();
            // Parse tags
            Set<String> tagFilters = Arrays.stream(tagsFilter.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());

            // Logging for future testing.
            Logger.getInstance().logEntry(0, null,
                    "Filteres aplied: \n\tStatus: " + statusFilter
                            + "\n\tName: " + nameFilter
                            + "\n\tTags: " + tagFilters.toString());

            // Filter enabled mods
            List<Mod> modLs = allMods.stream()
                    .filter(mod -> matchesStatus(mod, statusFilter))
                    .filter(mod -> matchesName(mod, nameFilter))
                    .filter(mod -> matchesTags(mod, tagFilters))
                    .collect(Collectors.toList());

            /// Separate enabled/disabled
            enabledMods = modLs.stream()
                    .filter(Mod::isEnabled)
                    .sorted(Comparator.comparingInt(Mod::getLoadOrder))
                    .collect(Collectors.toList());

            disabledMods = modLs.stream()
                    .filter(m -> !m.isEnabled())
                    .sorted(Comparator.comparing(Mod::getName))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            showError("Failed to load mods: " + e.getMessage(), e);
            enabledMods = new ArrayList<>();
            disabledMods = new ArrayList<>();
        }
        displayModList();
    }

    /**
     * Displays the final mod lists.
     */
    private void displayModList() {
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
            modListPanel.add(new DividerCard("Disabled Mods", Color.RED));
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
    }

    private ModCard createModCard(Mod mod) {
        return new ModCard(
                mod,
                this::toEditModPage,
                this::toggleMod,
                this::updateLoadOrder);
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

    private void clearFilters() {
        filterStatusComboBox.setSelectedIndex(0);
        filterNameTextField.setText("");
        filterTagsTextField.setText("");
        // updateModList(); // Show all mods again
        loadMods();
    }

    /// /// /// Button Events /// /// ///

    private void toEditModPage(Mod mod) {
        navigator.navigateTo("editMod", Map.of(
                "game", manager,
                "modId", mod.getId()));
    }

    private void toggleMod(Mod mod) {
        try {
            mod.setEnabled(!mod.isEnabled());
            // ModManager.getInstance().updateModState(manager, mod);

            // TODO add/remove it
            loadMods(); // Reload to re-categorize
            // filterMods(); // Re-apply filters
        } catch (Exception e) {
            showError("Failed to toggle mod: " + e.getMessage(), e);
        }
    }

    private void updateLoadOrder(Mod mod) {
        try {
            // TODO Can just move the mod in the list here.
            System.out.println("Update mod: " + mod.getId());

            loadMods(); // Reload to get new order
            // filterMods();
        } catch (Exception e) {
            showError("Failed to update load order: " + e.getMessage(), e);
        }
    }

    /**
     * Apply all changes to the current GameState and rebuild.
     */
    private void applyChanges() {
        try {
            // Apply to currentGameState
            gameState.setOrderedMods(enabledMods);

            // Apply to Game
            // manager.deployGameState(gameState);
            System.out.println("Updated gameState: " + gameState.toString());

            JOptionPane.showMessageDialog(this,
                    "Mod changes applied successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            showError("Failed to apply changes: " + e.getMessage(), e);
        }
    }
} // Class