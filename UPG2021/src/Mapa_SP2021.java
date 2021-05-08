
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

public class Mapa_SP2021 {
	
	private static int [] dataProGraf;
	private static int maxHodnota;
	/**
	 * Metoda main, tvori instance tridy JPanel, a instance tridy DrawingPanel
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */

	public static void main(String[] args) throws FileNotFoundException {

		JFrame okno = new JFrame();

		okno.setTitle("Pavel Shevnin A20B0231P");
		okno.setSize(900, 700);

		DrawingPanel panel = new DrawingPanel();
		okno.add(panel);// prida komponentu
		// nacteniPGM(args[0], panel);
		nacteniPGM("data\\data_plzen.pgm", panel);
		JPanel buttonPanel = new JPanel();

		JButton btnHist = new JButton("Histogram");
		JButton btnGraf = new JButton("Graf");
		
		buttonPanel.add(btnHist, BorderLayout.WEST);
		buttonPanel.add(btnGraf, BorderLayout.EAST);
		
		
		btnHist.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("histogram");
				histogramGraf();
				
			}
		});
		btnGraf.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("graf");
				
				
			}
		});

		

		okno.add(buttonPanel, BorderLayout.SOUTH);
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
		int[] nezpracovanaData = new int[width * height];

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
		int pocetBarev = ((int) (255 / krokVysky) + 1);

		panel.setMaxHodnota(maxColor);
		
		maxHodnota = maxColor;
		
		
		panel.setKrokVysky(krokVysky);

		panel.setPocetBarev(pocetBarev);
		boolean[] otevreneVrstevnice = new boolean[pocetBarev];
		
		for(int i = 0; i < pocetBarev; i++) {
			otevreneVrstevnice[i] = false;
		}
		panel.setOtevreneVrs(otevreneVrstevnice);
		
		
		panel.setPoleBarev(panel.getBarvy(pocetBarev));

		panel.setData(pixels);

		dataProGraf = nezpracovanaData;
		panel.setNezpracovaneData(nezpracovanaData);

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		img.setRGB(0, 0, width, height, pixels, 0, width);
		panel.setWidth(img.getWidth());
		panel.setHeight(img.getHeight());
		
		panel.prvniZaplneniSour();
		panel.setImage(img);
	}
	
	private static void histogramGraf () {
		JFrame win = new JFrame();
		win.setTitle("Histogram prevyseni");
		
		int[] data = poleVysek();
		ChartPanel panel = new ChartPanel(
				createBarChart(data)
				);
		win.add(panel);
		
		win.pack();
		win.setLocationRelativeTo(null);
		win.setVisible(true);
		
	}
	
	private static int[] poleVysek() {
		int delka = maxHodnota/50;
		int [] poleVysek = new int[delka];
		for(int i = 0; i < delka; i++) {
			poleVysek[i] = 0;
		}
		
		for(int a = 0; a < dataProGraf.length; a++) {
			int koef = 0;
			for(int b = 0; b < delka; b++) {
				if(dataProGraf[a] <= koef && dataProGraf[a] >= koef - 50) {
					poleVysek[b]++;
				}
				koef += 50;
			}
		}
		return poleVysek;
		
	}
	
	private static JFreeChart createBarChart(int [] data) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		for(int a = 0; a < data.length; a++) {
			String vyska1 = Integer.toString(a * 50);
			String vyska2 = Integer.toString((a * 50) + 50);
			String vyskaVal = (vyska1 + "-" + vyska2);
			dataset.addValue(data[a],vyskaVal, vyskaVal );
		}
	
		JFreeChart chart = ChartFactory.createBarChart(
				"Histogram prevyseni", 
				"Vyska", 
				"Pocet bodu ve vysce", 
				dataset);
		
		CategoryPlot plot =	chart.getCategoryPlot();
		
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeMinorGridlinePaint(Color.LIGHT_GRAY);
		plot.setRangeGridlinesVisible(true);
		plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
		
		CategoryItemRenderer render = plot.getRenderer();
		
		render.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getIntegerInstance()));
		render.setDefaultItemLabelFont(new Font("Calibri", Font.PLAIN, 11));
		
		render.setDefaultItemLabelsVisible(true);
		
		BarRenderer br = (BarRenderer)render;
		br.setItemMargin(0.01);
		br.setBarPainter(new StandardBarPainter());
		
		return chart;
	}
	
	
	

}
