package org.game.gamescreen;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.game.util.constants.AppContext;

public class RotatableSprite extends SpriteComponent {

    private double angleRad = 0.0;

    public RotatableSprite() {}

    public RotatableSprite(BufferedImage image) {
        super(image);
    }

    public double getRotationDegrees() {
        return Math.toDegrees(angleRad);
    }

    public void setRotationDegrees(double degrees) {
        this.angleRad = Math.toRadians(degrees);

        // BufferedImage image = getImage();

        // if (image != null) {
        //     Dimension rotatedSize = calculateRotatedSize(image);
        //     setSize(rotatedSize);
        // }

        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        // BufferedImage image = getImage();

        // if (image == null) {
        //     return new Dimension(0, 0);
        // }

        // return calculateRotatedSize(image);

        return new Dimension(AppContext.BIRD_BOX_SIZE, AppContext.BIRD_BOX_SIZE);
    }

    // private Dimension calculateRotatedSize(BufferedImage image) {
    //     double sin = Math.abs(Math.sin(angleRad));
    //     double cos = Math.abs(Math.cos(angleRad));

    //     int originalWidth = image.getWidth();
    //     int originalHeight = image.getHeight();

    //     int rotatedWidth = (int) Math.ceil(originalWidth * cos + originalHeight * sin);
    //     int rotatedHeight = (int) Math.ceil(originalWidth * sin + originalHeight * cos);

    //     return new Dimension(rotatedWidth, rotatedHeight);
    // }

    @Override
    protected void paintComponent(Graphics g) {
        BufferedImage image = getImage();

        if (image == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int componentWidth = getWidth();
        int componentHeight = getHeight();

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        int centerX = componentWidth / 2;
        int centerY = componentHeight / 2;

        AffineTransform oldTransform = g2.getTransform();

        g2.rotate(angleRad, centerX, centerY);

        int drawX = centerX - imageWidth / 2;
        int drawY = centerY - imageHeight / 2;

        g2.drawImage(image, drawX, drawY, this);

        g2.setTransform(oldTransform);
        g2.dispose();
    }
}