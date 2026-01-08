/**
 * Author Stephanos B
 * Date 8/01/2026
 */
package gui.components;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class ModCard extends JPanel {
	// TODO

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
		// Generated using JFormDesigner Educational license - Balden (eduv4822854)
		loadOrderSpinner = new JSpinner();
		dragHandle = new JButton();
		modTitleLabel = new JLabel();
		modDescriptionTextPane = new JTextPane();
		formattedTextField1 = new JFormattedTextField();
		button2 = new JButton();
		deployToggleButton = new JToggleButton();

		//======== this ========
		setPreferredSize(new Dimension(380, 120));
		setBorder(new EmptyBorder(4, 6, 4, 6));

		//---- loadOrderSpinner ----
		loadOrderSpinner.setPreferredSize(new Dimension(45, 80));
		loadOrderSpinner.setMinimumSize(new Dimension(42, 60));
		loadOrderSpinner.setFont(new Font("Noto Sans", Font.PLAIN, 14));

		//---- dragHandle ----
		dragHandle.setText("---");
		dragHandle.setMaximumSize(new Dimension(40, 100));
		dragHandle.setMinimumSize(new Dimension(35, 92));
		dragHandle.setBorder(new BevelBorder(BevelBorder.LOWERED));
		dragHandle.setPreferredSize(new Dimension(35, 92));

		//---- modTitleLabel ----
		modTitleLabel.setText("mod name");
		modTitleLabel.setFont(new Font("Noto Sans", Font.BOLD, 14));

		//---- modDescriptionTextPane ----
		modDescriptionTextPane.setText("description of the mod goes here... This should be able to display lenthy lines that just yap on and on...");

		//---- formattedTextField1 ----
		formattedTextField1.setText("download source");
		formattedTextField1.setEditable(false);
		formattedTextField1.setHorizontalAlignment(SwingConstants.CENTER);
		formattedTextField1.setToolTipText("Click to follow link");
		formattedTextField1.setFont(new Font("Noto Sans", Font.ITALIC, 13));
		formattedTextField1.setRequestFocusEnabled(false);

		//---- button2 ----
		button2.setText("edit");
		button2.setToolTipText("Edit mod");

		//---- deployToggleButton ----
		deployToggleButton.setText("enable");
		deployToggleButton.setFont(new Font("Noto Sans", Font.PLAIN, 12));
		deployToggleButton.setBorder(new BevelBorder(BevelBorder.RAISED));
		deployToggleButton.setMinimumSize(new Dimension(50, 20));
		deployToggleButton.setMaximumSize(new Dimension(60, 30));

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(deployToggleButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(loadOrderSpinner, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addComponent(dragHandle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addGroup(layout.createParallelGroup()
						.addComponent(modDescriptionTextPane, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
						.addComponent(modTitleLabel, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(formattedTextField1)
						.addComponent(button2, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)))
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(modTitleLabel, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(modDescriptionTextPane))
				.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
					.addComponent(button2)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(formattedTextField1))
				.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
					.addComponent(deployToggleButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(loadOrderSpinner, GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
				.addComponent(dragHandle, GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
		);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
	// Generated using JFormDesigner Educational license - Balden (eduv4822854)
	private JSpinner loadOrderSpinner;
	private JButton dragHandle;
	private JLabel modTitleLabel;
	private JTextPane modDescriptionTextPane;
	private JFormattedTextField formattedTextField1;
	private JButton button2;
	private JToggleButton deployToggleButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
