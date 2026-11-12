package edu.epsevg.prop.lab.c4.jugador;

import edu.epsevg.prop.lab.c4.Tauler;
import static org.junit.Assert.assertEquals;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * SESSIÓ 2 · Tests unitaris de {@link HeuristicaFinestres}.
 *
 * <p>Executa'ls a NetBeans amb botó dret sobre aquest fitxer → <i>Test File</i>
 * (Ctrl+F6). Van en ordre, de manera que pots implementar pas a pas:</p>
 * <ol>
 *   <li><b>pas1_*</b>: només {@code puntuaFinestra()}, una finestra cada vegada.
 *       Comença per aquí: no cal haver fet {@code avalua()}.</li>
 *   <li><b>pas2_*</b>: {@code avalua()} recorrent tot el tauler.</li>
 * </ol>
 *
 * <p>Els taulers s'escriuen de <b>dalt a baix</b> (l'última cadena és la fila 0):
 * {@code x} = color 1, {@code o} = color -1, {@code .} = buida. Les fitxes han de
 * respectar la gravetat. A tots els tests "jo" sóc {@code x} (color 1), excepte
 * quan es diu el contrari.</p>
 *
 * <p>No canviïs els valors esperats perquè passin els tests: estan calculats a mà a
 * partir de l'enunciat (2 = 100, 3 = 1000, negatiu per al rival, barrejades = 0).</p>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HeuristicaFinestresTest {

    private static final int X = 1;
    private static final int O = -1;

    // Direccions {df, dc}
    private static final int[] HORITZONTAL = {0, 1};
    private static final int[] VERTICAL = {1, 0};
    private static final int[] DIAGONAL_DRETA = {1, 1};      // ↗
    private static final int[] DIAGONAL_ESQUERRA = {1, -1};  // ↖

    private final HeuristicaFinestres h = new HeuristicaFinestres();

    // ======================================================================
    //  PAS 1 · puntuaFinestra(): una sola finestra
    // ======================================================================

    @Test
    public void pas1a_finestraBuidaVal0() {
        assertFinestra("Finestra buida", 0, baix("........"), 0, 0, HORITZONTAL, X);
    }

    @Test
    public void pas1b_unaFitxaVal0() {
        assertFinestra("Una sola fitxa no puntua", 0, baix("x......."), 0, 0, HORITZONTAL, X);
    }

    @Test
    public void pas1c_duesMevesJuntesVal100() {
        assertFinestra("x x . .", 100, baix("xx......"), 0, 0, HORITZONTAL, X);
    }

    @Test
    public void pas1d_duesMevesSeparadesValenIgual() {
        assertFinestra("x . x .  (l'ordre dins la finestra no importa)", 100, baix("x.x....."), 0, 0, HORITZONTAL, X);
        assertFinestra("x . . x", 100, baix("x..x...."), 0, 0, HORITZONTAL, X);
    }

    @Test
    public void pas1e_tresMevesVal1000() {
        assertFinestra("x x x .", 1000, baix("xxx....."), 0, 0, HORITZONTAL, X);
        assertFinestra("x . x x", 1000, baix("x.xx...."), 0, 0, HORITZONTAL, X);
    }

    @Test
    public void pas1f_fitxesDelRivalSonNegatives() {
        assertFinestra("o o . .", -100, baix("oo......"), 0, 0, HORITZONTAL, X);
        assertFinestra("o o o .", -1000, baix("ooo....."), 0, 0, HORITZONTAL, X);
    }

    @Test
    public void pas1g_finestresBarrejadesValen0() {
        assertFinestra("x o . .", 0, baix("xo......"), 0, 0, HORITZONTAL, X);
        assertFinestra("x x o .", 0, baix("xxo....."), 0, 0, HORITZONTAL, X);
        assertFinestra("x o o .", 0, baix("xoo....."), 0, 0, HORITZONTAL, X);
        assertFinestra("x x x o", 0, baix("xxxo...."), 0, 0, HORITZONTAL, X);
    }

    @Test
    public void pas1h_depenDelPuntDeVista() {
        assertFinestra("x x . . vist per o", -100, baix("xx......"), 0, 0, HORITZONTAL, O);
        assertFinestra("o o o . vist per o", 1000, baix("ooo....."), 0, 0, HORITZONTAL, O);
    }

    @Test
    public void pas1i_finestraQueNoComencaAlaColumna0() {
        // Finestra de les columnes 4..7
        assertFinestra("finestra que comença a (0,4)", 100, baix("....xx.."), 0, 4, HORITZONTAL, X);
        assertFinestra("finestra que comença a (0,4)", 1000, baix(".....xxx"), 0, 4, HORITZONTAL, X);
    }

    @Test
    public void pas1j_vertical() {
        Tauler t = tauler(
                "........",
                "........",
                "........",
                "........",
                "........",
                "x.......",
                "x.......",
                "x.......");
        assertFinestra("vertical des de (0,0): x x x .", 1000, t, 0, 0, VERTICAL, X);
        assertFinestra("vertical des de (1,0): x x . .", 100, t, 1, 0, VERTICAL, X);
        assertFinestra("vertical des de (2,0): x . . .", 0, t, 2, 0, VERTICAL, X);
    }

    @Test
    public void pas1k_diagonalCapAlaDreta() {
        // x a (0,0) i (1,1); la o de (0,1) només fa de suport
        Tauler t = tauler(
                "........",
                "........",
                "........",
                "........",
                "........",
                "........",
                ".x......",
                "xo......");
        assertFinestra("diagonal ↗ des de (0,0): x x . .", 100, t, 0, 0, DIAGONAL_DRETA, X);
    }

    @Test
    public void pas1l_diagonalCapAlaEsquerra() {
        // Finestra (0,3) (1,2) (2,1) (3,0): x a (0,3) i (1,2); la o de (0,2) és suport
        Tauler t = tauler(
                "........",
                "........",
                "........",
                "........",
                "........",
                "........",
                "..x.....",
                "..ox....");
        assertFinestra("diagonal ↖ des de (0,3): x x . .", 100, t, 0, 3, DIAGONAL_ESQUERRA, X);
    }

    // ======================================================================
    //  PAS 2 · avalua(): suma de totes les finestres del tauler
    // ======================================================================

    @Test
    public void pas2a_esVisitenExactament130Finestres() {
        final int[] crides = {0};
        HeuristicaFinestres comptadora = new HeuristicaFinestres() {
            @Override
            protected int puntuaFinestra(Tauler t, int f, int c, int df, int dc, int colorJo) {
                crides[0]++;
                return super.puntuaFinestra(t, f, c, df, dc, colorJo);
            }
        };
        comptadora.avalua(new Tauler(8), X);
        assertEquals("En un 8x8 hi ha 40 horitzontals + 40 verticals + 25 + 25 diagonals."
                + " Si en surten 260, compteu cada finestra dues vegades; si en surten menys,"
                + " reviseu els límits.", 130, crides[0]);
    }

    @Test
    public void pas2b_taulerBuitVal0() {
        assertEquals("Tauler buit", 0, h.avalua(new Tauler(8), X));
    }

    @Test
    public void pas2c_duesEnHoritzontal() {
        // Només la finestra de les columnes 0..3 en té 2
        assertEquals("x x a baix a l'esquerra", 100, h.avalua(baix("xx......"), X));
        assertEquals("el mateix tauler vist pel rival", -100, h.avalua(baix("xx......"), O));
    }

    @Test
    public void pas2d_duesALaCantonadaDreta() {
        // Només la finestra de les columnes 4..7: comprova els límits per la dreta
        assertEquals(". . . . . . x x", 100, h.avalua(baix("......xx"), X));
    }

    @Test
    public void pas2e_tresEnHoritzontal() {
        // Finestra 0..3 = 3 fitxes (1000), finestra 1..4 = 2 fitxes (100)
        assertEquals("x x x . . . . .", 1100, h.avalua(baix("xxx....."), X));
    }

    @Test
    public void pas2f_tresEnVertical() {
        Tauler t = tauler(
                "........",
                "........",
                "........",
                "........",
                "........",
                "...x....",
                "...x....",
                "...x....");
        assertEquals("Tres en vertical (1 de 3 + 1 de 2)", 1100, h.avalua(t, X));
    }

    @Test
    public void pas2g_finestresBarrejadesNoSumen() {
        assertEquals("x x x o . . . .", 0, h.avalua(baix("xxxo...."), X));
    }

    @Test
    public void pas2h_diagonalAmbFitxesDelRival() {
        // x: diagonal ↗ (0,0)..(3,3) = 3 (+1000), diagonal ↗ (1,1)..(4,4) = 2 (+100)
        // o: horitzontal fila 0 des de la columna 1: o o . . (-100)
        //    diagonal ↗ des de (0,1): o o . . (-100)
        // Total: 1000 + 100 - 100 - 100 = 900
        Tauler t = tauler(
                "........",
                "........",
                "........",
                "........",
                "........",
                "..x.....",
                ".xo.....",
                "xoo.....");
        assertEquals("Diagonal ↗ amb suport del rival", 900, h.avalua(t, X));
    }

    @Test
    public void pas2i_diagonalEsquerraEsElMirall() {
        Tauler t = tauler(
                "........",
                "........",
                "........",
                "........",
                "........",
                ".....x..",
                ".....ox.",
                ".....oox");
        assertEquals("Mirall del tauler anterior: ha de valer el mateix", 900, h.avalua(t, X));
    }

    @Test
    public void pas2j_simetriaDeColors() {
        String[] files = {
            "........",
            "........",
            "........",
            "...o....",
            "...xo...",
            "..oxx...",
            ".xxoo...",
            "oxoxxo.."};
        Tauler t = tauler(files);
        assertEquals("avalua(t, 1) == -avalua(t, -1)", h.avalua(t, X), -h.avalua(t, O));
        assertEquals("intercanviar x i o al tauler canvia el signe",
                h.avalua(t, X), -h.avalua(tauler(intercanvia(files)), X));
    }

    // ======================================================================
    //  Utilitats
    // ======================================================================

    private void assertFinestra(String descripcio, int esperat, Tauler t,
            int f, int c, int[] dir, int colorJo) {
        assertEquals(descripcio + "  [finestra des de (" + f + "," + c + "), direcció {"
                + dir[0] + "," + dir[1] + "}, colorJo=" + colorJo + "]",
                esperat, h.puntuaFinestra(t, f, c, dir[0], dir[1], colorJo));
    }

    /** Tauler 8x8 amb només la fila de baix plena segons el patró. */
    private static Tauler baix(String filaDeBaix) {
        return tauler("........", "........", "........", "........",
                "........", "........", "........", filaDeBaix);
    }

    /** Construeix un tauler de dalt a baix deixant caure les fitxes amb afegeix(). */
    private static Tauler tauler(String... files) {
        int mida = files.length;
        Tauler t = new Tauler(mida);
        for (int c = 0; c < mida; c++) {
            for (int f = 0; f < mida; f++) {           // f = 0 és la fila de baix
                char ch = files[mida - 1 - f].charAt(c);
                if (ch != '.') {
                    t.afegeix(c, ch == 'x' ? X : O);
                }
            }
        }
        return t;
    }

    private static String[] intercanvia(String[] files) {
        String[] r = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            r[i] = files[i].replace('x', '#').replace('o', 'x').replace('#', 'o');
        }
        return r;
    }
}
