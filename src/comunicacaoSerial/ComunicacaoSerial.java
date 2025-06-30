package comunicacaoSerial;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import comunicacao.ComunicacaoBase;
import comunicacao.ComunicacaoUI;

public class ComunicacaoSerial extends ComunicacaoBase {
	
	private SerialPort serialPort;

	public ComunicacaoSerial(ComunicacaoUI ui, String porta, int baudRate, int dataBits, int stopBits, int parity) {
		super(ui);
		configurarSerial(porta, baudRate, dataBits, stopBits, parity);
	}

	private void configurarSerial(String porta, int baudRate, int dataBits, int stopBits, int parity) {
		serialPort = SerialPort.getCommPort(porta);
		serialPort.setBaudRate(baudRate);
		serialPort.setNumDataBits(dataBits);
		serialPort.setNumStopBits(stopBits);
		serialPort.setParity(parity);
	}

	@Override
	public void conectar() throws Exception {
		if (!serialPort.openPort()) {
			throw new Exception("Falha ao abrir a porta serial.");
		}
		ui.escreverPane("Porta serial " + serialPort.getSystemPortName() + " aberta com sucesso.", false);
	}

	@Override
	public void desconectar() throws Exception {
		if (serialPort.isOpen()) {
			serialPort.closePort();
			ui.escreverPane("Porta serial fechada.", false);
		}
	}

	@Override
	public void enviar(String mensagem) throws Exception {
		if (serialPort.isOpen()) {
			serialPort.writeBytes(mensagem.getBytes(StandardCharsets.ISO_8859_1), mensagem.length());
			ui.escreverPane("LIS:" + mensagem);
		} else {
			throw new Exception("Porta serial não está aberta.");
		}
	}

	@Override
	public void ler() throws Exception {
		serialPort.addDataListener(new SerialPortDataListener() {

			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
			}

			@Override
			public void serialEvent(SerialPortEvent event) {
				if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
					byte[] buffer = new byte[serialPort.bytesAvailable()];
					int numRead = serialPort.readBytes(buffer, buffer.length);
					if (numRead > 0) {
						String mensagem = new String(buffer, 0, numRead, StandardCharsets.ISO_8859_1);
						System.out.println("Mensagem recebida iso: " + mensagem);
						
						
						String mensagemCorrigida = new String(
								mensagem.getBytes(StandardCharsets.ISO_8859_1), 
				                Charset.forName("CP850")
				            );
						
						System.out.println("Mensagem corrigida: " + mensagemCorrigida);

					}
				}
			}
		});
	}

	private String corrigirEncoding(byte[] buffer, int numRead) {
	    // Decodifica primeiro como ISO-8859-1
	    String mensagem = new String(buffer, 0, numRead, StandardCharsets.ISO_8859_1);
	    
	    // Se houver "¡" (0xA1 em ISO-8859-1), substitui pelo "í" do CP850
	    if (mensagem.contains("¡")) {
	        byte[] bufferCP850 = Arrays.copyOf(buffer, numRead);
	        for (int i = 0; i < bufferCP850.length; i++) {
	            if (bufferCP850[i] == (byte) 0xA1) {
	                // Decodifica apenas o byte 0xA1 como CP850
	                String charCorrigido = new String(new byte[]{bufferCP850[i]}, Charset.forName("CP850"));
	                mensagem = mensagem.substring(0, i) + charCorrigido + mensagem.substring(i + 1);
	            }
	        }
	    }
	    return mensagem;
	}
	
	@Override
	public boolean estaConectado() {
		return serialPort.isOpen();
	}
}