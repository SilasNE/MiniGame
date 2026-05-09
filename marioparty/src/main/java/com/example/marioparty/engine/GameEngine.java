package com.example.marioparty.engine;

import com.example.marioparty.model.GameState;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class GameEngine extends AnimationTimer {

    private final Pane pane;
    private final InputHandler input;
    private final GameState state;

    private GameScene currentScene;
    private long lastTime = 0;

    public GameEngine(Pane pane, Scene fxScene) {
        this.pane = pane;
        this.input = new InputHandler(fxScene);
        this.state = new GameState();
        fxScene.getRoot().requestFocus();
    }

    public void setScene(GameScene scene) {
        if (currentScene != null) currentScene.onExit();
        currentScene = scene;
        currentScene.onEnter();
    }

    @Override
    public void handle(long now) {
        double dt = (lastTime == 0) ? 0 : (now - lastTime) / 1_000_000_000.0;
        lastTime = now;
        if (dt > 0.1) dt = 0.1;

        if (currentScene != null) {
            currentScene.update(dt, input);
        }
    }

    public InputHandler getInput() { return input; }
    public GameState getState()    { return state; }
    public Pane getPane()          { return pane; }
}