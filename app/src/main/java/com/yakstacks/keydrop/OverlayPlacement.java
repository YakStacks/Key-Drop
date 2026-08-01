package com.yakstacks.keydrop;

/**
 * Pure geometry for the floating button.
 *
 * <p>The button is stored as an edge plus a fraction of its vertical travel rather than as
 * absolute pixels, so a saved position survives rotation, split screen, and display-size
 * changes instead of being clipped to the old screen bounds.
 */
public final class OverlayPlacement {
    private final int screenWidth;
    private final int screenHeight;
    private final int buttonSize;
    private final int inset;

    public OverlayPlacement(int screenWidth, int screenHeight, int buttonSize, int inset) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.buttonSize = buttonSize;
        this.inset = inset;
    }

    public int leftEdgeX() {
        return Math.min(inset, Math.max(0, screenWidth - buttonSize));
    }

    public int rightEdgeX() {
        return Math.max(leftEdgeX(), screenWidth - buttonSize - inset);
    }

    public int edgeX(boolean snappedRight) {
        return snappedRight ? rightEdgeX() : leftEdgeX();
    }

    public int clampX(int x) {
        return Math.max(0, Math.min(x, Math.max(0, screenWidth - buttonSize)));
    }

    public int clampY(int y) {
        return Math.max(minY(), Math.min(y, maxY()));
    }

    /** True when the button's centre sits in the right half of the screen. */
    public boolean snapsRight(int x) {
        return x + buttonSize / 2 >= screenWidth / 2;
    }

    /** Converts a pixel Y into its 0..1 position within the available vertical travel. */
    public float verticalFraction(int y) {
        int travel = maxY() - minY();
        if (travel <= 0) return 0f;
        return (clampY(y) - minY()) / (float) travel;
    }

    /** Converts a 0..1 vertical position back into a pixel Y for the current screen. */
    public int yForFraction(float fraction) {
        return minY() + Math.round(clampFraction(fraction) * (maxY() - minY()));
    }

    public static float clampFraction(float fraction) {
        if (Float.isNaN(fraction)) return 0f;
        return Math.max(0f, Math.min(1f, fraction));
    }

    private int minY() {
        return Math.min(inset, Math.max(0, screenHeight - buttonSize));
    }

    private int maxY() {
        return Math.max(minY(), screenHeight - buttonSize - inset);
    }
}
