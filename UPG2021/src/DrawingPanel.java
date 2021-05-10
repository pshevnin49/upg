import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JPanel;



public class DrawingPanel extends JPanel {

	private BufferedImage bg_img;
	private BufferedImage image;
	private BufferedImage imageVrstevnice;
	private int[] data;
	private int[] nezpracovanaData;
	private int maxHodnota;
	private int minHodnota;

	private int pocetBarev = 0;
	double krokVysky = 0;
	Color[] poleBarev;
	private int startX;
	private int startY;
	private int iW;
	private int iH;
	private boolean[] otevreneVrst;
	private int souradniceProVypisX;
	private int souradniceProVypisY;
	private int zmacknutaVyska;
	private int thisWidth;
	private int thisHeight;
	private int vyskaLegendy = 28;
	private int indexMinimalniBarvy = 100000;
	

	private double scale;

	List<VrstenviceSour>[] poleSouradnicVrst;

	private int windowsWidth = 0;
	private int windowsHeight = 0;
	
	/**
	 * Konstruktor DrawingPanel zpracovava zmacknuti mysi 
	 * 
	 * @throws FileNotFoundException
	 */
	public DrawingPanel() throws FileNotFoundException {
		this.setPreferredSize(new Dimension(800, 600));
		
		thisWidth = this.getWidth();
		thisHeight = this.getHeight();

		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {

				Color vrstColor = new Color(153, 204, 255);

				int x = (int) ((e.getX() - startX) / scale);
				int y = (int) ((e.getY() - startY) / scale);

				souradniceProVypisX = x;
				souradniceProVypisY = y;

				zmacknutaVyska = getVyska(x, y);

				for (int i = 0; i < otevreneVrst.length; i++) {
					if (otevreneVrst[i]) {
						barveniVrstevnice(i, vrstColor);
						repaint();
					}
				}

				int vyska = getVyska(x, y);

				int indexVrstevnice = indexBarvyVysky(vyska) + 1;

				if (indexVrstevnice > poleSouradnicVrst.length) {
					indexVrstevnice--;
				}

				if (indexVrstevnice >= poleSouradnicVrst.length - 3) {
					indexVrstevnice -= 1;
				}

				int polePixelu[] = new int[iW * iH];
				image.getRGB(0, 0, iW, iH, polePixelu, 0, iW);
				Color redColor = Color.RED;

				barveniVrstevnice(indexVrstevnice, redColor);
				otevreneVrst[indexVrstevnice] = true;
				
				System.out.println(indexBarvyVysky(vyska) + " indexBarvy");

				repaint();

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * Metoda prepsana z JPanel, vyvolava metodu drawPlzenImage
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;

		processImage();

		try {

			thisHeight = this.getHeight() - vyskaLegendy;//
			thisWidth = this.getWidth();
			drawPlzenImage(g2);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Metoda prijima index vrstevnice kterou se treba zabarvit, a barvu.
	 * @param indexVrstevnice
	 * @param color
	 */
	private void barveniVrstevnice(int indexVrstevnice, Color color) {

		try {
			for (int i = 0; i < poleSouradnicVrst[indexVrstevnice].size(); i++) {
				poleSouradnicVrst[indexVrstevnice].get(i).setColor(color);
			}
		} catch (IndexOutOfBoundsException e) {
			// TODO: handle exception
		}

	}

	/**
	 * Metoda kresli pod mapou spravnou legendu, a prekresluje ji v pripade zmeny
	 * rozmeru okna. Kdyz barev je prilis mnoho, zvetsuje krok.
	 * 
	 * @param g2
	 * @param W
	 * @param H
	 * @param startX
	 * @param startY
	 * @param scale
	 */
	public void drawLegendaMapy(Graphics2D g2, int W, int H, int startX, int startY, double scale) {
		int vyska; 
		if(minHodnota < indexMinimalniBarvy * 50) {
			vyska = (indexMinimalniBarvy - 1) * 50;
		}
		else {
			vyska = indexMinimalniBarvy * 50;
		}
		
		int index = 0;
		int posuvDolu = 0;
		int indexVyski = 1;
		int krokBarvy = pocetBarev / 10;
		if (pocetBarev < 30) {
			for (int i = indexMinimalniBarvy; i < pocetBarev; i++) {

				int vyska2 = vyska + 50;
				int x = startX + 10;

				if (x + 30 + (index * 36) > W + startX) {
					posuvDolu++;
					vyskaLegendy = 25 * indexVyski + 28;
					indexVyski++;

					index = 0;
				}

				int y = H + 5 + (posuvDolu * 25);
				x += (index * 36);

				String podpisString = Integer.toString(vyska);
				String podpisString2 = Integer.toString(vyska2);
				Font font = new Font("Times new roman", Font.PLAIN, 9);

				g2.setFont(font);
				g2.setColor(poleBarev[i]);
				g2.fillRect(x, y, 10, 10);
				g2.setColor(Color.BLACK);
				g2.drawString((podpisString + "-" + podpisString2), x, y + 20);
				vyska += 50;
				index++;

			}
		} else {

			for (int i = indexMinimalniBarvy; i < pocetBarev; i += krokBarvy) {
				
				int vyska2 = vyska + (50 * krokBarvy);
				int x = startX + 10;

				if (x + 50 + (index * 55) > W + startX) {
					posuvDolu++;
					vyskaLegendy = 25 * indexVyski + 35;
					indexVyski++;

					index = 0;
				}

				int y = H + 5 + (posuvDolu * 25);
				x += (index * 55);

				String podpisString = Integer.toString(vyska);
				String podpisString2 = Integer.toString(vyska2);
				Font font = new Font("Times new roman", Font.PLAIN, 10);

				g2.setFont(font);
				g2.setColor(poleBarev[i]);
				g2.fillRect(x, y, 10, 10);
				g2.setColor(Color.BLACK);
				g2.drawString((podpisString + "-" + podpisString2), x, y + 20);
				vyska += (50 * krokBarvy);
				index++;

			}
		}

	}

	/**
	 * Metoda vykresluje obrazek v centru okna, a vyvolava metody vykreslujici sibky
	 * a nadpisy v souradnicih maximallnihi a minimalniho prevyseni, a maximalniho
	 * sloupani
	 * 
	 * @param g2 Graphics2D
	 * @param W  sirka
	 * @param H  vyska
	 * @throws FileNotFoundException
	 */
	public void drawPlzenImage(Graphics2D g2) throws FileNotFoundException {

		g2.setColor(Color.BLACK);

		double scaleX = ((double) thisWidth) / iW;
		double scaleY = ((double) thisHeight) / iH;

		scale = Math.min(scaleX, scaleY);

		int niW = (int) (iW * scale);
		int niH = (int) (iH * scale);

		startX = (thisWidth - niW) / 2;
		startY = 0;

		int[][] pixels = arrayToDoubleArray(nezpracovanaData);

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
		
		
		
		imageVrstevnice = kresleniVrstevnic(image);
		
		g2.drawImage(imageVrstevnice, startX, startY, niW, niH, null);
		

		g2.setRenderingHints(aliasing);
		
		
		
		drawLegendaMapy(g2, niW, niH, startX, startY, scale);

		windowsWidth = this.getWidth();
		windowsHeight = this.getHeight();

		vypisZmacVysky(g2, scale, startX, startY);

		drawArrow(maxSloupaniX, maxSloupaniY, g2, scale, startX, startY, niW, niH);
		drawDesc(maxSloupaniX, maxSloupaniY, "Max. sloupani", g2, scale, startX, startY);

		drawArrow(maxPrevyseniX, maxPrevyseniY, g2, scale, startX, startY, niW, niH);
		drawDesc(maxPrevyseniX, maxPrevyseniY, "Max. prevyseni", g2, scale, startX, startY);

		drawArrow(minPrevyseniX, minPrevyseniY, g2, scale, startX, startY, niW, niH);
		drawDesc(minPrevyseniX, minPrevyseniY, "Min. prevyseni", g2, scale, startX, startY);

	}
	
	
	

	/**
	 * Metoda processImage() prizpusobuje PGM data nactene ze souboru, do
	 * cernobileho obrazku.
	 */
	private void processImage() {

		int iWidth = bg_img.getWidth();
		int iHeight = bg_img.getHeight();

		this.image = new BufferedImage(iWidth, iHeight, BufferedImage.TYPE_3BYTE_BGR);

		int[] pixels = new int[iWidth * iHeight];
		Color[] colors = new Color[iWidth * iHeight];

		bg_img.getRGB(0, 0, iWidth, iHeight, pixels, 0, iWidth);// kopiruje vsichni pixely do arraje

		for (int i = 0; i < pixels.length; i++) {
			int in_rgb = pixels[i];

			int b = in_rgb;
			int g = in_rgb;
			int r = in_rgb;

			int out_rgb = (r << 16) | (g << 8) | b;

			Color out = new Color(in_rgb);

			int grColor = nezpracovanaData[i];

			int koef = indexBarvyVysky(grColor);

			in_rgb = poleBarev[koef].getRGB();

			if (indexMinimalniBarvy > koef) {
				indexMinimalniBarvy = koef;
			}

			pixels[i] = in_rgb;

		}
		image.setRGB(0, 0, iWidth, iHeight, pixels, 0, iWidth);// kopiruje vsichni pixely z arraje
	}

	/**
	 * Metoda prijima pocet potrebny pocet barev, a generuje adekvatni barevnou
	 * skalu
	 * 
	 */
	public Color[] getBarvy(int pocetBarev) {

		double krokBarvy = 255.0 / pocetBarev;;

		double indexR = 0;
		double indexG = 255;
		double indexB = 80;

		Color[] poleBarev = new Color[pocetBarev];

		for (int i = 0; i < pocetBarev; i++) {

			while (indexR > 255) {
				indexR--;
			}
			if (indexR > 70) {
				indexB += 10;
			}
			while (indexB > 255) {
				indexB--;
			}
			if (indexR > 250) {
				indexG += 25;
			}
			while (indexG > 255) {
				indexG--;
			}

			if (indexR >= 252 && indexG >= 252 && indexB >= 252) {
				indexR = 102;
				indexG = 19;
				indexB = 182;
			}

			Color color1 = new Color((int) indexR, (int) indexG, (int) indexB);

			poleBarev[i] = color1;
			indexR += krokBarvy;
			indexG -= krokBarvy;
		}
		return poleBarev;
	}

	/**
	 * Metoda zabarvi vsichni pixely, ktere se nachazi mezi dvema vyskama v barvu
	 * vrstevnice, a vrati Image uz ze vrstevnicama. Taky vklada souradnice
	 * vrstevnic pro kazdou barvu do vlastnihi arraje v pole
	 * 
	 * @param image
	 * @return
	 */
	private BufferedImage kresleniVrstevnic(BufferedImage imageVrstevnic) {
		
		//BufferedImage imageVrstevnic = new BufferedImage(iW, iH, BufferedImage.TYPE_3BYTE_BGR);  
		System.out.println("kresleni start");
		int[] pixels = new int[iW * iH];
		imageVrstevnic.getRGB(0, 0, iW, iH, pixels, 0, iW);
		int doublePixels[][] = arrayToDoubleArray(pixels);

		for (int a = 0; a < poleSouradnicVrst.length; a++) {
			for (int b = 0; b < poleSouradnicVrst[a].size(); b++) {
				
				int x = poleSouradnicVrst[a].get(b).getX();
				int y = poleSouradnicVrst[a].get(b).getY();
				doublePixels[x][y] = poleSouradnicVrst[a].get(b).getColor().getRGB();

			}
		}

		pixels = doubleArrayToArray(doublePixels);
		imageVrstevnic.setRGB(0, 0, iW, iH, pixels, 0, iW);
		System.out.println("kresleni stop");
		return imageVrstevnic;

	}

	/**
	 * Metoda zaplnuje pole souradnic vrstevnic prvnima datama.
	 * 
	 * @param image
	 * @return
	 */
	public void prvniZaplneniSour() {

		int dataVysek[][] = arrayToDoubleArray(nezpracovanaData);
		
		poleSouradnicVrst = new ArrayList[pocetBarev];

		
		for (int i = 0; i < pocetBarev; i++) {
			List<VrstenviceSour> list = new ArrayList<>();
			poleSouradnicVrst[i] = list;
		}
		
		
		for (int a = 0; a < iW; a++) {
			for (int b = 0; b < iH; b++) {

				try {

					int indexBarvyPixel = indexBarvyVysky(dataVysek[a][b]);
					int indexBarvySoused1 = indexBarvyVysky(dataVysek[a + 1][b]);
					int indexBarvySoused2 = indexBarvyVysky(dataVysek[a - 1][b]);
					int indexBarvySoused3 = indexBarvyVysky(dataVysek[a][b + 1]);
					int indexBarvySoused4 = indexBarvyVysky(dataVysek[a][b - 1]);
					
					
					
					VrstenviceSour souradniceVrstevnice = new VrstenviceSour(a, b);

					if (indexBarvyPixel > indexBarvySoused1) {
						
						
						poleSouradnicVrst[indexBarvyPixel].add(souradniceVrstevnice);

					} else if (indexBarvyPixel > indexBarvySoused2) {
						
						poleSouradnicVrst[indexBarvyPixel].add(souradniceVrstevnice);

					} else if (indexBarvyPixel > indexBarvySoused3) {
						
						poleSouradnicVrst[indexBarvyPixel].add(souradniceVrstevnice);

					} else if (indexBarvyPixel > indexBarvySoused4) {
						
						poleSouradnicVrst[indexBarvyPixel].add(souradniceVrstevnice);

					}

				} catch (IndexOutOfBoundsException e) {

				}

			}
		}

	}

	/**
	 * Metoda prijima vysku a vrati koeficient barvy kterou tato vyska musi byt
	 * zabarvena
	 * 
	 * @param vyska
	 * @return
	 */
	public int indexBarvyVysky(int vyska) {
		int index = (int)Math.ceil(vyska/50.0);
		
		return index;
	}

	/**
	 * Method maxSloupani prijima pixels[][], hleda bod maximalniho sloupani a vrati
	 * objekt SouradniceXY maximalniho sloupani
	 * 
	 * @param pixels pole dat obrazku
	 * @return maxSloupaniXY souradnice maximalniho sloupani
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
	 * Metoda kresli zmacknutou vysku na mape
	 * 
	 * @param g2
	 */
	public void vypisZmacVysky(Graphics2D g2, double scale, int startX, int startY) {

		if (souradniceProVypisX != 0 || souradniceProVypisY != 0) {
			if (zmacknutaVyska != -1) {
				double velkostFontu = 15;
				g2.setColor(Color.BLACK);
				Font font = new Font("Times new roman", Font.PLAIN, (int) velkostFontu);
				g2.setFont(font);
				String vypis = Integer.toString(zmacknutaVyska);
				FontMetrics fm = g2.getFontMetrics();

				int delka = (int) fm.getStringBounds(vypis, g2).getWidth();
				int vyska = (int) fm.getStringBounds(vypis, g2).getHeight();

				int x = (int) (souradniceProVypisX * scale) + startX;
				int y = (int) (souradniceProVypisY * scale) + startY;

				if (x + delka > this.getWidth() - startX) {
					x -= delka;
				}
				if (y - vyska < 0) {
					y += vyska;
				}

				g2.drawString(vypis, x, y);

			}
		}

	}

	/**
	 * Method maxVyska prijima pixels[][], hleda bod maximalniho prevyseni a vrati
	 * objekt SouradniceXY souradnice maximalniho prevyseni
	 * 
	 * @param pixels pole datmapy
	 * @return maxVyskaXY souradnice maximalni vyski
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
	public SouradniceXY minPrevyseni(int pixels[][]) {
		int minVyska = 10000000;
		int minVyskaX = 0;
		int minVyskaY = 0;
		int width = pixels.length;
		int height = pixels[0].length;

		for (int a = 0; a < height; a++) {//
			for (int b = 0; b < width; b++) {
				if (pixels[b][a] <= minVyska) {
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
	 * Metoda prijima jednorozmerne pole, zpracovava a vrati dvourozmerne pole
	 * 
	 * @param data []
	 * @return pixels[][]
	 */

	public int[][] arrayToDoubleArray(int[] data) {

		int width = iW;
		int height = iH;

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
	 * Metoda prijima dvourozmerne pole, zpracovava a vrati jednorozmerne pole
	 * 
	 * @param data []
	 * @return pixels[][]
	 */
	public int[] doubleArrayToArray(int[][] data) {

		int width = iW;
		int height = iH;

		int[] pixels = new int[width * height];

		int index = 0;
		for (int a = 0; a < height; a++) {
			for (int b = 0; b < width; b++) {
				pixels[index] = data[b][a];

				index++;

			}
		}

		return pixels;
	}

	/**
	 * Metoda prijima souradnice bodu, vyvolava metodu vypocetSouradniceX1Y1, a v
	 * pripade, kdyz bod zacatku sibky se nachazi na obrazku, vykresluje sibku
	 * 
	 * @param x2
	 * @param y2
	 * @param g2
	 * @param scale
	 * @param startX
	 * @param startY
	 */
	private void drawArrow(int x2, int y2, Graphics2D g2, double scale, int startX, int startY, int width, int height) {

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

		double v_x = u_y;
		double v_y = -u_x;

		v_x *= 0.5 * tip_length;
		v_y *= 0.5 * tip_length;

		double c_x = x2 - u_x * tip_length;
		double c_y = y2 - u_y * tip_length;

		if (x1 > startX && x1 < width + startX) {
			if (y1 > 0 && y1 < height) {

				g2.draw(new Line2D.Double(x1, y1, x2, y2));
				g2.setStroke(new BasicStroke(3));
				g2.draw(new Line2D.Double(c_x + v_x, c_y + v_y, x2, y2));
				g2.draw(new Line2D.Double(c_x - v_x, c_y - v_y, x2, y2));

			}
		}

	}

	/**
	 * Metoda vypisuje nadpis na obrazek
	 * 
	 * @param x2     souradnice X konce sibky
	 * @param y2     souradnice Y konce sibky
	 * @param nadpis Text, ktery bude vypsan
	 * @param g2     Graphics2D g2
	 * @param scale  Scalovani obrazku
	 * @param startX Posuv obrazku
	 * @param startY Posuv obrazku
	 */
	private void drawDesc(int x2, int y2, String nadpis, Graphics2D g2, double scale, int startX, int startY) {

		x2 = (int) (x2 * scale + startX);
		y2 = (int) (y2 * scale + startY);

		SouradniceXY souradniceX2Y2 = vypocetSouradniceX1Y1(x2, y2, startX, startY, scale);

		int x1 = souradniceX2Y2.getX();
		int y1 = souradniceX2Y2.getY();

		double velkostFontu = 15;
		g2.setColor(Color.BLACK);
		Font font = new Font("Times new roman", Font.PLAIN, (int) velkostFontu);
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

		if (sourPopisYInt > startY && sourPopisY < windowsHeight - startY) {
			if (delka < (windowsWidth - 2 * (startX))) {
				while (sourPopisXInt <= startX) {

					sourPopisXInt += 1;
				}
				while (sourPopisXInt + delka > windowsWidth - startX) {
					sourPopisXInt -= 1;
				}
				g2.drawString(nadpis, sourPopisXInt, sourPopisYInt);

			}
		}

	}

	/**
	 * Metoda prijima souradnice bodu, a spocita souradnice zacutku sibky.
	 * 
	 * @param x2 souradnice X konce sibky
	 * @param y2 souradnice Y konce sibky
	 * @return vypoceSouradniceXY
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

	/**
	 * Metoda vrati vysku, ktera se nachazi na techto souradnicich
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int getVyska(int x, int y) {
		int[][] doubleData = arrayToDoubleArray(nezpracovanaData);

		try {
			return doubleData[x][y];
		} catch (IndexOutOfBoundsException e) {
			return -1;
		}

	}

	/**
	 * Metoda vrati vysku, ktera se nachazi na techto souradnicich pracuje s polem
	 * zpracovanych vysek (prevedene do formatu 0-255)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int getZpracovanaVyska(int x, int y) {
		int[][] doubleData = arrayToDoubleArray(data);

		try {
			return doubleData[x][y];
		} catch (IndexOutOfBoundsException e) {
			return -1;
		}

	}
	
	
	public void setWidth(int iW) {
		this.iW = iW;

	}

	public void setHeight(int iH) {
		this.iH = iH;
	}
	
	public void setOtevreneVrs(boolean[] otevreneVrst) {
		this.otevreneVrst = otevreneVrst;
	}

	public void setMaxHodnota(int maxHodnota) {
		this.maxHodnota = maxHodnota;
	}
	public void setMinHodnota(int minHodnota) {
		this.minHodnota = minHodnota;
	}

	public void setKrokVysky(double krokVysky) {
		this.krokVysky = krokVysky;
	}

	public void setPoleBarev(Color[] poleBarev) {
		this.poleBarev = poleBarev;
	}

	public void setPocetBarev(int pocetBarev) {
		this.pocetBarev = pocetBarev;
	}

	public void setImage(BufferedImage bg_img) {
		this.bg_img = bg_img;
		
	}

	public void setData(int[] data) {
		this.data = data;
	}

	public void setNezpracovaneData(int[] nezpracovanaData) {
		this.nezpracovanaData = nezpracovanaData;
	}

}