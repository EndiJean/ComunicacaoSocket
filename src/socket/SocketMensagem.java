package socket;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;

public class SocketMensagem {

	SocketUI socket;
	
	public SocketMensagem(SocketUI socketUI, String tipoComando) {
		this.socket = socketUI;
		abrirTelaMensagem(tipoComando);
	}

	private void abrirTelaMensagem(String tipoComando) {
	    JFrame telaMensagem = new JFrame(tipoComando);
	    telaMensagem.setSize(400, 300);
	    telaMensagem.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	    telaMensagem.setLocationRelativeTo(socket);
	    
	    JTextArea areaMensagem = new JTextArea();
	    areaMensagem.setLineWrap(false);
	    areaMensagem.setWrapStyleWord(false);
	    areaMensagem.setCaretPosition(0);
	    
	    JScrollPane scrollPane = new JScrollPane(areaMensagem);
	    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    
	    if (tipoComando.equals("Query")) {
	        areaMensagem.setText(socket.getQuery());
	    } else {
	        areaMensagem.setText(socket.getResultado());
	    }
	    
	    JButton botaoEnviar = new JButton("Enviar");
	    botaoEnviar.addActionListener(e -> {
	        String mensagem = areaMensagem.getText().trim();
	        if (!mensagem.isEmpty()) {
	            enviarComandoTexto(tipoComando, mensagem);
	            telaMensagem.dispose();
	        } else {
	            JOptionPane.showMessageDialog(telaMensagem, "Por favor, digite uma mensagem!", "Erro", JOptionPane.ERROR_MESSAGE);
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
	    
	    
	    telaMensagem.setLayout(new BorderLayout());
	    telaMensagem.add(scrollPane, BorderLayout.CENTER);
	    telaMensagem.add(panelBotoes, BorderLayout.SOUTH);
	    
	    telaMensagem.setVisible(true);
	}
	
	private void enviarComandoTexto(String tipoComando, String mensagem) {
        try {
            String mensagemConvertida = converterRepresentacaoParaCodigo(mensagem);

            if (socket != null && socket.socket != null) {
                PrintWriter printer = new PrintWriter(socket.socket.getOutputStream(), true);
                enviaComandoTexto(tipoComando, mensagem, mensagemConvertida, printer);
            } else {
            	new Thread(() -> {
                    synchronized (socket.clientSockets) {
                        for (Socket socketClient : socket.clientSockets) {
                            try {
                                PrintWriter printer = new PrintWriter(socketClient.getOutputStream(), true);
                                enviaComandoTexto(tipoComando, mensagem, mensagemConvertida, printer);
                            } catch (IOException | BadLocationException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private void enviaComandoTexto(String tipoComando, String mensagem, String mensagemConvertida, PrintWriter printer) throws BadLocationException {
		if (tipoComando.equals("Query")) {
			this.socket.setQuery(mensagem);
		    escrever(printer, mensagemConvertida);
		} else if (tipoComando.equals("Resultado")) {
			this.socket.setResultado(mensagem);
		    escrever(printer, mensagemConvertida);
		}
	}
	
	private void escrever(PrintWriter printer, String mensagem) throws BadLocationException {
		socket.escreverPane("LIS:" + mensagem, true);
        printer.write(mensagem);
        printer.flush();
    }
	
	private String converterRepresentacaoParaCodigo(String mensagem) {
        StringBuilder mensagemConvertida = new StringBuilder();

        for (int i = 0; i < mensagem.length(); i++) {
            char c = mensagem.charAt(i);

            if (c == '[') {
                int fechamento = mensagem.indexOf(']', i);
                if (fechamento != -1) {
                    String representacao = mensagem.substring(i, fechamento + 1);

                    Integer codigo = AsciiChar.getCode(representacao);
                    if (codigo != null) {
                        mensagemConvertida.append((char) codigo.intValue());
                    } else {
                    	mensagemConvertida.append(mensagem.substring(i, fechamento + 1));
                    }

                    i = fechamento;
                }
            } else {
                mensagemConvertida.append(c);
            }
        }

        return mensagemConvertida.toString();
    }
}
