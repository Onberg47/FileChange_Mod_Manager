import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
/*
 * Created by JFormDesigner on Thu Jan 15 17:17:51 SAST 2026
 */



/**
 * @author onberg
 */
public class ModCard extends JPanel {
	public ModCard() {
		initComponents();
	}

	private void DraggingLabelMouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
		// Generated using JFormDesigner Educational license - Balden (eduv4822854)
		loadOrderSpinner = new JSpinner();
		modTitleLabel = new JLabel();
		modDescriptionTextPane = new JTextPane();
		sourceFormattedTextField = new JFormattedTextField();
		editButton = new JButton();
		deployToggleButton = new JToggleButton();
		DraggingLabel = new JLabel();
		modVersionLabel = new JLabel();

		//======== this ========
		setPreferredSize(new Dimension(800, 100));
		setBorder(new CompoundBorder(
			new EmptyBorder(2, 2, 2, 2),
			new CompoundBorder(
				new LineBorder(Color.green, 1, true),
				new EmptyBorder(6, 6, 6, 6))));

		//---- loadOrderSpinner ----
		loadOrderSpinner.setPreferredSize(new Dimension(45, 80));
		loadOrderSpinner.setMinimumSize(new Dimension(42, 60));
		loadOrderSpinner.setFont(new Font("Noto Sans", Font.PLAIN, 14));
		loadOrderSpinner.setToolTipText("Adjust load order");

		//---- modTitleLabel ----
		modTitleLabel.setText("mod name");
		modTitleLabel.setFont(new Font("Noto Sans", Font.BOLD, 14));
		modTitleLabel.setMinimumSize(new Dimension(80, 20));
		modTitleLabel.setMaximumSize(new Dimension(2147483647, 20));
		modTitleLabel.setPreferredSize(new Dimension(30, 20));

		//---- modDescriptionTextPane ----
		modDescriptionTextPane.setText("description of the mod goes here... This should be able to display lenthy lines that just yap on and on...");
		modDescriptionTextPane.setMinimumSize(new Dimension(80, 20));
		modDescriptionTextPane.setPreferredSize(new Dimension(60, 60));

		//---- sourceFormattedTextField ----
		sourceFormattedTextField.setText("download source");
		sourceFormattedTextField.setEditable(false);
		sourceFormattedTextField.setHorizontalAlignment(SwingConstants.CENTER);
		sourceFormattedTextField.setToolTipText("Click to follow link {URL}");
		sourceFormattedTextField.setFont(new Font("Noto Sans", Font.ITALIC, 13));
		sourceFormattedTextField.setRequestFocusEnabled(false);
		sourceFormattedTextField.setPreferredSize(new Dimension(80, 60));
		sourceFormattedTextField.setMinimumSize(new Dimension(60, 20));

		//---- editButton ----
		editButton.setText("edit");
		editButton.setToolTipText("Edit mod");
		editButton.setMinimumSize(new Dimension(76, 20));
		editButton.setPreferredSize(new Dimension(80, 34));

		//---- deployToggleButton ----
		deployToggleButton.setText("enable");
		deployToggleButton.setFont(new Font("Noto Sans", Font.PLAIN, 12));
		deployToggleButton.setBorder(new LineBorder(Color.black, 1, true));
		deployToggleButton.setMinimumSize(new Dimension(50, 20));
		deployToggleButton.setMaximumSize(new Dimension(60, 30));
		deployToggleButton.setToolTipText("Toggle enabled");
		deployToggleButton.setPreferredSize(new Dimension(50, 25));

		//---- DraggingLabel ----
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

		//---- modVersionLabel ----
		modVersionLabel.setText("Version: 1.0");
		modVersionLabel.setToolTipText("Mod version");
		modVersionLabel.setEnabled(false);
		modVersionLabel.setPreferredSize(new Dimension(80, 25));
		modVersionLabel.setMaximumSize(new Dimension(100, 25));
		modVersionLabel.setMinimumSize(new Dimension(80, 20));
		modVersionLabel.setHorizontalAlignment(SwingConstants.TRAILING);

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(loadOrderSpinner, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(deployToggleButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(DraggingLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
							.addComponent(modTitleLabel, GroupLayout.DEFAULT_SIZE, 707, Short.MAX_VALUE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(modVersionLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(modDescriptionTextPane, GroupLayout.DEFAULT_SIZE, 793, Short.MAX_VALUE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(sourceFormattedTextField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(editButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(deployToggleButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(12, 12, 12)
					.addComponent(loadOrderSpinner, GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE))
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(editButton, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
						.addComponent(modVersionLabel, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
						.addComponent(modTitleLabel, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
					.addGap(0, 0, 0)
					.addGroup(layout.createParallelGroup()
						.addComponent(sourceFormattedTextField, GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
						.addComponent(modDescriptionTextPane, GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)))
				.addComponent(DraggingLabel, GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
		);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
	// Generated using JFormDesigner Educational license - Balden (eduv4822854)
	private JSpinner loadOrderSpinner;
	private JLabel modTitleLabel;
	private JTextPane modDescriptionTextPane;
	private JFormattedTextField sourceFormattedTextField;
	private JButton editButton;
	private JToggleButton deployToggleButton;
	private JLabel DraggingLabel;
	private JLabel modVersionLabel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
