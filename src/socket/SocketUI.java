package socket;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class SocketUI extends JFrame {

    private JTextPane pane;

    private transient ServerSocket serverSocket;
    public transient Socket socket;
    public transient List<Socket> clientSockets = new ArrayList<>();

    private String query = "";
    private String resultado = "";
    private boolean isServerMode;

    public SocketUI(String ip, String porta, boolean isServerMode) {
        this.isServerMode = isServerMode;
        configurarUI();
        if (isServerMode) {
            iniciarServidor(porta);
        } else {
            iniciarConexao(ip, porta);
        }
    }

    private void configurarUI() {
        this.setTitle(isServerMode ? "Socket Server" : "Socket Client");
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(800, 500));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        pane = new JTextPane();
        pane.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(pane);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JPanel panelBotoes = new JPanel(new FlowLayout());
        List<BotaoComando> botoes = Arrays.asList(
            new BotaoComando("ACK", (char) 6),
            new BotaoComando("ENQ", (char) 5),
            new BotaoComando("NULL", (char) 0),
            new BotaoComando("STX", (char) 2),
            new BotaoComando("ETX", (char) 3),
            new BotaoComando("EOT", (char) 4),
            new BotaoComando("Query", 'Q'),
            new BotaoComando("Resultado", 'R'),
            new BotaoComando("Limpar", 'L'),
            new BotaoComando("Copiar", 'C')
        );

        for (BotaoComando botao : botoes) {
            JButton button = new JButton(botao.getLabel());
            button.addActionListener(e -> {
                if (botao.getLabel().equals("Query") || botao.getLabel().equals("Resultado")) {
                    new SocketMensagem(SocketUI.this, botao.getLabel());
                } else if (botao.getLabel().equals("Copiar")) {
                	String texto = pane.getText();
                	if (!texto.isEmpty()) {
                		copiarParaClipboard(texto);
                	}
                } else {
                    enviarComando(botao.getComando());
                }
            });
            panelBotoes.add(button);
        }

        this.add(scrollPane, BorderLayout.CENTER);
        this.add(panelBotoes, BorderLayout.SOUTH);
        this.setVisible(true);
    }

    private void iniciarConexao(String ip, String porta) {
        try {
            escreverPane("Conectando ao servidor " + ip + ":" + porta);
            this.socket = new Socket(ip, Integer.valueOf(porta));
            Recebimento recebimento = new Recebimento(new BufferedInputStream(socket.getInputStream()));
            recebimento.start();
        } catch (IOException | BadLocationException e) {
            try {
                escreverPane("Erro ao conectar ao servidor: " + e.getMessage());
            } catch (BadLocationException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void iniciarServidor(String porta) {
        new Thread(() -> {
            try {
                this.serverSocket = new ServerSocket(Integer.valueOf(porta));
                escreverPane("Servidor iniciado na porta " + porta);

                while (true) {
                    Socket socketAux = serverSocket.accept();
                    escreverPane("Novo cliente conectado: " + socketAux.getInetAddress());

                    synchronized (clientSockets) {
                        clientSockets.add(socketAux);
                    }

                    new Thread(new Recebimento(socketAux)).start();
                }
            } catch (IOException | BadLocationException e) {
                try {
                    escreverPane("Erro ao conectar ao servidor: " + e.getMessage());
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();
    }

    protected void escreverPane(String mensagem) throws BadLocationException {
    	escreverPane(mensagem, false);
    }
    
    protected void escreverPane(String mensagem, boolean pintarLetra) throws BadLocationException {
        StringBuilder textoConvertido = new StringBuilder();
        for (char c : mensagem.toCharArray()) {
            String representacao = AsciiChar.getRepresentacao(c);
            if (representacao != null) {
                textoConvertido.append(representacao);
            } else {
                textoConvertido.append(c);
            }
        }

        
		StyledDocument doc = pane.getStyledDocument();
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		if (pintarLetra) {
			StyleConstants.setForeground(attrs, Color.RED);
		}

		doc.insertString(doc.getLength(), textoConvertido.toString() + "\n", attrs);
    }

    private void enviarComando(char comando) {
        if (isServerMode) {
            new Thread(() -> {
                synchronized (clientSockets) {
                    for (Socket socketCliente : clientSockets) {
                        try {
                            enviaComandoEscrever(comando, socketCliente);
                        } catch (IOException | BadLocationException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } else {
            try {
                if (socket != null) {
                    enviaComandoEscrever(comando, socket);
                }
            } catch (IOException | BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

	private void enviaComandoEscrever(char comando, Socket socket) throws IOException, BadLocationException {
		PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
		if (comando == 'L') {
		    pane.setText("");
		} else {
		    escrever(printer, comando);
		}
	}

    private void escrever(PrintWriter printer, char mensagem) throws BadLocationException {
        escreverPane("LIS:" + mensagem, true);
        printer.write(mensagem);
        printer.flush();
    }
    
    private void copiarParaClipboard(String texto) {
        StringSelection selecao = new StringSelection(texto);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selecao, null);
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    class Recebimento extends Thread {
        private Socket socket;
        private BufferedInputStream stream;

        public Recebimento(BufferedInputStream stream) {
            super("Thread-recebimento");
            this.stream = stream;
        }

        public Recebimento(Socket socket) throws IOException {
            super("Thread-recebimento");
            this.socket = socket;
            this.stream = new BufferedInputStream(socket.getInputStream());
        }

        @Override
        public void run() {
            while (true) {
                try {
                    byte[] buffer = new byte[stream.available()];
                    int numBytes = stream.read(buffer);
                    if (numBytes > 0) {
                        String mensagem = new String(buffer, 0, numBytes);
                        escreverPane("EQUIPAMENTO:" + mensagem);
                    }
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
