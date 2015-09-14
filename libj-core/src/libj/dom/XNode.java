package libj.dom;

public abstract class XNode implements Cloneable {

	private XNode parent;

	protected XNode() {
		this.parent = null;
	}

	@Override
	public XNode clone() throws CloneNotSupportedException {
		return (XNode) super.clone();
	}

	public XNode getParent() {
		return parent;
	}

	protected void setParent(XNode parent) {

		if (parent != this.parent) {
			this.parent = parent;
		}
	}

	public XDocument getDocument() {

		if (this instanceof XDocument) {
			return (XDocument) this;
		}

		for (XNode node = this; node != null; node = node.getParent()) {
			if (node instanceof XDocument) {
				return (XDocument) node;
			}
		}

		throw new RuntimeException("Parent document is not reachable");
	}

}
