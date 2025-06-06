package com.uade.tpo.deportes.patterns.factory;

import com.uade.tpo.deportes.enums.TipoDeporte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeporteFactoryProvider {
    
    @Autowired
    private FutbolFactory futbolFactory;
    
    @Autowired
    private BasquetFactory basquetFactory;
    
    @Autowired
    private VoleyFactory voleyFactory;
    
    @Autowired
    private TenisFactory tenisFactory;
    
    public DeporteFactory getFactory(TipoDeporte tipo) {
        switch (tipo) {
            case FUTBOL:
                return futbolFactory;
            case BASQUET:
                return basquetFactory;
            case VOLEY:
                return voleyFactory;
            case TENIS:
                return tenisFactory;
            default:
                throw new IllegalArgumentException("Tipo de deporte no soportado: " + tipo);
        }
    }
}