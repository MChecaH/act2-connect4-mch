package edu.epsevg.prop.lab.c4.jugador;

import edu.epsevg.prop.lab.c4.Tauler;

/**
 * Sessió 1 · Heurística trivial: +INFINIT si guanyo, -INFINIT si perdo, 0 altrament.
 *
 * <p>Abans d'escriure-la, llegeix <b>README.md, secció 2.1 (pas 4)</b>: és més curta
 * del que sembla.</p>
 */
public class HeuristicaTrivial implements Heuristica {
    
    /** Valor que fem servir com a "infinit". Molt més gran que qualsevol heurística. */
    public static final int INFINIT = 1_000_000_000;

    @Override
    public int avalua(Tauler t, int colorJo) {
        
        // Cas 1: joc no acabat
        if (!t.isGameOver()) return 0;
        
        // Cas 2: joc acabat, determinar guanyador
        else
        {
            int guanyador = t.getGuanyador();
        
            if (guanyador == colorJo) guanyador = INFINIT;
            else if (guanyador == -colorJo) guanyador = -INFINIT;

            return guanyador;
        }
    }

    @Override
    public String nom() {
        return "Trivial";
    }
}
