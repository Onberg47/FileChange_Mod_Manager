import java.awt.*;
import javax.swing.*;
/*
 * Created by JFormDesigner on Thu Jan 08 16:04:55 SAST 2026
 */



/**
 * @author onberg
 */
public class ModMetaDataPanel extends JPanel {
	public ModMetaDataPanel() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
		// Generated using JFormDesigner Educational license - Balden (eduv4822854)
		menuBar1 = new JMenuBar();
		label7 = new JLabel();
		button1 = new JButton();
		label1 = new JLabel();
		textField1 = new JTextField();
		label2 = new JLabel();
		textField2 = new JTextField();
		label3 = new JLabel();
		textField3 = new JTextField();
		label4 = new JLabel();
		textField4 = new JTextField();
		label5 = new JLabel();
		textField5 = new JTextField();
		label6 = new JLabel();
		textField6 = new JTextField();

		//======== this ========
		setLayout(new GridBagLayout());
		((GridBagLayout)getLayout()).columnWidths = new int[] {126, 150, 0};
		((GridBagLayout)getLayout()).rowHeights = new int[] {42, 0, 0, 0, 0, 0, 0, 0};
		((GridBagLayout)getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
		((GridBagLayout)getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

		//======== menuBar1 ========
		{
			menuBar1.setMinimumSize(new Dimension(161, 34));
			menuBar1.setPreferredSize(new Dimension(160, 34));

			//---- label7 ----
			label7.setText("Compile Mod");
			menuBar1.add(label7);

			//---- button1 ----
			button1.setText("settings");
			menuBar1.add(button1);
		}
		add(menuBar1, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
			GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
			new Insets(4, 4, 12, 4), 0, 0));

		//---- label1 ----
		label1.setText("Name:");
		add(label1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 6, 8, 6), 0, 0));
		add(textField1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 8, 8), 0, 0));

		//---- label2 ----
		label2.setText("Description:");
		add(label2, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 6, 8, 6), 0, 0));
		add(textField2, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 8, 8), 0, 0));

		//---- label3 ----
		label3.setText("Version:");
		add(label3, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 6, 8, 6), 0, 0));
		add(textField3, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 8, 8), 0, 0));

		//---- label4 ----
		label4.setText("Load Order:");
		add(label4, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 6, 8, 6), 0, 0));
		add(textField4, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 8, 8), 0, 0));

		//---- label5 ----
		label5.setText("Download Source:");
		add(label5, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 6, 8, 6), 0, 0));
		add(textField5, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 8, 8), 0, 0));

		//---- label6 ----
		label6.setText("Download URL:");
		add(label6, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 6, 0, 6), 0, 0));
		add(textField6, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 8), 0, 0));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
	// Generated using JFormDesigner Educational license - Balden (eduv4822854)
	private JMenuBar menuBar1;
	private JLabel label7;
	private JButton button1;
	private JLabel label1;
	private JTextField textField1;
	private JLabel label2;
	private JTextField textField2;
	private JLabel label3;
	private JTextField textField3;
	private JLabel label4;
	private JTextField textField4;
	private JLabel label5;
	private JTextField textField5;
	private JLabel label6;
	private JTextField textField6;
	// JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
