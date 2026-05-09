package com.example.marioparty.engine;

public abstract class GameScene {

    protected final GameEngine engine;

    public GameScene(GameEngine engine) {
        this.engine = engine;
    }

    public void onEnter() {}

    public void onExit() {
        engine.getPane().getChildren().clear();
    }

    public abstract void update(double dt, InputHandler input);
}