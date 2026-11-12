package edu.epsevg.prop.lab.c4.ui;

import edu.epsevg.prop.lab.c4.jugador.IAuto;
import edu.epsevg.prop.lab.c4.jugador.Jugador;
import edu.epsevg.prop.lab.c4.Tauler;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/**
 * Finestra principal del joc. No cal tocar-la per fer la pràctica:
 * els jugadors es configuren a {@code Juga2.main()}.
 */
public class FinestraJoc extends JFrame {

    /** Mida del tauler (8x8, com el {@code ProfePlayer}). */
    public static final int MIDA = 8;
    /** Temps mínim entre moviments quan juguen dues IA, per poder seguir la partida. */
    private static final int RETARD_IA_VS_IA_MS = 400;

    private final Jugador[] jugadors;
    private final boolean autoMode;

    private Tauler tauler;
    private int torn;               // 0 -> jugador 1 (color 1), 1 -> jugador 2 (color -1)
    private int numMoviment;
    private boolean pensant;
    private boolean acabada;
    private int generacio;          // invalida els càlculs en curs quan es reinicia la partida

    private final TaulerPanel taulerPanel = new TaulerPanel(MIDA);
    private final TargetaJugador[] targetes = new TargetaJugador[2];
    private final DefaultListModel<String> historial = new DefaultListModel<>();
    private final JList<String> llistaHistorial = new JList<>(historial);
    private final JLabel estat = new JLabel(" ");
    private final JToggleButton botoPausa = new JToggleButton("Pausa");
    private final JButton botoSeguent = new JButton("Següent moviment IA");

    /**
     * Mostra la finestra del joc.
     *
     * @param p1       jugador 1 (vermell, color 1, comença)
     * @param p2       jugador 2 (groc, color -1)
     * @param autoMode true: les IA juguen soles. false: cal clicar per fer moure la IA.
     */
    public static void mostra(Jugador p1, Jugador p2, boolean autoMode) {
        SwingUtilities.invokeLater(() -> {
            Tema.aplica(true);
            FinestraJoc f = new FinestraJoc(p1, p2, autoMode);
            f.setVisible(true);
            f.novaPartida();
        });
    }

    FinestraJoc(Jugador p1, Jugador p2, boolean autoMode) {
        super("Connecta 4 · PROP");
        this.jugadors = new Jugador[]{p1, p2};
        this.autoMode = autoMode;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Image icona = Tema.imatge("logo.png");
        if (icona != null) {
            setIconImage(icona);
        }

        targetes[0] = new TargetaJugador(p1, 1, "P1");
        targetes[1] = new TargetaJugador(p2, -1, "P2");
        // Les dues targetes amb la mateixa mida (i una mica de marge per als textos que canvien)
        Dimension d0 = targetes[0].getPreferredSize();
        Dimension d1 = targetes[1].getPreferredSize();
        Dimension d = new Dimension(Math.max(260, Math.max(d0.width, d1.width) + 40), Math.max(d0.height, d1.height));
        targetes[0].setPreferredSize(d);
        targetes[1].setPreferredSize(d);

        JPanel arrel = new JPanel(new BorderLayout(0, 0));
        arrel.add(capcalera(), BorderLayout.NORTH);
        arrel.add(centre(), BorderLayout.CENTER);
        arrel.add(lateral(), BorderLayout.EAST);
        arrel.add(barraEstat(), BorderLayout.SOUTH);
        setContentPane(arrel);

        taulerPanel.setEnClicar(this::columnaClicada);
        pack();
        setMinimumSize(getPreferredSize());
        setLocationRelativeTo(null);
    }

    // ------------------------------------------------------------------
    //  Construcció de la UI
    // ------------------------------------------------------------------

    private JComponent capcalera() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setBorder(BorderFactory.createEmptyBorder(14, 18, 6, 18));

        ImageIcon logo = Tema.icona("logo.png", 44);
        JLabel titol = new JLabel("Connecta 4", logo, JLabel.LEADING);
        titol.setIconTextGap(12);
        titol.setFont(Tema.font(Font.BOLD, 24f));
        JLabel subtitol = new JLabel("PROP · Laboratori de minimax");
        subtitol.setFont(Tema.font(Font.PLAIN, 13f));
        subtitol.setForeground(Tema.textSuau());
        JPanel titols = new JPanel(new BorderLayout());
        titols.setOpaque(false);
        titols.add(titol, BorderLayout.CENTER);
        titols.add(subtitol, BorderLayout.SOUTH);
        subtitol.setBorder(BorderFactory.createEmptyBorder(0, 56, 0, 0));
        p.add(titols, BorderLayout.WEST);

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        barra.setOpaque(false);
        JButton nova = new JButton("Nova partida");
        nova.addActionListener(e -> novaPartida());
        JButton copia = new JButton("Copia tauler");
        copia.setToolTipText("Copia el tauler en format Proves.tauler(...) per crear proves");
        copia.addActionListener(e -> copiaTauler());
        JToggleButton tema = new JToggleButton("Tema clar");
        tema.addActionListener(e -> {
            tema.setText(tema.isSelected() ? "Tema fosc" : "Tema clar");
            canviaTema(!tema.isSelected());
        });
        botoPausa.setToolTipText("Atura les IA entre moviments");
        botoPausa.addActionListener(e -> {
            botoPausa.setText(botoPausa.isSelected() ? "Continua" : "Pausa");
            seguentTorn();
        });
        botoSeguent.addActionListener(e -> {
            if (!acabada && !pensant && !taulerPanel.estaAnimant() && actual() instanceof IAuto) {
                mouIA();
            }
        });
        barra.add(nova);
        barra.add(Box.createHorizontalStrut(6));
        if (hiHaIA()) {
            barra.add(autoMode ? botoPausa : botoSeguent);
            barra.add(Box.createHorizontalStrut(6));
        }
        barra.add(copia);
        barra.add(Box.createHorizontalStrut(6));
        barra.add(tema);
        p.add(barra, BorderLayout.EAST);
        return p;
    }

    private JComponent centre() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBorder(BorderFactory.createEmptyBorder(8, 18, 4, 8));

        JPanel jugadorsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1;
        gc.gridy = 0;
        gc.gridx = 0;
        jugadorsPanel.add(targetes[0], gc);
        JLabel vs = new JLabel("VS");
        vs.setFont(Tema.font(Font.BOLD, 15f));
        vs.setForeground(Tema.textSuau());
        gc.weightx = 0;
        gc.gridx = 1;
        gc.insets = new Insets(0, 14, 0, 14);
        jugadorsPanel.add(vs, gc);
        gc.weightx = 1;
        gc.gridx = 2;
        gc.insets = new Insets(0, 0, 0, 0);
        jugadorsPanel.add(targetes[1], gc);

        p.add(jugadorsPanel, BorderLayout.NORTH);
        p.add(taulerPanel, BorderLayout.CENTER);
        return p;
    }

    private JComponent lateral() {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 18));
        JLabel t = new JLabel("Moviments");
        t.setFont(Tema.font(Font.BOLD, 14f));
        p.add(t, BorderLayout.NORTH);
        llistaHistorial.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        llistaHistorial.setFocusable(false);
        JScrollPane sp = new JScrollPane(llistaHistorial);
        sp.setPreferredSize(new Dimension(240, 200));
        p.add(sp, BorderLayout.CENTER);
        JLabel ajuda = new JLabel("<html>Les columnes es numeren de 0 a 7,<br>igual que al codi.</html>");
        ajuda.setFont(Tema.font(Font.PLAIN, 11f));
        ajuda.setForeground(Tema.textSuau());
        p.add(ajuda, BorderLayout.SOUTH);
        return p;
    }

    private JComponent barraEstat() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(6, 20, 10, 20));
        estat.setFont(Tema.font(Font.PLAIN, 13f));
        p.add(estat, BorderLayout.CENTER);
        return p;
    }

    // ------------------------------------------------------------------
    //  Lògica de la partida
    // ------------------------------------------------------------------

    private boolean hiHaIA() {
        return jugadors[0] instanceof IAuto || jugadors[1] instanceof IAuto;
    }

    private boolean duesIA() {
        return jugadors[0] instanceof IAuto && jugadors[1] instanceof IAuto;
    }

    private Jugador actual() {
        return jugadors[torn];
    }

    private int colorActual() {
        return torn == 0 ? 1 : -1;
    }

    private void novaPartida() {
        generacio++;
        tauler = new Tauler(MIDA);
        torn = 0;
        numMoviment = 0;
        pensant = false;
        acabada = false;
        historial.clear();
        taulerPanel.reinicia(tauler);
        for (TargetaJugador t : targetes) {
            t.reinicia();
        }
        seguentTorn();
    }

    private void seguentTorn() {
        targetes[0].setActiu(!acabada && torn == 0);
        targetes[1].setActiu(!acabada && torn == 1);
        if (acabada || pensant || taulerPanel.estaAnimant()) {
            return;
        }
        Jugador j = actual();
        String qui = j.nom() + " (" + Tema.nomColor(colorActual()) + ")";
        if (j instanceof IAuto) {
            taulerPanel.setColorFantasma(0);
            botoSeguent.setEnabled(true);
            if (autoMode && !botoPausa.isSelected()) {
                mouIA();
            } else {
                estat("Torn de " + qui + ". Prem \"" + (autoMode ? "Continua" : "Següent moviment IA")
                        + "\" o clica el tauler.");
            }
        } else {
            botoSeguent.setEnabled(false);
            taulerPanel.setColorFantasma(colorActual());
            estat("Torn de " + qui + ": clica una columna.");
        }
    }

    private void columnaClicada(int col) {
        if (acabada || pensant || taulerPanel.estaAnimant()) {
            return;
        }
        if (actual() instanceof IAuto) {
            if (!autoMode || botoPausa.isSelected()) {
                mouIA();
            }
            return;
        }
        if (!tauler.movpossible(col)) {
            Toolkit.getDefaultToolkit().beep();
            estat("La columna " + col + " és plena.");
            return;
        }
        aplicaMoviment(col, -1);
    }

    private void mouIA() {
        pensant = true;
        final int gen = generacio;
        final int jo = torn;
        final int color = colorActual();
        final Jugador j = actual();
        final Tauler copia = new Tauler(tauler);   // la IA no pot espatllar el tauler real
        final boolean retard = duesIA();
        targetes[jo].setPensant(true);
        botoSeguent.setEnabled(false);
        estat(j.nom() + " està pensant…");

        new SwingWorker<Integer, Void>() {
            private long ms;

            @Override
            protected Integer doInBackground() throws Exception {
                long t0 = System.nanoTime();
                int col = j.moviment(copia, color);
                ms = (System.nanoTime() - t0) / 1_000_000;
                if (retard && ms < RETARD_IA_VS_IA_MS) {
                    Thread.sleep(RETARD_IA_VS_IA_MS - ms);
                }
                return col;
            }

            @Override
            protected void done() {
                if (gen != generacio) {
                    return;                         // s'ha reiniciat la partida mentre pensava
                }
                pensant = false;
                targetes[jo].setPensant(false);
                try {
                    aplicaMoviment(get(), ms);
                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause();
                    causa.printStackTrace();
                    acabaPerError(j.nom() + " ha llançat " + causa.getClass().getSimpleName()
                            + (causa.getMessage() != null ? ": " + causa.getMessage() : ""));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }

    private void aplicaMoviment(int col, long ms) {
        int color = colorActual();
        Jugador j = actual();
        if (col < 0 || col >= MIDA || !tauler.movpossible(col)) {
            acabaPerError(j.nom() + " ha fet un moviment il·legal (columna " + col + ")");
            return;
        }
        int fila = 0;
        while (tauler.getColor(fila, col) != 0) {
            fila++;
        }
        tauler.afegeix(col, color);
        numMoviment++;
        historial.addElement(String.format("%3d. %-7s → col %d%s", numMoviment, Tema.nomColor(color), col,
                ms >= 0 ? String.format("  %5d ms", ms) : ""));
        llistaHistorial.ensureIndexIsVisible(historial.size() - 1);
        targetes[torn].setUltim(col, ms);
        taulerPanel.setColorFantasma(0);

        final int f = fila;
        final int gen = generacio;
        taulerPanel.animaCaiguda(fila, col, color, () -> {
            if (gen == generacio) {
                comprovaFinal(f, col, color);
            }
        });
    }

    private void comprovaFinal(int fila, int col, int color) {
        if (!tauler.isGameOver()) {
            torn = 1 - torn;
            seguentTorn();
        } else if (tauler.getGuanyador() != 0) {
            acabada = true;
            List<int[]> linia = TaulerPanel.liniaGuanyadora(tauler, fila, col);
            taulerPanel.setGuanyadores(linia);
            String msg = "Guanya " + actual().nom() + " (" + Tema.nomColor(color) + ") amb un moviment a la columna " + col;
            targetes[torn].setResultat("Guanya!", Tema.VERD_OK);
            targetes[1 - torn].setResultat("Perd", Tema.textSuau());
            fi(msg, "Victòria de " + actual().nom());
        } else {
            acabada = true;
            targetes[0].setResultat("Taules", Tema.textSuau());
            targetes[1].setResultat("Taules", Tema.textSuau());
            fi("Taules: el tauler és ple.", "Taules");
        }
    }

    private void acabaPerError(String motiu) {
        acabada = true;
        pensant = false;
        Jugador guanyador = jugadors[1 - torn];
        targetes[torn].setResultat("Desqualificat", Tema.VERMELL);
        targetes[1 - torn].setResultat("Guanya per desqualificació", Tema.VERD_OK);
        historial.addElement("  ✗ " + motiu);
        fi(motiu + ". Guanya " + guanyador.nom() + ".", "Partida acabada");
    }

    private void fi(String msg, String titol) {
        estat(msg);
        targetes[0].setActiu(false);
        targetes[1].setActiu(false);
        final int gen = generacio;
        Timer t = new Timer(900, e -> {
            if (gen != generacio) {
                return;
            }
            Object[] opcions = {"Torna a jugar", "Mira el tauler"};
            int r = JOptionPane.showOptionDialog(this, msg + "\n\nVols tornar a jugar?", titol,
                    JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, Tema.icona("logo.png", 64),
                    opcions, opcions[0]);
            if (r == 0) {
                novaPartida();
            }
        });
        t.setRepeats(false);
        t.start();
    }

    private void estat(String s) {
        estat.setText(s);
    }

    private void copiaTauler() {
        StringBuilder sb = new StringBuilder("Proves.tauler(\n");
        for (int f = MIDA - 1; f >= 0; f--) {
            sb.append("        \"");
            for (int c = 0; c < MIDA; c++) {
                int v = tauler.getColor(f, c);
                sb.append(v == 1 ? 'x' : v == -1 ? 'o' : '.');
            }
            sb.append('"').append(f > 0 ? ",\n" : ");\n");
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString()), null);
        System.out.println(sb);
        estat("Tauler copiat al porta-retalls (i escrit a la consola). x = vermell (1), o = groc (-1).");
    }

    private void canviaTema(boolean fosc) {
        Tema.aplica(fosc);
        SwingUtilities.updateComponentTreeUI(this);
        repaint();
    }
}
