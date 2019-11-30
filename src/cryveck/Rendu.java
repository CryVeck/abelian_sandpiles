package cryveck;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Rendu {
	
	public static final int SIZE=5;
	
	public static void save (String nom, int[] tab, int width, int height, int maxColor) {
		File nomfichier = new File("/home/grothendieck/out/"+nom + ".png");
		BufferedImage bi = new BufferedImage(width*SIZE, height*SIZE, BufferedImage.TYPE_3BYTE_BGR);
		render(bi.getGraphics(), tab, SIZE, width, height, maxColor);
		try {
			ImageIO.write(bi, "PNG", nomfichier);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void render(Graphics g, int[] tab, int size, int width, int height, int maxColor) {
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++) {
				g.setColor(getColor(tab[i + j*width + 1], maxColor, 0));
				g.fillRect(i*size, j*size, size, size);
			}
	}
	
	public static Color getColor(double value, int max, int min) {
		double map = (double) (Math.max(Math.min(value, max) - min, 0)/(max - min)*255);
		double facteur = 255/85;
		if (map < 85) {
			return new Color(0, (int) (map*facteur), 255);
		} else if (map < 170) {
			return new Color((int) ((map-85)*facteur), 255, (int) (255 - (map-85)*facteur));
		} else {
			return new Color(255, (int) (255 - (map-170)*facteur), 0);
		}
	}
	
	public static void printMat(int[][] M) {
		for (int i = 0; i < M.length; i++) {
			System.out.println();
			for (int j = 0; j < M[0].length; j++) {
				System.out.print(" " + M[i][j]);
			}
		}
	}
	
	public static void printMat(boolean[][] M) {
		for (int i = 0; i < M.length; i++) {
			System.out.println();
			for (int j = 0; j < M[0].length; j++) {
				System.out.print(" " + M[i][j]);
			}
		}
	}
	
	public static void printMat(boolean[] M, int width) {
		for (int j = 0; j < M.length; j++) {
			System.out.print(" " + (M[j] ? 1 : 0));
			if (j % width == 0) System.out.println();
		}
		System.out.println();
	}
	

	public static void printMat(int[] M, int width) {
		for (int j = 0; j < M.length; j++) {
			System.out.print(" " + M[j]);
			if (j % width == 0) System.out.println();
		}
		System.out.println();
	}
	

	public static void printMat(int[] M) {
		for (int j = 0; j < M.length; j++) {
			System.out.print(" " + M[j]);
		}
		System.out.println();
	}
	
	public static void printMat(boolean[] M) {
		for (int j = 0; j < M.length; j++) {
			System.out.print(" " + M[j]);
		}
		System.out.println();
	}
}
