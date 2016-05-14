package util;


public abstract class HeapElement {
	abstract public Double getValue();
	abstract public int getIndex();
	abstract public int compare(HeapElement e);
	public String toString() {
		return getValue().toString()+"/"+getIndex();
	}
}
