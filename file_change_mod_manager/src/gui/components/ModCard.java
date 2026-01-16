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
 * A Card that represents all details of a single Mod and facilitates button
 * events and Drag-to-order events.
 * 
 * @author Stephanos B
 * @since v3
 */
public class ModCard extends JPanel {
        private final Mod mod;
        private final Consumer<Mod> onEdit;
        private final Consumer<Mod> onToggle;
        private final Consumer<Mod> onOrderChange;
        // dragging:
        private boolean isDragging = false;
        private Consumer<Mod> onDragStart;
        private Consumer<Mod> onDragEnd;

        // UI Components
        private JSpinner loadOrderSpinner;
        private JLabel modTitleLabel;
        private JTextPane modDescriptionTextPane;
        private JLabel sourceLinkLabel;
        private JButton editButton;
        private JToggleButton deployToggleButton;
        private JLabel DraggingLabel;
        private JLabel modVersionLabel;

        public ModCard(Mod mod,
                        Consumer<Mod> onEdit,
                        Consumer<Mod> onToggle,
                        Consumer<Mod> onOrderChange,
                        Consumer<Mod> onDragStart,
                        Consumer<Mod> onDragEnd) {
                this.mod = mod;
                this.onEdit = onEdit;
                this.onToggle = onToggle;
                this.onOrderChange = onOrderChange;
                this.onDragStart = onDragStart;
                this.onDragEnd = onDragEnd;

                initComponents();
                setupData();
                setupEventHandlers();
        }

        private void setupData() {
                // Set mod data
                modTitleLabel.setText(mod.getName());
                modTitleLabel.setToolTipText("ID: " + mod.getId());
                modVersionLabel.setText("Version: " + mod.getVersion());
                modDescriptionTextPane.setText(mod.getDescription());
                modDescriptionTextPane.setEditable(false);

                // Set source as clickable link
                sourceLinkLabel.setText("<HTML><U>" + mod.getDownloadSource() + "</U></HTML>");
                sourceLinkLabel.setToolTipText("Click to open: " + mod.getDownloadLink());

                // Set load order
                SpinnerNumberModel spinnerModel = new SpinnerNumberModel(
                                mod.getLoadOrder(), 0, 100, -1); // reverse the order.
                loadOrderSpinner.setModel(spinnerModel);

                // Set toggle state
                deployToggleButton.setSelected(mod.isEnabled());
                deployToggleButton.setText(mod.isEnabled() ? "Enabled" : "Disabled");
                deployToggleButton.setForeground(mod.isEnabled() ? new Color(0, 100, 0) : Color.GRAY);
                deployToggleButton.setBorder(
                                new BevelBorder(mod.isEnabled() ? BevelBorder.RAISED : BevelBorder.LOWERED));

                // Set border color based on status
                this.setTheme();
        }

        private void setupEventHandlers() {
                // Edit button
                editButton.addActionListener(e -> onEdit.accept(mod));

                // Toggle button
                deployToggleButton.addActionListener(e -> {
                        boolean newState = deployToggleButton.isSelected();
                        deployToggleButton.setText(newState ? "Enabled" : "Disabled");
                        deployToggleButton.setForeground(newState ? new Color(0, 100, 0) : Color.GRAY);
                        deployToggleButton.setBorder(
                                        new BevelBorder(newState ? BevelBorder.RAISED : BevelBorder.LOWERED));
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
                sourceLinkLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                                openSourceUrl();
                        }
                });
                sourceLinkLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                                sourceLinkLabel.setForeground(new Color(0, 102, 204)); // Darker blue
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                                sourceLinkLabel.setForeground(Color.BLUE);
                        }

                        @Override
                        public void mouseClicked(MouseEvent e) {
                                openSourceUrl();
                        }
                });

                // Drag handle
                DraggingLabel.setEnabled(true);
                DraggingLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                                startDragging();
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                                stopDragging();
                        }
                });
        } // setupEventHandlers()

        /// /// /// Button Logic /// /// ///

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

        private void startDragging() {
                isDragging = true;

                // Visual feedback
                setBorder(new CompoundBorder(
                                new EmptyBorder(0, 0, 0, 0),
                                new CompoundBorder(
                                                new LineBorder(Color.ORANGE, 2, true),
                                                new EmptyBorder(3, 4, 4, 4))));
                DraggingLabel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

                // Notify parent
                if (onDragStart != null) {
                        onDragStart.accept(mod);
                }
        }

        private void stopDragging() {
                if (!isDragging)
                        return;

                isDragging = false;

                // Restore visual
                setTheme();
                DraggingLabel.setCursor(Cursor.getDefaultCursor());

                // Notify parent
                if (onDragEnd != null) {
                        onDragEnd.accept(mod);
                }
        }

        public boolean isDragging() {
                return isDragging;
        }

        /**
         * Handles border and background themes.
         */
        private void setTheme() {
                Color borderColor = mod.isEnabled()
                                ? new Color(0, 150, 0)
                                : Color.RED;

                setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createEmptyBorder(2, 2, 2, 2),
                                BorderFactory.createLineBorder(borderColor, 2, true)));
                // Set background based on status
                setBackground(mod.isEnabled()
                                ? new Color(240, 255, 240) // Light green for enabled
                                : new Color(250, 250, 250) // Light gray for disabled
                );
        }

        /// /// /// Getters for UI testing /// /// ///

        public Mod getMod() {
                return mod;
        }

        public JSpinner getLoadOrderSpinner() {
                return loadOrderSpinner;
        }

        public JToggleButton getDeployToggleButton() {
                return deployToggleButton;
        }

        /// /// /// INIT /// /// ///

        private void initComponents() {
                loadOrderSpinner = new JSpinner();
                modTitleLabel = new JLabel();
                modDescriptionTextPane = new JTextPane();
                sourceLinkLabel = new JLabel();
                editButton = new JButton();
                deployToggleButton = new JToggleButton();
                DraggingLabel = new JLabel();
                modVersionLabel = new JLabel();

                // ======== this ========
                setPreferredSize(new Dimension(800, 85));
                setMinimumSize(new Dimension(600, 80));
                setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

                // Set the border with proper inner padding
                setBorder(new CompoundBorder(
                                new LineBorder(Color.green, 1, true),
                                new EmptyBorder(3, 3, 3, 3)));
                int EdgePadding = 4; // for quick access

                // ---- loadOrderSpinner ----
                loadOrderSpinner.setPreferredSize(new Dimension(55, 40));
                loadOrderSpinner.setMinimumSize(new Dimension(42, 30));
                loadOrderSpinner.setFont(new Font("Noto Sans", Font.PLAIN, 16));
                loadOrderSpinner.setToolTipText("Adjust load order");

                // ---- modTitleLabel ----
                modTitleLabel.setText("Mod name");
                modTitleLabel.setFont(new Font("Noto Sans", Font.BOLD, 15));
                modTitleLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
                modTitleLabel.setPreferredSize(new Dimension(30, 20));
                modTitleLabel.setMinimumSize(new Dimension(80, 20));

                // ---- modDescriptionTextPane ----
                modDescriptionTextPane.setText("Description of the mod goes here... ");
                modDescriptionTextPane.setFocusable(false);
                modDescriptionTextPane.setBorder(new EmptyBorder(0, 0, 0, 0)); // Remove internal borders
                modDescriptionTextPane.setPreferredSize(new Dimension(60, 60));
                modDescriptionTextPane.setMinimumSize(new Dimension(80, 20));

                // ---- sourceFormattedTextField ----
                sourceLinkLabel = new JLabel("<html><u>download source</u></html>");
                sourceLinkLabel.setHorizontalAlignment(SwingConstants.CENTER);
                sourceLinkLabel.setToolTipText("Click to follow link {URL}");
                sourceLinkLabel.setFont(new Font("Noto Sans", Font.ITALIC, 15));
                sourceLinkLabel.setForeground(Color.BLUE);
                sourceLinkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                sourceLinkLabel.setPreferredSize(new Dimension(80, 60));
                sourceLinkLabel.setMinimumSize(new Dimension(60, 20));

                // ---- editButton ----
                editButton.setText("edit");
                editButton.setToolTipText("Edit mod");
                editButton.setPreferredSize(new Dimension(80, 34));
                editButton.setMinimumSize(new Dimension(76, 20));

                // ---- deployToggleButton ----
                deployToggleButton.setText("enable");
                deployToggleButton.setFont(new Font("Noto Sans", Font.PLAIN, 12));
                deployToggleButton.setToolTipText("Toggle enabled");
                deployToggleButton.setMaximumSize(new Dimension(55, 30));
                deployToggleButton.setPreferredSize(new Dimension(55, 25));
                deployToggleButton.setMinimumSize(new Dimension(50, 20));
                deployToggleButton.setBorder(new BevelBorder(BevelBorder.RAISED));

                // ---- DraggingLabel ----
                DraggingLabel.setText("☰"); // More recognizable drag handle
                DraggingLabel.setHorizontalAlignment(SwingConstants.CENTER);
                DraggingLabel.setBorder(BorderFactory.createCompoundBorder(
                                new LineBorder(new Color(150, 150, 150), 1),
                                new EmptyBorder(4, 4, 4, 4)));
                DraggingLabel.setHorizontalAlignment(SwingConstants.CENTER);
                DraggingLabel.setToolTipText("Drag to reorder");
                DraggingLabel.setEnabled(false);
                DraggingLabel.setOpaque(true);
                DraggingLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                DraggingLabel.setMaximumSize(new Dimension(35, 100));
                DraggingLabel.setPreferredSize(new Dimension(30, 80));
                DraggingLabel.setMinimumSize(new Dimension(15, 20));

                // ---- modVersionLabel ----
                modVersionLabel.setText("Version: 1.0.0.0");
                modVersionLabel.setHorizontalAlignment(SwingConstants.TRAILING);
                modVersionLabel.setToolTipText("Mod version");
                modVersionLabel.setEnabled(false);
                modVersionLabel.setPreferredSize(new Dimension(120, 25));
                modVersionLabel.setMaximumSize(new Dimension(200, 25));
                modVersionLabel.setMinimumSize(new Dimension(80, 20));

                GroupLayout layout = new GroupLayout(this);
                setLayout(layout);
                layout.setHorizontalGroup(
                                layout.createParallelGroup()
                                                .addGroup(layout.createSequentialGroup()
                                                                .addGap(EdgePadding)
                                                                .addGroup(layout.createParallelGroup(
                                                                                GroupLayout.Alignment.LEADING, false)
                                                                                .addComponent(loadOrderSpinner,
                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(deployToggleButton,
                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                GroupLayout.PREFERRED_SIZE))
                                                                .addGap(6)
                                                                .addComponent(DraggingLabel, GroupLayout.PREFERRED_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                .addGap(6)
                                                                .addGroup(layout.createParallelGroup()
                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                .addComponent(modTitleLabel,
                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                0,
                                                                                                                Short.MAX_VALUE)
                                                                                                .addGap(6)
                                                                                                .addComponent(modVersionLabel,
                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                GroupLayout.PREFERRED_SIZE))
                                                                                .addComponent(modDescriptionTextPane,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                0, Short.MAX_VALUE))
                                                                .addGap(6)
                                                                .addGroup(layout.createParallelGroup(
                                                                                GroupLayout.Alignment.LEADING, false)
                                                                                .addComponent(sourceLinkLabel,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE)
                                                                                .addComponent(editButton,
                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                GroupLayout.PREFERRED_SIZE))
                                                                .addGap(EdgePadding))

                );
                layout.setVerticalGroup(
                                layout.createParallelGroup()
                                                .addGroup(layout.createSequentialGroup()
                                                                .addGap(EdgePadding)
                                                                .addGroup(layout.createParallelGroup(
                                                                                GroupLayout.Alignment.LEADING)
                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                .addComponent(deployToggleButton,
                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                GroupLayout.PREFERRED_SIZE)
                                                                                                .addGap(6, 6, 6)
                                                                                                .addComponent(loadOrderSpinner,
                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                GroupLayout.PREFERRED_SIZE))
                                                                                .addComponent(DraggingLabel,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE)
                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                .addGroup(layout.createParallelGroup(
                                                                                                                GroupLayout.Alignment.BASELINE)
                                                                                                                .addComponent(editButton,
                                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                                34,
                                                                                                                                GroupLayout.PREFERRED_SIZE)
                                                                                                                .addComponent(modVersionLabel,
                                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                                33,
                                                                                                                                GroupLayout.PREFERRED_SIZE)
                                                                                                                .addComponent(modTitleLabel,
                                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                                33,
                                                                                                                                GroupLayout.PREFERRED_SIZE))
                                                                                                .addGap(0) // title-description
                                                                                                .addGroup(layout.createParallelGroup()
                                                                                                                .addComponent(sourceLinkLabel,
                                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                                Short.MAX_VALUE)
                                                                                                                .addComponent(modDescriptionTextPane,
                                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                                Short.MAX_VALUE))))
                                                                .addGap(EdgePadding))

                );
        }
} // Class