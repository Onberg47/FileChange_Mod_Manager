
/*
 * Author Stephanos B
 * Date 11/01/2026
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author onberg
 */
public class ModCard extends JPanel {

	private void DraggingLabelMouseClicked(MouseEvent e) {
		// TODO nothing for now.
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
		setBorder(new CompoundBorder(
				new EmptyBorder(2, 2, 2, 2),
				new LineBorder(Color.green, 1, true)));

		// ---- loadOrderSpinner ----
		loadOrderSpinner.setPreferredSize(new Dimension(45, 80));
		loadOrderSpinner.setMinimumSize(new Dimension(42, 60));
		loadOrderSpinner.setFont(new Font("Noto Sans", Font.PLAIN, 14));
		loadOrderSpinner.setToolTipText("Adjust load order");

		// ---- modTitleLabel ----
		modTitleLabel.setText("mod name");
		modTitleLabel.setFont(new Font("Noto Sans", Font.BOLD, 14));

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
		sourceFormattedTextField.setMinimumSize(new Dimension(90, 76));

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
		DraggingLabel.setPreferredSize(new Dimension(35, 106));
		DraggingLabel.setMinimumSize(new Dimension(20, 20));
		DraggingLabel.setMaximumSize(new Dimension(40, 150));
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
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
										.addComponent(deployToggleButton, GroupLayout.DEFAULT_SIZE,
												GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(loadOrderSpinner, GroupLayout.DEFAULT_SIZE,
												GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(DraggingLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(layout.createParallelGroup()
										.addComponent(modDescriptionTextPane, GroupLayout.DEFAULT_SIZE, 781,
												Short.MAX_VALUE)
										.addComponent(modTitleLabel, GroupLayout.DEFAULT_SIZE, 781, Short.MAX_VALUE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
										.addComponent(sourceFormattedTextField, GroupLayout.DEFAULT_SIZE,
												GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(editButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE))
								.addContainerGap()));
		layout.setVerticalGroup(
				layout.createParallelGroup()
						.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
										.addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
												.addGap(5, 5, 5)
												.addGroup(layout.createParallelGroup()
														.addComponent(editButton, GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(modTitleLabel, GroupLayout.PREFERRED_SIZE, 25,
																GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(layout.createParallelGroup()
														.addComponent(sourceFormattedTextField,
																GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(modDescriptionTextPane)))
										.addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
												.addContainerGap()
												.addGroup(layout.createParallelGroup()
														.addComponent(DraggingLabel, GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addGroup(layout.createSequentialGroup()
																.addComponent(deployToggleButton,
																		GroupLayout.PREFERRED_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(loadOrderSpinner,
																		GroupLayout.DEFAULT_SIZE, 0,
																		Short.MAX_VALUE)))))
								.addContainerGap()));
	}

	private JSpinner loadOrderSpinner;
	private JLabel modTitleLabel;
	private JTextPane modDescriptionTextPane;
	private JFormattedTextField sourceFormattedTextField;
	private JButton editButton;
	private JToggleButton deployToggleButton;
	private JLabel DraggingLabel;
} // Class