package edu.epsevg.prop.lab.c4;

import static edu.epsevg.prop.lab.c4.Proves.comprova;
import static edu.epsevg.prop.lab.c4.Proves.tauler;

import edu.epsevg.prop.lab.c4.jugador.HeuristicaFinestres;

/**
 * SESSIÓ 2 · Proves de {@link HeuristicaFinestres} sobre taulers sencers.
 *
 * <p>Executa aquesta classe (botó dret → Run File / Shift+F6). No cal tocar-la.
 * Si vols anar pas a pas (primer una sola finestra, després tot el tauler), fes servir
 * els tests JUnit de {@code HeuristicaFinestresTest} (Test Packages → Test File).</p>
 */
public class ProvesHeuristica {

    public static void main(String[] args) {
        System.out.println("=== SESSIÓ 2 · Heurística de finestres ===");
        provesFinestres();
        Proves.resum();
    }

    static void provesFinestres() {

        HeuristicaFinestres h = new HeuristicaFinestres();

        // 1. Nombre de finestres: comptem quantes vegades es crida puntuaFinestra.
        final int[] crides = {0};
        HeuristicaFinestres comptadora = new HeuristicaFinestres() {
            @Override
            protected int puntuaFinestra(Tauler t, int f, int c, int df, int dc, int colorJo) {
                crides[0]++;
                return super.puntuaFinestra(t, f, c, df, dc, colorJo);
            }
        };
        comptadora.avalua(new Tauler(8), 1);
        comprova("Es visiten exactament 130 finestres en un tauler 8x8", 130, crides[0]);

        // 2. Tauler buit.
        comprova("Tauler buit", 0, h.avalua(new Tauler(8), 1));

        // 3. Dues fitxes juntes a baix a l'esquerra: només la finestra horitzontal
        //    de les columnes 0..3 en té 2 i la resta buides.
        Tauler dues = tauler(
                "........",
                "........",
                "........",
                "........",
                "........",
                "........",
                "........",
                "xx......");
        comprova("Dues en horitzontal (finestres: 1 de 2)", 100, h.avalua(dues, 1));
        comprova("El mateix tauler vist pel rival", -100, h.avalua(dues, -1));

        // 4. Tres en horitzontal: finestra 0..3 té 3 (1000), finestra 1..4 en té 2 (100).
        Tauler tres = tauler(
                "........",
                "........",
                "........",
                "........",
                "........",
                "........",
                "........",
                "xxx.....");
        comprova("Tres en horitzontal (1 de 3 + 1 de 2)", 1100, h.avalua(tres, 1));

        // 5. Tres en vertical a la columna 3: finestres verticals que comencen a la
        //    fila 0 (3 fitxes) i a la fila 1 (2 fitxes).
        Tauler vertical = tauler(
                "........",
                "........",
                "........",
                "........",
                "........",
                "...x....",
                "...x....",
                "...x....");
        comprova("Tres en vertical (1 de 3 + 1 de 2)", 1100, h.avalua(vertical, 1));

        // 6. Finestres barrejades no compten: cap finestra té només x i buides
        //    amb 2 o més x, ni només o amb 2 o més o.
        Tauler barrejat = tauler(
                "........",
                "........",
                "........",
                "........",
                "........",
                "........",
                "........",
                "xxxo....");
        comprova("Finestres barrejades valen 0", 0, h.avalua(barrejat, 1));

        // 7. Diagonal "/": x a (0,0), (1,1), (2,2). Les o fan de suport.
        //    x: diagonal (0,0)->(3,3) té 3 x i 1 buida = +1000
        //       diagonal (1,1)->(4,4) té 2 x i 2 buides = +100
        //    o: horitzontal fila 0 des de la columna 1: o,o,.,.           = -100
        //       diagonal "/" des de (0,1): (0,1)o,(1,2)o,(2,3).,(3,4).    = -100
        //       (la vertical de la columna 2, o,o,x,. està barrejada: 0)
        //    Total: 1000 + 100 - 100 - 100 = 900
        Tauler diagonal = tauler(
                "........",
                "........",
                "........",
                "........",
                "........",
                "..x.....",
                ".xo.....",
                "xoo.....");
        comprova("Diagonal / amb suport del rival", 900, h.avalua(diagonal, 1));

        // 8. Diagonal "\": el mirall del tauler anterior ha de valer el mateix.
        Tauler mirall = tauler(
                "........",
                "........",
                "........",
                "........",
                "........",
                ".....x..",
                ".....ox.",
                ".....oox");
        comprova("Diagonal \\ (mirall de l'anterior)", 900, h.avalua(mirall, 1));

        // 9. Propietat de signe: avalua(t, 1) == -avalua(t, -1) en qualsevol tauler.
        Tauler qualsevol = tauler(
                "........",
                "........",
                "........",
                "...o....",
                "...xo...",
                "..oxx...",
                ".xxoo...",
                "oxoxxo..");
        comprova("avalua(t, 1) == -avalua(t, -1)",
                h.avalua(qualsevol, 1), -h.avalua(qualsevol, -1));
        }
}
