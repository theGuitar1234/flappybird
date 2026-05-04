package org.game.gamescreen;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import javax.swing.JLabel;

public class RotatableLabel extends JLabel {

    private double angleRad = 0.0;

    public double getRotationDegrees() {
        return Math.toDegrees(angleRad);
    }

    public void setRotationDegrees(double degrees) {
        this.angleRad = Math.toRadians(degrees);
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();

        double sin = Math.abs(Math.sin(angleRad));
        double cos = Math.abs(Math.cos(angleRad));

        int w = (int) Math.ceil(d.width * cos + d.height * sin);
        int h = (int) Math.ceil(d.width * sin + d.height * cos);

        return new Dimension(w, h);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        AffineTransform old = g2.getTransform();
        g2.rotate(angleRad, cx, cy);

        super.paintComponent(g2);

        g2.setTransform(old);
        g2.dispose();
    }

}