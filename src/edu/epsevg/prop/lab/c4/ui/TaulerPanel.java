package edu.epsevg.prop.lab.c4.ui;

import edu.epsevg.prop.lab.c4.Tauler;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Component que dibuixa el tauler: fitxes amb volum, animació de caiguda,
 * fitxa "fantasma" sobre la columna on és el ratolí i ressaltat de la línia guanyadora.
 */
class TaulerPanel extends JComponent {

    private static final double GRAVETAT = 0.00011;   // celles / ms^2

    private Tauler tauler;
    private final int mida;
    private IntConsumer enClicar = c -> { };

    private int colHover = -1;
    private int colorFantasma = 0;              // 0 = no es mostra
    private int ultimaF = -1;
    private int ultimaC = -1;
    private List<int[]> guanyadores = new ArrayList<>();
    private long iniciPols;

    // Animació de caiguda
    private int animF = -1;
    private int animC = -1;
    private int animColor;
    private double animY;                       // en celles, des del centre de la fila fantasma
    private double animV;
    private int rebots;
    private long animT;
    private Runnable enAcabarAnimacio;
    private final Timer timer;

    // Geometria (es recalcula a cada paint)
    private double cel;
    private double x0;
    private double y0;
    private double pad;

    TaulerPanel(int mida) {
        this.mida = mida;
        this.tauler = new Tauler(mida);
        setPreferredSize(new Dimension(620, 640));
        setMinimumSize(new Dimension(320, 340));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        timer = new Timer(12, e -> pasAnimacio());

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int c = columnaA(e.getX());
                if (c != colHover) {
                    colHover = c;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                colHover = -1;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int c = columnaA(e.getX());
                if (c >= 0) {
                    enClicar.accept(c);
                }
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    void setEnClicar(IntConsumer accio) {
        this.enClicar = accio;
    }

    void reinicia(Tauler t) {
        timer.stop();
        tauler = t;
        animF = animC = -1;
        ultimaF = ultimaC = -1;
        guanyadores = new ArrayList<>();
        repaint();
    }

    void setColorFantasma(int color) {
        colorFantasma = color;
        repaint();
    }

    boolean estaAnimant() {
        return animF >= 0;
    }

    void setGuanyadores(List<int[]> celles) {
        guanyadores = celles;
        iniciPols = System.currentTimeMillis();
        timer.start();                          // fa servir el mateix timer per pulsar
    }

    /**
     * Anima la caiguda d'una fitxa ja afegida al tauler a (f, c).
     */
    void animaCaiguda(int f, int c, int color, Runnable enAcabar) {
        animF = f;
        animC = c;
        animColor = color;
        animY = 0;
        animV = 0;
        rebots = 0;
        animT = System.currentTimeMillis();
        enAcabarAnimacio = enAcabar;
        ultimaF = f;
        ultimaC = c;
        timer.start();
    }

    private void pasAnimacio() {
        long ara = System.currentTimeMillis();
        if (animF >= 0) {
            double dt = Math.min(40, ara - animT);
            animT = ara;
            double desti = (mida - 1 - animF) + 1.35;   // de la fila fantasma al centre de la casella
            animV += GRAVETAT * dt;
            animY += animV * dt;
            if (animY >= desti) {
                animY = desti;
                if (rebots < 1 && animV > 0.004) {
                    animV = -animV * 0.22;
                    rebots++;
                } else {
                    animF = animC = -1;
                    if (guanyadores.isEmpty()) {
                        timer.stop();
                    }
                    repaint();
                    Runnable r = enAcabarAnimacio;
                    enAcabarAnimacio = null;
                    if (r != null) {
                        r.run();
                    }
                    return;
                }
            }
        }
        repaint();
    }

    private int columnaA(int x) {
        if (cel <= 0) {
            return -1;
        }
        int c = (int) Math.floor((x - x0 - pad) / cel);
        return (c >= 0 && c < mida) ? c : -1;
    }

    private double cx(int c) {
        return x0 + pad + c * cel + cel / 2;
    }

    private double cy(int f) {
        return y0 + pad + (mida - 1 - f) * cel + cel / 2;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // --- Geometria: una fila extra a dalt per a la fitxa fantasma, i etiquetes a baix
        double w = getWidth();
        double h = getHeight();
        cel = Math.min(w / (mida + 0.6), h / (mida + 2.0));
        pad = cel * 0.3;
        double ample = mida * cel + 2 * pad;
        double alt = mida * cel + 2 * pad;
        x0 = (w - ample) / 2;
        y0 = cel * 1.15;
        double r = cel * 0.40;

        Color fons = Tema.fons();
        Color forat = Tema.barreja(fons, Color.BLACK, Tema.esFosc() ? 0.35 : 0.12);

        // --- Columna ressaltada i fitxa fantasma
        boolean potClicar = colorFantasma != 0 && colHover >= 0 && !estaAnimant() && guanyadores.isEmpty();
        if (potClicar && tauler.movpossible(colHover)) {
            Color c = Tema.color(colorFantasma);
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 150));
            double gx = cx(colHover);
            double gy = y0 - cel * 0.55;
            g.fill(new Ellipse2D.Double(gx - r, gy - r, 2 * r, 2 * r));
        }

        // --- Ombra del tauler (abans de les fitxes, perquè no les enfosqueixi)
        g.setColor(new Color(0, 0, 0, Tema.esFosc() ? 90 : 45));
        g.fill(new RoundRectangle2D.Double(x0 + cel * 0.06, y0 + cel * 0.12, ample, alt, cel * 0.5, cel * 0.5));

        // --- Forats i fitxes (per sota del tauler)
        for (int f = 0; f < mida; f++) {
            for (int c = 0; c < mida; c++) {
                double px = cx(c);
                double py = cy(f);
                g.setColor(forat);
                g.fill(new Ellipse2D.Double(px - r, py - r, 2 * r, 2 * r));
                int v = tauler.getColor(f, c);
                if (v != 0 && !(f == animF && c == animC)) {
                    pintaFitxa(g, px, py, r * 0.97, Tema.color(v));
                }
            }
        }
        if (animF >= 0) {
            double py = y0 - cel * 0.55 + animY * cel;
            pintaFitxa(g, cx(animC), py, r * 0.97, Tema.color(animColor));
        }

        // --- Tauler (rectangle arrodonit amb els forats retallats)
        RoundRectangle2D cos = new RoundRectangle2D.Double(x0, y0, ample, alt, cel * 0.5, cel * 0.5);
        Area area = new Area(cos);
        for (int f = 0; f < mida; f++) {
            for (int c = 0; c < mida; c++) {
                area.subtract(new Area(new Ellipse2D.Double(cx(c) - r, cy(f) - r, 2 * r, 2 * r)));
            }
        }
        g.setPaint(new GradientPaint((float) x0, (float) y0, Tema.TAULER_CLAR,
                (float) (x0 + ample), (float) (y0 + alt), Tema.TAULER_FOSC));
        g.fill(area);
        // vora interior dels forats per donar profunditat
        g.setStroke(new BasicStroke((float) (cel * 0.045)));
        g.setColor(new Color(0, 0, 0, 70));
        for (int f = 0; f < mida; f++) {
            for (int c = 0; c < mida; c++) {
                g.draw(new Ellipse2D.Double(cx(c) - r, cy(f) - r, 2 * r, 2 * r));
            }
        }
        // ressaltat de la columna sota el ratolí
        if (potClicar) {
            g.setColor(new Color(255, 255, 255, 28));
            g.fill(new RoundRectangle2D.Double(x0 + pad + colHover * cel, y0 + pad * 0.4,
                    cel, alt - pad * 0.8, cel * 0.4, cel * 0.4));
        }

        // --- Marca de l'última jugada
        if (ultimaF >= 0 && animF < 0 && guanyadores.isEmpty()) {
            g.setColor(new Color(255, 255, 255, 200));
            double d = r * 0.28;
            g.fill(new Ellipse2D.Double(cx(ultimaC) - d / 2, cy(ultimaF) - d / 2, d, d));
        }

        // --- Línia guanyadora (anell que pulsa)
        if (!guanyadores.isEmpty()) {
            double fase = (System.currentTimeMillis() - iniciPols) / 380.0;
            float alpha = (float) (0.55 + 0.45 * Math.sin(fase));
            g.setStroke(new BasicStroke((float) (cel * 0.08)));
            g.setColor(new Color(1f, 1f, 1f, alpha));
            for (int[] fc : guanyadores) {
                double rr = r * 1.02;
                g.draw(new Ellipse2D.Double(cx(fc[1]) - rr, cy(fc[0]) - rr, 2 * rr, 2 * rr));
            }
            int[] a = guanyadores.get(0);
            int[] b = guanyadores.get(guanyadores.size() - 1);
            g.setStroke(new BasicStroke((float) (cel * 0.12), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(1f, 1f, 1f, alpha * 0.8f));
            g.drawLine((int) cx(a[1]), (int) cy(a[0]), (int) cx(b[1]), (int) cy(b[0]));
        }

        // --- Números de columna (els mateixos índexs que fa servir el codi!)
        g.setFont(Tema.font(Font.BOLD, (float) Math.max(11, cel * 0.22)));
        FontMetrics fm = g.getFontMetrics();
        for (int c = 0; c < mida; c++) {
            String s = String.valueOf(c);
            g.setColor(c == colHover && potClicar ? Tema.text() : Tema.textSuau());
            g.drawString(s, (float) (cx(c) - fm.stringWidth(s) / 2.0), (float) (y0 + alt + cel * 0.45));
        }
        g.dispose();
    }

    private static void pintaFitxa(Graphics2D g, double x, double y, double r, Color base) {
        Color clar = Tema.barreja(base, Color.WHITE, 0.45);
        Color fosc = Tema.barreja(base, Color.BLACK, 0.35);
        g.setPaint(new RadialGradientPaint(new Point2D.Double(x - r * 0.35, y - r * 0.4), (float) (r * 1.5),
                new float[]{0f, 0.5f, 1f}, new Color[]{clar, base, fosc}));
        g.fill(new Ellipse2D.Double(x - r, y - r, 2 * r, 2 * r));
        // anell interior típic de les fitxes
        g.setStroke(new BasicStroke((float) (r * 0.09)));
        g.setColor(Tema.barreja(base, Color.BLACK, 0.18));
        double ri = r * 0.68;
        g.draw(new Ellipse2D.Double(x - ri, y - ri, 2 * ri, 2 * ri));
        g.setColor(new Color(255, 255, 255, 60));
        g.draw(new Ellipse2D.Double(x - ri + r * 0.05, y - ri + r * 0.07, 2 * ri, 2 * ri));
    }

    /**
     * Busca les 4 (o més) caselles en ratlla que passen per (f, c).
     */
    static List<int[]> liniaGuanyadora(Tauler t, int f, int c) {
        int color = t.getColor(f, c);
        int[][] dirs = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
        for (int[] d : dirs) {
            List<int[]> linia = new ArrayList<>();
            int i = f;
            int j = c;
            while (dins(t, i - d[0], j - d[1]) && t.getColor(i - d[0], j - d[1]) == color) {
                i -= d[0];
                j -= d[1];
            }
            while (dins(t, i, j) && t.getColor(i, j) == color) {
                linia.add(new int[]{i, j});
                i += d[0];
                j += d[1];
            }
            if (linia.size() >= 4) {
                return linia;
            }
        }
        return new ArrayList<>();
    }

    private static boolean dins(Tauler t, int f, int c) {
        return f >= 0 && c >= 0 && f < t.getMida() && c < t.getMida();
    }
}
