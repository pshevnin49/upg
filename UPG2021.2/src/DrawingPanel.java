package src;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import javax.swing.JPanel;

public class DrawingPanel extends JPanel {

	private BufferedImage bg_img = nacteniPGM("data\\data_plzen.pgm");
	private BufferedImage image;
	public static int[] data;
	
	private int windowsWidth = 1000;
	private int windowsHeight = 1000;

	public DrawingPanel() throws FileNotFoundException {
		this.setPreferredSize(new Dimension(800, 800));

	}

	public void setPixels(int[] data) {
		this.data = data;
	}

	/**
	 * Prizpusobuje PGM data do RGB;
	 */
	private void processImage() {
		int iW = bg_img.getWidth();
		int iH = bg_img.getHeight();
		this.image = new BufferedImage(iW, iH, BufferedImage.TYPE_3BYTE_BGR);

		int[] pixels = new int[iW * iH];

		bg_img.getRGB(0, 0, iW, iH, pixels, 0, iW);// kopiruje vsichni pixely do arraje

		for (int i = 0; i < pixels.length; i++) {
			int in_rgb = pixels[i];

			int b = in_rgb;
			int g = in_rgb;
			int r = in_rgb;

			int gr = (1 * b + 3 * r + 6 * g) / 10;

			b = gr;
			g = gr;
			r = gr;

			int out_rgb = (r << 16) | (g << 8) | b;
			pixels[i] = out_rgb;

		}
		image.setRGB(0, 0, iW, iH, pixels, 0, iW);// kopiruje vsichni pixely z arraje
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;

		processImage();
		try {
			drawPlzenImage(g2, this.getWidth(), this.getHeight());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		windowsWidth = this.getWidth();
		windowsHeight = this.getHeight();

	}

	public void drawPlzenImage(Graphics2D g2, int W, int H) throws FileNotFoundException {

		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, W, H);

		int iW = image.getWidth();
		int iH = image.getHeight();

		double scaleX = ((double) W) / iW;
		double scaleY = ((double) H) / iH;

		double scale = Math.min(scaleX, scaleY);

		int niW = (int) (iW * scale);
		int niH = (int) (iH * scale);

		int startX = (W - niW) / 2;
		int startY = (H - niH) / 2;

		int[][] pixels = arrayToDoubleArray(data);

		SouradniceXY maxSloupaniSour = maxSloupani(pixels);
		int maxSloupaniX = maxSloupaniSour.getX();
		int maxSloupaniY = maxSloupaniSour.getY();

		SouradniceXY maxPrevyseniSour = maxPrevyseni(pixels);
		int maxPrevyseniX = maxPrevyseniSour.getX();
		int maxPrevyseniY = maxPrevyseniSour.getY();

		SouradniceXY minPrevyseniSour = minPrevyseni(pixels);
		int minPrevyseniX = minPrevyseniSour.getX();
		int minPrevyseniY = minPrevyseniSour.getY();

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		RenderingHints aliasing = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setRenderingHints(rh);

		g2.drawImage(image, startX, startY, niW, niH, null);

		g2.setRenderingHints(aliasing);

		drawArrow(maxSloupaniX, maxSloupaniY, g2, scale, startX, startY);
		drawDesc(maxSloupaniX, maxSloupaniY, "Max. sloupani", g2, scale, startX, startY);

		drawArrow(maxPrevyseniX, maxPrevyseniY, g2, scale, startX, startY);
		drawDesc(maxPrevyseniX, maxPrevyseniY, "Max. prevyseni", g2, scale, startX, startY);

		drawArrow(minPrevyseniX, minPrevyseniY, g2, scale, startX, startY);
		drawDesc(minPrevyseniX, minPrevyseniY, "Max. prevyseni", g2, scale, startX, startY);

	}

	public static BufferedImage nacteniPGM(String jmenoSouboru) throws FileNotFoundException {
		FileReader read = new FileReader(jmenoSouboru);
		Scanner scn = new Scanner(read);
		int maxColor;
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

		int[] pixels = new int[width * height];

		for (int i = 0; i < pixels.length; i++) {
			if (maxColor > 255) {
				double koef = maxColor / 255;
				koef++;

				int color = (int) (scn.nextInt() / koef);
				pixels[i] = color;
			} else if (maxColor < 255) {
				double koef = 255 / maxColor;
				koef++;
				int color = (int) (scn.nextInt() * koef);
				pixels[i] = color;
			} else {
				pixels[i] = scn.nextInt();
			}
		}

		data = pixels;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		img.setRGB(0, 0, width, height, pixels, 0, width);
		return img;
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

	public int[][] arrayToDoubleArray(int[] data) {

		int width = image.getWidth();
		int height = image.getHeight();

		int[][] pixels = new int[width][height];

		int index = 0;
		for (int a = 0; a < height; a++) {
			for (int b = 0; b < width; b++) {
				pixels[b][a] = data[index];

				index++;

			}
		}

		return pixels;
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
	 * @param startY
	 * @param startX
	 */
	private void drawArrow(int x2, int y2, Graphics2D g2, double scale, int startX, int startY) {

		x2 = (int) ((x2 * scale) + startX);
		y2 = (int) ((y2 * scale) + startY);

		SouradniceXY souradniceX2Y2 = vypocetSouradniceX1Y1(x2, y2, startX, startY, scale);

		int x1 = (int) (souradniceX2Y2.getX());
		int y1 = (int) (souradniceX2Y2.getY());

		double u_x = x2 - x1;
		double u_y = y2 - y1;
		double u_len1 = 1 / Math.sqrt(u_x * u_x + u_y * u_y);

		u_x *= u_len1;
		u_y *= u_len1;
		double tip_length = 10;

		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(3));

		g2.draw(new Line2D.Double(x1, y1, x2, y2));// zopacnil jsem x1 y1 a x2 y2

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
	 * @param startY
	 * @param startX
	 * @param velkostFontu velikost fontu
	 */
	private void drawDesc(int x2, int y2, String nadpis, Graphics2D g2, double scale, int startX, int startY) {

		x2 = (int) (x2 * scale + startX);
		y2 = (int) (y2 * scale + startY);

		SouradniceXY souradniceX2Y2 = vypocetSouradniceX1Y1(x2, y2, startX, startY, scale);

		int x1 = souradniceX2Y2.getX();
		int y1 = souradniceX2Y2.getY();

		double velkostFontu = 15;
		g2.setColor(Color.BLACK);
		Font font = new Font("Calibri", Font.PLAIN, (int) velkostFontu);
		g2.setFont(font);

		FontMetrics fm = g2.getFontMetrics();

		int delka = (int) fm.getStringBounds(nadpis, g2).getWidth();
		int vyska = (int) fm.getStringBounds(nadpis, g2).getHeight();

		double sourPopisX = 0;// souradnice X leveho dolniho rohu podpisu
		double sourPopisY = 0;// souradnice Y leveho dolniho rohu podpisu

		if (y1 == y2) {
			if (x1 > x2) {
				sourPopisY = y1;
				sourPopisX = x1 + 5;
			} else if (x1 < x2) {
				sourPopisY = y1;
				sourPopisX = x1 - (5 + delka);
			}
		} else if (y1 > y2) {
			if (x1 == x2) {
				sourPopisX = x1 - delka / 2;
				sourPopisY = y1 + vyska;
			} else if (x1 > x2) {
				sourPopisX = x1 + 5;
				sourPopisY = y1;
			} else if (x1 < x2) {
				sourPopisY = y1;
				sourPopisX = x1 - (5 + delka);
			}
		} else if (y1 < y2) {
			if (x1 == x2) {
				sourPopisX = x1 - delka / 2;
				sourPopisY = y1 - vyska;
			} else if (x1 > x2) {
				sourPopisX = x1 + 5;
				sourPopisY = y1;
			} else if (x1 < x2) {
				sourPopisY = y1;
				sourPopisX = x1 - (5 + delka);
			}
		}

		int sourPopisXInt = (int) sourPopisX;
		int sourPopisYInt = (int) sourPopisY;

		
		if(delka <= windowsWidth - 2*(startX)) {
//			while(sourPopisX < startX) {
//				sourPopisXInt++;
//			}
//			while(sourPopisX + delka > windowsWidth - startX) {
//				sourPopisXInt--;
//			}
			g2.drawString(nadpis, sourPopisXInt, sourPopisYInt);
		}
		
		
//		if (sourPopisX > startX && sourPopisX + delka < windowsWidth - startX) {
//			if (sourPopisY > startY && sourPopisY + vyska < windowsHeight - startY) {
//				
//			}
//
//		}

	}

	/**
	 * Methoda spočitá souřadníce X a Y začatku šibký a vratí objekt
	 * SouradniceXY
	 * 
	 * @param x2
	 * @param y2
	 * @return
	 */
	private SouradniceXY vypocetSouradniceX1Y1(int x2, int y2, int startX, int startY, double scale) {

		double x1 = 0;
		double y1 = 0;

		int startX2 = (int) ((x2 - startX) / scale);
		int startY2 = (int) ((y2 - startY) / scale);

		int width = image.getWidth();
		int height = image.getHeight();

		if (startX2 >= width - 50) {
			if (startY2 >= height - 50) {
				x1 = x2 - 31;
				y1 = y2 - 31;
			} else if (startY2 <= 0 + 50) {
				x1 = x2 - 31;
				y1 = y2 + 31;
			} else {
				x1 = x2 - 42;
			}
		} else if (startX2 <= 0 + 50) {
			if (startY2 >= height - 50) {
				x1 = x2 + 31;
				y1 = y2 - 31;
			} else if (startY2 <= 0 + 50) {
				x1 = x2 + 31;
				y1 = y2 + 31;
			} else {
				x1 = x2 + 42;
			}
		} else {
			if (startY2 >= height - 50) {
				x1 = x2 - 0;
				y1 = y2 - 42;
			} else if (startY2 <= 0 + 50) {
				x1 = x2 - 0;
				y1 = y2 + 42;
			} else {
				x1 = x2 - 31;
				y1 = y2 + 31;
			}
		}

		SouradniceXY vypoceSouradniceXY = new SouradniceXY((int) x1, (int) y1);
		return vypoceSouradniceXY;
	}

}
