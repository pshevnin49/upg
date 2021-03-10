import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import javax.swing.JPanel;

public class DrawingPanel extends JPanel {

	static int maxColor = 0;
	int[][] pixels = nacteni("mona_lisa.ascii.pgm");
	
	int width = pixels.length;
	int height = pixels[0].length;
	
	int maxVyskaX = 0;
	int maxVyskaY = 0;
	int maxVyska = 0;
	
	int minVyskaX = 0;
	int minVyskaY = 0;
	int minVyska = 1000;
	
	double x_min = 0;
	double y_min = 0;
	double x_max = width ;
	double y_max = height;
	
	double world_width = x_max - x_min;
	double world_height = y_max - y_min;
	
	public DrawingPanel() throws FileNotFoundException {
		
		
		this.setPreferredSize(new Dimension(width, height));
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;

		double scale_x = this.getWidth() / world_width;
		double scale_y = this.getHeight() / world_height;
		double scale = Math.min(scale_x, scale_y);
		


		
		for (int a = 0; a < height; a++) {//
			for (int b = 0; b < width; b++) {

				if (pixels[b][a] > maxVyska) {
					maxVyska = pixels[b][a];
					maxVyskaX = b;
					maxVyskaY = a;
					System.out.println("Max Vyska" + maxVyska);

				}
				if (pixels[b][a] < minVyska) {
					minVyska = pixels[b][a];
					minVyskaX = b;
					minVyskaY = a;
					System.out.println("Min Vyska" + minVyska);

				}
				if (maxColor > 255) {
					double koef = maxColor / 255;
					koef++;
					
					int color = (int) (pixels[b][a] / koef);
					g.setColor(new Color(color, color, color));
					g2.fill(new Rectangle2D.Double((b - x_min) * scale, (a - y_min) * scale, 1 * scale, 1 * scale));
				} else if (maxColor < 255) {
					double koef = 255 / maxColor;
					koef++;
					int color = (int) (pixels[b][a] * koef);
					g.setColor(new Color(color, color, color));
					g2.fill(new Rectangle2D.Double((b - x_min) * scale, (a - y_min) * scale, 1 * scale, 1 * scale));
				} else {
					g.setColor(new Color(pixels[b][a], pixels[b][a], pixels[b][a]));
					g2.fill(new Rectangle2D.Double((b - x_min) * scale, (a - y_min) * scale, 1 * scale, 1 * scale));
				}
			}
		}
		//g.drawLine(maxVyskaX, maxVyskaY, width / 2, height / 2);
		drawArrow( (vypocetSourX(maxVyskaX, maxVyskaY)- x_min)*scale, (vypocetSourY(maxVyskaX, maxVyskaY)- y_min)*scale,(maxVyskaX - x_min)*scale, (maxVyskaY - y_min) * scale, 15, g2);
		drawArrow( (vypocetSourX(minVyskaX, minVyskaY)- x_min)*scale, (vypocetSourY(minVyskaX, minVyskaY)- y_min)*scale,(minVyskaX - x_min)*scale, (minVyskaY - y_min) * scale, 15, g2);
		popisBodu( (vypocetSourX(maxVyskaX, maxVyskaY)- x_min)*scale, (vypocetSourY(maxVyskaX, maxVyskaY)- y_min)*scale,(maxVyskaX - x_min)*scale, (maxVyskaY - y_min) * scale, "testovaci otazka", g2, 7*scale);
		
		drawArrow( (vypocetSourX(0, 70)- x_min)*scale, (vypocetSourY(0, 70)- y_min)*scale,(0 - x_min)*scale, (70 - y_min) * scale, 15, g2);//testovaci
		popisBodu( (vypocetSourX(0, 70)- x_min)*scale, (vypocetSourY(0, 70)- y_min)*scale,(0 - x_min)*scale, (70 - y_min) * scale, "testovaci otazka", g2, 7*scale);
		
		drawArrow( (vypocetSourX(80, 70)- x_min)*scale, (vypocetSourY(80, 70)- y_min)*scale,(80 - x_min)*scale, (70 - y_min) * scale, 15, g2);//testovaci
		popisBodu( (vypocetSourX(80, 70)- x_min)*scale, (vypocetSourY(80, 70)- y_min)*scale,(80 - x_min)*scale, (70 - y_min) * scale, "test", g2, 7*scale);
		
		System.out.println("Max Vyska: " + maxVyska);
		System.out.println("X1 " + maxVyskaX );
		System.out.println("Y1 " + maxVyskaY );
		System.out.println("X2 " + (vypocetSourX(maxVyskaX, maxVyskaY)));
		System.out.println("Y2 " + (vypocetSourY(maxVyskaX, maxVyskaY)));
		
		//System.out.println(maxVyska);
	}

	public static int[][] nacteni(String jmenoSouboru) throws FileNotFoundException {
		FileReader read = new FileReader(jmenoSouboru);
		Scanner scn = new Scanner(read);
		int width = 0;
		int height = 0;
		scn.nextLine();
		String line = scn.nextLine();
		if (line.startsWith("#")) {
			while (line.startsWith("#")) {
				line = scn.nextLine();
			}
			Scanner l = new Scanner(line);
			width = l.nextInt();
			height = l.nextInt();
			maxColor = scn.nextInt();
		} else {
			Scanner l = new Scanner(line);
			width = l.nextInt();
			height = l.nextInt();
			maxColor = scn.nextInt();
		}

		int[][] pixels = new int[width][height];

		System.out.println("width " + width);
		System.out.println("height " + height);
		System.out.println("Max value of color " + maxColor);

		for (int a = 0; a < height; a++) {
			for (int b = 0; b < width; b++) {
				pixels[b][a] = scn.nextInt();

			}
		}

		return pixels;
	}

	public static int[][] polace(int[][] array) { 
		int height = array[0].length;
		int width = array.length;
		int newWidth = (width * 2);
		int newHeight = (height * 2);
		System.out.println(newWidth);
		System.out.println(newHeight);
		int[][] newArray = new int[newWidth][newHeight];
		int koefWidth = 0;
		int koefHeight = 0;

		for (int a = 0; a < newHeight; a += 2) {
			for (int b = 0; b < newWidth; b += 2) {

				newArray[b][a] = array[koefWidth][koefHeight];
				koefWidth++;

			}
			koefWidth = 0;
			koefHeight++;
		}
		for (int a = 0; a < newHeight - 1; a += 2) {
			for (int b = 1; b < newWidth - 1; b += 2) {

				newArray[b][a] = (newArray[b - 1][a] + newArray[b + 1][a]) / 2;

			}

		}
		for (int a = 0; a < newWidth - 1; a++) {
			for (int b = 1; b < newHeight - 1; b += 2) {

				newArray[a][b] = (newArray[a][b - 1] + newArray[a][b + 1]) / 2;
			}

		}

		return newArray;

	}
	
	private void drawArrow(double x1, double y1, double x2, double y2, double tip_length, Graphics2D g2) {// kresli sibku podle souradnic
        
		double u_x = x2 - x1;
        double u_y = y2 - y1;
        double u_len1 = 1 / Math.sqrt(u_x * u_x + u_y*u_y);
        u_x *= u_len1;
        u_y *= u_len1;
        
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        
        g2.draw(new Line2D.Double(x1,y1,x2,y2));
        
        double v_x = u_y;
        double v_y = -u_x;
        
       
        v_x *= 0.5 * tip_length;
        v_y *= 0.5 * tip_length;
        
        double c_x =x2 - u_x*tip_length;
        double c_y =y2 - u_y*tip_length;
        
        g2.setStroke(new BasicStroke(3));
        g2.draw(new Line2D.Double(c_x + v_x,c_y + v_y,x2,y2));    
        g2.draw(new Line2D.Double(c_x - v_x,c_y - v_y,x2,y2)); 
	}
	/**
	 * Metoda priima vsichni souradnice sibky, a spocita spravne souradnice pro nadpis
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param nadpis
	 * @param g2
	 * @param font
	 */
	private void popisBodu (double x1, double y1, double x2, double y2, String nadpis, Graphics2D g2, double font ) {// pise popis bodu
		
		double sourPopisX = 0;
		double sourPopisY = 0;
		
		int delkaPopis = nadpis.length();
		if(y1 == y2) {
			if(x1 > x2) {
				sourPopisY = y1;
				sourPopisX = x1 + 5;
			}
			else if(x1 < x2) {
				sourPopisY = y1;
				sourPopisX = x1 - (5 + delkaPopis * font);
			}
		}
		else if(y1 > y2) {
			if(x1 == x2) {
				sourPopisX = x1 - font * 2 ;
				sourPopisY = y1 + font;
			}
			else if(x1 > x2) {
				sourPopisX = x1 + 5;
				sourPopisY = y1;
			}
			else if(x1 < x2) {
				sourPopisY = y1;
				sourPopisX = x1 - (5 + delkaPopis * font);
			}
		}
		else if(y1 < y2) {
			
		}
		
		
		
		FontMetrics fm = g2.getFontMetrics();
		g2.setColor(Color.BLACK);
		
		g2.setFont(new Font ("Calibri", Font.PLAIN, (int)font));
		g2.drawString(nadpis, (int)sourPopisX, (int)sourPopisY);
		
	}
	
	
	
	
	private double vypocetSourX(double x1, double y1) {
		double x2 = 0;
		double y2 = 0;
		if(x1 >= width - 50) {
			if(y1 >= height - 50) {
				x2 = x1 - 25;
			}
			else if (y1 <= 0 + 50 ) {
				x2 = x1 - 25;
			}
			else {
				x2 = x1 - 35;
			}
		}
		else if(x1 <= 0 + 50) {
			if(y1 >= height - 50) {
				x2 = x1 + 25;
			}
			else if (y1 <= 0 + 50 ) {
				x2 = x1 + 25;
			}
			else {
				x2 = x1 + 35;
			}
		}
		else {
			if(y1 >= height - 50) {
				x2 = x1 - 0;
			}
			else if (y1 <= 0 + 50 ) {
				x2 = x1 - 0;
			}
			else {
				x2 = x1 - 25;
			}
		}
		return x2;
	}
	private double vypocetSourY(double x1, double y1) {//nefungchni
		double x2 = 0;
		double y2 = 0;
		if(y1 >= height - 50) {
			if(x1 >= width - 50) {
				y2 = y1 - 25;
			}
			else if (x1 <= 0 + 50 ) {
				y2 = y1 - 25;
			}
			else {
				y2 = y1 - 35;
			}
		}
		else if(y1 <= 0 + 50) {
			if(x1 >= width - 50) {
				y2 = y1 + 25;
			}
			else if (x1 <= 0 + 50 ) {
				y2 = y1 + 25;
			}
			else {
				y2 = y1 + 35;
			}
		}
		else {
			if(x1 >= width - 50) {
				y2 = y1 + 0;
			}
			else if (x1 <= 0 + 50 ) {
				y2 = y1 + 0;
			}
			else {
				y2 = y1 + 25;
			}
		}
		return y2;
	}
	

}
