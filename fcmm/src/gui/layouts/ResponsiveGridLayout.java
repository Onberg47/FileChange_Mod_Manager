/**
 * Date 17/01/2026
 */
package gui.layouts;

// Add this custom layout manager class
import java.awt.*;

/**
 * A responsive grid layout that automatically adjusts columns
 * based on available width while keeping cell size fixed.
 */
public class ResponsiveGridLayout implements LayoutManager2 {
    private final int cellWidth;
    private final int cellHeight;
    private final int hGap;
    private final int vGap;

    public ResponsiveGridLayout(int cellWidth, int cellHeight, int hGap, int vGap) {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.hGap = hGap;
        this.vGap = vGap;
    }

    public ResponsiveGridLayout(int cellSize, int gap) {
        this(cellSize, cellSize, gap, gap);
    }

    @Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int availableWidth = parent.getWidth() - insets.left - insets.right;

            // Calculate columns that fit
            int columns = Math.max(1, (availableWidth + hGap) / (cellWidth + hGap));
            columns = Math.min(columns, parent.getComponentCount());

            // Calculate starting position for centering
            int totalRowWidth = columns * cellWidth + (columns - 1) * hGap;
            int xOffset = insets.left + Math.max(0, (availableWidth - totalRowWidth) / 2);

            // Position each component
            for (int i = 0; i < parent.getComponentCount(); i++) {
                Component comp = parent.getComponent(i);
                if (comp.isVisible()) {
                    int row = i / columns;
                    int col = i % columns;

                    int x = xOffset + col * (cellWidth + hGap);
                    int y = insets.top + row * (cellHeight + vGap);

                    comp.setBounds(x, y, cellWidth, cellHeight);
                }
            }
        }
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return minimumLayoutSize(parent);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int componentCount = getVisibleComponentCount(parent);

            if (componentCount == 0) {
                return new Dimension(
                        insets.left + insets.right,
                        insets.top + insets.bottom);
            }

            // Assume 2 columns minimum for preferred size
            int columns = 2;
            int rows = (int) Math.ceil(componentCount / (double) columns);

            int width = columns * cellWidth + (columns - 1) * hGap;
            int height = rows * cellHeight + (rows - 1) * vGap;

            return new Dimension(
                    width + insets.left + insets.right,
                    height + insets.top + insets.bottom);
        }
    }

    @Override
    public Dimension maximumLayoutSize(Container parent) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0.5f;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0.5f;
    }

    @Override
    public void invalidateLayout(Container target) {
    }

    private int getVisibleComponentCount(Container parent) {
        int count = 0;
        for (Component comp : parent.getComponents()) {
            if (comp.isVisible())
                count++;
        }
        return count;
    }
}