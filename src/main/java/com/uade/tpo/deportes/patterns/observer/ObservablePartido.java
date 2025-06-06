package com.uade.tpo.deportes.patterns.observer;

public interface ObservablePartido {
    void agregarObserver(ObserverPartido observer);
    void removerObserver(ObserverPartido observer);
    void notificarObservers();
}