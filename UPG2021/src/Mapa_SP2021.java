 

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.JFrame;



public class Mapa_SP2021 {
	/**
	 * Metoda main, tvori instance tridy JPanel, a instance tridy DrawingPanel
	 * @param args
	 * @throws FileNotFoundException
	 */
	
	
	public static void main(String[] args) throws FileNotFoundException {

		JFrame okno = new JFrame();
		
		okno.setTitle("Pavel Shevnin A20B0231P");
		okno.setSize(620, 480);
		
		DrawingPanel panel = new DrawingPanel();
		okno.add(panel);//prida komponentu
		//nacteniPGM(args[0], panel);
		nacteniPGM("data\\data_plzen.pgm", panel);
		
		okno.pack(); // prepocte velikost okna

		okno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		okno.setLocationRelativeTo(null);// posice okna centrum
		okno.setVisible(true);

	}
	
	/**
	 * Metoda prijima jmeno souboru, a nacita pgm p2 data. Zpracovava data a vrati
	 * BufferedImage
	 * 
	 * @param jmenoSouboru
	 * @return img // obrazek BufferedImage
	 * @throws FileNotFoundException
	 */
	public static void nacteniPGM(String jmenoSouboru, DrawingPanel panel) throws FileNotFoundException {
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
		int[] nezpracovanaData = new int [width * height];

		for (int i = 0; i < pixels.length; i++) {
			
			int hodnota = scn.nextInt();
			if (maxColor > 255) {
				double koef = maxColor / 255;
				koef++;

				int color = (int) (hodnota / koef);
				pixels[i] = color;
			} else if (maxColor < 255) {
				double koef = 255 / maxColor;
				koef++;
				int color = (int) (hodnota * koef);
				pixels[i] = color;
			} else {
				pixels[i] = hodnota;
			}
			nezpracovanaData[i] = hodnota;
			
		}
		
		
		
		
		double krokVysky = (255.0 / maxColor) * 50;
		int pocetBarev = (int) (255/krokVysky);
		panel.setMaxHodnota(maxColor);
		panel.setKrokVysky(krokVysky);
		panel.setPocetBarev(pocetBarev);
		panel.setPoleBarev(panel.getBarvy(pocetBarev));
		
		
		
		
		panel.setData(pixels);
		panel.setNezpracovaneData(nezpracovanaData);
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		img.setRGB(0, 0, width, height, pixels, 0, width);
		panel.setImage(img);
	}

	
}
