package edu.epsevg.prop.lab.c4.ui;

import edu.epsevg.prop.lab.c4.jugador.IAuto;
import edu.epsevg.prop.lab.c4.jugador.Jugador;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * Targeta amb la informació d'un jugador: avatar, nom, color i estat.
 */
class TargetaJugador extends JPanel {

    private final int color;
    private final JLabel nom = new JLabel();
    private final JLabel detall = new JLabel();
    private final JProgressBar pensant = new JProgressBar();
    private boolean actiu;

    TargetaJugador(Jugador j, int color, String etiqueta) {
        super(new BorderLayout(12, 0));
        this.color = color;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 16));

        boolean ia = j instanceof IAuto;
        JLabel avatar = new JLabel(Tema.icona(ia ? "robot.png" : "huma.png", 46));
        add(avatar, BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel fitxa = new JLabel(etiqueta + " · " + Tema.nomColor(color) + (ia ? " · IA" : " · Humà"), new Disc(color, 11), JLabel.LEADING);
        fitxa.setFont(Tema.font(Font.PLAIN, 12f));
        fitxa.setIconTextGap(6);
        nom.setText(j.nom());
        nom.setFont(Tema.font(Font.BOLD, 16f));
        detall.setFont(Tema.font(Font.PLAIN, 12f));
        pensant.setIndeterminate(true);
        pensant.setVisible(false);
        pensant.setMaximumSize(new Dimension(160, 6));
        pensant.setPreferredSize(new Dimension(160, 6));
        pensant.setAlignmentX(LEFT_ALIGNMENT);
        text.add(fitxa);
        text.add(Box.createVerticalStrut(2));
        text.add(nom);
        text.add(Box.createVerticalStrut(4));
        text.add(detall);
        text.add(pensant);
        add(text, BorderLayout.CENTER);
        reinicia();
    }

    void reinicia() {
        detall.setText(" ");
        detall.setForeground(Tema.textSuau());
        setPensant(false);
    }

    void setActiu(boolean actiu) {
        this.actiu = actiu;
        repaint();
    }

    void setPensant(boolean p) {
        pensant.setVisible(p);
        detall.setVisible(!p);
    }

    void setUltim(int columna, long ms) {
        detall.setForeground(Tema.textSuau());
        detall.setText("Última: columna " + columna + (ms >= 0 ? "  ·  " + ms + " ms" : ""));
    }

    void setResultat(String text, Color c) {
        detall.setVisible(true);
        pensant.setVisible(false);
        detall.setForeground(c);
        detall.setText(text);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fons = Tema.barreja(Tema.fons(), Tema.esFosc() ? Color.WHITE : Color.BLACK, Tema.esFosc() ? 0.05 : 0.04);
        RoundRectangle2D rr = new RoundRectangle2D.Double(1, 1, getWidth() - 3, getHeight() - 3, 18, 18);
        g.setColor(fons);
        g.fill(rr);
        if (actiu) {
            g.setStroke(new BasicStroke(2.5f));
            g.setColor(Tema.color(color));
        } else {
            g.setStroke(new BasicStroke(1f));
            g.setColor(Tema.vora());
        }
        g.draw(rr);
        g.dispose();
        super.paintComponent(g0);
    }

    /** Icona circular amb el color de la fitxa. */
    static class Disc implements Icon {
        private final int color;
        private final int mida;

        Disc(int color, int mida) {
            this.color = color;
            this.mida = mida;
        }

        @Override
        public void paintIcon(java.awt.Component c, Graphics g0, int x, int y) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Tema.color(color));
            g.fillOval(x, y, mida, mida);
            g.dispose();
        }

        @Override
        public int getIconWidth() {
            return mida;
        }

        @Override
        public int getIconHeight() {
            return mida;
        }
    }
}
