package comunicacao;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MensagemQuebradaDialog extends JDialog {

    private JTextArea txtMensagem;
    private JTextField txtDelimitador;
    private JPanel panelMensagens;
    private JCheckBox checkQuebrarLinha;
    private List<String> partes = new ArrayList<>();

    public interface EnviarCallback {
        void enviarMensagem(String msg);
    }

    public MensagemQuebradaDialog(Frame owner, EnviarCallback callback) {
        super(owner, "Enviar Mensagens", true);
        setSize(600, 500);
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
        checkQuebrarLinha.addActionListener(e -> {
            txtDelimitador.setEnabled(!checkQuebrarLinha.isSelected());
        });

        JButton btnQuebrar = new JButton("Quebrar");
        setAlturaFixa(btnQuebrar, 20);
        delimitadorPanel.add(btnQuebrar);

        topo.add(delimitadorPanel, BorderLayout.SOUTH);
        add(topo, BorderLayout.NORTH);

        panelMensagens = new JPanel(new GridBagLayout());
        JScrollPane scroll = new JScrollPane(panelMensagens);
        add(scroll, BorderLayout.CENTER);

        btnQuebrar.addActionListener(e -> quebrarMensagens(callback));
    }

    private void quebrarMensagens(EnviarCallback callback) {
        panelMensagens.removeAll();
        partes.clear();

        String texto = txtMensagem.getText();

        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha a mensagem e o delimitador!");
            return;
        }

        String[] split;
        String delimitador = txtDelimitador.getText();

        if (checkQuebrarLinha.isSelected()) {
            split = texto.split("\\r?\\n");
            for (String parte : split) {
                parte = parte.trim();
                if (!parte.isEmpty()) {
                    partes.add(parte);
                }
            }
        } else {
            if (delimitador.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o delimitador!");
                return;
            }
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
        gbc.gridx = 0;
        gbc.gridy = 0;
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

            JButton btnEnviar = new JButton("Enviar");
            setAlturaFixa(btnEnviar, 20);
            btnEnviar.addActionListener(e -> callback.enviarMensagem(parte));
            linha.add(btnEnviar, BorderLayout.EAST);

            panelMensagens.add(linha, gbc);
            gbc.gridy++;
        }

        gbc.weighty = 1.0;
        panelMensagens.add(Box.createVerticalGlue(), gbc);
        
        panelMensagens.revalidate();
        panelMensagens.repaint();
    }

    private void setAlturaFixa(JComponent comp, int altura) {
        Dimension d = comp.getPreferredSize();
        d.height = altura;
        comp.setPreferredSize(d);
        comp.setMinimumSize(d);
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, altura));
    }
}
