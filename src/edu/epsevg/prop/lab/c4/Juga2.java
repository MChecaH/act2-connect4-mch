package edu.epsevg.prop.lab.c4;

import edu.epsevg.prop.lab.c4.jugador.RandomPlayer;
import edu.epsevg.prop.lab.c4.jugador.MinimaxAlphaBetaPlayer;
import edu.epsevg.prop.lab.c4.jugador.MinimaxPlayer;
import edu.epsevg.prop.lab.c4.jugador.HeuristicaFinestres;
import edu.epsevg.prop.lab.c4.jugador.HeuristicaTrivial;
import edu.epsevg.prop.lab.c4.jugador.Jugador;
import edu.epsevg.prop.lab.c4.jugador.ManualPlayer;
import edu.epsevg.prop.lab.c4.jugador.ProfePlayer;
import edu.epsevg.prop.lab.c4.ui.FinestraJoc;

/**
 * Punt d'entrada del joc amb interfície gràfica.
 *
 * <p>Només cal tocar el {@link #main}: tria quins jugadors s'enfronten.
 * El jugador 1 juga amb les vermelles (color 1) i comença; el jugador 2 juga amb
 * les grogues (color -1).</p>
 *
 * <p>Per jugar moltes partides seguides sense UI, fes servir {@link Torneig}.
 * Per comprovar el teu codi, executa {@link ProvesMinimax}, {@link ProvesMinimaxAlphaBeta} i {@link ProvesHeuristica}.</p>
 */
public class Juga2 {

    public static void main(String[] args) {
        // Defineix al vostre gust els jugadors a enfrontar.
        Jugador p1 = new ManualPlayer();
        // Jugador p1 = new RandomPlayer();
        // Jugador p1 = new ProfePlayer(4, false);   // ProfePlayer(profunditat, false=H1 més difícil / true=H2 escalfament)

        Jugador p2 = new ProfePlayer(4, false);
        // Jugador p2 = new MinimaxPlayer(4, new HeuristicaTrivial());
        // Jugador p2 = new MinimaxAlphaBetaPlayer(6, new HeuristicaTrivial());
        // Jugador p2 = new MinimaxAlphaBetaPlayer(6, new HeuristicaFinestres());     
        // Jugador p2 = new ManualPlayer();

        // true: les IA juguen soles. false: cal clicar per fer moure cada IA (pas a pas).
        boolean autoMode = true;

        FinestraJoc.mostra(p1, p2, autoMode);
    }
}
