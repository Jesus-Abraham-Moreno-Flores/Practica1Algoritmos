package org.example.practica1aedd.solitaire;


import org.example.practica1aedd.DeckOfCards.Carta;
import org.example.practica1aedd.DeckOfCards.CartaInglesa;
import org.example.practica1aedd.DeckOfCards.Palo;
import org.example.practica1aedd.gui.Pila;
import org.example.practica1aedd.solitaire.TableauDeck;
import org.example.practica1aedd.solitaire.WastePile;


import java.lang.reflect.Array;
import java.util.ArrayList;
/**
 * Juego de solitario.
 *
 * @author (Cecilia Curlango Rosas)
 * @version (2025-2)
 */
public class SolitaireGame {
    ArrayList<TableauDeck> tableau = new ArrayList<>();
    ArrayList<FoundationDeck> foundation = new ArrayList<>();
    FoundationDeck lastFoundationUpdated;
    DrawPile drawPile;
    WastePile wastePile;
    private Pila<Movimiento> historial = new Pila<>(200);

    public SolitaireGame() {
        drawPile = new DrawPile();
        wastePile = new WastePile();
        createTableaux();
        createFoundations();
        wastePile.addCartas(drawPile.retirarCartas());
    }

    /**
     * Move cards from Waste pile to Draw Pile.
     */
    public void reloadDrawPile() {
        historial.push(capturarEstado(Movimiento.Tipo.RELOAD));
        ArrayList<CartaInglesa> cards = wastePile.emptyPile();
        drawPile.recargar(cards);
    }

    /**
     * Move cards from Draw pile to Waste Pile.
     */
    public void drawCards() {
        historial.push(capturarEstado(Movimiento.Tipo.DRAW));
        ArrayList<CartaInglesa> cards = drawPile.retirarCartas();
        wastePile.addCartas(cards);
    }

    /**
     * Tomar la carta del Waste pile y ponerla en el tableau
     *
     * @param tableauDestino donde se coloca la carta
     * @return true si se pudo hacer el movimiento, false si no
     */
    public boolean moveWasteToTableau(int tableauDestino) {
        historial.push(capturarEstado(Movimiento.Tipo.WASTE_TO_TABLEAU));
        boolean movimientoRealizado = false;
        TableauDeck destino = tableau.get(tableauDestino - 1);
        if (moveWasteToTableau(destino)) {
            movimientoRealizado = true;
        }
        if(!movimientoRealizado){
            historial.pop();
        }
        return movimientoRealizado;
    }

    /**
     * Tomar varias cartas del Tableau fuente y colocarlas en el
     * Tableau destino.
     *
     * @param tableauFuente  de donde se toma la carta (1-7)
     * @param tableauDestino donde se coloca la carta (1-7)
     * @return true si se pudo hacer el movimiento, false si no
     */
    public boolean moveTableauToTableau(int tableauFuente, int tableauDestino) {
        historial.push(capturarEstado(Movimiento.Tipo.TABLEAU_TO_TABLEAU));
        boolean movimientoRealizado = false;
        TableauDeck fuente = tableau.get(tableauFuente - 1);
        if (!fuente.isEmpty()) {
            TableauDeck destino = tableau.get(tableauDestino - 1);
            int valorQueDebeTener;
            if(!destino.isEmpty()) {
                valorQueDebeTener = destino.verUltimaCarta().getValor() - 1;
            } else {
                valorQueDebeTener = 13;
            }
            CartaInglesa cartaInicial = fuente.viewCardStartingAt(valorQueDebeTener);
            if(cartaInicial != null && destino.sePuedeAgregarCarta(cartaInicial)){
                ArrayList<CartaInglesa> cartas = fuente.removeStartingAt(valorQueDebeTener);
                if(destino.agregarBloqueDeCartas(cartas)){
                    if(!fuente.isEmpty()){
                        fuente.verUltimaCarta().makeFaceUp();
                    }
                    movimientoRealizado = true;
                }
            }
        }
        if(!movimientoRealizado){
            historial.pop();
        }
        return movimientoRealizado;
    }


    /**
     * Tomar la carta de Tableau y colocarla en el Foundation.
     *
     * @param numero de tableau donde se moverá la carta (1-7)
     * @return true si se pudo move la carta, false si no
     */
    public boolean moveTableauToFoundation(int numero) {
        historial.push(capturarEstado(Movimiento.Tipo.TABLEAU_TO_FOUNDATION));
        TableauDeck fuente = tableau.get(numero - 1);
        CartaInglesa carta = fuente.getUltimaCarta();
        if(carta == null){
            historial.pop();
            return false;
        }
        int cualFoundation = carta.getPalo().ordinal();
        FoundationDeck destino = foundation.get(cualFoundation);
        if(destino.sePuedeAgregar(carta)){
            fuente.removerUltimaCarta();
            destino.agregarCarta(carta);
            lastFoundationUpdated = destino;
            return true;
        }
        historial.pop();
        return false;
    }

    /**
     * Tomar la carta de Waste y colocarla en el Tableau.
     *
     * @param tableau donde se moverá la carta
     * @return true si se pudo move la carta, false si no
     */
    public boolean moveWasteToTableau(TableauDeck tableau) {
        boolean movimientoRealizado = false;

        CartaInglesa carta = wastePile.verCarta();
        if (moveCartaToTableau(carta, tableau)) {
            // si es movimiento válido, elimina la carta de la pila
            carta = wastePile.getCarta();
            movimientoRealizado = true;
        }
        return movimientoRealizado;
    }

    /**
     * Tomar una carta de Waste y ponerla en una de las Foundations.
     *
     * @return true si se pudo hacer el movimiento.
     */
    public boolean moveWasteToFoundation() {
        historial.push(capturarEstado(Movimiento.Tipo.WASTE_TO_FOUNDATION));
        boolean movimientoRealizado = false;

        CartaInglesa carta = wastePile.verCarta();
        if (moveCartaToFoundation(carta)) {
            // si es movimiento válido, elimina la carta de la pila
            carta = wastePile.getCarta();
            movimientoRealizado = true;
        }
        if(!movimientoRealizado){
            historial.pop(); //Si no hubo movimiento, se descarta el snapshot
        }
        return movimientoRealizado;
    }

    /**
     * Coloca la carta recibida en el Tableau recibido.
     *
     * @param carta   a colocar
     * @param destino Tableau que recibe la carta.
     * @return true si se pudo hacer el movimiento, false si no
     */
    private boolean moveCartaToTableau(CartaInglesa carta, TableauDeck destino) {
        return destino.agregarCarta(carta);
    }

    /**
     * Coloca la carta recibida en el Foundation correspondiente.
     *
     * @param carta a colocar
     * @return true si se pudo hacer el movimiento, false si no.
     */
    private boolean moveCartaToFoundation(CartaInglesa carta) {
        int cualFoundation = carta.getPalo().ordinal();
        FoundationDeck destino = foundation.get(cualFoundation);
        lastFoundationUpdated = destino;
        return destino.agregarCarta(carta);
    }

    public boolean moveFoundationToTableau(int foundationIdx, int tableauDestino){
        historial.push(capturarEstado(Movimiento.Tipo.FOUNDATION_TO_TABLEAU));
        FoundationDeck fuente = foundation.get(foundationIdx);
        if(fuente.estaVacio()){
            historial.pop();
            return false;
        }
        TableauDeck destino = tableau.get(tableauDestino - 1);
        CartaInglesa carta = fuente.getUltimaCarta();
        if(destino.sePuedeAgregarCarta(carta)){
            fuente.removerUltimaCarta();
            destino.agregarCarta(carta);
            return true;
        }
        historial.pop();
        return false;
    }

    /**
     * Determina si se terminó el juego. El juego se
     * termina cuando todas las cartas están en Foundation
     *
     * @return true si se terminó el juego
     */
    public boolean isGameOver() {
        boolean gameOver = true;
        for (FoundationDeck foundation : foundation) {
            if (foundation.estaVacio()) {
                gameOver = false;
            } else {
                CartaInglesa ultimaCarta = foundation.getUltimaCarta();
                // si la última carta no es rey, no se ha terminado
                if (ultimaCarta.getValor() != 13) {
                    gameOver = false;
                }
            }
        }
        return gameOver;
    }

    private void createFoundations() {
        for (Palo palo : Palo.values()) {
            foundation.add(new FoundationDeck(palo));
        }
    }

    private void createTableaux() {
        for (int i = 0; i < 7; i++) {
            TableauDeck tableauDeck = new TableauDeck();
            tableauDeck.inicializar(drawPile.getCartas(i + 1));
            tableau.add(tableauDeck);
        }
    }

    public DrawPile getDrawPile() {
        return drawPile;
    }

    public ArrayList<TableauDeck> getTableau() {
        return tableau;
    }

    public ArrayList<FoundationDeck> getFoundation() {return foundation;}

    public WastePile getWastePile() {
        return wastePile;
    }

    public FoundationDeck getLastFoundationUpdated() {
        return lastFoundationUpdated;
    }

    //Metodo principal para la captura de movimientos realizados por el jugador justo en el momento
    public Movimiento capturarEstado(Movimiento.Tipo tipo){
        ArrayList<Movimiento.EstadoCarta> drawSnap = new ArrayList<>();
        for(CartaInglesa carta : drawPile.toList()){
            drawSnap.add(new Movimiento.EstadoCarta(carta));
        }
        ArrayList<Movimiento.EstadoCarta> wasteSnap = new ArrayList<>();
        for(CartaInglesa carta : wastePile.toList()){
            wasteSnap.add(new Movimiento.EstadoCarta(carta));
        }
        ArrayList<ArrayList<Movimiento.EstadoCarta>> tableauSnap = new ArrayList<>();
        for(TableauDeck deck : tableau){
            ArrayList<Movimiento.EstadoCarta> colSnap = new ArrayList<>();
            for(CartaInglesa carta : deck.toList()){
                colSnap.add(new Movimiento.EstadoCarta(carta));
            }
            tableauSnap.add(colSnap);
        }
        ArrayList<Movimiento.EstadoCarta>[] foundationSnap = new ArrayList[4];
        for(int i = 0; i < 4; i++){
            foundationSnap[i] = new ArrayList<>();
            for(CartaInglesa carta : foundation.get(i).toList()){
                foundationSnap[i].add(new Movimiento.EstadoCarta(carta));
            }
        }
        return new Movimiento(tipo, drawSnap, wasteSnap, tableauSnap, foundationSnap);
    }

    //Permite restaurar el tablero completo desde un snapshot
    public void deshacer() {
        if (historial.pilaVacia()) return;
        Movimiento anterior = historial.pop();

        // Restaurar DrawPile
        ArrayList<CartaInglesa> drawList = new ArrayList<>();
        for (Movimiento.EstadoCarta ec : anterior.drawSnapshot) {
            ec.carta.makeFaceDown(); // siempre boca abajo en el draw
            drawList.add(ec.carta);
        }
        drawPile.recargar(drawList);

        // Restaurar WastePile
        wastePile = new WastePile();
        ArrayList<CartaInglesa> wasteList = new ArrayList<>();
        for (Movimiento.EstadoCarta ec : anterior.wasteSnapshot) {
            ec.carta.makeFaceUp(); // siempre boca arriba en el waste
            wasteList.add(ec.carta);
        }
        wastePile.addCartas(wasteList);

        // Restaurar tableau — aquí está la clave
        for (int i = 0; i < 7; i++) {
            ArrayList<CartaInglesa> colList = new ArrayList<>();
            for (Movimiento.EstadoCarta ec : anterior.tableauSnapshot.get(i)) {
                // Restaura el faceup exacto que tenía antes del movimiento
                if (ec.faceup) ec.carta.makeFaceUp();
                else ec.carta.makeFaceDown(); // ← esto es lo que pone boca abajo la carta destapada
                colList.add(ec.carta);
            }
            tableau.get(i).restaurar(colList);
        }

        // Restaurar foundations
        for (int i = 0; i < 4; i++) {
            ArrayList<CartaInglesa> foundList = new ArrayList<>();
            for (Movimiento.EstadoCarta ec : anterior.foundationSnapshot[i]) {
                if (ec.faceup) ec.carta.makeFaceUp();
                else ec.carta.makeFaceDown();
                foundList.add(ec.carta);
            }
            foundation.get(i).restaurar(foundList);
        }
    }

    public boolean hayMovimientosParaDeshacer(){
        return !historial.pilaVacia();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        // add foundations
        str.append("Foundation\n");
        for (FoundationDeck foundationDeck : foundation) {
            str.append(foundationDeck);
            str.append("\n");
        }

        // add tableaux
        str.append("\nTableaux\n");
        int tableauNumber = 1;
        for (TableauDeck tableauDeck : tableau) {
            str.append(tableauNumber + " ");
            str.append(tableauDeck);
            str.append("\n");
            tableauNumber++;
        }
        str.append("Waste\n");
        str.append(wastePile);
        str.append("\nDraw\n");
        str.append(drawPile);
        return str.toString();
    }


}
