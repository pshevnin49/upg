import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import javax.swing.JPanel;

public class DrawingPanel extends JPanel {

	static int maxColor = 0;

	int[][] pixels = nacteni("data_plzen.pgm");
	
	
	private BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
	
	
	static int width;
	static int height;

	double x_min = 0;
	double y_min = 0;
	double x_max = width;
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

		SouradniceXY maxSloupaniXY = maxSloupani(pixels);
		int maxSloupaniX = maxSloupaniXY.getX();
		int maxSloupaniY = maxSloupaniXY.getY();

		SouradniceXY maxPrevyseniXY = maxPrevyseni(pixels);
		int maxVyskaX = maxPrevyseniXY.getX();
		int maxVyskaY = maxPrevyseniXY.getY();

		SouradniceXY minPrevyseniXY = minPrevyseni(pixels);
		int minVyskaX = minPrevyseniXY.getX();
		int minVyskaY = minPrevyseniXY.getY();

		SouradniceXY maxPrevArrowSourXY2 = arrowSourXY2(maxVyskaX, maxVyskaY);
		int maxPrevArrowX2 = maxPrevArrowSourXY2.getX();
		int maxPrevArrowY2 = maxPrevArrowSourXY2.getY();

		SouradniceXY minPrevArrowSourXY2 = arrowSourXY2(minVyskaX, minVyskaY);
		int minPrevArrowX2 = minPrevArrowSourXY2.getX();
		int minPrevArrowY2 = minPrevArrowSourXY2.getY();

		SouradniceXY maxSloupArrowSourXY2 = arrowSourXY2(maxSloupaniX, maxSloupaniY);
		int maxSloupArrowX2 = maxSloupArrowSourXY2.getX();
		int maxSloupArrowY2 = maxSloupArrowSourXY2.getY();

		drawImage(pixels, scale, g2);

		drawArrow((maxPrevArrowX2 - x_min) * scale, (maxPrevArrowY2 - y_min) * scale, (maxVyskaX - x_min) * scale,
				(maxVyskaY - y_min) * scale, 15, g2);
		popisBodu((maxPrevArrowX2), (maxPrevArrowY2), (maxVyskaX), (maxVyskaY), "max. Vyska", g2, 9 * scale, scale);

		drawArrow((minPrevArrowX2 - x_min) * scale, (minPrevArrowY2 - y_min) * scale, (minVyskaX - x_min) * scale,
				(minVyskaY - y_min) * scale, 15, g2);
		popisBodu(minPrevArrowX2, minPrevArrowY2, (minVyskaX), (minVyskaY), "min. Vyska", g2, 9 * scale, scale);

		drawArrow((maxSloupArrowX2 - x_min) * scale, (maxSloupArrowY2 - y_min) * scale, (maxSloupaniX - x_min) * scale,
				(maxSloupaniY - y_min) * scale, 15, g2);
		popisBodu(maxSloupArrowX2, maxSloupArrowY2, (maxSloupaniX), maxSloupaniY, "max. Sloupani", g2, 9 * scale,
				scale);

	}

	/**
	 * Method maxSloupani prijima pixels[][], hleda bod maximalniho sloupani a vrati
	 * objekt SouradniceXY maximalniho sloupani
	 * 
	 * @param pixels
	 * @return
	 */
	public static SouradniceXY maxSloupani(int pixels[][]) {

		int maxSloupani = 0;
		int maxSloupaniX = 0;
		int maxSloupaniY = 0;
		int width = pixels.length;
		int height = pixels[0].length;

		for (int a = 0; a < height; a++) {//
			for (int b = 0; b < width; b++) {
				try {
					if (maxSloupani < Math.abs(pixels[b][a] - pixels[b - 1][a])) {
						maxSloupani = Math.abs(pixels[b][a] - pixels[b - 1][a]);
						maxSloupaniX = b;
						maxSloupaniY = a;
					}
					if (maxSloupani < Math.abs(pixels[b][a] - pixels[b][a - 1])) {
						maxSloupani = Math.abs(pixels[b][a] - pixels[b][a - 1]);
						maxSloupaniX = b;
						maxSloupaniY = a;
					}
					if (maxSloupani < Math.abs(pixels[b][a] - pixels[b][a + 1])) {
						maxSloupani = Math.abs(pixels[b][a] - pixels[b][a + 1]);
						maxSloupaniX = b;
						maxSloupaniY = a;
					}
					if (maxSloupani < Math.abs(pixels[b][a] - pixels[b + 1][a])) {
						maxSloupani = Math.abs(pixels[b][a] - pixels[b + 1][a]);
						maxSloupaniX = b;
						maxSloupaniY = a;

					}
				} catch (IndexOutOfBoundsException e) {

				}

			}
		}
		SouradniceXY maxSloupaniXY = new SouradniceXY(maxSloupaniX, maxSloupaniY);

		return maxSloupaniXY;
	}

	/**
	 * Method maxVyska prijima pixels[][], hleda bod maximalniho prevyseni a vrati
	 * objekt SouradniceXY souradnice maximalniho prevyseni
	 * 
	 * @param pixels
	 * @return
	 */
	public static SouradniceXY maxPrevyseni(int pixels[][]) {
		int maxVyska = 0;
		int maxVyskaX = 0;
		int maxVyskaY = 0;
		int width = pixels.length;
		int height = pixels[0].length;

		for (int a = 0; a < height; a++) {//
			for (int b = 0; b < width; b++) {
				if (pixels[b][a] > maxVyska) {
					maxVyska = pixels[b][a];
					maxVyskaX = b;
					maxVyskaY = a;

				}
			}
		}
		SouradniceXY maxVyskaXY = new SouradniceXY(maxVyskaX, maxVyskaY);
		return maxVyskaXY;
	}

	/**
	 * Method minVyska prijima pixels[][], hleda bod minimalniho prevyseni a vrati
	 * objekt SouradniceXY souradnice minimalniho prevyseni
	 * 
	 * @param pixels
	 * @return SouradniceXY
	 */
	public static SouradniceXY minPrevyseni(int pixels[][]) {
		int minVyska = 1000;
		int minVyskaX = 0;
		int minVyskaY = 0;
		int width = pixels.length;
		int height = pixels[0].length;

		for (int a = 0; a < height; a++) {//
			for (int b = 0; b < width; b++) {
				if (pixels[b][a] < minVyska) {
					minVyska = pixels[b][a];
					minVyskaX = b;
					minVyskaY = a;
					System.out.println("Min Vyska" + minVyska);

				}
			}
		}
		SouradniceXY minVyskaXY = new SouradniceXY(minVyskaX, minVyskaY);

		return minVyskaXY;
	}

	/**
	 * Metoda nacita data z libovolneho pgm (p2) souboru
	 * 
	 * @param jmenoSouboru
	 * @return pixels array dat ze souboru
	 * @throws FileNotFoundException
	 */
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

		
		for (int a = 0; a < height; a++) {
			for (int b = 0; b < width; b++) {
				pixels[b][a] = scn.nextInt();

			}
		}

		return pixels;
	}

	

	/**
	 * Metoda vykresli Obrazek podle dat z arraje, taky hleda parametry max.a min.
	 * Vyska, max. Sloupani
	 * 
	 * @param pixels
	 * @param scale
	 * @param g2
	 */
	private void drawImage(int[][] pixels, double scale, Graphics2D g2) {

		for (int a = 0; a < height; a++) {//
			for (int b = 0; b < width; b++) {

				if (maxColor > 255) {
					double koef = maxColor / 255;
					koef++;

					int color = (int) (pixels[b][a] / koef);
					g2.setColor(new Color(color, color, color));
					g2.fill(new Rectangle2D.Double((b - x_min) * scale, (a - y_min) * scale, 1 * scale, 1 * scale));
				} else if (maxColor < 255) {
					double koef = 255 / maxColor;
					koef++;
					int color = (int) (pixels[b][a] * koef);
					g2.setColor(new Color(color, color, color));
					g2.fill(new Rectangle2D.Double((b - x_min) * scale, (a - y_min) * scale, 1 * scale, 1 * scale));
				} else {
					g2.setColor(new Color(pixels[b][a], pixels[b][a], pixels[b][a]));
					g2.fill(new Rectangle2D.Double((b - x_min) * scale, (a - y_min) * scale, 1 * scale, 1 * scale));
				}
			}
		}

	}

	/**
	 * Kreslí šibku, podlé zadané souřadníce
	 * 
	 * @param x1         začatek šibky x
	 * @param y1         začatek šibky y
	 * @param x2         konec šibký x
	 * @param y2         konec šibký y
	 * @param tip_length delka ¨vousů¨ šibký
	 * @param g2         Draphics 2D
	 */
	private void drawArrow(double x1, double y1, double x2, double y2, double tip_length, Graphics2D g2) {
		double u_x = x2 - x1;
		double u_y = y2 - y1;
		double u_len1 = 1 / Math.sqrt(u_x * u_x + u_y * u_y);
		u_x *= u_len1;
		u_y *= u_len1;

		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(3));

		g2.draw(new Line2D.Double(x1, y1, x2, y2));

		double v_x = u_y;
		double v_y = -u_x;

		v_x *= 0.5 * tip_length;
		v_y *= 0.5 * tip_length;

		double c_x = x2 - u_x * tip_length;
		double c_y = y2 - u_y * tip_length;

		g2.setStroke(new BasicStroke(3));
		g2.draw(new Line2D.Double(c_x + v_x, c_y + v_y, x2, y2));
		g2.draw(new Line2D.Double(c_x - v_x, c_y - v_y, x2, y2));
	}

	/**
	 * Metoda priima vsichni souradnice sibky, a spocita spravne souradnice pro
	 * nadpis
	 * 
	 * @param x1           zacatek sibky X
	 * @param y1           zacatek sibky Y
	 * @param x2           konec sibky X
	 * @param y2           konec sibky Y
	 * @param nadpis       String co bude napsano
	 * @param g2           Graphics2D
	 * @param velkostFontu velikost fontu
	 */
	private void popisBodu(double x1, double y1, double x2, double y2, String nadpis, Graphics2D g2,
			double velkostFontu, double scale) {

		g2.setColor(Color.BLACK);
		Font font = new Font("Calibri", Font.PLAIN, (int) velkostFontu);
		g2.setFont(font);

		FontMetrics fm = g2.getFontMetrics();

		int delka = (int) fm.getStringBounds(nadpis, g2).getWidth();
		double vyska = (int) fm.getStringBounds(nadpis, g2).getHeight();

		System.out.println(velkostFontu + " font");
		System.out.println(delka + " delka");
		System.out.println(vyska + ": vyska");

		double sourPopisX = 0;// souradnice X leveho dolniho rohu podpisu
		double sourPopisY = 0;// souradnice Y leveho dolniho rohu podpisu

		if (y1 == y2) {
			if (x1 > x2) {
				sourPopisY = (y1 - y_min) * scale;
				sourPopisX = ((x1 - x_min) * scale) + 5;
			} else if (x1 < x2) {
				sourPopisY = (y1 - y_min) * scale;
				sourPopisX = ((x1 - x_min) * scale) - (5 + delka);
			}
		} else if (y1 > y2) {
			if (x1 == x2) {
				sourPopisX = ((x1 - x_min) * scale) - delka / 2;
				sourPopisY = ((y1 - y_min) * scale) + vyska;
			} else if (x1 > x2) {
				sourPopisX = (x1 - x_min) * scale + 5;
				sourPopisY = (y1 - y_min) * scale;
			} else if (x1 < x2) {
				sourPopisY = (y1 - y_min) * scale;
				sourPopisX = (x1 - x_min) * scale - (5 + delka);
			}
		} else if (y1 < y2) {
			if (x1 == x2) {
				sourPopisX = ((x1 - x_min) * scale) - delka / 2;
				sourPopisY = ((y1 - y_min) * scale) - vyska;
			} else if (x1 > x2) {
				sourPopisX = ((x1 - x_min) * scale) + 5;
				sourPopisY = (y1 - y_min) * scale;
			} else if (x1 < x2) {
				sourPopisY = (y1 - y_min) * scale;
				sourPopisX = ((x1 - x_min) * scale) - (5 + delka);
			}
		}

		g2.drawString(nadpis, (int) sourPopisX, (int) sourPopisY);

	}

	/**
	 * Methoda spočitá souřadníce X a Y začatku šibký a vratí objekt SouradniceXY
	 * 
	 * @param x2
	 * @param y2
	 * @return
	 */
	private SouradniceXY arrowSourXY2(double x2, double y2) {
		double x1 = 0;
		double y1 = 0;

		if (x2 >= width - 50) {
			if (y2 >= height - 50) {
				x1 = x2 - 25;
				y1 = y2 - 25;
			} else if (y2 <= 0 + 50) {
				x1 = x2 - 25;
				y1 = y2 + 25;
			} else {
				x1 = x2 - 35;
			}
		} else if (x2 <= 0 + 50) {
			if (y2 >= height - 50) {
				x1 = x2 + 25;
				y1 = y2 - 25;
			} else if (y2 <= 0 + 50) {
				x1 = x2 + 25;
				y1 = y2 + 25;
			} else {
				x1 = x2 + 35;
			}
		} else {
			if (y2 >= height - 50) {
				x1 = x2 - 0;
				y1 = y2 - 35;
			} else if (y2 <= 0 + 50) {
				x1 = x2 - 0;
				y1 = y2 + 35;
			} else {
				x1 = x2 - 25;
				y1 = y2 + 25;
			}
		}

		SouradniceXY vypoceSouradniceXY = new SouradniceXY((int) x1, (int) y1);
		return vypoceSouradniceXY;
	}

}
