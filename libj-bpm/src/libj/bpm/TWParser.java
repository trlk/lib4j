package libj.bpm;

import java.io.StringWriter;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamResult;

import libj.debug.Debug;
import libj.debug.Log;
import libj.error.Throw;
import libj.utils.Cal;
import libj.utils.Text;
import libj.utils.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import teamworks.TWList;
import teamworks.TWObject;

public class TWParser {

	private String dateFormat = Cal.DEFAULT_DATE_FORMAT;
	private String timeFormat = Cal.DEFAULT_TIME_FORMAT;
	private String doubleFormat = Text.EMPTY_STRING;

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

	public String getDoubleFormat() {
		return doubleFormat;
	}

	public void setDoubleFormat(String doubleFormat) {
		this.timeFormat = doubleFormat;
	}

	private Element twParse(Object object, Node parentNode, String nodeName) {

		Document doc;

		if (parentNode instanceof Document)
			doc = (Document) parentNode;
		else
			doc = parentNode.getOwnerDocument();

		Element node;

		if (nodeName != null) {

			node = doc.createElement(nodeName);
			parentNode.appendChild(node);

		} else {
			node = (Element) parentNode;
		}

		if (object != null) {

			String nodeType = object.getClass().getSimpleName();

			String twType = null;

			if (object instanceof TWObject) {

				try {
					twType = ((TWObject) object).getTWClassName();
				} catch (Exception e) {
					twType = null;
				}
			}

			boolean isList = false;

			if (twType != null) {
				nodeType = twType;
				isList = twType.contains("[]");
			}

			// поехали...
			if (isList) {
				TWList list = (TWList) object;

				int size = list.getArraySize();
				for (int i = 0; i < size; i++) {

					Element item = twParse(list.getArrayData(i), node, Xml.TAG_NAME_ITEM);

					item.setAttribute(Xml.ATTR_NAME_INDEX, Integer.toString(i));
				}

			} else if (object instanceof TWObject) {

				TWObject obj = (TWObject) object;
				Set<String> set;

				try {
					set = obj.getPropertyNames();
				} catch (Exception e) {
					set = null;
				}

				if (set != null) {
					for (String propName : set) {
						twParse(obj.getPropertyValue(propName), node, propName);
					}
				}

			} else if (object instanceof GregorianCalendar) {

				node.setAttribute(Xml.ATTR_NAME_TYPE, java.util.Date.class.getSimpleName());

				java.util.Date dateObject = ((GregorianCalendar) object).getTime();

				node.setTextContent(fmtDate(dateObject));

				if (dateFormat != null)
					node.setAttribute("dateFormat", dateFormat);

				if (timeFormat != null) {

					node.setAttribute("time", fmtTime(dateObject));

					node.setAttribute("timeFormat", timeFormat);
				}

				// date in words
				node.setAttribute("words", fmtDateInWords(dateObject));
				node.setAttribute("words2", fmtDateInWords2(dateObject));

			} else if (object instanceof Double) {

				node.setAttribute(Xml.ATTR_NAME_TYPE, Double.class.getSimpleName());

				Double doubleObject = (Double) object;
				node.setTextContent(fmtDouble(doubleObject));

				// amount in words
				node.setAttribute("words", fmtDoubleInWords(doubleObject));
				node.setAttribute("words2", fmtDoubleInWords2(doubleObject));

			} else if (object instanceof Integer) {

				node.setAttribute(Xml.ATTR_NAME_TYPE, Integer.class.getSimpleName());

				Integer integerObject = (Integer) object;
				node.setTextContent(fmtInteger(integerObject));

				// amount in words
				String integerInWords = fmtIntegerInWords(integerObject);

				if (integerInWords != null) {
					node.setAttribute("words", integerInWords);
				}
				
			} else if (object instanceof String) {

				node.setAttribute(Xml.ATTR_NAME_TYPE, String.class.getSimpleName());

				String stringObject = (String) object;
				node.setTextContent(stringObject);

				// upper
				node.setAttribute("lower", stringObject.toLowerCase());
				node.setAttribute("upper", stringObject.toUpperCase());

			} else {

				node.setTextContent(object.toString());
				node.setAttribute(Xml.ATTR_NAME_TYPE, nodeType);

				if (Debug.isEnabled()) {

					Log.debug("node=%s, type=%s, hex=%s", node.getNodeName(), nodeType,
							Text.getHexString(object.toString().getBytes()));
				}
			}
		}

		return node;
	}

	public Document createDom(TWObject object) {

		if (object == null)
			Throw.runtimeException("%s: Object is null", Debug.thisMethodName());

		try {

			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			DocumentBuilder b = f.newDocumentBuilder();
			Document doc = b.newDocument();

			String rootName = Text.toLowerCamelCase(object.getTWClassName());
			twParse(object, doc, rootName);

			return doc;
		}

		catch (Exception e) {
			Throw.runtimeException(e);
		}

		return null;
	}

	public Document createDom(TWObject object, String outRootName) {

		if (object == null)
			Throw.runtimeException("%s: Object is null", Debug.thisMethodName());

		try {

			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();

			DocumentBuilder b = f.newDocumentBuilder();
			Document doc = b.newDocument();

			outRootName = Text.toLowerCamelCase(object.getTWClassName());

			twParse(object, doc, null);

			return doc;
		}

		catch (Exception e) {
			Throw.runtimeException(e);
		}

		return null;
	}

	public Map<String, String> createMap(TWObject object) {

		try {

			Document doc = createDom(object);

			return Xml.createMap(doc);
		}

		catch (Exception e) {
			Throw.runtimeException(e);
		}

		return null;
	}

	public void serialize(TWObject object, StreamResult stream) {

		Document doc = createDom(object);
		Xml.serialize(doc, stream);

	}

	public String serialize(TWObject object) {

		StringWriter stringWriter = new StringWriter();
		StreamResult streamResult = new StreamResult(stringWriter);

		serialize(object, streamResult);

		return stringWriter.toString();
	}

	// форматирование даты
	public String fmtDate(java.util.Date date) {

		if (dateFormat == null) {

			XMLGregorianCalendar xgc;
			xgc = Cal.getXmlDate(date);
			return xgc.toString();

		} else {
			return Cal.formatDate(date, dateFormat);
		}
	}

	// форматирование времени
	public String fmtTime(java.util.Date date) {

		return Cal.formatDate(date, timeFormat);
	}

	// дата прописью
	public String fmtDateInWords(java.util.Date date) {

		return null;
	}

	// дата прописью
	public String fmtDateInWords2(java.util.Date date) {

		return null;
	}

	// форматирование числа с плавающей точкой
	public String fmtDouble(Double value) {

		return Text.formatDouble(value, doubleFormat);
	}

	// сумма прописью
	public String fmtDoubleInWords(Double value) {

		return null;
	}

	// сумма прописью #2
	public String fmtDoubleInWords2(Double value) {

		return null;
	}

	// форматирование целого
	public String fmtInteger(Integer value) {

		return value.toString();
	}

	// целое прописью
	public String fmtIntegerInWords(Integer value) {

		return null;
	}

}
