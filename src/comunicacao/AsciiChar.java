package comunicacao;

public enum AsciiChar {

	NUL(0, "[NUL]"),
    SOH(1, "[SOH]"),
    STX(2, "[STX]"),
    ETX(3, "[ETX]"),
    EOT(4, "[EOT]"), 
    ENQ(5, "[ENQ]"),
    ACK(6, "[ACK]"),
    LF(10, "[LF]"),
    SB(11, "[SB]"),
    CR(13, "[CR]"),
    DLE(16, "[DLE]"),
    D1(17, "[DC1]"),
    NAK(21, "[NAK]"), 
    SYN(22, "[SYN]"),
    ETB(23, "[ETB]"),
    CAN(24, "[CAN]"),
    EOF(26, "[EOF]"), 
    EB(28, "[EB]"), 
    GS(29, "[GS]"), 
    RS(30, "[RS]"),
    SP(32, "[SP]"),
    DEL(127, "[DEL]"),
    FD(253, "[FD]"),
    FS(28, "[FS]");

    private final int code;
    private final String representacao;

    AsciiChar(int code, String representacao) {
        this.code = code;
        this.representacao = representacao;
    }
    
    public static Integer getCode(String representacao) {
        for (AsciiChar ascii : values()) {
        	if (ascii.representacao.equals(representacao)) {
                return ascii.code;
            }
        }
        return null;
    }

    public static String getRepresentacao(char c) {
        for (AsciiChar ascii : values()) {
            if (ascii.code == c) {
                return ascii.representacao;
            }
        }
        return null;
    }
	
}
