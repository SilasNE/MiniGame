package com.example.marioparty.minigames;

import com.example.marioparty.engine.InputHandler;
import com.example.marioparty.model.Player;
import javafx.scene.layout.Pane;

/**
 * Minispiel-Basis: läuft im gleichen {@link Pane} wie die {@code MiniGameScene}
 * (JavaFX Scene Graph — keine Canvas-/GraphicsContext-API).
 */
public abstract class MiniGame {

    protected boolean started = false;
    protected boolean finished = false;
    protected Player winner;
    protected final Pane pane;

    public MiniGame(Pane pane) {
        this.pane = pane;
    }

    public abstract String getName();

    public abstract String getDescription();

    public abstract void update(double dt, InputHandler input);

    /** Nach {@link #start()}: Nodes ins {@link #pane} legen. */
    protected void onStart() {}

    public void start() {
        started = true;
        onStart();
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isFinished() {
        return finished;
    }

    public Player getWinner() {
        return winner;
    }
}
