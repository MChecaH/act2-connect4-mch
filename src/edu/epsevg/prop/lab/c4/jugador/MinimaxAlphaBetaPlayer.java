package edu.epsevg.prop.lab.c4.jugador;

import edu.epsevg.prop.lab.c4.Tauler;
import static edu.epsevg.prop.lab.c4.jugador.MinimaxPlayer.INFINIT;

/**
 * Sessió 1 (segona part) · Minimax amb poda alfa-beta.
 *
 * <p>Copia-hi el teu {@link MinimaxPlayer} i adapta'l seguint <b>README.md, secció 2.2</b>.
 * Comprova-ho amb {@code ProvesMinimaxAlphaBeta}.</p>
 *
 * @author (el teu nom)
 */
public class MinimaxAlphaBetaPlayer implements Jugador, IAuto {

    /** Valor que fem servir com a "infinit". Molt més gran que qualsevol heurística. */
    public static final int INFINIT = 1_000_000_000;

    private final int profunditat;
    private final Heuristica heuristica;

    /** Ordre en què s'exploren les columnes (Sessió 2, experiment d'ordenació). */
    private final int[] ordre;

    /** Ordre "natural" 0..7: el mateix que el MinimaxPlayer. */
    public static final int[] ORDRE_NATURAL = {0, 1, 2, 3, 4, 5, 6, 7};
    /** Del centre cap enfora. */
    public static final int[] ORDRE_CENTRE = {3, 4, 2, 5, 1, 6, 0, 7};

    private int colorJo;
    private int colorRival;

    // --- Estadístiques (ja fetes) ---
    private long nodesExplorats;
    private boolean mostraEstadistiques = true;

    /**
     * Crea un jugador minimax amb poda alfa-beta.
     *
     * @param profunditat nombre de mitges jugades a explorar (mínim 1)
     * @param heuristica  funció d'avaluació de les fulles no terminals
     */
    public MinimaxAlphaBetaPlayer(int profunditat, Heuristica heuristica) {
        this(profunditat, heuristica, ORDRE_NATURAL);
    }

    /**
     * Com l'anterior, però explorant les columnes en l'ordre indicat (Sessió 2).
     *
     * @param profunditat nombre de mitges jugades a explorar (mínim 1)
     * @param heuristica  funció d'avaluació de les fulles no terminals
     * @param ordre       les 8 columnes en l'ordre en què s'han d'explorar
     */
    public MinimaxAlphaBetaPlayer(int profunditat, Heuristica heuristica, int[] ordre) {
        if (profunditat < 1) {
            throw new IllegalArgumentException("La profunditat mínima és 1");
        }
        this.profunditat = profunditat;
        this.heuristica = heuristica;
        this.ordre = ordre.clone();
    }

    /** Ja fet: prepara els colors, crida {@link #arrelMinimax} i mostra estadístiques. */
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
     * Arrel: prova tots els meus moviments mantenint alfa i beta.
     *
     * @param t    tauler actual (treballa amb còpies)
     * @param prof profunditat total de la cerca
     * @return columna escollida (sempre ha de ser un moviment legal!)
     */
    private int arrelMinimax(Tauler t, int prof) {
        // TODO (README 2.2): copia el teu arrelMinimax i adapta-l.
        // Codi provisional perquè la UI funcioni (esborra'l):
        int alfa = -INFINIT;
        int beta = INFINIT;
        int columna = -1;
        
        for (int c = 0; c < t.getMida(); c++) {
            if (!t.movpossible(c)) continue;
            else if (columna == -1) columna = c; // guardem la primera columna lliure per defecte si segueix a -1
            Tauler fill = new Tauler(t);    // crea còpia taulell
            fill.afegeix(c, colorJo);       // tira la meva fitxa al fill
            
            // avalua el fill (ara li toca al rival) 
            int valorFill = min(fill, prof-1, alfa, beta);
            if (valorFill > alfa)                             
            {
                // si és millor que el que tenies, recorda el valor i la columna
                alfa = valorFill;
                columna = c;
            }
        }
        // retorna la millor columna
        return columna;
    }

    // TODO pas 1 (README 2.1): escriu aquí un mètode privat valorTerminal(Tauler t).
    private int valorTerminal(Tauler t) {
        int guanyador = t.getGuanyador();
        
        if (guanyador == colorJo) guanyador = INFINIT;
        else if (guanyador == colorRival) guanyador = -INFINIT;
        
        return guanyador;
    }
    
    /**
     * Node MAX amb poda.
     *
     * @param t    tauler del node
     * @param prof mitges jugades que queden
     * @param alfa millor valor garantit per a MAX en el camí
     * @param beta millor valor garantit per a MIN en el camí
     * @return valor del node (des del punt de vista de colorJo)
     */
    private int max(Tauler t, int prof, int alfa, int beta) {
        nodesExplorats++;

        if (t.isGameOver()) return valorTerminal(t); // si la partida s'ha acabat retorna el valor terminal
        if (prof == 0) return heuristica.avalua(t, colorJo); // si no queda profunditat retorna l'heurística
        
        int valor = alfa;
        
        for (int c = 0; c < t.getMida(); c++) {
            if (!t.movpossible(c) || alfa >= beta) continue;
            Tauler fill = new Tauler(t);    // crea còpia taulell
            fill.afegeix(c, colorJo);       // tira la meva fitxa al fill
            
            // avalua el fill (ara li toca al rival) 
            int valorFill = min(fill, prof-1, alfa, beta);
            if (valorFill > valor) {
                valor = valorFill; // si és millor que el que tenies, recorda el valor i la columna
                alfa = valorFill;
            }
        }
        
        return valor;
    }

    /**
     * Node MIN amb poda.
     *
     * @param t    tauler del node
     * @param prof mitges jugades que queden
     * @param alfa millor valor garantit per a MAX en el camí
     * @param beta millor valor garantit per a MIN en el camí
     * @return valor del node (des del punt de vista de colorJo)
     */
    private int min(Tauler t, int prof, int alfa, int beta) {
        nodesExplorats++;

        if (t.isGameOver()) return valorTerminal(t); // si la partida s'ha acabat retorna el valor terminal
        if (prof == 0) return heuristica.avalua(t, colorRival); // si no queda profunditat retorna l'heurística
        
        int valor = beta;
        
        for (int c = 0; c < t.getMida(); c++) {
            if (!t.movpossible(c) || alfa >= beta) continue;
            Tauler fill = new Tauler(t);    // crea còpia taulell
            fill.afegeix(c, colorRival);    // tira la fitxa del rival al fill
            
            // avalua el fill (ara li toca al jugador) 
            int valorFill = max(fill, prof-1, alfa, beta);
            if (valorFill < valor) {
                valor = valorFill; // si és millor que el que tenies, recorda el valor i la columna
                beta = valorFill;
            }
        }
        
        return valor;
    }

    // ------------------------------------------------------------------------
    //  Ja fet
    // ------------------------------------------------------------------------

    /**
     * Activa o desactiva la línia d'estadístiques per consola.
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
        String sufix = java.util.Arrays.equals(ordre, ORDRE_NATURAL) ? "" : " ordenat";
        return "AlfaBeta P" + profunditat + " " + heuristica.nom() + sufix;
    }
}
