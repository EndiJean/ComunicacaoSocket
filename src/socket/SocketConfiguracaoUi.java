package socket;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class SocketConfiguracaoUi {
	
	private String ip = "127.0.0.1";
	private String porta = "5001";

	public SocketConfiguracaoUi() {
		JFrame frame = new JFrame("Config. de Conexão");
		frame.setSize(350, 210);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		frame.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);

		JLabel labelIp = new JLabel("IP:");
		JTextField campoIp = new JTextField(ip, 15);
		gbc.gridx = 0;
		gbc.gridy = 0;
		frame.add(labelIp, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		frame.add(campoIp, gbc);

		JLabel labelPorta = new JLabel("Porta:");
		JTextField campoPorta = new JTextField(porta, 15);
		gbc.gridx = 0;
		gbc.gridy = 1;
		frame.add(labelPorta, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		frame.add(campoPorta, gbc);
		
		JLabel labelTipo = new JLabel("Tipo de Conexão:");
        String[] opcoes = {"Socket Single", "Socket Server"};
        JComboBox<String> comboBox = new JComboBox<>(opcoes);
        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(labelTipo, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(comboBox, gbc);
		
		JButton botaoConectar = new JButton("Conectar");
		botaoConectar.addActionListener(e -> {
			ip = campoIp.getText();
			porta = campoPorta.getText();
			String tipoConexao = (String) comboBox.getSelectedItem();
			
			frame.dispose();
			new SocketUI(ip, porta, !"Socket Single".equals(tipoConexao));
		});

		gbc.gridx = 1;
        gbc.gridy = 3;
		frame.add(botaoConectar, gbc);

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public String getIp() {
		return ip;
	}

	public String getPorta() {
		return porta;
	}
}
