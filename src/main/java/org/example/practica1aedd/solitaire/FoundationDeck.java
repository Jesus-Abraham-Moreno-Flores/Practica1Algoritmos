package org.example.practica1aedd.solitaire;

import org.example.practica1aedd.DeckOfCards.CartaInglesa;
import org.example.practica1aedd.DeckOfCards.Palo;
import org.example.practica1aedd.gui.Pila;
import java.util.ArrayList;

/**
 * Modela un monículo donde se ponen las cartas
 * de un solo palo.
 *
 * @author Cecilia M. Curlango
 * @version 2025
 */
public class FoundationDeck {
    Palo palo;
    private Pila<CartaInglesa> cartas;

    public FoundationDeck(Palo palo) {
        this.palo = palo;
        cartas = new Pila<>(13);
    }

    public FoundationDeck(CartaInglesa carta) {
        palo = carta.getPalo();
        cartas = new Pila<>(13);
        if(carta.getValorBajo() == 1){
            cartas.push(carta);
        }
    }

    /**
     * Agrega una carta al montículo. Sólo la agrega si
     * la carta es del palo del montículo y el la siguiente
     * carta en la secuencia.
     *
     * @param carta que se intenta almancenar
     * @return true si se pudo guardar la carta, false si no
     */
    public boolean agregarCarta(CartaInglesa carta) {
        if(!sePuedeAgregar(carta)){
            return false;
        }
        cartas.push(carta);
        return true;
    }

    public boolean sePuedeAgregar(CartaInglesa carta) {
        if(!carta.tieneElMismoPalo(palo)){
            return false;
        }
        if(cartas.pilaVacia()){
            return carta.getValorBajo() == 1;
        }
        return cartas.peek().getValorBajo() + 1 == carta.getValorBajo();
    }

    /**
     * Remover la última carta del montículo.
     *
     * @return la carta que removió, null si estaba vacio
     */
    public CartaInglesa removerUltimaCarta() {
        return cartas.pop();
    }

    @Override
    public String toString() {
        if(cartas.pilaVacia()){
            return "---";
        }
        return cartas.peek().toString();
    }

    public ArrayList<CartaInglesa> toList() {
        ArrayList<CartaInglesa> lista = new ArrayList<>();
        Pila<CartaInglesa> temp = new Pila<>(13);
        while(!cartas.pilaVacia()){
            CartaInglesa carta = cartas.pop();
            lista.add(carta);
            temp.push(carta);
        }
        while(!temp.pilaVacia()){
            cartas.push(temp.pop());
        }
        java.util.Collections.reverse(lista);
        return lista;
    }

    public void restaurar(ArrayList<CartaInglesa> lista){
        cartas = new Pila<>(13);
        for(CartaInglesa carta : lista){
            cartas.push(carta);
        }
    }

    /**
     * Determina si hay cartas en el Foundation.
     * @return true hay al menos una carta, false no hay cartas
     */
    public boolean estaVacio() {
        return cartas.pilaVacia();
    }

    /**
     * Obtiene la última carta del Foundation sin removerla.
     * @return última carta, null si no hay cartas
     */
    public CartaInglesa getUltimaCarta() {
        return cartas.peek();
    }
}
