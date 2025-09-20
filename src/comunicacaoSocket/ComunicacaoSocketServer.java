package comunicacaoSocket;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import comunicacao.ComunicacaoBase;
import comunicacao.ComunicacaoUI;

public class ComunicacaoSocketServer extends ComunicacaoBase {
	
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private int porta;
	private boolean escreverEmByte;
	private boolean escreverEmUTF16;
	
	private volatile boolean rodando = false;
	
	public ComunicacaoSocketServer(ComunicacaoUI ui, int porta, boolean escreverEmByte, boolean escreverEmUTF16) {
		super(ui);
		this.porta = porta;
		this.escreverEmByte = escreverEmByte;
		this.escreverEmUTF16 = escreverEmUTF16;
	}

	@Override
	public void conectar() throws Exception {
	    rodando = true;
	    serverSocket = new ServerSocket(porta);
	    ui.escreverMensagem("Servidor iniciado na porta " + porta);

	    new Thread(() -> {
	        try {
	            while (rodando) {
	                clientSocket = serverSocket.accept();
	                ui.escreverMensagem("Cliente conectado: " + clientSocket.getInetAddress());
	                ler();
	            }
	        } catch (IOException e) {
	            if (rodando) {
	                ui.escreverMensagem("Erro ao aceitar conexão: " + e.getMessage());
	            }
	        } catch (Exception e) {
				e.printStackTrace();
			}
	    }).start();
	}


	@Override
	public void enviar(String mensagem) throws Exception {
		if (clientSocket != null && clientSocket.isConnected()) {
			DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream());
			
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
			throw new Exception("Nenhum cliente conectado.");
		}
	}

	@Override
	public void ler() throws Exception {
	    new Thread(() -> {
	        try {
	            BufferedInputStream stream = new BufferedInputStream(clientSocket.getInputStream());
	            byte[] buffer = new byte[1024];
	            while (rodando && !clientSocket.isClosed()) {
	                int numBytes = stream.read(buffer);
	                if (numBytes == -1) {
	                    ui.escreverMensagem("Cliente desconectado.");
	                    clientSocket.close();
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
	        }
	    }).start();
	}
	
	@Override
	public void desconectar() throws Exception {
	    rodando = false;
	    if (clientSocket != null && !clientSocket.isClosed()) {
	        clientSocket.close();
	    }
	    if (serverSocket != null && !serverSocket.isClosed()) {
	        serverSocket.close();
	    }
	    ui.escreverMensagem("Servidor fechado.");
	}

	@Override
	public boolean estaConectado() {
		return clientSocket != null && clientSocket.isConnected();
	}
}
