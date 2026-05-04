package com.example.marioparty.engine;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Set;

/**
 * Verwaltet Tastendrücke. Unterscheidet zwei Fälle:
 *   - isDown(key)         → für gehaltene Tasten (z. B. laufen)
 *   - wasJustPressed(key) → für einmalige Aktionen (z. B. springen, würfeln)
 *
 * wasJustPressed verbraucht das Event, d. h. es liefert nur einmal true.
 */
public class InputHandler {

    private final Set<KeyCode> pressed = new HashSet<>();
    private final Set<KeyCode> justPressed = new HashSet<>();

    public InputHandler(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (!pressed.contains(e.getCode())) {
                justPressed.add(e.getCode());
            }
            pressed.add(e.getCode());
        });
        scene.setOnKeyReleased(e -> pressed.remove(e.getCode()));
    }

    public boolean isDown(KeyCode key) {
        return pressed.contains(key);
    }

    /** Liefert true beim ersten Frame nach Tastendruck, danach false. */
    public boolean wasJustPressed(KeyCode key) {
        return justPressed.remove(key);
    }
}
