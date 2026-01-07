import java.awt.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
/*
 * Created by JFormDesigner on Tue Jan 06 11:21:46 SAST 2026
 */



/**
 * @author onberg
 */
public class CompileModPanel extends JPanel {
	public CompileModPanel() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
		// Generated using JFormDesigner Educational license - Balden (eduv4822854)
		menuBar1 = new JMenuBar();
		label2 = new JLabel();
		separator1 = new JPopupMenu.Separator();
		button1 = new JButton();
		label1 = new JLabel();
		txtField_name = new JTextField();
		label3 = new JLabel();
		txtField_description = new JTextField();
		label4 = new JLabel();
		txtField_version = new JTextField();
		label5 = new JLabel();
		txtField_loadorder = new JTextField();
		txtField_source = new JTextField();
		label6 = new JLabel();
		label7 = new JLabel();
		txtField_url = new JTextField();
		label8 = new JLabel();

		//======== this ========

		//======== menuBar1 ========
		{
			menuBar1.setMargin(new Insets(4, 4, 4, 4));

			//---- label2 ----
			label2.setText("Compile New Mod");
			menuBar1.add(label2);
			menuBar1.add(separator1);

			//---- button1 ----
			button1.setText("settings");
			menuBar1.add(button1);
		}

		//---- label1 ----
		label1.setText("Name:");

		//---- label3 ----
		label3.setText("Description:");

		//---- label4 ----
		label4.setText("Version:");

		//---- label5 ----
		label5.setText("Load Order:");

		//---- label6 ----
		label6.setText("Source:");

		//---- label7 ----
		label7.setText("URL:");

		//---- label8 ----
		label8.setText("Mod Files:");

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(menuBar1, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
				.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
					.addContainerGap(19, Short.MAX_VALUE)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addGroup(layout.createSequentialGroup()
							.addComponent(label1, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(txtField_name, GroupLayout.PREFERRED_SIZE, 289, GroupLayout.PREFERRED_SIZE))
						.addGroup(layout.createParallelGroup()
							.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
								.addComponent(label3, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(txtField_description, GroupLayout.PREFERRED_SIZE, 289, GroupLayout.PREFERRED_SIZE))
							.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
								.addComponent(label4, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(txtField_version, GroupLayout.PREFERRED_SIZE, 289, GroupLayout.PREFERRED_SIZE))
							.addGroup(layout.createSequentialGroup()
								.addComponent(label5, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(txtField_loadorder, GroupLayout.PREFERRED_SIZE, 289, GroupLayout.PREFERRED_SIZE))
							.addGroup(layout.createSequentialGroup()
								.addComponent(label6, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(txtField_source, GroupLayout.PREFERRED_SIZE, 289, GroupLayout.PREFERRED_SIZE))
							.addGroup(layout.createSequentialGroup()
								.addComponent(label7, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(txtField_url, GroupLayout.PREFERRED_SIZE, 289, GroupLayout.PREFERRED_SIZE))
							.addComponent(label8, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(menuBar1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(txtField_name, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label1))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(txtField_description, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label3))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(txtField_version, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label4))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(txtField_loadorder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label5))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(txtField_source, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label6))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(txtField_url, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label7))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(label8)
					.addContainerGap(149, Short.MAX_VALUE))
		);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
	// Generated using JFormDesigner Educational license - Balden (eduv4822854)
	private JMenuBar menuBar1;
	private JLabel label2;
	private JPopupMenu.Separator separator1;
	private JButton button1;
	private JLabel label1;
	private JTextField txtField_name;
	private JLabel label3;
	private JTextField txtField_description;
	private JLabel label4;
	private JTextField txtField_version;
	private JLabel label5;
	private JTextField txtField_loadorder;
	private JTextField txtField_source;
	private JLabel label6;
	private JLabel label7;
	private JTextField txtField_url;
	private JLabel label8;
	// JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
