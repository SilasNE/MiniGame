package com.example.marioparty.engine;

import com.example.marioparty.model.GameState;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Herzstück der Engine: läuft als AnimationTimer (~60 FPS) und delegiert
 * jeden Frame an die aktuelle Szene. Hält außerdem InputHandler und GameState.
 */
public class GameEngine extends AnimationTimer {

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final InputHandler input;
    private final GameState state;

    private GameScene currentScene;
    private long lastTime = 0;

    public GameEngine(Canvas canvas, Scene fxScene) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.input = new InputHandler(fxScene);
        this.state = new GameState();
        // Canvas muss Fokus haben, damit Tasten ankommen
        fxScene.getRoot().requestFocus();
    }

    /** Wechselt die aktuelle Szene. Ruft onExit/onEnter auf. */
    public void setScene(GameScene scene) {
        if (currentScene != null) currentScene.onExit();
        currentScene = scene;
        currentScene.onEnter();
    }

    @Override
    public void handle(long now) {
        // Delta-Time in Sekunden für framerate-unabhängige Bewegung
        double dt = (lastTime == 0) ? 0 : (now - lastTime) / 1_000_000_000.0;
        lastTime = now;
        // Falls das Fenster pausiert war: dt deckeln, damit nichts "wegspringt"
        if (dt > 0.1) dt = 0.1;

        if (currentScene != null) {
            currentScene.update(dt, input);
            currentScene.render(gc);
        }
    }

    public InputHandler getInput() { return input; }
    public GameState getState() { return state; }
    public Canvas getCanvas() { return canvas; }
}
