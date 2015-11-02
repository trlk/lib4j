package libj.utils;

public class Const {

	public static final char cr = (char) 0x0d;
	public static final char lf = (char) 0x0a;
	public static final char spc = (char) 0x20;
	public static final char amp = (char) 0x26;

	public static final String CR = String.valueOf(cr).intern();
	public static final String LF = String.valueOf(lf).intern();
	public static final String CRLF = CR.concat(LF).intern();
	public static final String SPC = String.valueOf(spc).intern();
	public static final String SPC2 = SPC.concat(SPC).intern();
	public static final String SPC3 = SPC2.concat(SPC).intern();
	public static final String SPC4 = SPC3.concat(SPC).intern();
	public static final String AMP = String.valueOf(amp).intern();
	public static final String EMPTY = new String().intern();

}
