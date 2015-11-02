package libj.poi.tag;

import libj.utils.Text;

public class Part {

	private String expr;
	private Object data;
	private boolean isLiteral;

	private char[] quotes = { '"', '\'', '“', '”', '’' };

	public Part(String expr) {

		setExpr(expr);
	}

	public boolean isExpr() {

		return !isLiteral;
	}

	public boolean isLiteral() {

		return isLiteral;
	}

	public String getExpr() {
		return expr;
	}

	protected void setExpr(String expr) {

		this.expr = expr;

		if (expr.length() > 1) {

			char firstChar = expr.charAt(0);
			char lastChar = expr.charAt(expr.length() - 1);

			isLiteral = Text.isEqualsAny(firstChar, quotes) && Text.isEqualsAny(lastChar, quotes);

			if (isLiteral) {
				setData(expr.substring(1, expr.length() - 1));
			}
		}
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {

		return Text.sprintf("Part: expr=%s; isLiteral=%b, data=%s", expr, isLiteral, data);
	}

}
