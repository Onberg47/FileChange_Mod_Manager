/*
 * Author Stephanos B
 * Date: 8/01/2026
 */
package gui.util;

import javax.imageio.ImageIO;
import javax.swing.*;

import core.config.AppConfig;
import gui.state.AppState;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Managers GUI icons.
 * 
 * @since v2
 */
public class IconLoader {
    private static final Map<String, ImageIcon> iconCache = new HashMap<>();
    private static final ImageIcon DEFAULT_GAME_ICON = createDefaultGameIcon();
    private static final ImageIcon DEFAULT_ICON = createDefaultModIcon();

    /**
     * Register all icons here.
     */
    public enum ICONS {
        ADD("ic_add.png"),
        ARCHIVE("ic_archive.png"),
        BACK("ic_back.png"),
        BUSY("ic_busy.png"),
        COPY_ALL("ic_copy_all.png"),
        CREATE("ic_create.png"),
        DONE("ic_done.png"),
        DRAG("ic_drag.png"),
        EDIT("ic_edit.png"),
        EXIT("ic_exit.png"),
        FILTER_ON("ic_filter_on.png"),
        FILTER_OFF("ic_filter_off.png"),
        HELP("ic_help.png"),
        LIBRARY("ic_library.png"),
        REDIRECT("ic_redirect.png"),
        SAVE("ic_save.png"),
        SETTINGS("ic_settings.png"),
        SYNC("ic_sync.png"),
        TRASH("ic_trash.png");

        private String filename;

        ICONS(String filename) {
            this.filename = filename;
        }

        public String getFilename() {
            return this.filename;
        }
    }

    /// /// /// Custom Icons

    /**
     * Load game icon from path with caching.
     */
    public static ImageIcon loadGameIcon(String gameId, Dimension size) {
        String cacheKey = "game_" + gameId;
        // Check cache first
        if (iconCache.containsKey(cacheKey)) {
            return iconCache.get(cacheKey);
        }
        /// check for different image types.
        Path iconPath = AppConfig.getInstance().getGameDir().resolve("icons");
        return load(iconPath, gameId, cacheKey, size, DEFAULT_GAME_ICON);
    } // loadGameIcon()

    /**
     * Load mod icon (you can implement similar logic).
     */
    public static ImageIcon loadModIcon(String modId, Path iconPath) {
        // TODO Similar implementation...
        return DEFAULT_ICON;
    }

    /**
     * Bulk loader for the icon.
     * 
     * @param iconPath Path to directory of expected icon.
     * @param iconId   ID/filename of icon with no file extenstion.
     * @param cacheKey cached key of icon.
     * @return Icon or DEFAULT_ICON.
     */
    private static ImageIcon load(Path iconPath, String iconId, String cacheKey, Dimension size,
            ImageIcon fallbackIcon) {
        // Check for available files.
        if (Files.exists(iconPath.resolve(iconId + ".png")))
            iconPath = iconPath.resolve(iconId + ".png");

        else if (Files.exists(iconPath.resolve(iconId + ".jpg")))
            iconPath = iconPath.resolve(iconId + ".jpg");

        else
            iconPath = iconPath.resolve("placeholder.png");

        // Try to load from file
        if (iconPath != null && iconPath.toFile().exists()) {
            try {
                ImageIcon icon = new ImageIcon(iconPath.toString());
                Image scaled = scaleImage(icon.getImage(), size.width, size.height);
                icon = new ImageIcon(scaled);
                iconCache.put(cacheKey, icon);
                return icon;
            } catch (Exception e) {
                System.err.println("Failed to load icon: " + iconPath + " - " + e.getMessage());
            }
        }
        // If all else fails, fallbalck to a drawn icon.
        iconCache.put(cacheKey, fallbackIcon);
        return fallbackIcon;
    }

    /// Resources

    /**
     * Loads an Icon from {@code resources/icons/}
     * 
     * @param ICONS Icon from the registered enum of all ICONS.
     * @param size
     * @return
     */
    public static ImageIcon loadResourceIcon(ICONS icon, Dimension size) {
        String cacheKey = "resource_" + icon.getFilename();
        // Check cache first
        if (iconCache.containsKey(cacheKey)) {
            return iconCache.get(cacheKey);
        }

        ImageIcon iconImg;
        try {
            InputStream iconStream = ResourceLoader.getResourceAsStream("icons/" + icon.getFilename());
            iconImg = new ImageIcon(ImageIO.read(iconStream));
            Image scaled = scaleImage(iconImg.getImage(), size.width, size.height);
            iconImg = new ImageIcon(scaled);

        } catch (Exception e) {
            System.err.println("Failed to load icon: " + icon.getFilename() + " - " + e.getMessage());
            iconImg = DEFAULT_ICON;
        }
        iconCache.put(cacheKey, iconImg);
        return iconImg;
    }

    /// /// /// Icon Processes

    /**
     * Scale image maintaining aspect ratio.
     */
    private static Image scaleImage(Image image, int maxWidth, int maxHeight) {
        int originalWidth = image.getWidth(null);
        int originalHeight = image.getHeight(null);

        // Calculate scaling factor
        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);

        int scaledWidth = (int) (originalWidth * ratio);
        int scaledHeight = (int) (originalHeight * ratio);

        return image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
    }

    /**
     * Create a default game icon (programmatically drawn).
     */
    private static ImageIcon createDefaultGameIcon() {
        // Create a simple game controller icon
        int size = 64;
        Image image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();

        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background
        g2d.setColor(new Color(70, 130, 180)); // Steel blue
        g2d.fillRoundRect(2, 2, size - 4, size - 4, 15, 15);

        // Draw game controller
        g2d.setColor(Color.WHITE);
        g2d.fillOval(15, 15, 34, 34); // Main circle

        g2d.setColor(new Color(70, 130, 180));
        g2d.fillOval(20, 20, 24, 24); // Inner circle

        // Draw buttons
        g2d.setColor(Color.WHITE);
        g2d.fillOval(45, 20, 10, 10); // Right button
        g2d.fillOval(45, 35, 10, 10); // Right button

        g2d.dispose();

        return new ImageIcon(image);
    }

    /**
     * Returns a pre-defiend icon that is drawn with no image assets as a final
     * fallback.
     * Size is fixed at 64x64.
     * 
     * @return
     */
    private static ImageIcon createDefaultModIcon() {
        // Similar to above but different design
        int size = 64;
        Image image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Puzzle piece design for mods
        g2d.setColor(new Color(154, 205, 50)); // Yellow-green
        int[] xPoints = { 20, 44, 44, 60, 60, 44, 44, 20, 20, 4, 4, 20 };
        int[] yPoints = { 4, 4, 20, 20, 44, 44, 60, 60, 44, 44, 20, 20 };
        g2d.fillPolygon(xPoints, yPoints, 12);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
        g2d.drawString("M", 24, 42);

        g2d.dispose();

        return new ImageIcon(image);
    }

    /**
     * Clear icon cache (call when games/mods are added/removed).
     */
    public static void clearCache() {
        iconCache.clear();
    }

    /**
     * Clear cache for specific game/mod.
     */
    public static void clearCache(String id) {
        iconCache.remove("game_" + id);
        iconCache.remove("mod_" + id);
    }

    /// /// /// Icon post-processes

    /**
     * Extracts a theme color from a game/mod icon for UI theming.
     * 
     * @param gameIcon  The game/mod icon image
     * @param intensity Target brightness (0.3-0.7 recommended)
     * @return A theme color derived from the icon
     */
    public static Color extractThemeColor(ImageIcon gameIcon, float intensity) {
        if (gameIcon == null) {
            return ColorExtractor.extractThemeColor(null, intensity);
        }
        return ColorExtractor.extractThemeColor(gameIcon.getImage(), intensity);
    }

    /// /// /// View Helpers /// /// ///

    public static void fetchIcon(Path icon) {
        System.out.println("Fetching icon...");
        if (!Files.exists(icon))
            return;
        if (!Files.isRegularFile(icon))
            return;

        System.out.println("Icon: " + icon);
        Path target = core.config.AppConfig.getInstance().getGameDir().resolve(
                "icons");

        if (icon.getFileName().toString().endsWith(".png"))
            target = target.resolve(AppState.getInstance().getCurrentGame().getId() + ".png");
        else if (icon.getFileName().toString().endsWith(".jpg"))
            target = target.resolve(AppState.getInstance().getCurrentGame().getId() + ".jpg");
        else {
            System.err.println("File is not a known image!");
            return;
        }

        // Not checking for direcotries because AppConfig handles all config
        // directories.
        try {
            System.out.println("Copy from: " + icon + "-> " + target);
            Files.copy(icon, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
    }

} // Class