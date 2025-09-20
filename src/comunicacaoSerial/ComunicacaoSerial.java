package comunicacaoSerial;

import java.nio.charset.StandardCharsets;

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
		ui.escreverMensagem("Porta serial " + serialPort.getSystemPortName() + " aberta com sucesso.");
	}

	@Override
	public void desconectar() throws Exception {
		if (serialPort.isOpen()) {
			serialPort.closePort();
			ui.escreverMensagem("Porta serial fechada.");
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
					int numBytes = 0;
					byte[] readBuffer = new byte[1024];
				    String tempString = "";
					while (serialPort.bytesAvailable() > 0) {
						numBytes = serialPort.readBytes(readBuffer, readBuffer.length);
			  			if (numBytes > 0) {
			  				tempString = new String(readBuffer, 0, numBytes, StandardCharsets.ISO_8859_1);
			  			}
					}
					
					ui.escreverPane("EQUIPAMENTO:" + tempString, true);
					ui.dispararEvento(ui.converteAsciiParaString(tempString));
				}
			}
		});
	}
	
	@Override
	public boolean estaConectado() {
		return serialPort.isOpen();
	}
}