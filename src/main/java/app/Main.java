package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.GameEngine;
import logic.MapLoader;
import model.BoardGraph;
import model.Player;
import view.MainBoardController;

import java.util.ArrayList;
import java.util.List;

/**
 * Einstiegspunkt der Anwendung.
 * Baut das Spielbrett, legt die Spieler an und lädt das FXML.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // 1) Modell vorbereiten
        BoardGraph board = MapLoader.loadFromJSON("/maps/demo.json");

        List<Player> players = new ArrayList<>();
        players.add(new Player("Mario",  board.getNode(0)));
        players.add(new Player("Luigi",  board.getNode(0)));
        players.add(new Player("Peach",  board.getNode(0)));
        players.add(new Player("Yoshi",  board.getNode(0)));

        GameEngine engine = new GameEngine(players, board);

        // 2) Oberfläche laden
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainBoard.fxml"));
        Parent root = loader.load();
        MainBoardController controller = loader.getController();
        controller.setEngine(engine);

        // 3) Bühne zeigen
        stage.setTitle("Mario Party Mini");
        stage.setScene(new Scene(root, 1200, 800));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
