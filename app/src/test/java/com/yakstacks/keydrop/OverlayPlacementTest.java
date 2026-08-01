package com.yakstacks.keydrop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class OverlayPlacementTest {
    private static final int BUTTON = 144;
    private static final int INSET = 24;

    private static final OverlayPlacement PORTRAIT =
            new OverlayPlacement(1080, 1920, BUTTON, INSET);
    private static final OverlayPlacement LANDSCAPE =
            new OverlayPlacement(1920, 1080, BUTTON, INSET);

    @Test
    public void edgesSitInsideTheScreen() {
        assertEquals(INSET, PORTRAIT.leftEdgeX());
        assertEquals(1080 - BUTTON - INSET, PORTRAIT.rightEdgeX());
        assertEquals(PORTRAIT.leftEdgeX(), PORTRAIT.edgeX(false));
        assertEquals(PORTRAIT.rightEdgeX(), PORTRAIT.edgeX(true));
    }

    @Test
    public void snappingFollowsTheButtonCentre() {
        assertFalse(PORTRAIT.snapsRight(0));
        assertFalse(PORTRAIT.snapsRight(400));
        assertTrue(PORTRAIT.snapsRight(600));
        assertTrue(PORTRAIT.snapsRight(PORTRAIT.rightEdgeX()));
    }

    @Test
    public void draggingCannotLeaveTheScreen() {
        assertEquals(0, PORTRAIT.clampX(-500));
        assertEquals(1080 - BUTTON, PORTRAIT.clampX(5000));
        assertEquals(INSET, PORTRAIT.clampY(-500));
        assertEquals(1920 - BUTTON - INSET, PORTRAIT.clampY(5000));
    }

    @Test
    public void verticalFractionRoundTripsThroughPixels() {
        int y = 900;

        assertEquals(y, PORTRAIT.yForFraction(PORTRAIT.verticalFraction(y)));
    }

    @Test
    public void fractionSpansTheFullTravel() {
        assertEquals(0f, PORTRAIT.verticalFraction(INSET), 0.0001f);
        assertEquals(1f, PORTRAIT.verticalFraction(1920 - BUTTON - INSET), 0.0001f);
        assertEquals(INSET, PORTRAIT.yForFraction(0f));
        assertEquals(1920 - BUTTON - INSET, PORTRAIT.yForFraction(1f));
    }

    @Test
    public void outOfRangeFractionsAreClamped() {
        assertEquals(INSET, PORTRAIT.yForFraction(-2f));
        assertEquals(1920 - BUTTON - INSET, PORTRAIT.yForFraction(4f));
        assertEquals(INSET, PORTRAIT.yForFraction(Float.NaN));
    }

    @Test
    public void rotationKeepsTheButtonOnScreen() {
        float fraction = PORTRAIT.verticalFraction(1700);

        int rotatedY = LANDSCAPE.yForFraction(fraction);

        assertTrue(rotatedY >= INSET);
        assertTrue(rotatedY <= 1080 - BUTTON - INSET);
        assertEquals(rotatedY, LANDSCAPE.clampY(rotatedY));
    }

    @Test
    public void rotationKeepsRelativeVerticalPosition() {
        float fraction = PORTRAIT.verticalFraction(PORTRAIT.yForFraction(0.75f));

        assertEquals(0.75f, LANDSCAPE.verticalFraction(LANDSCAPE.yForFraction(fraction)), 0.01f);
    }

    @Test
    public void legacyPixelPositionFromATallerScreenMigratesInsideTheNewScreen() {
        // A position saved on a 1920px-tall screen, reopened in landscape.
        float migrated = LANDSCAPE.verticalFraction(1800);

        assertEquals(1f, migrated, 0.0001f);
        assertEquals(1080 - BUTTON - INSET, LANDSCAPE.yForFraction(migrated));
    }

    @Test
    public void screensSmallerThanTheButtonStayInBounds() {
        OverlayPlacement tiny = new OverlayPlacement(100, 100, BUTTON, INSET);

        assertEquals(0, tiny.clampX(50));
        assertEquals(0, tiny.leftEdgeX());
        assertEquals(0, tiny.rightEdgeX());
        assertEquals(0, tiny.clampY(50));
        assertEquals(0f, tiny.verticalFraction(50), 0.0001f);
        assertEquals(0, tiny.yForFraction(0.5f));
    }
}
