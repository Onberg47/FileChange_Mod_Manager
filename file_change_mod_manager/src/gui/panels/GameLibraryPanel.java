import java.awt.*;
import javax.swing.*;
/*
 * Created by JFormDesigner on Tue Jan 06 10:59:33 SAST 2026
 */



/**
 * @author onberg
 */
public class GameLibraryPanel extends JPanel {
	public GameLibraryPanel() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
		// Generated using JFormDesigner Educational license - Balden (eduv4822854)
		menuBar_main = new JMenuBar();
		label1 = new JLabel();
		separator1 = new JSeparator();
		button1 = new JButton();
		tabbedPane1 = new JTabbedPane();

		//======== this ========
		setLayout(new BorderLayout());

		//======== menuBar_main ========
		{
			menuBar_main.setMargin(new Insets(4, 4, 4, 4));

			//---- label1 ----
			label1.setText("Game Libary");
			menuBar_main.add(label1);

			//---- separator1 ----
			separator1.setMinimumSize(new Dimension(1, 12));
			menuBar_main.add(separator1);

			//---- button1 ----
			button1.setText("settings");
			menuBar_main.add(button1);
		}
		add(menuBar_main, BorderLayout.NORTH);
		add(tabbedPane1, BorderLayout.CENTER);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
	// Generated using JFormDesigner Educational license - Balden (eduv4822854)
	private JMenuBar menuBar_main;
	private JLabel label1;
	private JSeparator separator1;
	private JButton button1;
	private JTabbedPane tabbedPane1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
