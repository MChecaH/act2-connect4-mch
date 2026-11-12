package edu.epsevg.prop.lab.c4.jugador;

import edu.epsevg.prop.lab.c4.Tauler;

/**
 * Funció d'avaluació (heurística) d'un tauler de Connecta-4.
 *
 * <p>El minimax la crida només a les <b>fulles no terminals</b> de l'arbre, és a dir,
 * quan s'ha arribat a la profunditat màxima i la partida encara no s'ha acabat.
 * Els estats terminals (algú ha guanyat o el tauler és ple) els resol el mateix
 * minimax a {@code max()} i {@code min()}, no l'heurística.</p>
 *
 * <p>Conveni de signes (MOLT IMPORTANT):</p>
 * <ul>
 *   <li>El valor es calcula sempre des del punt de vista de {@code colorJo}, el jugador
 *       que ha cridat {@code moviment()} (l'arrel de l'arbre), <b>independentment</b> de
 *       a qui li toqui moure al tauler avaluat.</li>
 *   <li>Positiu = bo per a mi. Negatiu = bo per a l'adversari. 0 = neutre.</li>
 * </ul>
 *
 * <p>Separar l'heurística en una interfície ens permet canviar-la sense tocar el
 * minimax: {@code new MinimaxPlayer(4, new HeuristicaTrivial())} contra
 * {@code new MinimaxPlayer(4, new HeuristicaFinestres())}.</p>
 */
public interface Heuristica {

    /**
     * Avalua el tauler des del punt de vista de {@code colorJo}.
     *
     * @param t       tauler a avaluar (no el modifiquis!)
     * @param colorJo color del jugador arrel (1 o -1)
     * @return valor heurístic: com més gran, millor per a {@code colorJo}
     */
    int avalua(Tauler t, int colorJo);

    /**
     * Nom curt de l'heurística, per mostrar-lo a la UI i al torneig.
     *
     * @return nom de l'heurística
     */
    String nom();
}
