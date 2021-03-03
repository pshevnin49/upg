import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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
	
//	int newWidth = (width * 2);
//	int newHeight = (height * 2);
//	int[][] newPixels = polace(pixels);

	public DrawingPanel() throws FileNotFoundException {
		this.setPreferredSize(new Dimension(width, height));
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		System.out.println(width);
		System.out.println(height);
		
		if(width < 800 && height < 600) {
			pixels = polace(pixels);
			width = pixels.length;
			height = pixels[0].length;
		}
		
		for (int a = 0; a < height; a++) {//
			for (int b = 0; b < width; b++) {
				
				if(pixels[b][a] > maxVyska) {
					maxVyska = pixels[b][a];
					maxVyskaX = b;
					maxVyskaY = a;
					
				}
				if (maxColor > 255) {
					double koef = maxColor / 255;
					koef++;
					int color = (int) (pixels[b][a] / koef);
					g.setColor(new Color(color, color, color));
					g.fillRect(b, a, 1, 1);
				} else if (maxColor < 255) {
					double koef = 255 / maxColor;
					koef++;
					int color = (int) (pixels[b][a] * koef);
					g.setColor(new Color(color, color, color));
					g.fillRect(b, a, 1, 1);
				} else {
					g.setColor(new Color(pixels[b][a], pixels[b][a], pixels[b][a]));
					g.fillRect(b, a, 1, 1);
				}
			}
		}
		g.drawLine(maxVyskaX, maxVyskaY, width/2, height/2);
		System.out.println(maxVyska);
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

}
