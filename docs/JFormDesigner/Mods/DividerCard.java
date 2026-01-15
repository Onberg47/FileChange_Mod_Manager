import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;

/**
 * @author onberg
 */
public class DividerCard extends JPanel {
	public DividerCard() {
		initComponents();
	}

	private void initComponents() {
		modTitleLabel = new JLabel();

		//======== this ========
		setPreferredSize(new Dimension(1000, 120));
		setBorder(new LineBorder(Color.black, 1, true));

		//---- modTitleLabel ----
		modTitleLabel.setText("Disabled Mods");
		modTitleLabel.setFont(new Font("Noto Sans", Font.BOLD, 16));
		modTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		modTitleLabel.setBorder(new CompoundBorder(
			new BevelBorder(BevelBorder.LOWERED),
			new CompoundBorder(
				new LineBorder(Color.green, 6),
				new BevelBorder(BevelBorder.RAISED))));
		modTitleLabel.setToolTipText("Divider");

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addComponent(modTitleLabel, GroupLayout.DEFAULT_SIZE, 986, Short.MAX_VALUE)
					.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
					.addContainerGap()
					.addComponent(modTitleLabel, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
					.addContainerGap())
		);
	}
	private JLabel modTitleLabel;

	private void modTitleLabelMouseReleased(MouseEvent e) {
		// TODO add your code here
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
		// Generated using JFormDesigner Educational license - Balden (eduv4822854)
		modTitleLabel = new JLabel();

		//======== this ========
		setPreferredSize(new Dimension(1000, 120));
		setBorder(new LineBorder(Color.black, 1, true));

		//---- modTitleLabel ----
		modTitleLabel.setText("Disabled Mods");
		modTitleLabel.setFont(new Font("Noto Sans", Font.BOLD, 16));
		modTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		modTitleLabel.setBorder(new CompoundBorder(
			new BevelBorder(BevelBorder.LOWERED),
			new CompoundBorder(
				new LineBorder(Color.green, 6),
				new BevelBorder(BevelBorder.RAISED))));
		modTitleLabel.setToolTipText("Divider");
		modTitleLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				modTitleLabelMouseReleased(e);
			}
		});

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addComponent(modTitleLabel, GroupLayout.DEFAULT_SIZE, 986, Short.MAX_VALUE)
					.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
					.addContainerGap()
					.addComponent(modTitleLabel, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
					.addContainerGap())
		);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
	// Generated using JFormDesigner Educational license - Balden (eduv4822854)
	private JLabel modTitleLabel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}