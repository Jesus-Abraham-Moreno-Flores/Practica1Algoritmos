package org.example.practica1aedd.solitaire;

import org.example.practica1aedd.DeckOfCards.CartaInglesa;
import org.example.practica1aedd.DeckOfCards.Mazo;
import org.example.practica1aedd.gui.Pila;

import java.util.ArrayList;

/**
 * Modela un mazo de cartas de solitario.
 * @author Cecilia Curlango
 * @version 2025
 */
public class DrawPile {
    private Pila<CartaInglesa> cartas;
    private int cuantasCartasSeEntregan = 3;

    public DrawPile() {
        Mazo mazo = new Mazo();
        cartas = new Pila<>(52);
        // Cargo las cartas del mazo en la pila
        for(CartaInglesa c : mazo.getCartas()){
            cartas.push(c);
        }
        setCuantasCartasSeEntregan(3);
    }

    /**
     * Establece cuantas cartas se sacan cada vez.
     * Puede ser 1 o 3 normalmente.
     * @param cuantasCartasSeEntregan
     */
    public void setCuantasCartasSeEntregan(int cuantasCartasSeEntregan) {
        this.cuantasCartasSeEntregan = cuantasCartasSeEntregan;
    }

    /**
     * Regresa la cantidad de cartas que se sacan cada vez.
     * @return cantidad de cartas que se entregan
     */
    public int getCuantasCartasSeEntregan() {
        return cuantasCartasSeEntregan;
    }

    /**
     * Retirar una cantidad de cartas. Este método se utiliza al inicio
     * de una partida para cargar las cartas de los tableaus.
     * Si se tratan de remover más cartas de las que hay,
     * se provocará un error.
     * @param cantidad de cartas que se quieren a retirar
     * @return cartas retiradas
     */
    public ArrayList<CartaInglesa> getCartas(int cantidad) {
        ArrayList<CartaInglesa> retiradas = new ArrayList<>();
        ArrayList<CartaInglesa> restantes = new ArrayList<>();

        while(hayCartas()){
            restantes.add(cartas.pop());
        }

        java.util.Collections.reverse(restantes);
        // Tomar las primeras cartas especificado por la cantidad
        for(int i = 0; i < cantidad && i < restantes.size(); i++){
            retiradas.add(restantes.get(i));
        }
        //El resto son devueltas a la pila
        for(int i = cantidad; i <restantes.size(); i++){
            cartas.push(restantes.get(i));
        }
        return retiradas;
    }

    /**
     * Retira y entrega las cartas del monton. La cantidad que retira
     * depende de cuántas cartas quedan en el montón y serán hasta el máximo
     * que se configuró inicialmente.
     * @return Cartas retiradas.
     */
    public ArrayList<CartaInglesa> retirarCartas() {
        ArrayList<CartaInglesa> retiradas = new ArrayList<>();
        int max = cuantasCartasSeEntregan;

        for (int i = 0; i < max && hayCartas(); i++) {
            CartaInglesa retirada = cartas.pop();
            retirada.makeFaceUp();
            retiradas.add(retirada);
        }
        return retiradas;
    }

    /**
     * Indica si aún quedan cartas para entregar.
     * @return true si hay cartas, false si no.
     */
    public boolean hayCartas() { return !cartas.pilaVacia(); }

    public CartaInglesa verCarta() { return cartas.peek(); }
    /**
     * Agrega las cartas recibidas al monton y las voltea
     * para que no se vean las caras.
     * @param cartasAgregar cartas que se agregan
     */
    public void recargar(ArrayList<CartaInglesa> cartasAgregar) {
        cartas = new Pila<>(52);
        for (CartaInglesa aCarta : cartasAgregar) {
            cartas.push(aCarta);
        }
    }

    //Metodo toList para poder realizar el undo
    public ArrayList<CartaInglesa> toList() {
        ArrayList<CartaInglesa> lista = new ArrayList<>();
        Pila<CartaInglesa> temp = new Pila<>(52);
        while(hayCartas()){
            CartaInglesa carta = cartas.pop();
            lista.add(carta);
            temp.push(carta);
        }
        // Aqui se restaura la pila original
        while(!temp.pilaVacia()){
            cartas.push(temp.pop());
        }
        java.util.Collections.reverse(lista);
        return lista;
    }

    @Override
    public String toString() {
        return cartas.pilaVacia() ? "-E-" : "@";
    }
}
