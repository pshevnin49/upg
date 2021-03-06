import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
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
	int[][] pixels = nacteni("data_plzen.pgm");
	int width = pixels.length;
	int height = pixels[0].length;
	
	int maxVyskaX = 0;
	int maxVyskaY = 0;
	int maxVyska = 10;
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
		
//		System.out.println(width);
//		System.out.println(height);

		
		for (int a = 0; a < height; a++) {//
			for (int b = 0; b < width; b++) {

				if (pixels[b][a] > maxVyska) {
					maxVyska = pixels[b][a];
					maxVyskaX = b;
					maxVyskaY = a;

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
		drawArrow( (vypocetSourX(maxVyskaX, maxVyskaY)- x_min)*scale, (vypocetSourX(maxVyskaX, maxVyskaY)- y_min)*scale,(maxVyskaX - x_min)*scale, (maxVyskaY - y_min) * scale, 15, g2);
		
		
		System.out.println("MinVyska: " + maxVyska);
		System.out.println("X1 " + maxVyskaX );
		System.out.println("Y1 " + maxVyskaY );
		
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
	
	private void drawArrow(double x1, double y1, double x2,double y2, double tip_length, Graphics2D g2) {
        
        double u_x = x2 - x1;
        double u_y = y2 - y1;
        double u_len1 = 1 / Math.sqrt(u_x * u_x + u_y*u_y);
        u_x*= u_len1;
        u_y*= u_len1;
        
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
				x2 = x1 - 50;
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
				x2 = x1 + 50;
			}
		}
		else {
			x2 = x1 - 25;
		}
		return x2;
	}
	private double vypocetSourY(double x1, double y1) {
		double x2 = 0;
		double y2 = 0;
		if(y1 >= height - 50) {
			if(y1 >= width - 50) {
				y2 = y1 + 25;
			}
			else if (y1 <= 0 + 50 ) {
				y2 = y1 + 25;
			}
			else {
				y2 = y1 + 50;
			}
		}
		else if(y1 <= 0 + 50) {
			if(x1 >= width - 50) {
				y2 = y1 - 25;
			}
			else if (x1 <= 0 + 50 ) {
				y2 = y1 - 25;
			}
			else {
				y2 = y1 - 50;
			}
		}
		else {
			y2 = y1 + 25;
		}
		return y2;
	}
	

}
