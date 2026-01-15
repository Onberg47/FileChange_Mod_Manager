/**
 * Author Stephanos B
 * Date 11/01/2026
 */
package gui.components;

import core.objects.Mod;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * @author onberg
 */
public class ModCard extends JPanel {
        private final Mod mod;
        private final Consumer<Mod> onEdit;
        private final Consumer<Mod> onToggle;
        private final Consumer<Mod> onOrderChange;

        // UI Components
        private JSpinner loadOrderSpinner;
        private JLabel modTitleLabel;
        private JTextPane modDescriptionTextPane;
        private JFormattedTextField sourceFormattedTextField;
        private JButton editButton;
        private JToggleButton deployToggleButton;
        private JLabel DraggingLabel;

        public ModCard(Mod mod,
                        Consumer<Mod> onEdit,
                        Consumer<Mod> onToggle,
                        Consumer<Mod> onOrderChange) {
                this.mod = mod;
                this.onEdit = onEdit;
                this.onToggle = onToggle;
                this.onOrderChange = onOrderChange;

                initComponents();
                setupData();
                setupEventHandlers();
        }

        private void setupData() {
                // Set mod data
                modTitleLabel.setText(mod.getName());
                modTitleLabel.setToolTipText("ID: " + mod.getId());
                modDescriptionTextPane.setText(mod.getDescription());
                modDescriptionTextPane.setEditable(false);

                // Set source as clickable link
                sourceFormattedTextField.setText(mod.getDownloadSource());
                sourceFormattedTextField.setToolTipText("Click to open: " + mod.getDownloadLink());

                // Set load order
                SpinnerNumberModel spinnerModel = new SpinnerNumberModel(
                                mod.getLoadOrder(), 0, 100, -1); // reverse the order.
                loadOrderSpinner.setModel(spinnerModel);

                // Set toggle state
                deployToggleButton.setSelected(mod.isEnabled());
                deployToggleButton.setText(mod.isEnabled() ? "Enabled" : "Disabled");
                deployToggleButton.setForeground(mod.isEnabled() ? new Color(0, 100, 0) : Color.GRAY);

                // Set border color based on status
                Color borderColor = mod.isEnabled() ? new Color(0, 150, 0) : Color.RED;
                setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createEmptyBorder(2, 2, 2, 2),
                                BorderFactory.createLineBorder(borderColor, 2, true)));

                // Set background based on status
                setBackground(mod.isEnabled() ? new Color(240, 255, 240) : // Light green for enabled
                                new Color(250, 250, 250) // Light gray for disabled
                );
        }

        private void setupEventHandlers() {
                // Edit button
                editButton.addActionListener(e -> onEdit.accept(mod));

                // Toggle button
                deployToggleButton.addActionListener(e -> {
                        boolean newState = deployToggleButton.isSelected();
                        deployToggleButton.setText(newState ? "Enabled" : "Disabled");
                        deployToggleButton.setForeground(newState ? new Color(0, 100, 0) : Color.GRAY);
                        onToggle.accept(mod);
                });

                // Load order spinner
                loadOrderSpinner.addChangeListener(e -> {
                        int newOrder = (Integer) loadOrderSpinner.getValue();
                        if (newOrder != mod.getLoadOrder()) {
                                mod.setLoadOrder(newOrder);
                                onOrderChange.accept(mod);
                        }
                });

                // Source link - open browser on click
                sourceFormattedTextField.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                                openSourceUrl();
                        }
                });

                // Drag handle (future implementation)
                DraggingLabel.setEnabled(false); // Disable for now
        }

        private void openSourceUrl() {
                String url = mod.getDownloadLink();
                if (url != null && !url.isEmpty()) {
                        try {
                                Desktop.getDesktop().browse(new java.net.URI(url));
                        } catch (Exception e) {
                                JOptionPane.showMessageDialog(this,
                                                "Cannot open URL: " + url,
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE);
                        }
                }
        }

        private void DraggingLabelMouseClicked(MouseEvent e) {
                // TODO nothing for now.
                System.out.println("Drage handle clicked!");
        }

        // Getters for UI testing
        public Mod getMod() {
                return mod;
        }

        public JSpinner getLoadOrderSpinner() {
                return loadOrderSpinner;
        }

        public JToggleButton getDeployToggleButton() {
                return deployToggleButton;
        }

        private void initComponents() {
                loadOrderSpinner = new JSpinner();
                modTitleLabel = new JLabel();
                modDescriptionTextPane = new JTextPane();
                sourceFormattedTextField = new JFormattedTextField();
                editButton = new JButton();
                deployToggleButton = new JToggleButton();
                DraggingLabel = new JLabel();

                // ======== this ========
                setPreferredSize(new Dimension(1000, 120));
                setMaximumSize(new Dimension(3000, 200));
                setBorder(new CompoundBorder(
                                new EmptyBorder(2, 2, 2, 2),
                                new LineBorder(Color.BLACK, 2, true)));

                // ---- loadOrderSpinner ----
                loadOrderSpinner.setPreferredSize(new Dimension(45, 80));
                loadOrderSpinner.setMinimumSize(new Dimension(42, 50));
                loadOrderSpinner.setFont(new Font("Noto Sans", Font.PLAIN, 14));
                loadOrderSpinner.setToolTipText("Adjust load order");

                // ---- modTitleLabel ----
                modTitleLabel.setText("mod name");
                modTitleLabel.setFont(new Font("Noto Sans", Font.BOLD, 16));

                // ---- modDescriptionTextPane ----
                modDescriptionTextPane.setText(
                                "description of the mod goes here... This should be able to display lenthy lines that just yap on and on...");

                // ---- sourceFormattedTextField ----
                sourceFormattedTextField.setText("download source");
                sourceFormattedTextField.setEditable(false);
                sourceFormattedTextField.setHorizontalAlignment(SwingConstants.CENTER);
                sourceFormattedTextField.setToolTipText("Click to follow link {URL}");
                sourceFormattedTextField.setFont(new Font("Noto Sans", Font.ITALIC, 13));
                sourceFormattedTextField.setRequestFocusEnabled(false);
                sourceFormattedTextField.setPreferredSize(new Dimension(90, 76));
                sourceFormattedTextField.setMinimumSize(new Dimension(90, 66));

                // ---- editButton ----
                editButton.setText("edit");
                editButton.setToolTipText("Edit mod");

                // ---- deployToggleButton ----
                deployToggleButton.setText("enable");
                deployToggleButton.setFont(new Font("Noto Sans", Font.PLAIN, 12));
                deployToggleButton.setBorder(new LineBorder(Color.black, 1, true));
                deployToggleButton.setMinimumSize(new Dimension(50, 20));
                deployToggleButton.setMaximumSize(new Dimension(60, 30));
                deployToggleButton.setToolTipText("Toggle enabled");
                deployToggleButton.setPreferredSize(new Dimension(50, 25));

                // ---- DraggingLabel ----
                DraggingLabel.setText("\u22ee");
                DraggingLabel.setHorizontalAlignment(SwingConstants.CENTER);
                DraggingLabel.setBorder(new BevelBorder(BevelBorder.RAISED));
                DraggingLabel.setToolTipText("Drag to reorder");
                DraggingLabel.setEnabled(false);
                DraggingLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                DraggingLabel.setPreferredSize(new Dimension(35, 95));
                DraggingLabel.setMinimumSize(new Dimension(20, 20));
                DraggingLabel.setMaximumSize(new Dimension(40, 150));
                DraggingLabel.setOpaque(true);
                DraggingLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                                DraggingLabelMouseClicked(e);
                        }
                });

                GroupLayout layout = new GroupLayout(this);
                setLayout(layout);
                layout.setHorizontalGroup(
                                layout.createParallelGroup()
                                                .addGroup(layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addGroup(layout.createParallelGroup(
                                                                                GroupLayout.Alignment.LEADING, false)
                                                                                .addComponent(deployToggleButton,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE)
                                                                                .addComponent(loadOrderSpinner,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE))
                                                                .addPreferredGap(
                                                                                LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(DraggingLabel, GroupLayout.PREFERRED_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                                LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addGroup(layout.createParallelGroup()
                                                                                .addComponent(modDescriptionTextPane,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                781,
                                                                                                Short.MAX_VALUE)
                                                                                .addComponent(modTitleLabel,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                781, Short.MAX_VALUE))
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(layout.createParallelGroup(
                                                                                GroupLayout.Alignment.LEADING, false)
                                                                                .addComponent(sourceFormattedTextField,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE)
                                                                                .addComponent(editButton,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE))
                                                                .addContainerGap()));
                layout.setVerticalGroup(
                                layout.createParallelGroup()
                                                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(
                                                                                GroupLayout.Alignment.TRAILING)
                                                                                .addGroup(GroupLayout.Alignment.LEADING,
                                                                                                layout.createSequentialGroup()
                                                                                                                .addGap(5, 5, 5)
                                                                                                                .addGroup(layout.createParallelGroup()
                                                                                                                                .addComponent(editButton,
                                                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                                                Short.MAX_VALUE)
                                                                                                                                .addComponent(modTitleLabel,
                                                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                                                25,
                                                                                                                                                GroupLayout.PREFERRED_SIZE))
                                                                                                                .addPreferredGap(
                                                                                                                                LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                .addGroup(layout.createParallelGroup()
                                                                                                                                .addComponent(sourceFormattedTextField,
                                                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                                                Short.MAX_VALUE)
                                                                                                                                .addComponent(modDescriptionTextPane)))
                                                                                .addGroup(GroupLayout.Alignment.LEADING,
                                                                                                layout.createSequentialGroup()
                                                                                                                .addContainerGap()
                                                                                                                .addGroup(layout.createParallelGroup()
                                                                                                                                .addComponent(DraggingLabel,
                                                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                                                Short.MAX_VALUE)
                                                                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                                                                .addComponent(deployToggleButton,
                                                                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                GroupLayout.PREFERRED_SIZE)
                                                                                                                                                .addPreferredGap(
                                                                                                                                                                LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                                                .addComponent(loadOrderSpinner,
                                                                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                0,
                                                                                                                                                                Short.MAX_VALUE)))))
                                                                .addContainerGap()));
        }
} // Class