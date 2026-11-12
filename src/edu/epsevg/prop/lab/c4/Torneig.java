package edu.epsevg.prop.lab.c4;

import edu.epsevg.prop.lab.c4.jugador.RandomPlayer;
import edu.epsevg.prop.lab.c4.jugador.MinimaxAlphaBetaPlayer;
import edu.epsevg.prop.lab.c4.jugador.MinimaxPlayer;
import edu.epsevg.prop.lab.c4.jugador.HeuristicaFinestres;
import edu.epsevg.prop.lab.c4.jugador.HeuristicaTrivial;
import edu.epsevg.prop.lab.c4.jugador.Jugador;
import edu.epsevg.prop.lab.c4.jugador.ProfePlayer;

import java.util.Random;

/**
 * SESSIÓ 2 · Enfrontaments sense UI per experimentar amb profunditats i heurístiques.
 *
 * <p>Juga {@code PARTIDES} partides entre dos jugadors, alternant qui comença.
 * Com que el minimax és determinista (dues partides iguals acaben igual), les
 * {@code OBERTURA} primeres jugades de cada partida es fan a l'atzar amb una
 * llavor fixa: així els resultats varien entre partides però són reproduïbles.</p>
 *
 * <p>Canvia els jugadors de {@link #main} i omple la taula d'experiments del README.</p>
 */
public class Torneig {

    static final int MIDA = 8;
    static final int PARTIDES = 20;
    static final int OBERTURA = 2;
    static final long LLAVOR = 2026;

    public static void main(String[] args) {
        // Defineix aquí els jugadors a enfrontar.
        Jugador a = new MinimaxAlphaBetaPlayer(4, new HeuristicaFinestres());
        // Jugador a = new MinimaxPlayer(4, new HeuristicaFinestres());
        Jugador b = new ProfePlayer(4, false);
        // Jugador b = new MinimaxAlphaBetaPlayer(4, new HeuristicaTrivial());
        // Jugador b = new RandomPlayer();

        juga(a, b, PARTIDES);
    }

    /**
     * Juga n partides entre a i b i mostra el resum per consola.
     *
     * @param a primer jugador
     * @param b segon jugador
     * @param n nombre de partides (cada jugador en comença n/2)
     */
    public static void juga(Jugador a, Jugador b, int n) {
        silencia(a);
        silencia(b);
        Random rnd = new Random(LLAVOR);
        int[] victories = new int[2];
        int taules = 0;
        long[] tempsTotal = new long[2];
        long[] moviments = new long[2];
        long[] nodes = new long[2];

        System.out.printf("Torneig: %s  vs  %s  (%d partides, %d jugades d'obertura a l'atzar)%n",
                a.nom(), b.nom(), n, OBERTURA);

        for (int p = 0; p < n; p++) {
            Jugador[] js = (p % 2 == 0) ? new Jugador[]{a, b} : new Jugador[]{b, a};
            int[] idx = (p % 2 == 0) ? new int[]{0, 1} : new int[]{1, 0};
            Tauler t = new Tauler(MIDA);
            int torn = 0;
            int jugada = 0;
            int guanyador = -1;            // index 0 (a) o 1 (b); -1 = taules
            String motiu = "";

            while (true) {
                int color = (torn == 0) ? 1 : -1;
                int col;
                if (jugada < OBERTURA) {
                    do {
                        col = rnd.nextInt(MIDA);
                    } while (!t.movpossible(col));
                } else {
                    long t0 = System.nanoTime();
                    try {
                        col = js[torn].moviment(new Tauler(t), color);
                    } catch (RuntimeException e) {
                        guanyador = idx[1 - torn];
                        motiu = " (" + js[torn].nom() + " ha llançat " + e + ")";
                        break;
                    }
                    tempsTotal[idx[torn]] += System.nanoTime() - t0;
                    moviments[idx[torn]]++;
                    nodes[idx[torn]] += nodes(js[torn]);
                }
                if (col < 0 || col >= MIDA || !t.movpossible(col)) {
                    guanyador = idx[1 - torn];
                    motiu = " (moviment il·legal de " + js[torn].nom() + ": columna " + col + ")";
                    break;
                }
                t.afegeix(col, color);
                jugada++;
                if (t.isGameOver()) {
                    int g = t.getGuanyador();          // 1, -1 o 0 (taules)
                    if (g != 0) {
                        guanyador = idx[torn];         // només pot guanyar qui acaba de moure
                    }
                    break;
                }
                torn = 1 - torn;
            }

            String res;
            if (guanyador < 0) {
                taules++;
                res = "taules";
            } else {
                victories[guanyador]++;
                res = "guanya " + (guanyador == 0 ? a : b).nom();
            }
            System.out.printf("  Partida %2d (comença %s): %s en %d jugades%s%n",
                    p + 1, js[0].nom(), res, jugada, motiu);
        }

        System.out.println();
        System.out.printf("%-32s %8s %8s %8s %14s %16s%n", "Jugador", "Guanya", "Taules", "Perd",
                "ms/moviment", "nodes/moviment");
        fila(a, victories[0], taules, victories[1], tempsTotal[0], moviments[0], nodes[0]);
        fila(b, victories[1], taules, victories[0], tempsTotal[1], moviments[1], nodes[1]);
    }

    private static void fila(Jugador j, int g, int e, int p, long nanos, long movs, long nodes) {
        double ms = movs == 0 ? 0 : nanos / 1e6 / movs;
        String n = nodes == 0 ? "-" : String.format("%,d", nodes / movs);
        System.out.printf("%-32s %8d %8d %8d %14.2f %16s%n", j.nom(), g, e, p, ms, n);
    }

    /** Nodes de l'últim moviment (només per als nostres jugadors; 0 per als altres). */
    private static long nodes(Jugador j) {
        if (j instanceof MinimaxPlayer) {
            return ((MinimaxPlayer) j).getNodesExplorats();
        }
        if (j instanceof MinimaxAlphaBetaPlayer) {
            return ((MinimaxAlphaBetaPlayer) j).getNodesExplorats();
        }
        return 0;
    }

    private static void silencia(Jugador j) {
        if (j instanceof MinimaxPlayer) {
            ((MinimaxPlayer) j).setMostraEstadistiques(false);
        }
        if (j instanceof MinimaxAlphaBetaPlayer) {
            ((MinimaxAlphaBetaPlayer) j).setMostraEstadistiques(false);
        }
    }
}
