import java.awt.Color;

public class VrstenviceSour {
	private SouradniceXY souradnice;
	private Color color = new Color(153, 204, 255);
	
	
	
	

	public VrstenviceSour(int x, int y) {
		SouradniceXY souradnice1 = new SouradniceXY(x, y);
		this.souradnice = souradnice1;
	}



	public SouradniceXY getSouradniceXY() {
		return souradnice;
	}
	public void setSouradnice(SouradniceXY souradnice) {
		this.souradnice = souradnice;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public int getX() {
		return getSouradniceXY().getX();
	}
	public int getY() {
		return getSouradniceXY().getY();
	}
	
}
