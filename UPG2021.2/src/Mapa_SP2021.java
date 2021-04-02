 package src;

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

	public static void main(String[] args) throws FileNotFoundException {

		JFrame okno = new JFrame();

		okno.setTitle("Pavel Shevnin A20B0231P");
		okno.setSize(620, 480);
		
		DrawingPanel panel = new DrawingPanel();
		okno.add(panel);//prida komponentu
		
		okno.pack(); // prepocte velikost okna

		okno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		okno.setLocationRelativeTo(null);// posice okna centrum
		okno.setVisible(true);

	}

	
}
