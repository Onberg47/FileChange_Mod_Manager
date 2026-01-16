import java.awt.*;
import javax.swing.*;
/*
 * Created by JFormDesigner on Thu Jan 08 16:12:40 SAST 2026
 */



/**
 * @author Stephanos B
 */
public class GameLibraryPanel extends JPanel {
	public GameLibraryPanel() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
		// Generated using JFormDesigner Stephanos
		menuBar_main = new JMenuBar();
		label1 = new JLabel();
		separator1 = new JSeparator();
		button1 = new JButton();
		scrollPane = new JScrollPane();

		//======== this ========
		setMinimumSize(new Dimension(200, 250));
		setPreferredSize(new Dimension(400, 400));
		setLayout(new BorderLayout());

		//======== menuBar_main ========
		{
			menuBar_main.setMargin(new Insets(4, 4, 4, 4));

			//---- label1 ----
			label1.setText("Game Libary");
			menuBar_main.add(label1);

			//---- separator1 ----
			separator1.setOrientation(SwingConstants.VERTICAL);
			menuBar_main.add(separator1);

			//---- button1 ----
			button1.setText("settings");
			menuBar_main.add(button1);
		}
		add(menuBar_main, BorderLayout.NORTH);

		//======== scrollPane ========
		{
			scrollPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			scrollPane.setMinimumSize(new Dimension(80, 80));
		}
		add(scrollPane, BorderLayout.CENTER);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
	// Generated using JFormDesigner Stephanos
	private JMenuBar menuBar_main;
	private JLabel label1;
	private JSeparator separator1;
	private JButton button1;
	private JScrollPane scrollPane;
	// JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
