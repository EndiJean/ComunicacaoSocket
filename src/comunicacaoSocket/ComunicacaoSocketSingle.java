package comunicacaoSocket;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import comunicacao.ComunicacaoBase;
import comunicacao.ComunicacaoUI;

public class ComunicacaoSocketSingle extends ComunicacaoBase {
	
	private Socket socket;
	private String ip;
	private int porta;
	private boolean escreverEmByte;

	public ComunicacaoSocketSingle(ComunicacaoUI ui, String ip, int porta, boolean escreverEmByte) {
		super(ui);
		this.ip = ip;
		this.porta = porta;
		this.escreverEmByte = escreverEmByte;
	}

	@Override
	public void conectar() throws Exception {
		socket = new Socket(ip, porta);
		ui.escreverPane("Conectado ao servidor " + ip + ":" + porta, false);
	}

	@Override
	public void desconectar() throws Exception {
		if (socket != null && !socket.isClosed()) {
			socket.close();
			ui.escreverPane("Conexão com o servidor fechada.", false);
		}
	}

	@Override
	public void enviar(String mensagem) throws Exception {
		if (socket != null && socket.isConnected()) {
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());
			
			if (this.escreverEmByte) {
				os.write(mensagem.getBytes());
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
		new Thread(() -> {
			while (true) {
				if (estaConectado()) {
					try {
						BufferedInputStream stream = new BufferedInputStream(socket.getInputStream());
						
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
		return socket != null && socket.isConnected();
	}
}