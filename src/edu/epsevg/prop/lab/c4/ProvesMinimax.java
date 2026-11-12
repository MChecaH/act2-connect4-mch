package edu.epsevg.prop.lab.c4;

import static edu.epsevg.prop.lab.c4.Proves.comprova;
import static edu.epsevg.prop.lab.c4.Proves.tauler;

import edu.epsevg.prop.lab.c4.jugador.HeuristicaTrivial;
import edu.epsevg.prop.lab.c4.jugador.Jugador;
import edu.epsevg.prop.lab.c4.jugador.MinimaxPlayer;
import java.util.function.IntFunction;

/**
 * SESSIÓ 1 · Proves del teu {@link MinimaxPlayer} amb l'heurística trivial.
 *
 * <p>Executa aquesta classe (botó dret → Run File / Shift+F6). Cada prova mostra
 * {@code OK} o {@code FALLA} amb el valor esperat i l'obtingut. No cal tocar-la.</p>
 *
 * <p>Si una prova falla, no la "arreglis" canviant el valor esperat: els valors
 * estan calculats a mà a partir de l'enunciat.</p>
 */
public class ProvesMinimax {

    public static void main(String[] args) {
        System.out.println("=== SESSIÓ 1 · MinimaxPlayer amb heurística trivial ===");
        provesNodes();
        provesTactiques(ProvesMinimax::minimax);
        Proves.resum();
    }

    /** Exploració completa de l'arbre (específic del minimax sense poda). */
    static void provesNodes() {

        // 1. Exploració completa: amb el tauler buit no hi ha cap estat terminal
        //    abans de 7 jugades, així que un minimax sense poda visita
        //    8 + 8^2 + ... + 8^prof nodes (sense comptar l'arrel).
        long[] esperats = {8, 8 + 64, 8 + 64 + 512, 8 + 64 + 512 + 4096};
        for (int prof = 1; prof <= 4; prof++) {
            MinimaxPlayer p = minimax(prof);
            p.moviment(new Tauler(8), 1);
            comprova("Nodes explorats amb tauler buit, profunditat " + prof,
                    esperats[prof - 1], p.getNodesExplorats());
        }

    }

    /**
     * Proves tàctiques que ha de superar qualsevol jugador minimax correcte
     * (també les fa servir {@link ProvesMinimaxAlphaBeta}).
     *
     * @param crea crea un jugador amb la profunditat donada i l'heurística trivial
     */
    static void provesTactiques(IntFunction<Jugador> crea) {
        // 1. Guanyar en 1: x té tres en ratlla a baix i pot fer la quarta a la columna 3.
        Tauler guanya = tauler(
                "........",
                "........",
                "........",
                "........",
                "........",
                "........",
                "........",
                "xxx.ooo.");
        for (int prof = 1; prof <= 4; prof++) {
            comprova("Guanya en 1 jugada (profunditat " + prof + ")",
                    3, crea.apply(prof).moviment(new Tauler(guanya), 1));
        }

        // 2. Bloquejar: o amenaça de fer 4 a la columna 3. Amb profunditat >= 2 cal tapar-la.
        Tauler bloqueja = tauler(
                "........",
                "........",
                "........",
                "........",
                "........",
                "........",
                "xx......",
                "ooo.....");
        for (int prof = 2; prof <= 4; prof++) {
            comprova("Bloqueja l'amenaça del rival (profunditat " + prof + ")",
                    3, crea.apply(prof).moviment(new Tauler(bloqueja), 1));
        }

        // 3. Guanyar en 3: jugant a la columna 3, x fa .xxx. amb dues amenaces
        //    (columnes 0 i 4). El rival només en pot tapar una.
        Tauler doble = tauler(
                "........",
                "........",
                "........",
                "........",
                "........",
                "........",
                ".oo.....",
                ".xx.....");
        for (int prof = 3; prof <= 4; prof++) {
            comprova("Crea una doble amenaça (profunditat " + prof + ")",
                    3, crea.apply(prof).moviment(new Tauler(doble), 1));
        }

        // 4. Partida perduda: o té dues amenaces (columnes 0 i 4). Facis el que facis,
        //    perds. Igualment has de retornar una columna LEGAL!
        Tauler perduda = tauler(
                "........",
                "........",
                "........",
                "........",
                "........",
                "........",
                ".......x",
                ".ooo...x");
        int col = crea.apply(2).moviment(new Tauler(perduda), 1);
        comprova("Si tot perd, retorna igualment un moviment legal",
                true, col >= 0 && col < 8 && perduda.movpossible(col));

        // 5. Simetria de colors: el mateix problema amb els colors intercanviats.
        Tauler guanyaO = tauler(
                "........",
                "........",
                "........",
                "........",
                "........",
                "........",
                "........",
                "ooo.xxx.");
        comprova("Juga bé també amb el color -1",
                3, crea.apply(2).moviment(new Tauler(guanyaO), -1));
        }

    static MinimaxPlayer minimax(int prof) {
        MinimaxPlayer p = new MinimaxPlayer(prof, new HeuristicaTrivial());
        p.setMostraEstadistiques(false);
        return p;
    }
}
