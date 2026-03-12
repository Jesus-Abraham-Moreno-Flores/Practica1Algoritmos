package org.example.practica1aedd.solitaire;

import org.example.practica1aedd.DeckOfCards.CartaInglesa;
import java.util.ArrayList;

public class Movimiento {

    public enum Tipo{
        DRAW,
        RELOAD,
        WASTE_TO_TABLEAU,
        WASTE_TO_FOUNDATION,
        TABLEAU_TO_TABLEAU,
        TABLEAU_TO_FOUNDATION,
        FOUNDATION_TO_TABLEAU
    }
    public final Tipo tipo;

    public final ArrayList<EstadoCarta> drawSnapshot;
    public final ArrayList<EstadoCarta> wasteSnapshot;
    public final ArrayList<ArrayList<EstadoCarta>> tableauSnapshot;
    public final ArrayList<EstadoCarta>[] foundationSnapshot;

    public Movimiento(Tipo tipo, ArrayList<EstadoCarta> drawSnapshot, ArrayList<EstadoCarta> wasteSnapshot,
                      ArrayList<ArrayList<EstadoCarta>> tableauSnapshot, ArrayList<EstadoCarta>[] foundationSnapshot) {
        this.tipo = tipo;
        this.drawSnapshot = drawSnapshot;
        this.wasteSnapshot = wasteSnapshot;
        this.tableauSnapshot = tableauSnapshot;
        this.foundationSnapshot = foundationSnapshot;
    }

    public static class EstadoCarta{
        public final CartaInglesa carta;
        public final boolean faceup;

        public EstadoCarta(CartaInglesa carta){
            this.carta = carta;
            this.faceup = carta.isFaceup();
        }
    }
}
