package comunicacaoSocket;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import comunicacao.ComunicacaoBase;
import comunicacao.ComunicacaoUI;

public class ComunicacaoSocketSingle extends ComunicacaoBase {
	
	private Socket socket;
	private String ip;
	private int porta;
	private boolean escreverEmByte;
	private boolean escreverEmUTF16;
	
	private volatile boolean rodando = false;

	public ComunicacaoSocketSingle(ComunicacaoUI ui, String ip, int porta, boolean escreverEmByte, boolean escreverEmUTF16) {
		super(ui);
		this.ip = ip;
		this.porta = porta;
		this.escreverEmByte = escreverEmByte;
		this.escreverEmUTF16 = escreverEmUTF16;
	}

	@Override
	public void conectar() throws Exception {
		socket = new Socket(ip, porta);
		ui.escreverMensagem("Conectado ao servidor " + ip + ":" + porta);
	}

	@Override
	public void enviar(String mensagem) throws Exception {
		if (socket != null && socket.isConnected()) {
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());
			
			if (this.escreverEmByte) {
				os.write(mensagem.getBytes());
			} else if (this.escreverEmUTF16) {
				byte[] dados = mensagem.getBytes(StandardCharsets.UTF_16LE);
			    os.write(dados);
			} else {
				os.writeUTF(mensagem);
			}
			os.flush();
			ui.escreverPane("LIS:" + mensagem, false);
		} else {
			throw new Exception("Socket não está conectado.");
		}
	}

	@Override
	public void ler() throws Exception {
	    rodando = true;
	    new Thread(() -> {
	        try {
	            BufferedInputStream stream = new BufferedInputStream(socket.getInputStream());
	            byte[] buffer = new byte[1024];
	            while (rodando) {
	                int numBytes = stream.read(buffer);
	                if (numBytes == -1) {
	                    ui.escreverMensagem("Servidor desconectou.");
	                    desconectar();
	                    break;
	                }
	                if (numBytes > 0) {
	                    String mensagem = new String(buffer, 0, numBytes);
	                    ui.escreverPane("EQUIPAMENTO:" + mensagem, true);
	                    ui.dispararEvento(ui.converteAsciiParaString(mensagem));
	                }
	            }
	        } catch (IOException e) {
	            if (rodando) {
	                ui.escreverMensagem("Conexão perdida: " + e.getMessage());
	            }
	            try {
	                desconectar();
	            } catch (Exception ex) {
	                ex.printStackTrace();
	            }
	        } catch (Exception e) {
				e.printStackTrace();
			}
	    }).start();
	}

	@Override
	public void desconectar() throws Exception {
	    rodando = false;
	    if (socket != null && !socket.isClosed()) {
	        socket.close();
	        ui.escreverMensagem("Conexão com o servidor fechada.");
	    }
	}

	@Override
	public boolean estaConectado() {
		return socket != null && socket.isConnected();
	}
}