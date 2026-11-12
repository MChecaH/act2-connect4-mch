package edu.epsevg.prop.lab.c4.jugador;

import edu.epsevg.prop.lab.c4.Tauler;

/**
 * Sessió 1 · Jugador de Connecta-4 amb minimax.
 *
 * <p>Guia, pseudocodi i errors típics: <b>README.md, secció 2.1</b>.
 * Comprova-ho amb {@code ProvesMinimax}.</p>
 *
 * @author (el teu nom)
 */
public class MinimaxPlayer implements Jugador, IAuto {

    /** Valor que fem servir com a "infinit". Molt més gran que qualsevol heurística. */
    public static final int INFINIT = 1_000_000_000;

    private final int profunditat;
    private final Heuristica heuristica;

    /** Color amb què jugo en aquesta partida (s'assigna a cada crida de moviment()). */
    private int colorJo;
    /** Color del rival: sempre {@code -colorJo}. */
    private int colorRival;

    // --- Estadístiques per a l'experimentació (Sessió 2). Ja estan fetes. ---
    private long nodesExplorats;
    private boolean mostraEstadistiques = true;

    /**
     * Crea un jugador minimax.
     *
     * @param profunditat nombre de mitges jugades a explorar (mínim 1)
     * @param heuristica  funció d'avaluació de les fulles no terminals
     */
    public MinimaxPlayer(int profunditat, Heuristica heuristica) {
        if (profunditat < 1) {
            throw new IllegalArgumentException("La profunditat mínima és 1");
        }
        this.profunditat = profunditat;
        this.heuristica = heuristica;
    }

    /**
     * Mètode que crida la UI (o el torneig) quan ens toca moure. Ja està fet:
     * prepara els colors, crida {@link #arrelMinimax} i mostra estadístiques per consola.
     */
    @Override
    public int moviment(Tauler t, int color) {
        colorJo = color;
        colorRival = -color;
        nodesExplorats = 0;

        long inici = System.nanoTime();
        int columna = arrelMinimax(t, profunditat);
        long ms = (System.nanoTime() - inici) / 1_000_000;

        if (mostraEstadistiques) {
            System.out.printf("[%s] columna %d · %,d nodes · %d ms%n",
                    nom(), columna, nodesExplorats, ms);
        }
        return columna;
    }

    /**
     * Arrel del minimax: prova tots els meus moviments i retorna la <b>columna</b>
     * del millor.
     *
     * @param t    tauler actual (no el modifiquis: treballa amb còpies)
     * @param prof profunditat total de la cerca (comptant aquest moviment)
     * @return columna escollida (sempre ha de ser un moviment legal!)
     */
    private int arrelMinimax(Tauler t, int prof) {
        // TODO pas 3 (README 2.1): ARREL.
        // Codi provisional perquè la UI funcioni mentre programes (esborra'l):
        for (int c = 0; c < t.getMida(); c++) {
            if (t.movpossible(c)) {
                return c;
            }
        }
        return 0;
    }

    // TODO pas 1 (README 2.1): escriu aquí un mètode privat valorTerminal(Tauler t).

    /**
     * Node MAX: em toca moure a mi ({@code colorJo}).
     *
     * @param t    tauler del node
     * @param prof mitges jugades que queden per explorar
     * @return el valor minimax del node, des del punt de vista de {@code colorJo}
     */
    private int max(Tauler t, int prof) {
        nodesExplorats++;

        // TODO pas 2 (README 2.1): MAX.
        return 0;
    }

    /**
     * Node MIN: li toca moure al rival ({@code colorRival}).
     *
     * @param t    tauler del node
     * @param prof mitges jugades que queden per explorar
     * @return el valor minimax del node, des del punt de vista de {@code colorJo}
     */
    private int min(Tauler t, int prof) {
        nodesExplorats++;

        // TODO pas 2 (README 2.1): MIN, el mirall de MAX.
        return 0;
    }

    // ------------------------------------------------------------------------
    //  Ja fet: utilitats per a la UI i per experimentar
    // ------------------------------------------------------------------------

    /**
     * Activa o desactiva la línia d'estadístiques que s'imprimeix a cada moviment
     * (el {@code Torneig} la desactiva per no omplir la consola).
     *
     * @param mostra true per imprimir-les
     */
    public void setMostraEstadistiques(boolean mostra) {
        this.mostraEstadistiques = mostra;
    }

    /**
     * Nodes explorats en l'últim moviment.
     *
     * @return nombre de nodes MAX + MIN visitats
     */
    public long getNodesExplorats() {
        return nodesExplorats;
    }

    @Override
    public String nom() {
        return "Minimax P" + profunditat + " " + heuristica.nom();
    }
}
