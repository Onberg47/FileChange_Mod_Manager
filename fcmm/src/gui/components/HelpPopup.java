/**
 * Author Stephanos B
 * Date 16/01/2026
 */
package gui.components;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import gui.util.ResourceLoader;

import javax.swing.JEditorPane;

/**
 * A Popup that displays the help information of the current View.
 * 
 * @since v3.3.4
 */
public class HelpPopup {
    private JEditorPane helpContent;
    private JScrollPane scrollPane;
    private JFrame frame;
    private JFrame parentFrame;
    private String viewId;

    public HelpPopup(JFrame parentFrame, String viewId) {
        this.parentFrame = parentFrame;
        this.viewId = viewId;
        setupGUI();
        loadHelpContent();
    }

    private void setupGUI() {
        frame = new JFrame("Help - " + (parentFrame != null ? parentFrame.getTitle() : "Help"));
        frame.setIconImage(parentFrame.getIconImage());

        helpContent = new JEditorPane();
        helpContent.setContentType("text/html");
        helpContent.setEditable(false);
        helpContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Set a reasonable font
        Font font = new Font("SansSerif", Font.PLAIN, 13);
        helpContent.setFont(font);

        scrollPane = new JScrollPane(helpContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);

        // Set popup size relative to parent
        if (parentFrame != null) {
            Dimension parentSize = parentFrame.getSize();
            int width = Math.min(1200, (int) (parentSize.width * 0.85));
            int height = Math.min(800, (int) (parentSize.height * 0.85));
            frame.setSize(width, height);
            frame.setLocationRelativeTo(parentFrame);
        } else {
            frame.setSize(1200, 800);
        }
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /// /// /// Logic and data /// /// ///

    private void loadHelpContent() {
        try {
            // Try to load help file from resources
            String html = ResourceLoader.getResourceAsString("help/" + viewId + ".html");
            helpContent.setText(html);
        } catch (Exception e) { // should be redundant but just in case.
            helpContent.setText("<html><body><h2>Error Loading Help</h2>" +
                    "<p>Could not load help content: " + e.getMessage() + "</p></body></html>");
        }
    }

    public static void openHelpInBrowser() {
        try {
            URI helpUrl = ResourceLoader.getResourceUrl("help/web-manual.html").toURI();
            Desktop.getDesktop().browse(helpUrl);

        } catch (URISyntaxException e) {
            JOptionPane.showMessageDialog(null,
                    "Help manual not found. Please check your resources folder.",
                    "Help Not Found",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Could not open help in browser: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /// /// /// Usage /// /// ///

    public void show() {
        frame.setVisible(true);
    }

    public void hide() {
        frame.setVisible(false);
    }

    public void refreshContent(String newViewId) {
        this.viewId = newViewId;
        loadHelpContent();
        frame.setTitle("Help - " + (parentFrame != null ? parentFrame.getTitle() : "Help"));
    }

} // Class