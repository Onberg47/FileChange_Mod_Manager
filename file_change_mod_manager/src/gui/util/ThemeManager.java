package gui.util;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import javax.swing.*;

public class ThemeManager {
    public enum Theme {
        DARK, LIGHT, MAC_DARK, MAC_LIGHT
    }
    
    private static Theme currentTheme = Theme.DARK;
    
    /**
     * Initialize theme at application start.
     * Must be called before creating any Swing components.
     */
    public static void initialize() {
        // Load saved theme preference
        String savedTheme = Config.getInstance().getTheme();
        if (savedTheme != null) {
            try {
                currentTheme = Theme.valueOf(savedTheme.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Use default
            }
        }
        
        applyTheme(currentTheme);
    }
    
    /**
     * Apply a theme.
     */
    public static void applyTheme(Theme theme) {
        try {
            switch (theme) {
                case DARK:
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                    break;
                case LIGHT:
                    UIManager.setLookAndFeel(new FlatLightLaf());
                    break;
                case MAC_DARK:
                    UIManager.setLookAndFeel(new FlatMacDarkLaf());
                    break;
                case MAC_LIGHT:
                    UIManager.setLookAndFeel(new FlatMacLightLaf());
                    break;
            }
            currentTheme = theme;
            
            // Update all existing windows
            updateAllWindows();
            
            // Save preference
            Config.getInstance().setTheme(theme.name().toLowerCase());
            
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to set theme: " + e.getMessage());
            // Fallback to system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // Give up
            }
        }
    }
    
    /**
     * Update all open windows to reflect theme change.
     */
    private static void updateAllWindows() {
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
        }
    }
    
    /**
     * Get current theme.
     */
    public static Theme getCurrentTheme() {
        return currentTheme;
    }
    
    /**
     * Toggle between dark and light.
     */
    public static void toggleTheme() {
        if (currentTheme == Theme.DARK || currentTheme == Theme.MAC_DARK) {
            applyTheme(currentTheme == Theme.DARK ? Theme.LIGHT : Theme.MAC_LIGHT);
        } else {
            applyTheme(currentTheme == Theme.LIGHT ? Theme.DARK : Theme.MAC_DARK);
        }
    }
    
    /**
     * Get nice display name for theme.
     */
    public static String getThemeDisplayName(Theme theme) {
        switch (theme) {
            case DARK: return "Dark";
            case LIGHT: return "Light";
            case MAC_DARK: return "macOS Dark";
            case MAC_LIGHT: return "macOS Light";
            default: return theme.name();
        }
    }
    
    /**
     * Get all available themes.
     */
    public static Theme[] getAllThemes() {
        return Theme.values();
    }
} // Class