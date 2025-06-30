package comunicacaoSerial;

import com.fazecast.jSerialComm.SerialPort;

import comunicacao.Comunicacao;
import comunicacao.ComunicacaoUI;

import javax.swing.*;
import java.awt.*;

public class PainelSerial extends JPanel {
	
    private JComboBox<String> comboPortas;
    private JComboBox<Integer> comboBaudRate;
    private JComboBox<Integer> comboDataBits;
    private JComboBox<Integer> comboStopBits;
    private JComboBox<String> comboParity;

    public PainelSerial() {
        setLayout(new GridBagLayout());
        inicializarComponentes();
        configurarLayout();
    }

    private void inicializarComponentes() {
        SerialPort[] portasDisponiveis = SerialPort.getCommPorts();
        String[] nomesPortas = new String[portasDisponiveis.length];
        for (int i = 0; i < portasDisponiveis.length; i++) {
            nomesPortas[i] = portasDisponiveis[i].getSystemPortName();
        }
        comboPortas = new JComboBox<>(nomesPortas);
        comboPortas.setPreferredSize(new Dimension(200, 25));

        Integer[] baudRates = {9600, 19200, 38400, 57600, 115200};
        comboBaudRate = new JComboBox<>(baudRates);
        comboBaudRate.setPreferredSize(new Dimension(200, 25));

        Integer[] dataBitsOptions = {5, 6, 7, 8};
        comboDataBits = new JComboBox<>(dataBitsOptions);
        comboDataBits.setSelectedIndex(dataBitsOptions.length - 1);
        comboDataBits.setPreferredSize(new Dimension(200, 25));

        Integer[] stopBitsOptions = {1, 2};
        comboStopBits = new JComboBox<>(stopBitsOptions);
        comboStopBits.setPreferredSize(new Dimension(200, 25));

        String[] parityOptions = {"Nenhuma", "Ímpar", "Par", "Marcada", "Espaçada"};
        comboParity = new JComboBox<>(parityOptions);
        comboParity.setPreferredSize(new Dimension(200, 25));
    }

    private void configurarLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        adicionarCampo(gbc, "Porta Serial:", comboPortas, 0);
        adicionarCampo(gbc, "Baud Rate:", comboBaudRate, 1);
        adicionarCampo(gbc, "Bits de Dados:", comboDataBits, 2);
        adicionarCampo(gbc, "Bits de Parada:", comboStopBits, 3);
        adicionarCampo(gbc, "Paridade:", comboParity, 4);
    }

    private void adicionarCampo(GridBagConstraints gbc, String label, JComponent componente, int linha) {
        gbc.gridx = 0;
        gbc.gridy = linha;
        add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.gridy = linha;
        add(componente, gbc);
    }

    public void iniciarComunicacao() {
    	String portaSerial = (String) comboPortas.getSelectedItem();
    	int baudRate = (int) comboBaudRate.getSelectedItem();
    	int dataBits = (int) comboDataBits.getSelectedItem();
    	int stopBits = (int) comboStopBits.getSelectedItem();
    	int parity = comboParity.getSelectedIndex();

    	ComunicacaoUI ui = new ComunicacaoUI("Comunicação Serial");
    	ui.setVisible(true);
    	
    	Comunicacao serial = new ComunicacaoSerial(ui, portaSerial, baudRate, dataBits, stopBits, parity);
    	ui.setComunicacao(serial);
    }
}