package com.uade.tpo.deportes.patterns.factory;

import com.uade.tpo.deportes.entity.Deporte;
import com.uade.tpo.deportes.enums.TipoDeporte;

public abstract class DeporteFactory {
    
    public abstract Deporte crearDeporte(TipoDeporte tipo);
    public abstract void configurarReglas(Deporte deporte);
    
    // Template method que utiliza los métodos abstractos
    public final Deporte crearDeporteCompleto(TipoDeporte tipo) {
        Deporte deporte = crearDeporte(tipo);
        configurarReglas(deporte);
        return deporte;
    }
}