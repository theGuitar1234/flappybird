package org.game;

import org.game.flabbybird.FlappyBird;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new FlappyBird().start();
        });
    }
}