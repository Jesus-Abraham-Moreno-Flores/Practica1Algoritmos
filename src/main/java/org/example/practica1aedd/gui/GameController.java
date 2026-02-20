package org.example.practica1aedd.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.practica1aedd.DeckOfCards.CartaInglesa;
import org.example.practica1aedd.solitaire.*;

import java.util.ArrayList;

public class GameController {

    private final SolitaireGame juego;
    private final BorderPane root;

    // Slots superiores
    private final StackPane[] foundationSlots = new StackPane[4];
    private StackPane drawSlot;
    private StackPane wasteSlot;

    // Columnas del tableau
    private final Pane[] tableauSlots = new Pane[7];

    // Carta seleccionada actualmente para movernos por clicks
    private CartaView cartaSeleccionada = null;
    private int columnaSeleccionada = -1;

    public GameController(SolitaireGame juego, Stage stage) {
        this.juego = juego;
        root = new BorderPane();
        root.getStyleClass().add("mesa");
        root.setTop(crearPanelSuperior());
        root.setCenter(crearPanelTableaux());
        renderizarTablero();
    }

    public BorderPane getRoot() {
        return root;
    }

    public HBox crearPanelSuperior() {
        HBox panel = new HBox(10);
        panel.setPadding(new Insets(12));
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setMinHeight(150);

        for (int i = 0; i < 4; i++) {
            foundationSlots[i] = crearSlotVacio();
            final int idx = i;
            foundationSlots[i].setOnMouseClicked(e -> onFoundationClick(idx));
            panel.getChildren().add(foundationSlots[i]);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        panel.getChildren().add(spacer);

        wasteSlot = crearSlotVacio();
        drawSlot  = crearSlotVacio();
        drawSlot.setOnMouseClicked(e -> onDrawClick());
        panel.getChildren().addAll(wasteSlot, drawSlot);

        return panel;
    }

    public HBox crearPanelTableaux() {
        HBox panel = new HBox(8);
        panel.setPadding(new Insets(4, 12, 12, 12));
        panel.setAlignment(Pos.TOP_CENTER);

        for (int i = 0; i < 7; i++) {
            Pane col = new Pane();
            col.setPrefWidth(80);
            col.setMinWidth(80);
            col.setMaxWidth(80);
            col.setPrefHeight(120);
            final int idx = i;
            col.setOnMouseClicked(e -> onTableauSlotClick(idx));
            tableauSlots[i] = col;
            panel.getChildren().add(col);
        }
        return panel;
    }

    private StackPane crearSlotVacio() {
        StackPane slot = new StackPane();
        slot.getStyleClass().add("slot");
        slot.setPrefSize(80, 120);
        slot.setMinSize(80, 120);
        slot.setMaxSize(80, 120);
        return slot;
    }

    public void renderizarTablero() {
        renderizarFoundations();
        renderizarTableaux();
        renderizarDrawPile();
        renderizarWastePile();
        if (juego.isGameOver()) mostrarVictoria();
    }

    public void renderizarFoundations() {
        var foundations = juego.getFoundation();
        for (int i = 0; i < 4; i++) {
            foundationSlots[i].getChildren().clear();
            CartaInglesa top = foundations.get(i).getUltimaCarta();
            if (top != null) {
                CartaView cv = new CartaView(top);
                cv.setMouseTransparent(true);
                foundationSlots[i].getChildren().add(cv);
            }
        }
    }

    public void renderizarTableaux() {
        var tableaux = juego.getTableau();
        for (int i = 0; i < 7; i++) {
            tableauSlots[i].getChildren().clear();

            ArrayList<CartaInglesa> cartas = tableaux.get(i).getCards();
            double offsetY = 0;

            for (int j = 0; j < cartas.size(); j++) {
                CartaInglesa carta = cartas.get(j);
                CartaView cv = new CartaView(carta);

                cv.setLayoutX(0);
                cv.setLayoutY(offsetY);

                if (j < cartas.size() - 1) {
                    offsetY += carta.isFaceup() ? 22 : 16;
                }

                if (carta.isFaceup()) {
                    final int col = i;
                    final CartaView cvFinal = cv;
                    cv.setOnMouseClicked(e -> {
                        e.consume();
                        onCartaTableauClick(col, cvFinal);
                    });
                } else {
                    cv.setMouseTransparent(true);
                }

                tableauSlots[i].getChildren().add(cv);
            }

            double alturaTotal = offsetY + CartaView.ALTO;
            tableauSlots[i].setPrefHeight(Math.max(alturaTotal, 120));
        }
    }

    public void renderizarDrawPile() {
        drawSlot.getChildren().clear();
        drawSlot.setOnMouseClicked(e -> onDrawClick());

        if (juego.getDrawPile().hayCartas()) {

            CartaInglesa top = juego.getDrawPile().verCarta();
            if (top != null) {
                top.makeFaceDown();
                CartaView cv = new CartaView(top);
                cv.setMouseTransparent(true);
                drawSlot.getChildren().add(cv);
            }
        } else {
            Label lbl = new Label("↺");
            lbl.getStyleClass().add("label-recarga");
            lbl.setMouseTransparent(true);
            drawSlot.getChildren().add(lbl);
        }
    }

    public void renderizarWastePile() {
        wasteSlot.getChildren().clear();
        CartaInglesa top = juego.getWastePile().verCarta();
        if (top != null) {
            CartaView cv = new CartaView(top);
            cv.setOnMouseClicked(e -> {
                e.consume();
                onWasteClick();
            });
            wasteSlot.getChildren().add(cv);
        }
    }

    public void onDrawClick() {
        limpiarSeleccion();
        if (juego.getDrawPile().hayCartas()) {
            juego.drawCards();
        } else {
            juego.reloadDrawPile();
        }
        renderizarTablero();
    }

    public void onWasteClick() {
        limpiarSeleccion();
        if (juego.moveWasteToFoundation()) {
            renderizarTablero();
            return;
        }
        for (int i = 1; i <= 7; i++) {
            if (juego.moveWasteToTableau(i)) {
                renderizarTablero();
                return;
            }
        }
    }

    public void onFoundationClick(int idx) {
        if (cartaSeleccionada != null && columnaSeleccionada == -1) {
            onWasteClick();
        }
        limpiarSeleccion();
    }

    public void onCartaTableauClick(int col, CartaView cv) {
        if (cartaSeleccionada == null) {
            cartaSeleccionada = cv;
            columnaSeleccionada = col;

        } else if (columnaSeleccionada == col && cartaSeleccionada == cv) {
            int colOrigen = columnaSeleccionada;
            limpiarSeleccion();
            if (juego.moveTableauToFoundation(colOrigen + 1)) {
                renderizarTablero();
            }

        } else if (columnaSeleccionada == col && cartaSeleccionada != cv) {

            limpiarSeleccion();
            cartaSeleccionada = cv;
            columnaSeleccionada = col;

        } else {
            int colOrigen = columnaSeleccionada;
            limpiarSeleccion();
            if (juego.moveTableauToTableau(colOrigen + 1, col + 1)) {
                renderizarTablero();
            } else {
                cartaSeleccionada = cv;
                columnaSeleccionada = col;
            }
        }
    }

    public void onTableauSlotClick(int col) {
        if (cartaSeleccionada != null) {
            if (columnaSeleccionada == -1) {
                juego.moveWasteToTableau(col + 1);
            } else {
                juego.moveTableauToTableau(columnaSeleccionada + 1, col + 1);
            }
            limpiarSeleccion();
            renderizarTablero();
        }
    }

    public void limpiarSeleccion() {
        if (cartaSeleccionada != null) {
        }
        cartaSeleccionada = null;
        columnaSeleccionada = -1;
    }

    public void mostrarVictoria() {
        Label msg = new Label("¡Has Ganaste!  ♠ ♥ ♦ ♣");
        msg.getStyleClass().add("victoria");
        root.setBottom(msg);
        BorderPane.setAlignment(msg, Pos.CENTER);
    }
}