package edu.epsevg.prop.lab.c4.jugador;

import edu.epsevg.prop.lab.c4.Tauler;

/**
 * Sessió 2 · Heurística de finestres de 4 caselles.
 *
 * <p>Guia, dibuixos, pseudocodi i errors típics: <b>README.md, secció 3.1</b>.
 * Comprova-ho amb {@code HeuristicaFinestresTest} (pas a pas) i {@code ProvesHeuristica}.</p>
 */
public class HeuristicaFinestres implements Heuristica {

    /** Punts d'una finestra amb 2 fitxes del mateix color i 2 buides. */
    public static final int PUNTS_2 = 100;
    /** Punts d'una finestra amb 3 fitxes del mateix color i 1 buida. */
    public static final int PUNTS_3 = 1000;

    /** Les 4 direccions {df, dc}: horitzontal, vertical, diagonal "/" i diagonal "\". */
    static final int[][] DIRECCIONS = {
        {0, 1},   // horitzontal  →
        {1, 0},   // vertical     ↑
        {1, 1},   // diagonal     ↗
        {1, -1}   // diagonal     ↖
    };

    /** Mida de la finestra (4 en ratlla). */
    static final int N = 4;

    @Override
    public int avalua(Tauler t, int colorJo) {
        int total = 0;

        // TODO pas 2 (README 3.1): suma totes les finestres del tauler.

        return total;
    }

    /**
     * Puntua una única finestra de 4 caselles.
     *
     * @param t       tauler
     * @param f       fila de la primera casella de la finestra
     * @param c       columna de la primera casella de la finestra
     * @param df      increment de fila per passar a la següent casella
     * @param dc      increment de columna per passar a la següent casella
     * @param colorJo color des del punt de vista del qual puntuem
     * @return +-PUNTS_2, +-PUNTS_3 o 0 (taula a README.md, secció 3)
     */
    protected int puntuaFinestra(Tauler t, int f, int c, int df, int dc, int colorJo) {
        int meves = 0;
        int seves = 0;
        int buides = 0;

        // TODO pas 1 (README 3.1): compta meves, seves i buides i decideix la puntuació.

        return 0;
    }

    /**
     * Ja fet: diu si una casella és dins del tauler.
     *
     * @param t tauler
     * @param f fila
     * @param c columna
     * @return true si (f, c) és una casella vàlida
     */
    static boolean dinsTauler(Tauler t, int f, int c) {
        return f >= 0 && f < t.getMida() && c >= 0 && c < t.getMida();
    }

    @Override
    public String nom() {
        return "Finestres";
    }
}
