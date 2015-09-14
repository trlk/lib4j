package libj.utils;

public class Const {

	public static final char cr = (char) 0x0d;
	public static final char lf = (char) 0x0a;
	public static final char spc = (char) 0x20;
	public static final char amp = (char) 0x26;

	public static final String CR = String.valueOf(cr);
	public static final String LF = String.valueOf(lf);
	public static final String CRLF = CR.concat(LF);
	public static final String SPC = String.valueOf(spc);
	public static final String SPC2 = SPC.concat(SPC);
	public static final String SPC3 = SPC2.concat(SPC);
	public static final String SPC4 = SPC3.concat(SPC);
	public static final String AMP = String.valueOf(amp);
	public static final String EMPTY = new String();

}
