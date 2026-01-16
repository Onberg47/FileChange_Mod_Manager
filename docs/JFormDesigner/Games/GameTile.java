/**
 * Author Stephanos B
 * Date 8/01/2026
 */
package gui.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Stephanos + JFormDesigner, refined by DeepSeekV3
 */
public class GameTile extends JPanel {
	private final String gameId;
	private final String gameName;
	private final boolean isAddButton;

	// === Constructor for normal game ===
	public GameTile(String gameId, String gameName) {
		this.gameId = gameId;
		this.gameName = gameName;
		this.isAddButton = false;
		initComponents();
		setupGameTile();
	}

	// === Constructor for "Add Game" button ===
	public GameTile() {
		this.gameId = null;
		this.gameName = null;
		this.isAddButton = true;
		initComponents();
		setupAddButtonTile();
	}

	private void setupGameTile() {
		gameIconLabel.setText("[Game Icon]");
		gameIconLabel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),
				gameName,
				TitledBorder.CENTER,
				TitledBorder.ABOVE_BOTTOM,
				new Font("Noto Sans", Font.BOLD, 14)));

		editButton.setText("Edit");
		editButton.setVisible(true);

		// Set tooltips
		gameIconLabel.setToolTipText("Manage mods for " + gameName);
		editButton.setToolTipText("Edit " + gameName + " settings");
	}

	private void setupAddButtonTile() {
		gameIconLabel.setText("+");
		gameIconLabel.setFont(new Font("Noto Sans", Font.BOLD, 48));
		gameIconLabel.setForeground(new Color(0, 120, 215));
		gameIconLabel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),
				"Add New Game",
				TitledBorder.CENTER,
				TitledBorder.ABOVE_BOTTOM,
				new Font("Noto Sans", Font.BOLD, 14)));

		editButton.setVisible(false); // Hide edit button for add tile

		// Visual feedback for add button
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setBackground(new Color(240, 248, 255)); // Light blue
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setBackground(null);
			}
		});

		// Set tooltip
		gameIconLabel.setToolTipText("Click to add a new game");
	}

	// === Getters ===
	public String getGameId() {
		return gameId;
	}

	public String getGameName() {
		return gameName;
	}

	public boolean isAddButton() {
		return isAddButton;
	}

	// === Event Listeners ===
	public void addTileClickListener(ActionListener listener) {
		gameIconLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				listener.actionPerformed(new ActionEvent(
						GameTile.this,
						ActionEvent.ACTION_PERFORMED,
						isAddButton ? "add_game" : "manage_game"));
			}
		});
	}

	public void addEditButtonListener(ActionListener listener) {
		editButton.addActionListener(listener);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
		// Generated using JFormDesigner Stephanos
		gameIconLabel = new JLabel();
		editButton = new JButton();

		//======== this ========
		setMinimumSize(new Dimension(100, 105));
		setPreferredSize(new Dimension(200, 205));
		setBorder(new BevelBorder(BevelBorder.RAISED));
		setLayout(new BorderLayout());

		//---- gameIconLabel ----
		gameIconLabel.setText("[icon]");
		gameIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		gameIconLabel.setFont(new Font("Noto Sans", Font.ITALIC, 13));
		gameIconLabel.setToolTipText("Click to mod");
		gameIconLabel.setBorder(new TitledBorder(null, "{game name}", TitledBorder.CENTER, TitledBorder.ABOVE_BOTTOM,
			new Font("Noto Sans", Font.BOLD, 14)));
		gameIconLabel.setMinimumSize(new Dimension(100, 100));
		gameIconLabel.setMaximumSize(new Dimension(400, 400));
		gameIconLabel.setPreferredSize(new Dimension(200, 200));
		add(gameIconLabel, BorderLayout.CENTER);

		//---- editButton ----
		editButton.setText("edit");
		editButton.setPreferredSize(new Dimension(60, 20));
		editButton.setMinimumSize(new Dimension(200, 10));
		editButton.setHorizontalTextPosition(SwingConstants.CENTER);
		editButton.setHorizontalAlignment(SwingConstants.TRAILING);
		editButton.setToolTipText("Edit game");
		add(editButton, BorderLayout.NORTH);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
	// Generated using JFormDesigner Stephanos
	private JLabel gameIconLabel;
	private JButton editButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
