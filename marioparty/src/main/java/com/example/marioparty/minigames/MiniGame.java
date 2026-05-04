package com.example.marioparty.minigames;

import com.example.marioparty.engine.InputHandler;
import com.example.marioparty.model.Player;
import javafx.scene.canvas.GraphicsContext;

/**
 * Basisklasse für alle Minispiele. Lebenszyklus:
 *   start()  → update()/render() pro Frame → isFinished() == true → getWinner()
 *
 * Eigenes Minispiel: erben, getName/getDescription/update/render implementieren,
 * am Ende this.winner = ... und this.finished = true setzen.
 */
public abstract class MiniGame {

    protected boolean started = false;
    protected boolean finished = false;
    protected Player winner;

    public abstract String getName();
    public abstract String getDescription();
    public abstract void update(double dt, InputHandler input);
    public abstract void render(GraphicsContext gc);

    public void start()         { started = true; }
    public boolean isStarted()  { return started; }
    public boolean isFinished() { return finished; }
    public Player getWinner()   { return winner; }
}
