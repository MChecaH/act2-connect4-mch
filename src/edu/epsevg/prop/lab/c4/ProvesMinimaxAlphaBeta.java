package edu.epsevg.prop.lab.c4;

import static edu.epsevg.prop.lab.c4.Proves.comprova;

import edu.epsevg.prop.lab.c4.jugador.Heuristica;
import edu.epsevg.prop.lab.c4.jugador.HeuristicaTrivial;
import edu.epsevg.prop.lab.c4.jugador.MinimaxAlphaBetaPlayer;
import edu.epsevg.prop.lab.c4.jugador.MinimaxPlayer;
import java.util.Random;

/**
 * SESSIÓ 1 (segona part) · Proves del teu {@link MinimaxAlphaBetaPlayer}.
 *
 * <p><b>Abans</b> d'executar-les, {@link ProvesMinimax} ha de sortir tot en verd: aquí
 * es compara l'alfa-beta amb el teu {@link MinimaxPlayer}, que fa de referència.</p>
 *
 * <p>Què es comprova:</p>
 * <ol>
 *   <li>Les mateixes proves tàctiques que el minimax (guanyar, bloquejar, ...).</li>
 *   <li>Que l'alfa-beta <b>poda</b>: explora menys nodes que el minimax amb el tauler buit.</li>
 *   <li>Que la poda <b>no canvia la decisió</b>: en moltes posicions aleatòries, l'alfa-beta
 *       tria exactament la mateixa columna que el minimax.</li>
 * </ol>
 *
 * <p>Executa aquesta classe (botó dret → Run File / Shift+F6). No cal tocar-la.</p>
 */
public class ProvesMinimaxAlphaBeta {

    /** Nodes d'un minimax sense poda amb el tauler buit: 8 + 8^2 + ... + 8^prof. */
    private static final long[] NODES_MINIMAX = {8, 72, 584, 4680, 37448};

    public static void main(String[] args) {
        System.out.println("=== SESSIÓ 1b · MinimaxAlphaBetaPlayer ===");

        System.out.println("-- Proves tàctiques");
        ProvesMinimax.provesTactiques(p -> alfaBeta(p, new HeuristicaTrivial()));

        System.out.println("-- Poda");
        provesPoda();

        System.out.println("-- Mateixes decisions que el minimax");
        MinimaxPlayer referencia = ProvesMinimax.minimax(2);
        referencia.moviment(new Tauler(8), 1);
        if (referencia.getNodesExplorats() != NODES_MINIMAX[1]) {
            comprova("El teu MinimaxPlayer ha de funcionar abans (executa ProvesMinimax)",
                    NODES_MINIMAX[1], referencia.getNodesExplorats());
        } else {
            provesMateixesDecisions(new HeuristicaTrivial());
            provesMateixesDecisions(new HeuristicaCentre());
        }

        Proves.resum();
    }

    private static void provesPoda() {
        // A profunditat 1 no es pot podar res: cal mirar els 8 fills.
        MinimaxAlphaBetaPlayer p1 = alfaBeta(1, new HeuristicaTrivial());
        p1.moviment(new Tauler(8), 1);
        comprova("Profunditat 1: no hi ha res a podar (8 nodes)", 8L, p1.getNodesExplorats());

        for (int prof = 2; prof <= 5; prof++) {
            for (Heuristica h : new Heuristica[]{new HeuristicaTrivial(), new HeuristicaCentre()}) {
                MinimaxAlphaBetaPlayer p = alfaBeta(prof, h);
                p.moviment(new Tauler(8), 1);
                long n = p.getNodesExplorats();
                long max = NODES_MINIMAX[prof - 1];
                comprova(String.format("Profunditat %d, heurística %s: poda (%d de %d nodes, %.0f%%)",
                        prof, h.nom(), n, max, 100.0 * n / max),
                        true, n > 0 && n < max);
                if (n >= max) {
                    System.out.println("         Pista: si no podes res, revisa que passes alfa i beta a les crides"
                            + " recursives i també des de l'arrel (amb l'alfa actualitzat).");
                }
            }
        }
    }

    /**
     * Genera posicions aleatòries (sempre les mateixes, llavor fixa) i comprova que
     * minimax i alfa-beta trien la mateixa columna a profunditats 1..5.
     */
    private static void provesMateixesDecisions(Heuristica h) {
        Random rnd = new Random(2026);
        for (int prof = 1; prof <= 5; prof++) {
            int posicions = 0;
            int iguals = 0;
            long nodesMinimax = 0;
            long nodesAlfaBeta = 0;
            String primeraDiferencia = null;
            while (posicions < 25) {
                Tauler t = posicioAleatoria(rnd);
                if (t == null) {
                    continue;
                }
                int color = rnd.nextBoolean() ? 1 : -1;
                MinimaxPlayer mm = new MinimaxPlayer(prof, h);
                mm.setMostraEstadistiques(false);
                MinimaxAlphaBetaPlayer ab = alfaBeta(prof, h);
                int c1 = mm.moviment(new Tauler(t), color);
                int c2 = ab.moviment(new Tauler(t), color);
                nodesMinimax += mm.getNodesExplorats();
                nodesAlfaBeta += ab.getNodesExplorats();
                posicions++;
                if (c1 == c2) {
                    iguals++;
                } else if (primeraDiferencia == null) {
                    primeraDiferencia = "minimax tria " + c1 + ", alfa-beta tria " + c2;
                }
            }
            comprova(String.format("Profunditat %d, heurística %s: mateixa columna en %d/%d posicions"
                    + " (nodes: minimax %,d, alfa-beta %,d)%s",
                    prof, h.nom(), iguals, posicions, nodesMinimax, nodesAlfaBeta,
                    primeraDiferencia == null ? "" : "  [p.ex. " + primeraDiferencia + "]"),
                    posicions, nodesAlfaBeta > 0 ? iguals : -1);
        }
    }

    /** Una posició de 4 a 20 fitxes a l'atzar en què la partida no s'ha acabat (o null). */
    private static Tauler posicioAleatoria(Random rnd) {
        Tauler t = new Tauler(8);
        int fitxes = 4 + rnd.nextInt(17);
        int color = 1;
        for (int i = 0; i < fitxes; i++) {
            int c;
            do {
                c = rnd.nextInt(8);
            } while (!t.movpossible(c));
            t.afegeix(c, color);
            if (t.isGameOver()) {
                return null;
            }
            color = -color;
        }
        return t;
    }

    private static MinimaxAlphaBetaPlayer alfaBeta(int prof, Heuristica h) {
        MinimaxAlphaBetaPlayer p = new MinimaxAlphaBetaPlayer(prof, h);
        p.setMostraEstadistiques(false);
        return p;
    }

    /**
     * Heurística senzilla només per a aquestes proves: les fitxes de les columnes
     * centrals valen més. Dona valors diferents de 0 perquè la poda tingui feina.
     */
    static class HeuristicaCentre implements Heuristica {
        private static final int[] PES = {1, 2, 3, 4, 4, 3, 2, 1};

        @Override
        public int avalua(Tauler t, int colorJo) {
            int v = 0;
            for (int f = 0; f < t.getMida(); f++) {
                for (int c = 0; c < t.getMida(); c++) {
                    v += t.getColor(f, c) * colorJo * PES[c];
                }
            }
            return v;
        }

        @Override
        public String nom() {
            return "Centre";
        }
    }
}
