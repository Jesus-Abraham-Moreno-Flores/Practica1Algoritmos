package org.example.practica1aedd.solitaire;

import org.example.practica1aedd.DeckOfCards.CartaInglesa;
import org.example.practica1aedd.gui.Pila;
import java.util.ArrayList;
/**
 * Modela el montículo donde se colocan las cartas
 * que se extraen de Draw pile.
 *
 * @author (Cecilia Curlango Rosas)
 * @version (2025-2)
 */
public class WastePile {
    private Pila<CartaInglesa> cartas;

    public WastePile() {
        cartas = new Pila<>(52);
    }

    public void addCartas(ArrayList<CartaInglesa> nuevas) {
        for(CartaInglesa carta : nuevas){
            cartas.push(carta);
        }
    }

    //Este metodo vacia la pila y devuelve las cartas como una lista
    public ArrayList<CartaInglesa> emptyPile() {
        ArrayList<CartaInglesa> lista = new ArrayList<>();
        while(hayCartas()){
            lista.add(cartas.pop());
        }
        return lista;
    }

    /**
     * Obtener la última carta sin removerla.
     * @return Carta que está encima. Si está vacía, es null.
     */
    public CartaInglesa verCarta() { return cartas.peek(); }

    public CartaInglesa getCarta() { return cartas.pop(); }

    public ArrayList<CartaInglesa> toList() {
        ArrayList<CartaInglesa> lista = new ArrayList<>();
        Pila<CartaInglesa> temp = new Pila<>(52);
        while(hayCartas()){
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

    @Override
    public String toString() {
        CartaInglesa top = cartas.peek();
        if(top == null){
            return "---";
        }
        top.makeFaceUp();
        return top.toString();
    }

    public boolean hayCartas() {
        return !cartas.pilaVacia();
    }
}
