package org.example.practica1aedd.gui;

import org.example.practica1aedd.DeckOfCards.CartaInglesa;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class CartaView extends StackPane{

    public static final double ANCHO = 80;
    public static final double ALTO = 120;
    private final CartaInglesa carta;

    public CartaView(CartaInglesa carta) {
        this.carta = carta;
        getStyleClass().add("carta");
        setMinSize(ANCHO,ALTO);
        setMaxSize(ANCHO,ALTO);
        ImageView iv = new ImageView(cargarImagen());
        iv.setFitWidth(ANCHO);
        iv.setFitHeight(ALTO);
        iv.setPreserveRatio(true);
        getChildren().add(iv);

        if(!carta.isFaceup()){
            setMouseTransparent(true);
        }
    }

    public Image cargarImagen(){
        String ruta = carta.isFaceup()
                ? "/images/cards/" + getNombre() + ".png"
                : "/images/cards/back.png";
        return new Image(getClass().getResourceAsStream(ruta));
    }

    public String getNombre(){
        String valor = switch (carta.getValor()){
            case 14 -> "ace";
            case 11 -> "jack";
            case 12 -> "queen";
            case 13 -> "king";
            default -> String.valueOf(carta.getValor());
        };
        String palo = switch (carta.getPalo()){
            case TREBOL -> "clubs";
            case DIAMANTE -> "diamonds";
            case CORAZON -> "hearts";
            case PICA -> "spades";
        };
        return valor + "_of_" + palo;
    }

    public CartaInglesa getCarta(){return carta;}
}
