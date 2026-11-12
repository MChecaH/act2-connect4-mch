package edu.epsevg.prop.lab.c4.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

/**
 * Colors, tipografia i look and feel de l'aplicació.
 *
 * <p>Fa servir FlatLaf (lib/flatlaf-3.5.4.jar) si és al classpath i, si no, cau a
 * Nimbus. FlatLaf es carrega pel nom de classe, de manera que el projecte compila
 * igualment sense el jar.</p>
 */
public final class Tema {

    /** Color de les fitxes del jugador 1 (color 1). */
    public static final Color VERMELL = new Color(0xEF, 0x44, 0x44);
    /** Color de les fitxes del jugador 2 (color -1). */
    public static final Color GROC = new Color(0xFA, 0xCC, 0x15);

    static final Color TAULER_CLAR = new Color(0x25, 0x63, 0xEB);
    static final Color TAULER_FOSC = new Color(0x1E, 0x3A, 0x8A);
    static final Color VERD_OK = new Color(0x22, 0xC5, 0x5E);

    private static boolean fosc = true;

    private Tema() {
    }

    /**
     * Instal·la el look and feel.
     *
     * @param modeFosc true per al tema fosc
     */
    public static void aplica(boolean modeFosc) {
        fosc = modeFosc;
        String flat = modeFosc ? "com.formdev.flatlaf.FlatDarkLaf" : "com.formdev.flatlaf.FlatLightLaf";
        try {
            UIManager.setLookAndFeel(flat);
        } catch (Exception noHiHaFlatLaf) {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignore) {
                // ens quedem amb el look and feel per defecte
            }
        }
        UIManager.put("Button.arc", 14);
        UIManager.put("Component.arc", 14);
        UIManager.put("ProgressBar.arc", 14);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 10);
    }

    /** @return true si el tema actual és fosc */
    public static boolean esFosc() {
        return fosc;
    }

    /**
     * @param color 1 o -1
     * @return color de la fitxa
     */
    public static Color color(int color) {
        return color == 1 ? VERMELL : GROC;
    }

    /**
     * @param color 1 o -1
     * @return nom del color, per a l'usuari
     */
    public static String nomColor(int color) {
        return color == 1 ? "Vermell" : "Groc";
    }

    static Color fons() {
        Color c = UIManager.getColor("Panel.background");
        return c != null ? c : new Color(0x1F, 0x22, 0x28);
    }

    static Color text() {
        Color c = UIManager.getColor("Label.foreground");
        return c != null ? c : Color.WHITE;
    }

    static Color textSuau() {
        Color c = UIManager.getColor("Label.disabledForeground");
        return c != null ? c : Color.GRAY;
    }

    static Color vora() {
        Color c = UIManager.getColor("Component.borderColor");
        return c != null ? c : Color.GRAY;
    }

    static Color barreja(Color a, Color b, double t) {
        return new Color(
                (int) Math.round(a.getRed() * (1 - t) + b.getRed() * t),
                (int) Math.round(a.getGreen() * (1 - t) + b.getGreen() * t),
                (int) Math.round(a.getBlue() * (1 - t) + b.getBlue() * t));
    }

    static Font font(int estil, float mida) {
        Font base = UIManager.getFont("Label.font");
        if (base == null) {
            base = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
        }
        return base.deriveFont(estil, mida);
    }

    /**
     * Carrega una imatge del directori {@code img/} i l'escala.
     *
     * @param nom  nom del fitxer (p.ex. "robot.png")
     * @param mida costat en píxels
     * @return la icona, o null si no es troba
     */
    static ImageIcon icona(String nom, int mida) {
        Image img = imatge(nom);
        return img == null ? null : new ImageIcon(img.getScaledInstance(mida, mida, Image.SCALE_SMOOTH));
    }

    static Image imatge(String nom) {
        URL url = Tema.class.getResource("img/" + nom);
        return url == null ? null : new ImageIcon(url).getImage();
    }
}
