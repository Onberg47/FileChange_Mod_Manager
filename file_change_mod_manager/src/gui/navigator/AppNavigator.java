// src/gui/navigator/AppNavigator.java
package gui.navigator;

import javax.swing.*;

import core.objects.Game;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Handles navigation between views with back stack support.
 * Similar to Android's FragmentManager.
 */
public class AppNavigator {
    private final JFrame mainFrame;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final Stack<ViewState> history;
    private final Map<String, ViewFactory.ViewCreator> viewRegistry;

    public static class ViewState {
        public final String viewId;
        public final Map<String, Object> params;

        public ViewState(String viewId, Map<String, Object> params) {
            this.viewId = viewId;
            this.params = params != null ? new HashMap<>(params) : new HashMap<>();
        }
    }

    public AppNavigator(JFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);
        this.history = new Stack<>();
        this.viewRegistry = new HashMap<>();

        mainFrame.setContentPane(cardPanel);
    }

    /**
     * Register a view that can be navigated to.
     */
    public void registerView(String viewId, ViewFactory.ViewCreator creator) {
        viewRegistry.put(viewId, creator);
    }

    /**
     * Navigate to a view with parameters.
     */
    public void navigateTo(String viewId) {
        navigateTo(viewId, new HashMap<>());
    }

    public void navigateTo(String viewId, Map<String, Object> params) {
        if (!viewRegistry.containsKey(viewId)) {
            throw new IllegalArgumentException("View not registered: " + viewId);
        }

        // Create new view state
        ViewState newState = new ViewState(viewId, params);
        history.push(newState);

        // Create and show the view
        JPanel view = viewRegistry.get(viewId).create(params);
        showView(viewId, view);

        updateWindowTitle(viewId, params);
    }

    /**
     * Go back to previous view.
     */
    public void goBack() {
        if (history.size() <= 1) {
            return; // Can't go back from first view
        }

        // Remove current
        history.pop();

        // Get previous
        ViewState previous = history.peek();
        JPanel view = viewRegistry.get(previous.viewId).create(previous.params);
        showView(previous.viewId, view);

        updateWindowTitle(previous.viewId, previous.params);
    }

    /**
     * Replace current view (no back stack entry).
     */
    public void replace(String viewId, Map<String, Object> params) {
        if (!viewRegistry.containsKey(viewId)) {
            throw new IllegalArgumentException("View not registered: " + viewId);
        }

        if (!history.isEmpty()) {
            history.pop(); // Remove current from history
        }

        navigateTo(viewId, params);
    }

    private void showView(String viewId, JPanel view) {
        // Remove existing view with this ID
        for (Component comp : cardPanel.getComponents()) {
            if (viewId.equals(comp.getName())) {
                cardPanel.remove(comp);
            }
        }

        // Add new view
        view.setName(viewId);
        cardPanel.add(view, viewId);

        // Show it
        cardLayout.show(cardPanel, viewId);
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    private void updateWindowTitle(String viewId, Map<String, Object> params) {
        String baseTitle = "Mod Manager";
        String viewTitle = getViewTitle(viewId, params);
        mainFrame.setTitle(baseTitle + " - " + viewTitle);
    }

    /**
     * Determines the view title for each page.
     * 
     * @param viewId The current View.
     * @param params Parameters being passed to that view.
     * @return String of the final View Title to displayu
     */
    private String getViewTitle(String viewId, Map<String, Object> params) {
        switch (viewId) {
            case "library":
                return "Game Library";
            case "modManager":
                Game game = (Game) params.get("game");
                return game != null ? game.getName() + " Mods" : "Mod Manager";
            case "settings":
                return "Settings";
            default:
                return viewId;
        }
    } // getViewTitle()

    public boolean canGoBack() {
        return history.size() > 1;
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }
} // Class