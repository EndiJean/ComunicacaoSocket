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
	
	private static final String QUERY = "Query";
	private static final String RESULTADO = "Resultado";

	private JTextPane pane;
	
	private transient Comunicacao comunicacao;
	
	private Mensagem mensagemQuery;
	private Mensagem mensagemResultado;
	
	private MensagemQuebradaDialog dialog;

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
				new BotaoComando(QUERY, 'Q'),
				new BotaoComando(RESULTADO, 'R'), 
				new BotaoComando("Limpar", 'L'), 
				new BotaoComando("Copiar", 'C'),
				new BotaoComando("📑", 'C'),
				new BotaoComando("🔄", 'X')
			);
		
		adicionaBotoes(panelBotoes, botoes);

		this.add(scrollPane, BorderLayout.CENTER);
		this.add(panelBotoes, BorderLayout.SOUTH);
	}

	private void adicionaBotoes(JPanel panelBotoes, List<BotaoComando> botoes) {
		for (BotaoComando botao : botoes) {
			JButton button = new JButton(botao.getLabel());
			button.addActionListener(e -> {
				if (botao.getLabel().equals(QUERY) || botao.getLabel().equals(RESULTADO)) {
					enviaMensagem(botao);
				} else if (botao.getLabel().equals("Copiar")) {
					copiarParaClipboard(pane.getText());
				} else if (botao.getLabel().equals("Limpar")) {
					limparPane();
				} else if (botao.getLabel().equals("🔄")) {
				    reconectar();
				} else if (botao.getLabel().equals("📑")) {
					if (dialog != null && dialog.isVisible()) {
						return;
					}
					if (dialog == null) {
						dialog = new MensagemQuebradaDialog(this, this::enviarComando);
					}
				    dialog.setVisible(true);
				} else {
					enviarComando(String.valueOf(botao.getComando()));
				}
			});
			panelBotoes.add(button);
		}
	}

	private void enviaMensagem(BotaoComando botao) {
		if (botao.getLabel().equals(QUERY)) {
			if (mensagemQuery == null) {
				mensagemQuery = new Mensagem(ComunicacaoUI.this);
			}
			mensagemQuery.setVisible(true);
		} else if (botao.getLabel().equals(RESULTADO)) {
			if (mensagemResultado == null) {
				mensagemResultado = new Mensagem(ComunicacaoUI.this);
			}
			mensagemResultado.setVisible(true);
		}
	}

	private void reconectar() {
		try {
		    if (comunicacao != null) {
		        comunicacao.desconectar();
		        comunicacao.conectar();
		        if (comunicacao.estaConectado()) {
		            comunicacao.ler();
		        }
		        escreverMensagem("Conexão reiniciada com sucesso.");
		    }
		} catch (Exception ex) {
		    escreverMensagem("Erro ao reiniciar: " + ex.getMessage());
		}
	}
	
	public void escreverPane(String mensagem) {
		escreverPane(mensagem, false);
	}

	public void escreverPane(String mensagem, boolean pintarLetra) {
		escreverMensagem(converteAsciiParaString(mensagem), pintarLetra);
	}

	public String converteAsciiParaString(String mensagem) {
		StringBuilder textoConvertido = new StringBuilder();
		char[] chars = mensagem.toCharArray();
		for (int i = 0; i < chars.length; i++) {
		    char c = chars[i];

		    if (c == 13 && (i + 1 < chars.length && chars[i + 1] == 10)) {
		        textoConvertido.append("[LINE]");
		        i++;
		        continue;
		    }

		    String representacao = AsciiChar.getRepresentacao(c);
		    if (representacao != null) {
		        textoConvertido.append(representacao);
		    } else {
		        textoConvertido.append(c);
		    }
		}
		return textoConvertido.toString();
	}
	
	public void escreverMensagem(String mensagem) {
		escreverMensagem(mensagem, false);
	}
	
	private void escreverMensagem(String mensagem, boolean pintarLetra) {
		try {
			StyledDocument doc = pane.getStyledDocument();
			SimpleAttributeSet attrs = new SimpleAttributeSet();
			if (pintarLetra) {
				StyleConstants.setForeground(attrs, Color.RED);
			}

			doc.insertString(doc.getLength(), mensagem + "\n", attrs);
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
			String converterRepresentacaoParaCodigo = converterRepresentacaoParaCodigo(comando);
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
					} else if (representacao.equalsIgnoreCase("[LINE]")) {
						mensagemConvertida.append((char) AsciiChar.getCode("[CR]").intValue());
						mensagemConvertida.append((char) AsciiChar.getCode("[LF]").intValue());
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

	public void dispararEvento(String mensagem) {
		if (dialog != null) {
			dialog.notificarEvento(mensagem, this::enviarComando);
		}
	}
}