package com.example.marioparty.engine;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

import java.util.HashSet;
import java.util.Set;

/**
 * Verwaltet Tastendruecke und Mauszustand.
 *   - isDown(key)           fuer gehaltene Tasten
 *   - wasJustPressed(key)   einmalige Aktion, verbraucht das Event
 *   - wasMouseJustPressed()  einmalig true pro Mausklick
 */
public class InputHandler {

    private final Set<KeyCode> pressed     = new HashSet<>();
    private final Set<KeyCode> justPressed = new HashSet<>();

    private double mouseX = 0;
    private double mouseY = 0;
    private boolean mouseJustPressed = false;
    private MouseButton lastMouseButton = MouseButton.NONE;

    public InputHandler(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (!pressed.contains(e.getCode())) justPressed.add(e.getCode());
            pressed.add(e.getCode());
        });
        scene.setOnKeyReleased(e -> pressed.remove(e.getCode()));

        scene.setOnMouseMoved(e ->   { mouseX = e.getX(); mouseY = e.getY(); });
        scene.setOnMouseDragged(e -> { mouseX = e.getX(); mouseY = e.getY(); });
        scene.setOnMousePressed(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
            mouseJustPressed = true;
            lastMouseButton  = e.getButton();
        });
    }

    public boolean isDown(KeyCode key)         { return pressed.contains(key); }
    public boolean wasJustPressed(KeyCode key)  { return justPressed.remove(key); }

    public boolean wasMouseJustPressed() {
        if (mouseJustPressed) { mouseJustPressed = false; return true; }
        return false;
    }

    public double      getMouseX()           { return mouseX; }
    public double      getMouseY()           { return mouseY; }
    public MouseButton getLastMouseButton()   { return lastMouseButton; }
}
