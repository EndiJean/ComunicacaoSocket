package comunicacao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
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

public class ComunicacaoUI extends JFrame {
	
	private JTextPane pane;
	
	private transient Comunicacao comunicacao;
	
	private Mensagem mensagemQuery;
	private Mensagem mensagemResultado;

	public ComunicacaoUI(String titulo) {
		setTitle(titulo);
		setLayout(new BorderLayout());
		setSize(new Dimension(800, 500));
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

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

		adicionaBotoes(panelBotoes, botoes);

		this.add(scrollPane, BorderLayout.CENTER);
		this.add(panelBotoes, BorderLayout.SOUTH);
	}

	private void adicionaBotoes(JPanel panelBotoes, List<BotaoComando> botoes) {
		for (BotaoComando botao : botoes) {
			JButton button = new JButton(botao.getLabel());
			button.addActionListener(e -> {
				if (botao.getLabel().equals("Query")) {
					if (mensagemQuery == null) {
						mensagemQuery = new Mensagem(ComunicacaoUI.this, botao.getLabel());
					}
					mensagemQuery.setVisible(true);
				} else if (botao.getLabel().equals("Resultado")) {
					if (mensagemResultado == null) {
						mensagemResultado = new Mensagem(ComunicacaoUI.this, botao.getLabel());
					}
					mensagemResultado.setVisible(true);
				} else if (botao.getLabel().equals("Copiar")) {
					copiarParaClipboard(pane.getText());
				} else if (botao.getLabel().equals("Limpar")) {
					limparPane();
				} else {
					enviarComando(String.valueOf(botao.getComando()));
				}
			});
			panelBotoes.add(button);
		}
	}
	
	public void escreverPane(String mensagem) {
		escreverPane(mensagem, false);
	}

	public void escreverPane(String mensagem, boolean pintarLetra) {
		try {
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
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void limparPane() {
		pane.setText("");
	}

	public void copiarParaClipboard(String texto) {
		if (!texto.isEmpty()) {
			StringSelection selecao = new StringSelection(texto);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selecao, null);
		}
	}
	
	public void enviarComando(String comando) {
		try {
			System.out.println("-" + comando + "-");
			String converterRepresentacaoParaCodigo = converterRepresentacaoParaCodigo(comando);
			System.out.println("-" + converterRepresentacaoParaCodigo + "-");
			comunicacao.enviar(converterRepresentacaoParaCodigo);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public void setComunicacao(Comunicacao comunicacao) {
		this.comunicacao = comunicacao;
		try {
			comunicacao.conectar();
			
			if (comunicacao.estaConectado()) {
				comunicacao.ler();
			}
		} catch (Exception e) {
			escreverPane("Erro - " + e.getMessage());
		}
	}
}