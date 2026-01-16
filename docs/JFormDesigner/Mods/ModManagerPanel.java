import java.awt.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;

/**
 * @author Stephanos B
 */
public class ModManagerPanel  {

	private void initComponents() {
		panel1 = new JPanel();
		utilityPanel = new JPanel();
		ApplyButton = new JButton();
		separator1 = new JSeparator();
		goBackButton = new JButton();
		separator2 = new JSeparator();
		compileNewButton = new JButton();
		modCardScrollPane = new JScrollPane();
		filterNameTextField = new JTextField();
		filterNameLabel = new JLabel();
		filterStatusLabel = new JLabel();
		filterStatusComboBox = new JComboBox();
		filterTagsLabel = new JLabel();
		filterTagsTextField = new JTextField();

		//======== panel1 ========
		{

			//======== utilityPanel ========
			{
				utilityPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

				//---- ApplyButton ----
				ApplyButton.setText("Apply");
				ApplyButton.setToolTipText("Apply the current mod layout");

				//---- separator1 ----
				separator1.setOrientation(SwingConstants.VERTICAL);

				//---- goBackButton ----
				goBackButton.setText("Go Back");
				goBackButton.setToolTipText("Page back");

				//---- separator2 ----
				separator2.setOrientation(SwingConstants.VERTICAL);

				//---- compileNewButton ----
				compileNewButton.setText("Compile New");
				compileNewButton.setToolTipText("Go to compile new mod");

				GroupLayout utilityPanelLayout = new GroupLayout(utilityPanel);
				utilityPanel.setLayout(utilityPanelLayout);
				utilityPanelLayout.setHorizontalGroup(
					utilityPanelLayout.createParallelGroup()
						.addGroup(utilityPanelLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(ApplyButton)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(separator2, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(compileNewButton)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 831, Short.MAX_VALUE)
							.addComponent(separator1, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(goBackButton)
							.addContainerGap())
				);
				utilityPanelLayout.setVerticalGroup(
					utilityPanelLayout.createParallelGroup()
						.addGroup(GroupLayout.Alignment.TRAILING, utilityPanelLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(utilityPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(separator2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
								.addGroup(GroupLayout.Alignment.LEADING, utilityPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(separator1, GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
									.addComponent(goBackButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(ApplyButton, GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
								.addComponent(compileNewButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
							.addContainerGap())
				);
			}

			//---- filterNameLabel ----
			filterNameLabel.setText("Search Name:");

			//---- filterStatusLabel ----
			filterStatusLabel.setText("Status:");

			//---- filterStatusComboBox ----
			filterStatusComboBox.setMaximumRowCount(6);
			filterStatusComboBox.setPrototypeDisplayValue("disabled");
			filterStatusComboBox.setToolTipText("Filter mod status");

			//---- filterTagsLabel ----
			filterTagsLabel.setText("Search Tags:");

			GroupLayout panel1Layout = new GroupLayout(panel1);
			panel1.setLayout(panel1Layout);
			panel1Layout.setHorizontalGroup(
				panel1Layout.createParallelGroup()
					.addGroup(panel1Layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(panel1Layout.createParallelGroup()
							.addComponent(utilityPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGroup(panel1Layout.createSequentialGroup()
								.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
									.addComponent(filterStatusLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(filterNameLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panel1Layout.createParallelGroup()
									.addComponent(filterStatusComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(filterNameTextField, GroupLayout.DEFAULT_SIZE, 1096, Short.MAX_VALUE)))
							.addGroup(panel1Layout.createSequentialGroup()
								.addComponent(filterTagsLabel, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(filterTagsTextField))
							.addComponent(modCardScrollPane))
						.addContainerGap())
			);
			panel1Layout.setVerticalGroup(
				panel1Layout.createParallelGroup()
					.addGroup(panel1Layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(utilityPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(filterStatusComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(filterStatusLabel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(filterNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(filterNameLabel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(filterTagsLabel)
							.addComponent(filterTagsTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(modCardScrollPane, GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
						.addContainerGap())
			);
		}
	}
	private JPanel panel1;
	private JPanel utilityPanel;
	private JButton ApplyButton;
	private JSeparator separator1;
	private JButton goBackButton;
	private JSeparator separator2;
	private JButton compileNewButton;
	private JScrollPane modCardScrollPane;
	private JTextField filterNameTextField;
	private JLabel filterNameLabel;
	private JLabel filterStatusLabel;
	private JComboBox filterStatusComboBox;
	private JLabel filterTagsLabel;
	private JTextField filterTagsTextField;
}
