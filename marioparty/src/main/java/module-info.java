/**
 * Modul-Definition für das Java Platform Module System (JPMS).
 * Für JavaFX seit Java 9 zwingend nötig.
 *
 *   requires javafx.controls  → JavaFX-Basis (Application, Scene, Canvas, Color ...)
 *   exports com.example.marioparty → erlaubt JavaFX, die Application-Klasse zu starten
 */
module com.example.marioparty {
    requires javafx.controls;
    requires java.desktop;

    exports com.example.marioparty;
}
