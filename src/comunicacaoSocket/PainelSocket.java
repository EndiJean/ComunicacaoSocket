package comunicacaoSocket;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import comunicacao.Comunicacao;
import comunicacao.ComunicacaoUI;

public class PainelSocket extends JPanel {
	
    private JTextField campoSocketIp;
    private JTextField campoSocketPorta;

    public PainelSocket() {
        setLayout(new GridBagLayout());
        inicializarComponentes();
        configurarLayout();
    }

    private void inicializarComponentes() {
        campoSocketIp = new JTextField("127.0.0.1");
        campoSocketIp.setPreferredSize(new Dimension(200, 25));
        campoSocketIp.setMinimumSize(new Dimension(200, 25));

        campoSocketPorta = new JTextField("5001");
        campoSocketPorta.setPreferredSize(new Dimension(200, 25));
        campoSocketPorta.setMinimumSize(new Dimension(200, 25));
    }

    private void configurarLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        adicionarCampo(gbc, "IP do Socket:", campoSocketIp, 0);
        adicionarCampo(gbc, "Porta do Socket:", campoSocketPorta, 1);
    }

    private void adicionarCampo(GridBagConstraints gbc, String label, JComponent componente, int linha) {
        gbc.gridx = 0;
        gbc.gridy = linha;
        add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.gridy = linha;
        add(componente, gbc);
    }

    public void iniciarComunicacaoSingle(boolean escreverEmByte) {
    	ComunicacaoUI ui = new ComunicacaoUI("Comunicação Socket Single");
    	ui.setVisible(true);
    	
    	Comunicacao socketSingle = new ComunicacaoSocketSingle(ui, campoSocketIp.getText(), Integer.parseInt(campoSocketPorta.getText()), escreverEmByte);
    	ui.setComunicacao(socketSingle);
    }

    public void iniciarComunicacaoServer(boolean escreverEmByte) {
    	ComunicacaoUI ui = new ComunicacaoUI("Comunicação Socket Server");
    	ui.setVisible(true);
    	
    	Comunicacao socketServer = new ComunicacaoSocketServer(ui, Integer.parseInt(campoSocketPorta.getText()), escreverEmByte);
    	ui.setComunicacao(socketServer);
    }
}