package edu.epsevg.prop.lab.c4.jugador;

import edu.epsevg.prop.lab.c4.Tauler;

/**
 * Sessió 1 · Heurística trivial: +INFINIT si guanyo, -INFINIT si perdo, 0 altrament.
 *
 * <p>Abans d'escriure-la, llegeix <b>README.md, secció 2.1 (pas 4)</b>: és més curta
 * del que sembla.</p>
 */
public class HeuristicaTrivial implements Heuristica {

    @Override
    public int avalua(Tauler t, int colorJo) {
        // TODO pas 4 (README 2.1)
        return 0;
    }

    @Override
    public String nom() {
        return "Trivial";
    }
}
