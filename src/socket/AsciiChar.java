package socket;

public enum AsciiChar {

	NUL(0, "[NUL]"),
    SOH(1, "[SOH]"),
    STX(2, "[STX]"),
    ETX(3, "[ETX]"),
    EOT(4, "[EOT]"), 
    ENQ(5, "[ENQ]"),
    ACK(6, "[ACK]"),
    BEL(7, "[BEL]"),
    BS(8, "[BS]"), 
    HT(9, "[HT]"),
    LF(10, "[LF]"),
    VT(11, "[VT]"),
    FF(12, "[FF]"), 
    CR(13, "[CR]"),
    SO(14, "[SO]"),
    SI(15, "[SI]"),
    DLE(16, "[DLE]"),
    D1(17, "[D1]"),
    D2(18, "[D2]"),
    D3(19, "[D3]"),
    D4(20, "[D4]"),
    NAK(21, "[NAK]"), 
    SYN(22, "[SYN]"),
    ETB(23, "[ETB]"),
    CAN(24, "[CAN]"),
    EM(25, "[EM]"),
    SUB(26, "[SUB]"),
    ESC(27, "[ESC]"),
    FS(28, "[FS]"), 
    GS(29, "[GS]"), 
    RS(30, "[RS]"),
    S(31, "[US]");

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
