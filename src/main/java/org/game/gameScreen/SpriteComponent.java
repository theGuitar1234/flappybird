package org.game.gamescreen;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class SpriteComponent extends JComponent {

    private BufferedImage image;

    public SpriteComponent() {}

    public SpriteComponent(BufferedImage image) {
        setImage(image);
    }

    public void setImage(BufferedImage image) {
        this.image = image;

        // if (image != null) {
        //     setSize(image.getWidth(), image.getHeight());
        // }

        repaint();
    }

    public BufferedImage getImage() {
        return image;
    }

    public int imageWidth() {
        return image == null ? 0 : image.getWidth();
    }

    public int imageHeight() {
        return image == null ? 0 : image.getHeight();
    }

    @Override
    public Dimension getPreferredSize() {
        if (image == null) {
            return new Dimension(0, 0);
        }

        return new Dimension(image.getWidth(), image.getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (image == null) {
            return;
        }

        g.drawImage(image, 0, 0, this);
    }
}