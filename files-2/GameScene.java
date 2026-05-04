package game.engine;

import javafx.scene.canvas.GraphicsContext;

/**
 * Basisklasse für alle Szenen (Menü, Board, Minigame ...).
 * Jede konkrete Szene implementiert update() und render().
 *
 * Lebenszyklus:
 *   onEnter() → update()/render() pro Frame → onExit()
 */
public abstract class GameScene {

    protected final GameEngine engine;

    public GameScene(GameEngine engine) {
        this.engine = engine;
    }

    /** Wird aufgerufen, wenn die Szene aktiviert wird. Initialisierung hier. */
    public void onEnter() {}

    /** Wird beim Wechsel zu einer anderen Szene aufgerufen. Aufräumen. */
    public void onExit() {}

    /** Logik-Update. dt = vergangene Sekunden seit letztem Frame. */
    public abstract void update(double dt, InputHandler input);

    /** Zeichnet die Szene. */
    public abstract void render(GraphicsContext gc);
}
