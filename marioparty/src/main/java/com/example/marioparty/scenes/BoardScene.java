package com.example.marioparty.scenes;

import com.example.marioparty.Main;
import com.example.marioparty.engine.GameEngine;
import com.example.marioparty.engine.GameScene;
import com.example.marioparty.engine.InputHandler;
import com.example.marioparty.model.Board;
import com.example.marioparty.model.Dice;
import com.example.marioparty.model.Field;
import com.example.marioparty.model.GameState;
import com.example.marioparty.model.Player;
import com.example.marioparty.model.graph.BoardKnot;
import com.example.marioparty.model.items.CoinBlockItem;
import com.example.marioparty.model.items.GameItem;
import com.example.marioparty.model.items.ItemCatalog;
import com.example.marioparty.model.items.ItemUseOutcome;
import com.example.marioparty.model.items.TripleMushroomItem;
import com.example.marioparty.model.items.WarpPipeItem;
import com.example.marioparty.ui.board.BoardGraphEdgeLayer;
import com.example.marioparty.ui.board.BoardKnotView;
import com.example.marioparty.ui.board.Split;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BoardScene extends GameScene {

    private enum Phase {
        TURN_ACTION_CHOICE,
        ROLLING,
        MOVING,
        PATH_CHOICE,
        FIELD_ACTION,
        STAR_OFFER,
        SHOP_OFFER,
        ITEM_USE_MENU,
        ITEM_EFFECT_MESSAGE,
        NEXT_TURN
    }

    private Phase phase = Phase.TURN_ACTION_CHOICE;
    private Phase phaseAfterItemEffect = Phase.TURN_ACTION_CHOICE;
    private int diceValue = 1;
    private int stepsLeft = 0;
    private double phaseTimer = 0;

    private double cpuPhaseTimer = 0;

    private List<ImageView> playerNodes;
    private ImageView[] hudFrames;
    private ImageView[] hudPortraits;
    private Rectangle[] hudHighlights;
    private Text[] hudStats;
    private Rectangle diceBox;
    private Image[] diceImages;
    private ImageView diceImageView;
    private Text messageText;

    private List<BoardKnotView> fieldViews;

    private Split splitOverlay;

    private Button starBuyButton;
    private Button starDeclineButton;

    private Button turnRollButton;
    private Button turnItemButton;

    private final VBox shopOfferBox = new VBox(8);
    private Button shopLeaveButton;
    private final boolean testMode;

    private final VBox itemUseBox = new VBox(8);
    private Button itemBackButton;

    public BoardScene(GameEngine engine) {
        this(engine, false);
    }

    public BoardScene(GameEngine engine, boolean testMode) {
        super(engine);
        this.testMode = testMode;
    }

    @Override
    public void onEnter() {
        phase = Phase.TURN_ACTION_CHOICE;
        phaseTimer = 0;
        cpuPhaseTimer = 0;

        Pane pane = engine.getPane();
        GameState state = engine.getState();
        List<Player> players = state.getPlayers();

        pane.getChildren().add(new Rectangle(Main.WIDTH, Main.HEIGHT, Color.web("#0a74cf")));
        addBoardBackdrop(pane);

        Board board = state.getBoard();
        pane.getChildren().add(new BoardGraphEdgeLayer(board));

        fieldViews = new ArrayList<>();
        for (int i = 0; i < state.getBoard().size(); i++) {
            BoardKnot knot = board.getKnot(i);
            BoardKnotView view = new BoardKnotView(knot.getX(), knot.getY(), 22);
            pane.getChildren().add(view);
            fieldViews.add(view);
        }

        playerNodes = new ArrayList<>();
        for (Player p : players) {
            ImageView sprite = new ImageView(loadPlayerImage(p));
            sprite.setFitWidth(42);
            sprite.setFitHeight(42);
            sprite.setPreserveRatio(true);
            sprite.setSmooth(false);
            sprite.setEffect(createSpriteOutline());
            pane.getChildren().add(sprite);
            playerNodes.add(sprite);
        }

        hudFrames = new ImageView[players.size()];
        hudPortraits = new ImageView[players.size()];
        hudHighlights = new Rectangle[players.size()];
        hudStats = new Text[players.size()];
        final double boxW = 232;
        final double boxH = 58;
        final double gap = 10;
        final double totalHudWidth = players.size() * boxW + Math.max(0, players.size() - 1) * gap;
        final double x0 = (Main.WIDTH - totalHudWidth) / 2.0;
        final double hudY = 10;
        for (int i = 0; i < players.size(); i++) {
            double x = x0 + i * (boxW + gap);
            Player hp = players.get(i);
            ImageView frame = new ImageView(loadHudImage(hp));
            frame.setFitWidth(boxW);
            frame.setFitHeight(boxH);
            frame.setPreserveRatio(false);
            frame.setSmooth(false);
            frame.setLayoutX(x);
            frame.setLayoutY(hudY);
            hudFrames[i] = frame;

            Rectangle highlight = new Rectangle(x + 2, hudY + 2, boxW - 8, boxH - 6);
            highlight.setFill(Color.TRANSPARENT);
            highlight.setStroke(Color.TRANSPARENT);
            highlight.setStrokeWidth(4);
            highlight.setArcWidth(12);
            highlight.setArcHeight(12);
            hudHighlights[i] = highlight;

            ImageView portrait = new ImageView(loadPlayerImage(hp));
            portrait.setFitWidth(44);
            portrait.setFitHeight(44);
            portrait.setPreserveRatio(true);
            portrait.setSmooth(false);
            portrait.setEffect(createSpriteOutline());
            portrait.setLayoutX(x + 8);
            portrait.setLayoutY(hudY + 7);
            hudPortraits[i] = portrait;

            Text name = new Text(x + 58, hudY + 20, hp.getName() + (hp.isHuman() ? " (Du)" : " (CPU)"));
            name.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            name.setFill(Color.WHITE);
            name.setStroke(Color.BLACK);
            name.setStrokeWidth(0.45);

            Text stats = new Text(x + 58, hudY + 38, "");
            stats.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            stats.setFill(Color.WHITE);
            stats.setStroke(Color.BLACK);
            stats.setStrokeWidth(0.35);
            stats.setWrappingWidth(boxW - 68);
            hudStats[i] = stats;

            pane.getChildren().addAll(frame, highlight, portrait, name, stats);
        }

        diceBox = new Rectangle(Main.WIDTH / 2.0 - 45, Main.HEIGHT - 130, 90, 90);
        diceBox.setFill(Color.WHITE);
        diceBox.setArcWidth(12);
        diceBox.setArcHeight(12);
        diceBox.setStroke(Color.BLACK);
        diceBox.setStrokeWidth(3);
        diceBox.setVisible(false);

        diceImages = new Image[7];
        for (int value = 1; value <= 6; value++) {
            diceImages[value] = loadDiceImage(value);
        }
        diceImageView = new ImageView(diceImages[diceValue]);
        diceImageView.setFitWidth(84);
        diceImageView.setFitHeight(84);
        diceImageView.setPreserveRatio(true);
        diceImageView.setSmooth(true);
        diceImageView.setVisible(false);

        pane.getChildren().addAll(diceBox, diceImageView);

        pane.getChildren().add(new Rectangle(0, Main.HEIGHT - 50, Main.WIDTH, 50) {{
            setFill(Color.rgb(0, 0, 0, 0.6));
        }});
        messageText = new Text(30, Main.HEIGHT - 18, "");
        messageText.setFont(Font.font("Arial", 22));
        messageText.setFill(Color.WHITE);
        pane.getChildren().add(messageText);

        starBuyButton = new Button("Stern kaufen (" + Board.STAR_COIN_COST + " Münzen)");
        starDeclineButton = new Button("Verzichten");
        styleOverlayButton(starBuyButton);
        styleOverlayButton(starDeclineButton);
        starBuyButton.setPrefWidth(260);
        starDeclineButton.setPrefWidth(160);
        double choiceY = Main.HEIGHT / 2.0 - 30;
        starBuyButton.setLayoutX(Main.WIDTH / 2.0 - 220);
        starBuyButton.setLayoutY(choiceY);
        starDeclineButton.setLayoutX(Main.WIDTH / 2.0 + 40);
        starDeclineButton.setLayoutY(choiceY);
        starBuyButton.setVisible(false);
        starDeclineButton.setVisible(false);
        starBuyButton.setOnAction(e -> onStarPurchaseChoice(true));
        starDeclineButton.setOnAction(e -> onStarPurchaseChoice(false));
        pane.getChildren().addAll(starBuyButton, starDeclineButton);

        turnRollButton = new Button("Würfeln");
        turnItemButton = new Button("Item verwenden");
        styleOverlayButton(turnRollButton);
        styleOverlayButton(turnItemButton);
        turnRollButton.setPrefWidth(200);
        turnItemButton.setPrefWidth(200);
        turnRollButton.setLayoutX(Main.WIDTH / 2.0 - 345);
        turnRollButton.setLayoutY(Main.HEIGHT - 118);
        turnItemButton.setLayoutX(Main.WIDTH / 2.0 + 145);
        turnItemButton.setLayoutY(Main.HEIGHT - 118);
        turnRollButton.setOnAction(e -> onChoseRoll());
        turnItemButton.setOnAction(e -> onChoseOpenItemMenu());
        pane.getChildren().addAll(turnRollButton, turnItemButton);

        shopOfferBox.setAlignment(Pos.CENTER_LEFT);
        shopOfferBox.setLayoutX(Main.WIDTH / 2.0 - 200);
        shopOfferBox.setLayoutY(Main.HEIGHT / 2.0 - 140);
        shopOfferBox.setVisible(false);
        shopOfferBox.setStyle("-fx-background-color: rgba(0,0,0,0.75); -fx-padding: 16; -fx-background-radius: 12;");
        shopLeaveButton = new Button("Shop verlassen");
        styleOverlayButton(shopLeaveButton);
        shopLeaveButton.setOnAction(e -> onShopLeave());
        pane.getChildren().add(shopOfferBox);

        itemUseBox.setAlignment(Pos.CENTER_LEFT);
        itemUseBox.setLayoutX(Main.WIDTH / 2.0 - 200);
        itemUseBox.setLayoutY(Main.HEIGHT / 2.0 - 140);
        itemUseBox.setVisible(false);
        itemUseBox.setStyle("-fx-background-color: rgba(0,0,0,0.75); -fx-padding: 16; -fx-background-radius: 12;");
        itemBackButton = new Button("Zurück");
        styleOverlayButton(itemBackButton);
        itemBackButton.setOnAction(e -> onItemMenuBack());
        pane.getChildren().add(itemUseBox);

        if (testMode) {
            addTestModeControls(pane);
        }

        splitOverlay = null;

        showTurnActionChoice(state.getCurrentPlayer());
        refreshNodes(state);
    }

    private static void styleOverlayButton(Button b) {
        b.setFont(Font.font("Arial", FontWeight.BOLD, 15));
    }

    private static DropShadow createSpriteOutline() {
        DropShadow outline = new DropShadow();
        outline.setBlurType(BlurType.GAUSSIAN);
        outline.setColor(Color.BLACK);
        outline.setRadius(4);
        outline.setSpread(0.82);
        outline.setOffsetX(0);
        outline.setOffsetY(0);
        return outline;
    }

    private void addBoardBackdrop(Pane pane) {
        Ellipse mainIsland = new Ellipse(Main.WIDTH / 2.0 + 40, 400, 455, 250);
        mainIsland.setFill(Color.web("#63c65f"));
        mainIsland.setStroke(Color.web("#fff6a8"));
        mainIsland.setStrokeWidth(5);

        Ellipse upperIsland = new Ellipse(560, 210, 250, 95);
        upperIsland.setFill(Color.web("#7bd86f"));
        upperIsland.setStroke(Color.rgb(255, 255, 255, 0.65));
        upperIsland.setStrokeWidth(4);

        Ellipse lowerIsland = new Ellipse(500, 535, 350, 105);
        lowerIsland.setFill(Color.web("#58bb59"));
        lowerIsland.setStroke(Color.rgb(255, 255, 255, 0.5));
        lowerIsland.setStrokeWidth(4);

        Ellipse leftCloud = new Ellipse(120, 610, 120, 55);
        leftCloud.setFill(Color.rgb(255, 255, 255, 0.35));
        Ellipse rightCloud = new Ellipse(890, 610, 150, 60);
        rightCloud.setFill(Color.rgb(255, 255, 255, 0.35));

        pane.getChildren().addAll(leftCloud, rightCloud, mainIsland, lowerIsland, upperIsland);
    }

    private Image loadDiceImage(int value) {
        String path = "/images/dice" + value + ".png";
        return new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(path),
                "Dice image missing: " + path));
    }

    private Image loadPlayerImage(Player player) {
        String path = switch (player.getName()) {
            case "Mario" -> "/images/Mario.png";
            case "Luigi" -> "/images/Luigi.png";
            case "Wario" -> "/images/Wario.png";
            case "Donkey Kong" -> "/images/DonkeyKong.png";
            default -> "/images/Mario.png";
        };
        return new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(path),
                "Player image missing: " + path));
    }

    private Image loadHudImage(Player player) {
        String path = switch (player.getName()) {
            case "Mario" -> "/images/roteHUD.png";
            case "Luigi" -> "/images/grueneHUD.png";
            case "Wario" -> "/images/gelbeHUD.png";
            case "Donkey Kong" -> "/images/blaueHUD.png";
            default -> "/images/roteHUD.png";
        };
        return new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(path),
                "HUD image missing: " + path));
    }

    private ImageView createItemIcon(GameItem item) {
        String path = switch (item.getId()) {
            case WarpPipeItem.ID -> "/images/roehre.png";
            case TripleMushroomItem.ID -> "/images/pilz.png";
            case CoinBlockItem.ID -> "/images/muenzblock.png";
            default -> null;
        };
        if (path == null) {
            return null;
        }
        ImageView icon = new ImageView(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(path),
                "Item image missing: " + path)));
        icon.setFitWidth(44);
        icon.setFitHeight(44);
        icon.setPreserveRatio(true);
        icon.setSmooth(false);
        return icon;
    }

    private void addTestModeControls(Pane pane) {
        VBox testBox = new VBox(6);
        testBox.setLayoutX(Main.WIDTH - 180);
        testBox.setLayoutY(Main.HEIGHT - 190);
        testBox.setStyle("-fx-background-color: rgba(0,0,0,0.65); -fx-padding: 8; -fx-background-radius: 10;");

        Text title = new Text("Testmodus");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Button shop = new Button("Itemshop öffnen");
        Button coins = new Button("+20 Münzen");
        Button miniGame = new Button("Minispiel starten");
        Button end = new Button("Testbrett beenden");
        styleTestModeButton(shop);
        styleTestModeButton(coins);
        styleTestModeButton(miniGame);
        styleTestModeButton(end);

        shop.setOnAction(e -> openDebugShop());
        coins.setOnAction(e -> {
            Player current = engine.getState().getCurrentPlayer();
            current.addCoins(20);
            messageText.setText("Testmodus: " + current.getName() + " bekommt +20 Münzen.");
        });
        miniGame.setOnAction(e -> engine.setScene(new MiniGameScene(engine, null, false, true)));
        end.setOnAction(e -> engine.setScene(new TestModeScene(engine, engine.getState().getStarsToWin())));

        testBox.getChildren().addAll(title, shop, coins, miniGame, end);
        pane.getChildren().add(testBox);
        testBox.toFront();
    }

    private static void styleTestModeButton(Button b) {
        b.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        b.setPrefWidth(150);
        b.setPrefHeight(28);
    }

    private void openDebugShop() {
        Player current = engine.getState().getCurrentPlayer();
        removeSplit();
        hideTurnActionChoice();
        hideItemUseMenu();
        hideStarChoiceButtons();
        messageText.setText("Test-Shop für " + current.getName());
        rebuildShopOffer(current);
        shopOfferBox.setVisible(true);
        shopOfferBox.toFront();
        phase = Phase.SHOP_OFFER;
        phaseTimer = 0;
        cpuPhaseTimer = 0;
    }

    private void showTurnActionChoice(Player current) {
        boolean human = current.isHuman();
        turnRollButton.setVisible(human);
        turnItemButton.setVisible(human);
        turnItemButton.setDisable(!current.hasUsableItems());
        if (human) {
            turnRollButton.toFront();
            turnItemButton.toFront();
        }
        starBuyButton.toFront();
        starDeclineButton.toFront();
        cpuPhaseTimer = 0;
    }

    private void hideTurnActionChoice() {
        turnRollButton.setVisible(false);
        turnItemButton.setVisible(false);
    }

    private void onChoseRoll() {
        if (phase != Phase.TURN_ACTION_CHOICE) {
            return;
        }
        hideTurnActionChoice();
        hideItemUseMenu();
        phase = Phase.ROLLING;
        phaseTimer = 0;
        cpuPhaseTimer = 0;
    }

    private void onChoseOpenItemMenu() {
        if (phase != Phase.TURN_ACTION_CHOICE) {
            return;
        }
        Player current = engine.getState().getCurrentPlayer();
        if (!current.hasUsableItems()) {
            return;
        }
        hideTurnActionChoice();
        rebuildItemUseMenu(current);
        itemUseBox.setVisible(true);
        itemUseBox.toFront();
        starBuyButton.toFront();
        starDeclineButton.toFront();
        phase = Phase.ITEM_USE_MENU;
    }

    private void onItemMenuBack() {
        if (phase != Phase.ITEM_USE_MENU) {
            return;
        }
        hideItemUseMenu();
        showTurnActionChoice(engine.getState().getCurrentPlayer());
        phase = Phase.TURN_ACTION_CHOICE;
    }

    private void hideItemUseMenu() {
        itemUseBox.setVisible(false);
        itemUseBox.getChildren().clear();
    }

    private void rebuildItemUseMenu(Player player) {
        itemUseBox.getChildren().clear();
        Text title = new Text(player.getName() + " — Item wählen:");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        itemUseBox.getChildren().add(title);
        itemUseBox.getChildren().add(itemBackButton);
        for (GameItem item : new ArrayList<>(player.getInventoryView())) {
            Button b = new Button(item.getDisplayName() + "  (nutzen)");
            styleOverlayButton(b);
            b.setMaxWidth(360);
            b.setOnAction(e -> onUseInventoryItem(player, item));
            itemUseBox.getChildren().add(b);
        }
    }

    private void onUseInventoryItem(Player player, GameItem item) {
        if (phase != Phase.ITEM_USE_MENU) {
            return;
        }
        if (!player.getInventory().contains(item)) {
            return;
        }
        resolveItemUse(player, item);
    }

    private void resolveItemUse(Player player, GameItem item) {
        if (!player.getInventory().contains(item)) {
            return;
        }
        Board board = engine.getState().getBoard();
        ItemUseOutcome out = new ItemUseOutcome();
        item.use(player, board, out);
        hideItemUseMenu();
        hideTurnActionChoice();
        messageText.setText(out.getMessage());
        if (out.hasTeleport()) {
            player.setBoardKnotId(out.getTeleportToKnotId());
            phaseAfterItemEffect = Phase.FIELD_ACTION;
        } else {
            phaseAfterItemEffect = Phase.TURN_ACTION_CHOICE;
        }
        phase = Phase.ITEM_EFFECT_MESSAGE;
        phaseTimer = 0;
        starBuyButton.toFront();
        starDeclineButton.toFront();
    }

    private void finishItemEffectMessage(Player player) {
        phase = phaseAfterItemEffect;
        phaseTimer = 0;
        if (phase == Phase.TURN_ACTION_CHOICE) {
            showTurnActionChoice(player);
            starBuyButton.toFront();
            starDeclineButton.toFront();
        }
    }

    private static GameItem pickCpuItemToUse(Player player, Board board) {
        if (!player.hasUsableItems()) {
            return null;
        }
        int distanceToStar = board.bsDistance(player.getBoardKnotId(), board.getStarKnotId());

        GameItem pipe = null;
        GameItem block = null;
        GameItem mushroom = null;
        for (GameItem item : player.getInventoryView()) {
            if (WarpPipeItem.ID.equals(item.getId())) {
                pipe = item;
            } else if (CoinBlockItem.ID.equals(item.getId())) {
                block = item;
            } else if (TripleMushroomItem.ID.equals(item.getId())) {
                mushroom = item;
            }
        }

        if (pipe != null && distanceToStar >= 4) {
            return pipe;
        }
        if (block != null && player.getCoins() < Board.STAR_COIN_COST) {
            return block;
        }
        if (mushroom != null && distanceToStar >= 5) {
            return mushroom;
        }
        return null;
    }

    private void hideShopOffer() {
        shopOfferBox.setVisible(false);
        shopOfferBox.getChildren().clear();
    }

    private void rebuildShopOffer(Player player) {
        shopOfferBox.getChildren().clear();
        Text title = new Text("Item-Shop — " + player.getName() + "  (" + player.getCoins() + " Münzen)");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        shopOfferBox.getChildren().add(title);
        for (GameItem template : ItemCatalog.shopTemplates()) {
            int price = template.getShopPrice();
            Button b = new Button(template.getDisplayName() + " — " + price + " Münzen");
            styleOverlayButton(b);
            b.setPrefWidth(380);
            b.setMinHeight(58);
            b.setAlignment(Pos.CENTER_LEFT);
            b.setGraphic(createItemIcon(template));
            b.setContentDisplay(ContentDisplay.LEFT);
            b.setGraphicTextGap(14);
            b.setDisable(player.getCoins() < price);
            GameItem toBuy = template;
            b.setOnAction(e -> onShopBuy(player, toBuy));
            shopOfferBox.getChildren().add(b);
        }
        shopOfferBox.getChildren().add(shopLeaveButton);
    }

    private void onShopBuy(Player player, GameItem template) {
        if (phase != Phase.SHOP_OFFER) {
            return;
        }
        int price = template.getShopPrice();
        if (player.getCoins() < price) {
            return;
        }
        player.addCoins(-price);
        player.addToInventory(template.copyForInventory());
        messageText.setText(player.getName() + " kauft: " + template.getDisplayName() + "  (Shop verlassen = Zug Ende)");
        rebuildShopOffer(player);
        shopOfferBox.toFront();
        starBuyButton.toFront();
        starDeclineButton.toFront();
    }

    private void onShopLeave() {
        if (phase != Phase.SHOP_OFFER) {
            return;
        }
        messageText.setText(engine.getState().getCurrentPlayer().getName() + " verlässt den Shop.");
        hideShopOffer();
        phase = Phase.NEXT_TURN;
        phaseTimer = 0;
    }

    private void removeSplit() {
        if (splitOverlay != null) {
            engine.getPane().getChildren().remove(splitOverlay);
            splitOverlay = null;
        }
    }

    private void showSplit(GameState state, Board board, Player mover, int forkKnotId, List<Integer> targets) {
        removeSplit();
        if (targets.size() < 2) {
            return;
        }
        int a = targets.get(0);
        int b = targets.get(1);
        BoardKnot from = board.getKnot(forkKnotId);
        BoardKnot ka = board.getKnot(a);
        BoardKnot kb = board.getKnot(b);
        splitOverlay = new Split(
                from.getX(), from.getY(),
                ka.getX(), ka.getY(), a,
                kb.getX(), kb.getY(), b,
                chosenKnotId -> onSplitChosen(state, mover, chosenKnotId)
        );
        engine.getPane().getChildren().add(splitOverlay);
        splitOverlay.toFront();
        starBuyButton.toFront();
        starDeclineButton.toFront();
    }

    private void onSplitChosen(GameState state, Player mover, int chosenKnotId) {
        if (phase != Phase.PATH_CHOICE) {
            return;
        }
        removeSplit();
        mover.setBoardKnotId(chosenKnotId);
        stepsLeft--;
        messageText.setText(mover.getName() + " nimmt den gewählten Weg.");
        if (stepsLeft <= 0) {
            phase = Phase.FIELD_ACTION;
        } else {
            phase = Phase.MOVING;
        }
        phaseTimer = 0;
    }

    private void hideStarChoiceButtons() {
        starBuyButton.setVisible(false);
        starDeclineButton.setVisible(false);
    }

    private void onStarPurchaseChoice(boolean buy) {
        if (phase != Phase.STAR_OFFER) {
            return;
        }
        removeSplit();
        GameState state = engine.getState();
        Player current = state.getCurrentPlayer();
        Board board = state.getBoard();
        hideStarChoiceButtons();
        if (buy) {
            if (current.getCoins() >= Board.STAR_COIN_COST) {
                current.addStars(1);
                current.addCoins(-Board.STAR_COIN_COST);
                board.respawnStarAfterPurchase();
                messageText.setText(current.getName() + " kauft einen Stern! (-"
                        + Board.STAR_COIN_COST + " Münzen) — der Stern wandert!");
            } else {
                messageText.setText(current.getName() + " hat nicht genug Münzen für den Stern.");
            }
        } else {
            messageText.setText(current.getName() + " verzichtet auf den Stern.");
        }
        phase = Phase.NEXT_TURN;
        phaseTimer = 0;
    }

    @Override
    public void update(double dt, InputHandler input) {
        GameState state = engine.getState();
        Player current = state.getCurrentPlayer();

        switch (phase) {
            case TURN_ACTION_CHOICE -> {
                messageText.setText(current.getName() + " — Würfeln oder Item?");
                turnItemButton.setDisable(!current.hasUsableItems());
                if (current.isHuman()) {
                    if (input.wasJustPressed(KeyCode.SPACE)) {
                        onChoseRoll();
                    }
                } else {
                    cpuPhaseTimer += dt;
                    if (cpuPhaseTimer >= 0.55) {
                        cpuPhaseTimer = 0;
                        GameItem use = pickCpuItemToUse(current, state.getBoard());
                        if (use != null) {
                            resolveItemUse(current, use);
                        } else {
                            onChoseRoll();
                        }
                    }
                }
            }
            case ROLLING -> {
                phaseTimer += dt;
                diceValue = Dice.roll();
                double rollAnimEnd = 1.0;
                if (phaseTimer > rollAnimEnd) {
                    diceValue = Dice.roll();
                    int bonus = current.getRollBonus();
                    stepsLeft = diceValue + bonus;
                    current.clearRollBonus();
                    phase = Phase.MOVING;
                    phaseTimer = 0;
                    String extra = bonus > 0 ? " (+" + bonus + " Item-Bonus)" : "";
                    messageText.setText(current.getName() + " würfelt eine " + diceValue + extra
                            + " → " + stepsLeft + " Schritte!");
                }
            }
            case MOVING -> {
                phaseTimer += dt;
                double stepDelay = 0.3;
                if (phaseTimer > stepDelay && stepsLeft > 0) {
                    Board board = state.getBoard();
                    int here = current.getBoardKnotId();
                    List<Integer> next = board.getTargetKnotIds(here);
                    if (next.size() == 1) {
                        current.setBoardKnotId(next.get(0));
                        stepsLeft--;
                        phaseTimer = 0;
                    } else if (next.size() > 1) {
                        if (current.isHuman()) {
                            showSplit(state, board, current, here, next);
                            phase = Phase.PATH_CHOICE;
                            phaseTimer = 0;
                        } else {
                            int star = board.getStarKnotId();
                            int pick = board.pickSuccessorTowardStar(star, next);
                            current.setBoardKnotId(pick);
                            stepsLeft--;
                            phaseTimer = 0;
                            messageText.setText(current.getName() + " (CPU) — Weg Richtung Stern (BS).");
                        }
                    } else {
                        stepsLeft = 0;
                        phaseTimer = 0;
                    }
                }
                if (stepsLeft == 0 && phase == Phase.MOVING) {
                    phase = Phase.FIELD_ACTION;
                    phaseTimer = 0;
                }
            }
            case PATH_CHOICE -> {
                messageText.setText(current.getName() + ": Weg wählen — Pfeil anklicken!");
            }
            case FIELD_ACTION -> {
                removeSplit();
                Board board = state.getBoard();
                int pos = current.getBoardKnotId();
                Field.Type t = board.getKnot(pos).getFieldType();
                if (board.isStarAt(pos)) {
                    if (current.getCoins() >= Board.STAR_COIN_COST) {
                        messageText.setText(current.getName() + " ist beim Stern — kaufen?");
                        cpuPhaseTimer = 0;
                        if (current.isHuman()) {
                            starBuyButton.setVisible(true);
                            starDeclineButton.setVisible(true);
                            starBuyButton.toFront();
                            starDeclineButton.toFront();
                        } else {
                            starBuyButton.setVisible(false);
                            starDeclineButton.setVisible(false);
                        }
                        phase = Phase.STAR_OFFER;
                    } else {
                        messageText.setText(current.getName() + " ist beim Stern, hat aber nur "
                                + current.getCoins() + " Münzen (Kosten: " + Board.STAR_COIN_COST + ").");
                        phase = Phase.NEXT_TURN;
                    }
                } else if (t == Field.Type.ITEM_SHOP) {
                    messageText.setText(current.getName() + " betritt den Item-Shop!");
                    cpuPhaseTimer = 0;
                    if (current.isHuman()) {
                        rebuildShopOffer(current);
                        shopOfferBox.setVisible(true);
                        shopOfferBox.toFront();
                        starBuyButton.toFront();
                        starDeclineButton.toFront();
                    } else {
                        hideShopOffer();
                    }
                    phase = Phase.SHOP_OFFER;
                } else {
                    Field f = board.getField(pos);
                    f.onLand(current);
                    messageText.setText(describeFieldEffect(current, f));
                    phase = Phase.NEXT_TURN;
                }
                phaseTimer = 0;
            }
            case STAR_OFFER -> {
                if (!current.isHuman()) {
                    cpuPhaseTimer += dt;
                    if (cpuPhaseTimer >= 0.55) {
                        cpuPhaseTimer = 0;
                        boolean buy = current.getCoins() >= Board.STAR_COIN_COST;
                        onStarPurchaseChoice(buy);
                    }
                }
            }
            case SHOP_OFFER -> {
                if (!current.isHuman()) {
                    cpuPhaseTimer += dt;
                    if (cpuPhaseTimer >= 0.6) {
                        cpuPhaseTimer = 0;
                        Board board = state.getBoard();
                        boolean bought = false;
                        for (GameItem template : ItemCatalog.shopTemplates()) {
                            if (current.getCoins() >= template.getShopPrice()) {
                                onShopBuy(current, template);
                                bought = true;
                                break;
                            }
                        }
                        if (bought) {
                            messageText.setText(current.getName() + " (CPU) kauft im Shop — Zug Ende.");
                        }
                        onShopLeave();
                    }
                }
            }
            case ITEM_USE_MENU -> {
                if (!current.isHuman()) {
                    cpuPhaseTimer += dt;
                    if (cpuPhaseTimer >= 0.35) {
                        cpuPhaseTimer = 0;
                        onItemMenuBack();
                    }
                }
            }
            case ITEM_EFFECT_MESSAGE -> {
                phaseTimer += dt;
                if (phaseTimer >= 1.6) {
                    finishItemEffectMessage(current);
                }
            }
            case NEXT_TURN -> {
                hideTurnActionChoice();
                phaseTimer += dt;
                if (phaseTimer > 1.5) {
                    removeSplit();
                    hideShopOffer();
                    hideItemUseMenu();
                    if (!testMode && state.isGameOver()) {
                        engine.setScene(new MenuScene(engine));
                        return;
                    }
                    state.nextPlayer();
                    if (!testMode && state.isGameOver()) {
                        engine.setScene(new MenuScene(engine));
                        return;
                    }
                    if (state.getCurrentPlayerIndex() == 0) {
                        engine.setScene(new MiniGameScene(engine, null, false, testMode));
                        return;
                    }
                    phase = Phase.TURN_ACTION_CHOICE;
                    showTurnActionChoice(state.getCurrentPlayer());
                    phaseTimer = 0;
                }
            }
        }

        refreshNodes(state);
    }

    private void refreshNodes(GameState state) {
        List<Player> players = state.getPlayers();
        Board board = state.getBoard();

        for (int i = 0; i < board.size(); i++) {
            BoardKnot knot = board.getKnot(i);
            boolean starHere = board.isStarAt(i);
            fieldViews.get(i).applyFieldTypeColor(knot.getFieldType(), starHere);
        }

        Map<Integer, Integer> occupiedSlots = new HashMap<>();
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            Field f = state.getBoard().getField(p.getBoardKnotId());
            int slot = occupiedSlots.getOrDefault(p.getBoardKnotId(), 0);
            occupiedSlots.put(p.getBoardKnotId(), slot + 1);
            double[][] offsets = {
                    {-14, -16},
                    {14, -16},
                    {-14, 14},
                    {14, 14}
            };
            double offsetX = offsets[Math.min(slot, offsets.length - 1)][0];
            double offsetY = offsets[Math.min(slot, offsets.length - 1)][1];
            ImageView sprite = playerNodes.get(i);
            sprite.setLayoutX(f.getX() + offsetX - sprite.getFitWidth() / 2.0);
            sprite.setLayoutY(f.getY() + offsetY - sprite.getFitHeight() / 2.0);
        }

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            boolean active = (i == state.getCurrentPlayerIndex());
            hudHighlights[i].setStroke(active ? Color.YELLOW : Color.TRANSPARENT);
            int inv = p.getInventory().size();
            hudStats[i].setText("★ " + p.getStars() + "   Münzen " + p.getCoins() + "\nItems " + inv);
        }

        boolean showDice = phase == Phase.ROLLING || phase == Phase.MOVING;
        diceBox.setVisible(showDice);
        diceImageView.setVisible(showDice);
        if (showDice) {
            diceImageView.setImage(diceImages[diceValue]);
            double x = Main.WIDTH / 2.0 - diceImageView.getFitWidth() / 2.0;
            double y = Main.HEIGHT - 130 + (90 - diceImageView.getFitHeight()) / 2.0;
            diceImageView.setLayoutX(x);
            diceImageView.setLayoutY(y);
        }
    }

    private String describeFieldEffect(Player p, Field f) {
        return switch (f.getType()) {
            case BLUE -> p.getName() + " landet auf BLAU: +3 Münzen";
            case RED -> p.getName() + " landet auf ROT: -3 Münzen";
            case STAR -> p.getName() + " landet auf einem Sternfeld!";
            case EVENT -> p.getName() + " landet auf einem Event-Feld!";
            case START -> p.getName() + " erreicht das Startfeld";
            case ITEM_SHOP -> p.getName() + " am Item-Shop.";
        };
    }
}
