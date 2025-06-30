package comunicacao;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import comunicacaoSerial.PainelSerial;
import comunicacaoSocket.PainelSocket;

public class TelaConfiguracao extends JFrame {
	
	private static final String SERIAL = "Serial";
    private static final String SOCKET_SINGLE = "Socke Single";
	private static final String SOCKET_SERVER = "Socket Server";

	private JComboBox<String> comboTipoComunicacao;
	
    private PainelSerial painelSerial;
    private PainelSocket painelSocket;
    
    private JButton botaoConectar;

    public TelaConfiguracao() {
        setTitle("Configuração de Comunicação");
        setSize(400, 300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        inicializarComponentes();

        configurarLayout();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void inicializarComponentes() {
        String[] tiposComunicacao = {SERIAL, SOCKET_SERVER, SOCKET_SINGLE};
        comboTipoComunicacao = new JComboBox<>(tiposComunicacao);
        comboTipoComunicacao.addActionListener(new ComboTipoComunicacaoListener());

        painelSerial = new PainelSerial();
        painelSocket = new PainelSocket();

        botaoConectar = new JButton("Conectar");
        botaoConectar.addActionListener(new BotaoConectarListener());
    }

    private void configurarLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(new JLabel("Tipo de Comunicação:"), gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        add(comboTipoComunicacao, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        add(painelSerial, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        add(painelSocket, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(botaoConectar, gbc);

        alternarVisibilidadePainel();
    }

    private void alternarVisibilidadePainel() {
        String tipoSelecionado = (String) comboTipoComunicacao.getSelectedItem();
        painelSerial.setVisible(SERIAL.equals(tipoSelecionado));
        painelSocket.setVisible(isSocket(tipoSelecionado));
    }

    private class ComboTipoComunicacaoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            alternarVisibilidadePainel();
        }
    }

    private class BotaoConectarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String tipoSelecionado = (String) comboTipoComunicacao.getSelectedItem();

            setVisible(false);
            if (SERIAL.equals(tipoSelecionado)) {
                painelSerial.iniciarComunicacao();
            } else if (SOCKET_SINGLE.equals(tipoSelecionado)) {
                painelSocket.iniciarComunicacaoSingle();
            } else if (SOCKET_SERVER.equals(tipoSelecionado)) {
            	painelSocket.iniciarComunicacaoServer();
            }
        }
    }
    
    private boolean isSocket(String tipoSelecionado) {
    	return SOCKET_SINGLE.equals(tipoSelecionado) || SOCKET_SERVER.equals(tipoSelecionado);
    }

    public static void main(String[] args) {
        new TelaConfiguracao();
    }
}