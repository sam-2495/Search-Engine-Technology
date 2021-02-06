package cecs.model;

public class DocWeight {
	private double ld;
	private double docLength;
	private double byteSize;
	private double avgtftd;

	public DocWeight(double ld, double docLength, double byteSize, double avgtftd) {
		super();
		this.ld = ld;
		this.docLength = docLength;
		this.byteSize = byteSize;
		this.avgtftd = avgtftd;
	}

	public DocWeight() {
		super();
		// TODO Auto-generated constructor stub
	}

	public double getLd() {
		return ld;
	}

	public void setLd(double ld) {
		this.ld = ld;
	}

	public double getDocLength() {
		return docLength;
	}

	public void setDocLength(double docLength) {
		this.docLength = docLength;
	}

	public double getByteSize() {
		return byteSize;
	}

	public void setByteSize(double byteSize) {
		this.byteSize = byteSize;
	}

	public double getAvgtftd() {
		return avgtftd;
	}

	public void setAvgtftd(double avgtftd) {
		this.avgtftd = avgtftd;
	}
}
