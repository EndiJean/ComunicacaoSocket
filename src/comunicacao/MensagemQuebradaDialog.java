package comunicacao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MensagemQuebradaDialog extends JDialog {

    private JTextArea txtMensagem;
    private JTextField txtDelimitador;
    private JPanel panelMensagens;
    private JCheckBox checkQuebrarLinha;

    private JTextField txtIntervalo;
    private JTextField txtEvento;
    private JLabel lblIntervalo;
    private JLabel lblEvento;
    private JComboBox<String> comboModo;
    private JButton btnIniciar;
    
    private JLabel lblStatusExecucao;

    private transient ScheduledExecutorService scheduler;
    private transient ScheduledFuture<?> scheduledTask;

    private List<String> partes = new ArrayList<>();
    private List<JLabel> statusLabels = new ArrayList<>();

    private int currentIndex = 0;
    private boolean enviando = false;

    private static final String ICON_AGUARDANDO = "⏳";
    private static final String ICON_ENVIADO = "✅";

    private enum ModoEnvio { TEMPO, EVENTO }
    private ModoEnvio modoEnvio = ModoEnvio.TEMPO;

    public interface EnviarCallback {
        void enviarMensagem(String msg);
    }

    public MensagemQuebradaDialog(Frame owner, EnviarCallback callback) {
        super(owner, "Enviar Mensagens", true);
        setSize(700, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel topo = new JPanel(new BorderLayout());
        txtMensagem = new JTextArea(6, 40);
        topo.add(new JScrollPane(txtMensagem), BorderLayout.CENTER);

        JPanel delimitadorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        delimitadorPanel.add(new JLabel("Delimitador:"));
        txtDelimitador = new JTextField("[CR]", 10);
        setAlturaFixa(txtDelimitador, 20);
        delimitadorPanel.add(txtDelimitador);

        checkQuebrarLinha = new JCheckBox("Quebrar por linhas");
        delimitadorPanel.add(checkQuebrarLinha);
        checkQuebrarLinha.addActionListener(e -> txtDelimitador.setEnabled(!checkQuebrarLinha.isSelected()));

        JButton btnQuebrar = new JButton("Quebrar");
        setAlturaFixa(btnQuebrar, 20);
        delimitadorPanel.add(btnQuebrar);

        topo.add(delimitadorPanel, BorderLayout.SOUTH);
        add(topo, BorderLayout.NORTH);

        panelMensagens = new JPanel(new GridBagLayout());
        JScrollPane scroll = new JScrollPane(panelMensagens);
        add(scroll, BorderLayout.CENTER);

        btnQuebrar.addActionListener(e -> quebrarMensagens(callback));

        iniciarConfigEnvio(callback);

        comboModo.addActionListener(e -> atualizarCampos());
        atualizarCampos();
    }

    private void iniciarConfigEnvio(EnviarCallback callback) {
        JButton btnParar;
        JPanel painelConfig = new JPanel(new FlowLayout(FlowLayout.LEFT));

        painelConfig.add(new JLabel("Modo:"));
        comboModo = new JComboBox<>(new String[]{"Tempo", "Evento"});
        painelConfig.add(comboModo);

        lblIntervalo = new JLabel("Intervalo (seg):");
        painelConfig.add(lblIntervalo);

        txtIntervalo = new JTextField("5", 5);
        setAlturaFixa(txtIntervalo, 20);
        painelConfig.add(txtIntervalo);

        lblEvento = new JLabel("Evento trigger:");
        painelConfig.add(lblEvento);

        txtEvento = new JTextField("[ACK]", 8);
        setAlturaFixa(txtEvento, 20);
        painelConfig.add(txtEvento);

        btnIniciar = new JButton("Iniciar");
        setAlturaFixa(btnIniciar, 20);
        painelConfig.add(btnIniciar);

        btnParar = new JButton("Parar");
        setAlturaFixa(btnParar, 20);
        painelConfig.add(btnParar);

        lblStatusExecucao = new JLabel("⏹ Aguardando");
        lblStatusExecucao.setForeground(Color.RED);
        painelConfig.add(lblStatusExecucao);

        add(painelConfig, BorderLayout.SOUTH);

        btnIniciar.addActionListener(e -> iniciarEnvioAutomatico(callback));
        btnParar.addActionListener(e -> pararEnvioAutomatico());
    }

    private void atualizarCampos() {
        boolean porTempo = comboModo.getSelectedIndex() == 0;

        lblIntervalo.setVisible(porTempo);
        txtIntervalo.setVisible(porTempo);

        lblEvento.setVisible(!porTempo);
        txtEvento.setVisible(!porTempo);

        lblIntervalo.getParent().revalidate();
        lblIntervalo.getParent().repaint();
    }


    private void quebrarMensagens(EnviarCallback callback) {
        panelMensagens.removeAll();
        partes.clear();
        statusLabels.clear();
        currentIndex = 0;

        String texto = txtMensagem.getText();
        if (texto.isEmpty()) return;

        String[] split;
        String delimitador = txtDelimitador.getText();

        if (checkQuebrarLinha.isSelected()) {
            split = texto.split("\\r?\\n");
            for (String parte : split) {
                parte = parte.trim();
                if (!parte.isEmpty()) partes.add(parte);
            }
        } else {
            if (delimitador.isEmpty()) return;
            split = texto.split(Pattern.quote(delimitador));
            for (int i = 0; i < split.length; i++) {
                String parte = split[i].trim();
                if (!parte.isEmpty()) {
                    if (i == split.length - 1 && !texto.endsWith(parte + delimitador)) {
                        partes.add(parte);
                    } else {
                        partes.add(parte + delimitador);
                    }
                }
            }
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        for (String parte : partes) {
            JPanel linha = new JPanel(new BorderLayout(5, 5));

            JTextField txt = new JTextField(parte);
            txt.setEditable(false);
            Dimension d = new Dimension(400, 20);
            txt.setPreferredSize(d);
            txt.setMinimumSize(d);
            txt.setMaximumSize(d);
            linha.add(txt, BorderLayout.CENTER);

            JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            JLabel lblStatus = new JLabel(ICON_AGUARDANDO);
            statusLabels.add(lblStatus);
            direita.add(lblStatus);

            JButton btnEnviar = new JButton("Enviar");
            setAlturaFixa(btnEnviar, 20);
            btnEnviar.addActionListener(e -> {
                callback.enviarMensagem(parte);
                lblStatus.setText(ICON_ENVIADO);

                if (statusLabels.stream().allMatch(l -> l.getText().equals(ICON_ENVIADO))) {
                    reiniciarIcone();
                }
            });
            direita.add(btnEnviar);

            linha.add(direita, BorderLayout.EAST);

            panelMensagens.add(linha, gbc);
            gbc.gridy++;
        }

        gbc.weighty = 1.0;
        panelMensagens.add(Box.createVerticalGlue(), gbc);

        panelMensagens.revalidate();
        panelMensagens.repaint();
    }

    private void iniciarEnvioAutomatico(EnviarCallback callback) {
        if (partes.isEmpty()) {
        	return;
        }
        
        pararEnvioAutomatico();

        currentIndex = 0;
        enviando = true;
        reiniciarIcone();

        modoEnvio = comboModo.getSelectedIndex() == 0 ? ModoEnvio.TEMPO : ModoEnvio.EVENTO;

        controlarBotoesEnvio(false);
        if (modoEnvio == ModoEnvio.TEMPO) {
            int intervalo;
            try {
                intervalo = Integer.parseInt(txtIntervalo.getText());
            } catch (NumberFormatException e) {
                return;
            }

            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduledTask = scheduler.scheduleAtFixedRate(() -> {
                enviarProximaParte(callback);
            }, 0, intervalo, TimeUnit.SECONDS);

            atualizarStatusExecucao("▶ Rodando (tempo)", Color.GREEN);
        } else {
            atualizarStatusExecucao("▶ Aguardando evento: " + txtEvento.getText(), Color.BLUE);
        }
    }
    
    public void controlarBotoesEnvio(boolean habilitar) {
    	comboModo.setEnabled(habilitar);
    	txtIntervalo.setEnabled(habilitar);
    	txtEvento.setEnabled(habilitar);
    	btnIniciar.setEnabled(habilitar);
    }

    private void enviarProximaParte(EnviarCallback callback) {
        if (currentIndex < partes.size()) {
            String parte = partes.get(currentIndex);
            callback.enviarMensagem(parte);

            JLabel lblStatus = statusLabels.get(currentIndex);
            SwingUtilities.invokeLater(() -> lblStatus.setText(ICON_ENVIADO));

            currentIndex++;
        } else {
            reiniciarIcone();
            if (modoEnvio == ModoEnvio.TEMPO) {
				atualizarStatusExecucao("✔ Concluído", Color.BLUE);
				pararEnvioAutomatico();
            }
        }
            
         if (modoEnvio == ModoEnvio.EVENTO && currentIndex == partes.size()) {
        	 currentIndex = 0;
        	 reiniciarIcone();
        	 atualizarStatusExecucao("▶ Aguardando evento: " + txtEvento.getText(), Color.BLUE);
         }
    }

    public void notificarEvento(String evento, EnviarCallback callback) {
        if (modoEnvio == ModoEnvio.EVENTO && enviando && evento.equals(txtEvento.getText())) {
            enviarProximaParte(callback);
            atualizarStatusExecucao("▶ Recebido evento: " + evento, Color.MAGENTA);
        }
    }

    private void reiniciarIcone() {
        SwingUtilities.invokeLater(() -> {
            for (JLabel lbl : statusLabels) {
                lbl.setText(ICON_AGUARDANDO);
            }
        });
    }

    private void pararEnvioAutomatico() {
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(true);
            scheduler.shutdownNow();
        }
        enviando = false;
        currentIndex = 0;
        reiniciarIcone();
        controlarBotoesEnvio(true);
        atualizarStatusExecucao("⏹ Parado", Color.RED);
    }

    private void atualizarStatusExecucao(String texto, Color cor) {
        SwingUtilities.invokeLater(() -> {
            lblStatusExecucao.setText(texto);
            lblStatusExecucao.setForeground(cor);
        });
    }

    private void setAlturaFixa(JComponent comp, int altura) {
        Dimension d = comp.getPreferredSize();
        d.height = altura;
        comp.setPreferredSize(d);
        comp.setMinimumSize(d);
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, altura));
    }
}
