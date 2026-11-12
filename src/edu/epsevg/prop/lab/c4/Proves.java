package edu.epsevg.prop.lab.c4;

/**
 * Utilitats compartides per {@link ProvesMinimax}, {@link ProvesMinimaxAlphaBeta} i
 * {@link ProvesHeuristica}. No cal tocar-la.
 *
 * <p>Els taulers s'escriuen amb {@link #tauler(String...)}, de <b>dalt a baix</b>
 * (l'última cadena és la fila 0, la de baix): {@code x} = color 1,
 * {@code o} = color -1, {@code .} = buida. El botó <i>"Copia tauler"</i> de la UI
 * copia la posició actual en aquest format.</p>
 */
public final class Proves {

    private static int ok = 0;
    private static int falla = 0;

    private Proves() {
    }

    /**
     * Construeix un tauler a partir de les seves files, de dalt a baix.
     * Les fitxes es deixen caure amb {@code afegeix()}, per tant el tauler ha de
     * respectar la gravetat (cap fitxa flotant).
     *
     * @param files files del tauler, la primera és la de dalt; x = 1, o = -1, . = buida
     * @return el tauler
     */
    public static Tauler tauler(String... files) {
        int mida = files.length;
        Tauler t = new Tauler(mida);
        for (int c = 0; c < mida; c++) {
            boolean buit = false;
            for (int f = 0; f < mida; f++) {           // f = 0 és la fila de baix
                char ch = files[mida - 1 - f].charAt(c);
                if (ch == '.') {
                    buit = true;
                } else {
                    if (buit) {
                        throw new IllegalArgumentException("Fitxa flotant a la columna " + c);
                    }
                    t.afegeix(c, ch == 'x' ? 1 : -1);
                }
            }
        }
        return t;
    }

    /**
     * Compara un valor esperat amb l'obtingut i escriu OK o FALLA.
     *
     * @param descripcio què es prova
     * @param esperat    valor esperat
     * @param obtingut   valor obtingut
     */
    public static void comprova(String descripcio, Object esperat, Object obtingut) {
        if (esperat.equals(obtingut)) {
            ok++;
            System.out.println("  OK     " + descripcio);
        } else {
            falla++;
            System.out.println("  FALLA  " + descripcio + "  (esperat: " + esperat + ", obtingut: " + obtingut + ")");
        }
    }

    /** Escriu el resum final. */
    public static void resum() {
        System.out.println();
        System.out.printf("Resultat: %d OK, %d FALLA%n", ok, falla);
        if (falla > 0) {
            System.out.println("Pista: comença per la primera prova que falla, les altres en depenen sovint.");
        }
    }
}
