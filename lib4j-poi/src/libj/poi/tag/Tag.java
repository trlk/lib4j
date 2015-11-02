package libj.poi.tag;

import java.util.ArrayList;
import java.util.List;

import libj.debug.Log;
import libj.debug.Trace;
import libj.error.RuntimeError;
import libj.poi.Engine;
import libj.utils.Text;

public class Tag {

	private Engine engine;
	private String brutto;
	private String netto;
	private String funcName;
	private List<Part> parts;

	public Tag(Engine engine, String brutto) {

		parts = new ArrayList<Part>();

		setEngine(engine);
		setBrutto(brutto);

		if (Trace.isEnabled()) {
			Log.trace(this.toString());
		}
	}

	public Engine getEngine() {
		return engine;
	}

	protected void setEngine(Engine engine) {
		this.engine = engine;
	}

	public String getBrutto() {
		return brutto;
	}

	protected void setBrutto(String brutto) {

		this.brutto = brutto;
		setNetto(brutto.substring(engine.getOpenTag().length(), brutto.length() - engine.getCloseTag().length()).trim());
	}

	public String getNetto() {
		return netto;
	}

	protected void setNetto(String netto) {

		this.netto = netto;

		String[] arr = netto.split("\\(");

		if (arr.length == 2) {

			setFuncName(arr[0]);
			setExpr(arr[1].split("\\)")[0]);

		} else {

			setFuncName(null);
			setExpr(netto);
		}
	}

	public boolean isFunc() {

		return Text.isNotEmpty(funcName);
	}

	public String getFuncName() {
		return funcName;
	}

	protected void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	public List<Part> getParts() {

		return parts;
	}

	protected void setParts(List<Part> parts) {

		this.parts = parts;
	}

	protected void setExpr(String expr) {

		setExprArray(expr.split("\\,"));
	}

	protected void setExprArray(String[] exprArray) {

		parts.clear();

		for (int i = 0; i < exprArray.length; i++) {

			Part part = new Part(exprArray[i]);

			parts.add(part);
		}
	}

	public String getExpr() {

		if (parts.size() == 0) {
			return null;
		} else if (parts.size() == 1) {
			return parts.get(0).getExpr();
		} else {
			throw new RuntimeError("Multipart expresssion cannot be returned as string");
		}
	}

	public String[] getExprArray() {

		List<String> list = new ArrayList<String>();

		for (Part part : parts) {

			if (part.isExpr()) {
				list.add(part.getExpr());
			}
		}

		return list.toArray(new String[list.size()]);
	}

	public Object[] getDataArray() {

		Object[] dataArray = new Object[parts.size()];

		for (int i = 0; i < parts.size(); i++) {
			dataArray[i] = parts.get(i).getData();
		}

		return dataArray;
	}

	@Override
	public String toString() {

		return Text.sprintf("Tag: brutto=%s; netto=%s; funcName=%s; parts=%s", brutto, netto, funcName,
				parts.toString());
	}

}
