package comunicacao;

public class BotaoComando {

	private final String label;
    private final char comando;

    public BotaoComando(String label, char comando) {
        this.label = label;
        this.comando = comando;
    }

	public String getLabel() {
		return label;
	}

	public char getComando() {
		return comando;
	}
    
}
