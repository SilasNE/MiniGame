package com.example.marioparty.minigames;

import com.example.marioparty.engine.InputHandler;
import com.example.marioparty.model.Player;
import javafx.scene.layout.Pane;

import java.util.List;


public abstract class MiniGame {

    protected boolean started = false;
    protected boolean finished = false;
    protected Player winner;
    protected List<Player> participants = List.of();
    protected final Pane pane;

    public MiniGame(Pane pane) {
        this.pane = pane;
    }

    public abstract String getName();

    public abstract String getDescription();

    public abstract void update(double dt, InputHandler input);

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

    public List<Player> getParticipants() {
        return participants;
    }
}
