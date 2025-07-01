package comunicacaoSocket;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import comunicacao.ComunicacaoBase;
import comunicacao.ComunicacaoUI;

public class ComunicacaoSocketServer extends ComunicacaoBase {
	
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private int porta;
	private boolean escreverEmByte;

	public ComunicacaoSocketServer(ComunicacaoUI ui, int porta, boolean escreverEmByte) {
		super(ui);
		this.porta = porta;
		this.escreverEmByte = escreverEmByte;
	}

	@Override
	public void conectar() throws Exception {
		serverSocket = new ServerSocket(porta);
		ui.escreverPane("Servidor iniciado na porta " + porta, false);

		new Thread(() -> {
			try {
				clientSocket = serverSocket.accept();
				ui.escreverPane("Cliente conectado: " + clientSocket.getInetAddress(), false);
				ler();
			} catch (Exception e) {
				ui.escreverPane("Erro ao aceitar conexão: " + e.getMessage(), false);
			}
		}).start();
	}

	@Override
	public void desconectar() throws Exception {
		if (clientSocket != null && !clientSocket.isClosed()) {
			clientSocket.close();
		}
		if (serverSocket != null && !serverSocket.isClosed()) {
			serverSocket.close();
		}
		ui.escreverPane("Servidor fechado.", false);
	}

	@Override
	public void enviar(String mensagem) throws Exception {
		if (clientSocket != null && clientSocket.isConnected()) {
			DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream());
			
			if (this.escreverEmByte) {
				os.write(mensagem.getBytes());
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
			while (true) {
				if (estaConectado()) {
					try {
						BufferedInputStream stream = new BufferedInputStream(clientSocket.getInputStream());
						
						byte[] buffer = new byte[stream.available()];
	                    int numBytes = stream.read(buffer);

	                    if (numBytes > 0) {
	                        String mensagem = new String(buffer, 0, numBytes);
	                        ui.escreverPane("EQUIPAMENTO:" + mensagem, true);
	                    }
	                    Thread.sleep(100);
					} catch (IOException | InterruptedException e) {
						Thread.currentThread().interrupt();
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	@Override
	public boolean estaConectado() {
		return clientSocket != null && clientSocket.isConnected();
	}
}
