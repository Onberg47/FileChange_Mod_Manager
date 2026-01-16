/**
 * Author Stephanos B (Fragment-like structure) + DeepSeekV3 (implementation)
 * Date 7/01/2026
 */
package gui.views;

import gui.components.ConsolePopup;
import gui.navigator.AppNavigator;
import javax.swing.*;

import core.utils.Logger;

import java.awt.*;
import java.util.Map;

/**
 * Base class for all views. Provides navigation and common functionality.
 * 
 * @author Stephanos B
 * @since v2.0
 */
public abstract class BaseView extends JPanel {
    protected final AppNavigator navigator;
    protected final Map<String, Object> params;

    public BaseView(AppNavigator navigator, Map<String, Object> params) {
        this.navigator = navigator;
        this.params = params;

        setLayout(new BorderLayout());
        initializeUI();
        initializeData();
        setupEventHandlers();
    }

    /**
     * Initialize UI components. Override in subclasses.
     */
    protected abstract void initializeUI();

    /**
     * Load data for this view. Override in subclasses.
     */
    protected void initializeData() {
        // Default: do nothing
    }

    /**
     * Set up event handlers. Override in subclasses.
     */
    protected void setupEventHandlers() {
        // Default: do nothing
    }

    /**
     * Called when view becomes visible.
     */
    public void onViewShown() {
        // Override to refresh data
    }

    /**
     * Called when view is hidden.
     */
    public void onViewHidden() {
        // Override to clean up
    }

    /**
     * Helper to get parameter with default value.
     */
    @SuppressWarnings("unchecked")
    protected <T> T getParam(String key, T defaultValue) {
        Object value = params.get(key);
        return value != null ? (T) value : defaultValue;
    }

    /**
     * Helper to get required parameter.
     */
    @SuppressWarnings("unchecked")
    protected <T> T getRequiredParam(String key) {
        Object value = params.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Required parameter missing: " + key);
        }
        return (T) value;
    }

    /**
     * Show error message dialog.
     */
    protected void showError(String message) {
        this.showError(message, null);
    }

    protected void showError(String message, Exception e) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
        Logger.getInstance().logError(message, e);
    }

    /**
     * Show confirmation dialog.
     */
    protected boolean confirm(String message) {
        int result = JOptionPane.showConfirmDialog(
                this,
                message,
                "Confirm",
                JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    /// /// Console

    protected ConsolePopup consolePopup;

    /**
     * SHows a popup for displaying the console print out.
     */
    protected void showConsole() {
        if (consolePopup == null) {
            consolePopup = new ConsolePopup(navigator.getMainFrame()); // You'll need to implement getParentFrame()
        }
        consolePopup.show();
    }
} // Class