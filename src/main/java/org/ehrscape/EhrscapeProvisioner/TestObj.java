package org.ehrscape.EhrscapeProvisioner;

//import javax.xml.bind.annotation.XmlRootElement;
//import javax.json.bind.annotation.JsonbAnnotation;

//@JsonbAnnotation
public class TestObj {
	
	private String A;
	private String B;
	private SubObj sub;
	
	public void SetA(String A) {
		this.A = A;
	}
	
	public void SetB(String B) {
		this.B = B;
	}
	
	public String getA() {
		return A;
	}
	
	public String getB() {
		return B;
	}
	
	public SubObj getSub() {
		return sub;
	}
	
	public void setSub(String C, String D) {
		//this.sub.SetC(C);
		//this.sub.SetD(D);
		this.sub = new SubObj("c","d");
	}

}
