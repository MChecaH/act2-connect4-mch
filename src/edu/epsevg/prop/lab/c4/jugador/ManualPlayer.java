package edu.epsevg.prop.lab.c4.jugador;

import edu.epsevg.prop.lab.c4.Tauler;

/**
 * Jugador Manual (sobre la UI)
 * @author Profe
 */
public class ManualPlayer implements Jugador{

    @Override
    public int moviment(Tauler t, int color) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String nom() {
        return "Manual";
    }
    
}
