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
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYDataset;

public class Mapa_SP2021 {

	private static int[] dataProGraf;
	private static int maxHodnota;
	private static int minHodnota = 1000000;

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
				prvniGraf1();

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
	 * BufferedImage taky zadava spoustu setteru pro panel
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
			if (minHodnota > hodnota) {
				minHodnota = hodnota;
			}

		}

		
		int pocetBarev = ((int) (maxColor / 50) + 2);

		panel.setMaxHodnota(maxColor);

		maxHodnota = maxColor;
		
		panel.setMinHodnota(minHodnota);
		

		panel.setPocetBarev(pocetBarev);
		
		boolean[] otevreneVrstevnice = new boolean[pocetBarev];

		for (int i = 0; i < pocetBarev; i++) {
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
	/**
	 * Metoda tvori okno pro graf histogram prevyseni, v okne vykresluje JFrechart graf
	 */
	private static void histogramGraf() {
		JFrame win = new JFrame();
		win.setTitle("Histogram prevyseni");

		
		ChartPanel panel = new ChartPanel(histogram(dataProGraf));
		win.add(panel);

		win.pack();
		win.setLocationRelativeTo(null);
		win.setVisible(true);

	}
	/**
	 * Metoda tvori okno pro prvni graf z ukolu prevyseni, v okne vykresluje JFrechart graf
	 */
	private static void prvniGraf1() {
		JFrame win1 = new JFrame();
		win1.setTitle("Graf");

		
		ChartPanel panel = new ChartPanel(prvniGraf());
		win1.add(panel);

		win1.pack();
		win1.setLocationRelativeTo(null);
		win1.setVisible(true);

	}

	/**
	 * Metoda tvori histogram vysek pomoci knihovny Jfreechart
	 * @param data
	 * @return
	 */
	private static JFreeChart histogram(int[] data) {

		double dataDouble[] = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			dataDouble[i] = data[i];
		}

		HistogramDataset dataset = new HistogramDataset();

		dataset.addSeries("Pocet bodu", dataDouble, 15);

		JFreeChart chart = ChartFactory.createHistogram("Histogram", "Data", "Pocet bodu", dataset);

		XYPlot plot = chart.getXYPlot();

		plot.setBackgroundPaint(Color.WHITE);

		XYItemRenderer render = plot.getRenderer();

		return chart;
	}
	/**
	 * Metoda tvori 1. graf z ukolu pomoci knihovny Jfreechart
	 * @return
	 */
	private static JFreeChart prvniGraf() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(maxHodnota, "Mapa", "Maximalni hodnota");
		dataset.addValue(minHodnota, "Mapa", "Minimalni hodnota");
		dataset.addValue(medianPrevyseni(dataProGraf), "Mapa", "Median prevyseni");
		dataset.addValue(horniKvartil(dataProGraf), "Mapa", "Horni kvartil");
		dataset.addValue(dolniKvartil(dataProGraf), "Mapa", "Dolni kvartil");

		JFreeChart chart = ChartFactory.createBarChart("Graf pro mapu", "Hodnota", "Vyska v metrech", dataset);

		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setRangeMinorGridlinePaint(Color.LIGHT_GRAY);
		plot.setRangeGridlinesVisible(true);
		plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);

		CategoryItemRenderer render = plot.getRenderer();

		render.setDefaultItemLabelGenerator(
				new StandardCategoryItemLabelGenerator("{2}m", NumberFormat.getIntegerInstance()));
		render.setDefaultItemLabelFont(new Font("Calibri", Font.PLAIN, 11));

		render.setDefaultItemLabelsVisible(true);

		BarRenderer br = (BarRenderer) render;
		br.setItemMargin(0.2);
		br.setBarPainter(new StandardBarPainter());

		return chart;

	}
	/**
	 * Metoda spocita median prevyseni
	 * @param data
	 * @return median
	 */
	private static int medianPrevyseni(int[] data) {
		long sum = 0;
		for (int i = 0; i < data.length; i++) {
			sum += data[i];
		}
		return (int) (sum / data.length);
	}

	/**
	 * Metoda spocita dolni kvartil
	 * @param data
	 * @return dolniKvartil
	 */
	private static int dolniKvartil(int[] data) {
		int[] sortedData = sort(data);
		int koef = (data.length/2)/2;
		return sortedData[koef];
	}

	/**
	 * Metoda spocita horni kvartil
	 * @param data
	 * @return horniKvartil
	 */
	private static int horniKvartil(int[] data) {
		
		int[] sortedData = sort(data);
		int ctvrt = (data.length/2)/2;
		int koef = (data.length/2) + ctvrt;
		return sortedData[koef];
	}
	
	/**
	 * Metoda sortuje pole data
	 * @param data
	 * @return sortedData
	 */
	static int[]sort(int[] arr) {
		System.out.println("start sort");
		List <Integer> data = new ArrayList<>();
		
		for(int i = 0; i < arr.length; i++) {
			 data.add(arr[i]);
		}
		
		Collections.sort(data);
		
		for(int i = 0; i < arr.length; i++) {
			arr[i] = data.get(i);
		}
		System.out.println("stop sort");
		return arr;
		
		
	}

}
