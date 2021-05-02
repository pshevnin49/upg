import java.awt.Color;

public class VrstenviceSour {
	private SouradniceXY souradnice;
	private Color color = new Color(153, 204, 255);
	private int x; 
	private int y;
	
	public VrstenviceSour(int x, int y) {
		this.x = x;
		this.y = y;
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
		return x;
	}
	public int getY() {
		return y;
	}
	
}
