package org.example.practica1aedd.gui;

public class Pila<T > {
    private int tope;
    private int maximoValor;
    private Object[] pila;

    public Pila(int maximoValor) {
        pila = (T[]) new Object[maximoValor];
        tope = -1;
    }

    public void push(T dato){
        if(pilaLlena()){
            System.out.println("Desbordamiento");
        } else {
            tope+=1;
            pila[tope] = dato;
        }
    }

    public T pop(){
        T dato = null;
        if(pilaVacia()){
            System.out.println("SubDesbordamiento");
        } else {
            dato = (T) pila[tope];
            tope--;
            return dato;
        }
        return dato;
    }

    public T peek(){
        if(pilaVacia()){
            return null;
        } else {
            return (T) pila[tope];
        }
    }

    public boolean pilaVacia(){
        return  tope == -1;
    }

    public boolean pilaLlena(){
        return tope == pila.length-1;
    }
}
