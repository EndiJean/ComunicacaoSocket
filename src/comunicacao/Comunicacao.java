package comunicacao;

public interface Comunicacao {
	
	public void conectar() throws Exception;
	
	public void desconectar() throws Exception;
	
	public void enviar(String mensagem) throws Exception;

	public void ler() throws Exception;

	public boolean estaConectado();
}
