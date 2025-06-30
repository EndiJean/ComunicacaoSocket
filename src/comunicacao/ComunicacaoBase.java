package comunicacao;

public abstract class ComunicacaoBase implements Comunicacao {

	protected ComunicacaoUI ui;
	protected StringBuilder bufferAcumulador = new StringBuilder();

    public ComunicacaoBase(ComunicacaoUI ui) {
        this.ui = ui;
    }
    
//    protected void escreverPane(String mensagem) throws BadLocationException {
//    	escreverPane(mensagem, false);
//    }
//
//    protected void escreverPane(String mensagem, boolean pintarLetra) throws BadLocationException {
//    	StringBuilder textoConvertido = new StringBuilder();
//        for (char c : mensagem.toCharArray()) {
//            String representacao = AsciiChar.getRepresentacao(c);
//            if (representacao != null) {
//                textoConvertido.append(representacao);
//            } else {
//                textoConvertido.append(c);
//            }
//        }
//
//        
//		StyledDocument doc = .getStyledDocument();
//		SimpleAttributeSet attrs = new SimpleAttributeSet();
//		if (pintarLetra) {
//			StyleConstants.setForeground(attrs, Color.RED);
//		}
//
//		doc.insertString(doc.getLength(), textoConvertido.toString() + "\n", attrs);
//    }
//
//    protected void processarMensagem(String mensagem) {
//        try {
//            escreverPane("EQUIPAMENTO: " + mensagem, false);
//        } catch (BadLocationException e) {
//            e.printStackTrace();
//        }
//    }
	
}
