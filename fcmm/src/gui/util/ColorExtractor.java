/**
 * Date 16/01/2026
 */
package gui.util;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * @since v3.3.4
 */
public class ColorExtractor {

    /**
     * Extracts a theme color from an image with intelligent filtering.
     * 
     * @param icon      The source image
     * @param intensity Target brightness (0.0 = black, 1.0 = white, 0.3-0.7
     *                  recommended)
     * @return A suitable theme color, or a fallback if extraction fails
     */
    public static Color extractThemeColor(Image icon, float intensity) {
        if (icon == null)
            return getFallbackColor(intensity);

        BufferedImage image = toBufferedImage(icon);
        if (image == null)
            return getFallbackColor(intensity);

        // Sample colors from the image (skip fully transparent pixels)
        List<Color> sampledColors = sampleColors(image, 1000);
        if (sampledColors.isEmpty())
            return getFallbackColor(intensity);

        // Group similar colors and find the largest cluster
        Color dominantColor = findDominantColor(sampledColors);

        // Adjust to desired intensity while preserving hue
        return adjustColorIntensity(dominantColor, intensity);
    }

    /**
     * Converts Image to BufferedImage for pixel analysis.
     */
    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(
                img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        java.awt.Graphics2D g = bimage.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return bimage;
    }

    /**
     * Randomly samples colors from the image, avoiding transparent pixels.
     */
    private static List<Color> sampleColors(BufferedImage image, int sampleCount) {
        List<Color> colors = new ArrayList<>();
        Random random = new Random();
        int width = image.getWidth();
        int height = image.getHeight();

        for (int i = 0; i < sampleCount; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);

            int rgb = image.getRGB(x, y);
            int alpha = (rgb >> 24) & 0xFF;

            // Only sample sufficiently opaque pixels (alpha > 128)
            if (alpha > 128) {
                colors.add(new Color(rgb, true));
            }
        }

        return colors;
    }

    /**
     * Groups colors by similarity and returns the most common hue family.
     */
    private static Color findDominantColor(List<Color> colors) {
        // Group colors by hue bucket (0-11 for 30° segments)
        Map<Integer, List<Color>> hueBuckets = new HashMap<>();

        for (Color color : colors) {
            float[] hsb = Color.RGBtoHSB(
                    color.getRed(), color.getGreen(), color.getBlue(), null);

            // Skip near-grayscale colors (low saturation)
            if (hsb[1] < 0.15f)
                continue;

            int hueBucket = (int) (hsb[0] * 12) % 12;
            hueBuckets.computeIfAbsent(hueBucket, k -> new ArrayList<>()).add(color);
        }

        // Find the largest bucket
        List<Color> largestBucket = null;
        for (List<Color> bucket : hueBuckets.values()) {
            if (largestBucket == null || bucket.size() > largestBucket.size()) {
                largestBucket = bucket;
            }
        }

        // If no colorful pixels found, average all colors
        if (largestBucket == null || largestBucket.isEmpty()) {
            return averageColor(colors);
        }

        // Return the average color of the largest hue bucket
        return averageColor(largestBucket);
    }

    /**
     * Calculates the average color from a list.
     */
    private static Color averageColor(List<Color> colors) {
        if (colors.isEmpty())
            return Color.GRAY;

        long r = 0, g = 0, b = 0;
        for (Color color : colors) {
            r += color.getRed();
            g += color.getGreen();
            b += color.getBlue();
        }

        int count = colors.size();
        return new Color(
                (int) (r / count),
                (int) (g / count),
                (int) (b / count));
    }

    /**
     * Adjusts a color to a specific intensity (brightness) while preserving hue.
     */
    private static Color adjustColorIntensity(Color color, float targetIntensity) {
        float[] hsb = Color.RGBtoHSB(
                color.getRed(), color.getGreen(), color.getBlue(), null);

        // Clamp intensity to avoid extremes
        float clampedIntensity = Math.max(0.2f, Math.min(0.8f, targetIntensity));

        // For very dark/light colors, boost saturation slightly
        float saturation = hsb[1];
        if (clampedIntensity > 0.7f || clampedIntensity < 0.3f) {
            saturation = Math.min(0.7f, saturation * 1.2f);
        }

        // Convert back to RGB with adjusted brightness
        return Color.getHSBColor(hsb[0], saturation, clampedIntensity);
    }

    /**
     * Returns a pleasant fallback color based on intensity.
     */
    private static Color getFallbackColor(float intensity) {
        // Nice blue hue that works for most UIs
        float hue = 0.6f; // Blue
        float saturation = 0.4f;
        float brightness = Math.max(0.3f, Math.min(0.7f, intensity));

        return Color.getHSBColor(hue, saturation, brightness);
    }

    /**
     * Creates a complementary color palette from a base color.
     * 
     * TODO colors do not cohere!
     */
    public static Map<String, Color> createColorPalette(Color baseColor) {
        Map<String, Color> palette = new LinkedHashMap<>();

        float[] hsb = Color.RGBtoHSB(
                baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), null);

        // Base theme color
        palette.put("primary", baseColor);

        // Lighter variant for backgrounds
        palette.put("light", Color.getHSBColor(hsb[0], hsb[1] * 0.5f, 0.95f));

        // Darker variant for text/accents
        palette.put("dark", Color.getHSBColor(hsb[0], hsb[1] * 1.2f, 0.3f));

        // Complementary color (opposite hue)
        float complementaryHue = (hsb[0] + 0.5f) % 1.0f;
        palette.put("accent", Color.getHSBColor(complementaryHue, 0.7f, 0.7f));

        // Muted version for borders
        palette.put("muted", Color.getHSBColor(hsb[0], hsb[1] * 0.3f, 0.8f));

        return palette;
    }
} // Class