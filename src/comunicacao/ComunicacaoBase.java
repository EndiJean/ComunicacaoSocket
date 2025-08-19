package comunicacao;

public abstract class ComunicacaoBase implements Comunicacao {

	protected ComunicacaoUI ui;
	protected StringBuilder bufferAcumulador = new StringBuilder();

    public ComunicacaoBase(ComunicacaoUI ui) {
        this.ui = ui;
    }
}
