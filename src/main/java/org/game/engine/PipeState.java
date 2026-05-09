package org.game.engine;

import org.game.gamescreen.SpriteComponent;

public final class PipeState {
    private final SpriteComponent sprite;
    private final boolean belowPipe;
    private final int width;

    private double x;
    private boolean scored = false;

    public void setScored(boolean scored) {
        this.scored = scored;
    }

    PipeState(SpriteComponent sprite, double x, int width, boolean belowPipe) {
        this.sprite = sprite;
        this.x = x;
        this.width = width;
        this.belowPipe = belowPipe;
    }

    public SpriteComponent getSprite() {
        return sprite;
    }

    public boolean isBelowPipe() {
        return belowPipe;
    }

    public int getWidth() {
        return width;
    }

    public double getX() {
        return x;
    }

    public boolean isScored() {
        return scored;
    }

    public void setX(double x) {
        this.x = x;
    }

    
}