package comunicacao;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

public class Mensagem extends JFrame {

	ComunicacaoUI ui;
	private String mensagem;
	
    public Mensagem(ComunicacaoUI ui, String tipoComando) {
    	this.ui = ui;
        abrirTelaMensagem(tipoComando);
    }

    private void abrirTelaMensagem(String tipoComando) {
        this.setSize(400, 300);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(ui);

        JTextArea areaMensagem = new JTextArea();
        areaMensagem.setLineWrap(false);
        areaMensagem.setWrapStyleWord(false);
        areaMensagem.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(areaMensagem);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        areaMensagem.setText(mensagem);

        JButton botaoEnviar = new JButton("Enviar");
        botaoEnviar.addActionListener(e -> {
            if (!areaMensagem.getText().trim().isEmpty()) {
            	mensagem = areaMensagem.getText().trim();
            	ui.enviarComando(mensagem);
            	this.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, digite uma mensagem!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton botaoLimpar = new JButton("Limpar");
        botaoLimpar.addActionListener(e -> areaMensagem.setText(""));

        JPanel panelBotoes = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        panelBotoes.add(botaoEnviar, gbc);
        gbc.gridx++;
        panelBotoes.add(botaoLimpar, gbc);

        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(panelBotoes, BorderLayout.SOUTH);
    }
}