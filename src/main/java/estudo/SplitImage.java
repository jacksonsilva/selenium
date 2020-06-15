package estudo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

public class SplitImage {

	public static void main(String[] args) throws IOException {

		String path = "C:\\Users\\dell\\AppData\\Local\\Temp\\screenshot\\input.jpg";
		File filePath = new File(path);

		SplitImage splitImage = new SplitImage();
		
		splitImage.readImageLocal(filePath);
		
		//29 143
		/*
		 * BufferedImage cropImage = splitImage.cropImage(filePath, 10, 60, 1, 1);
		 * 
		 * 
		 * String pathOut =
		 * "C:\\Users\\dell\\AppData\\Local\\Temp\\screenshot\\out.png"; File
		 * filePathOut = new File(pathOut); ImageIO.write(cropImage, "png",
		 * filePathOut);
		 */

	}

	private static BufferedImage cropImage(File filePath, int x, int y, int w, int h) {

		try {
			BufferedImage originalImgage = ImageIO.read(filePath);
			
			ImageIO.read(filePath).getHeight();
		    System.out.println(originalImgage.getHeight()); 
		    System.out.println(originalImgage.getWidth());
		    
		    int height = originalImgage.getHeight(); 
		    int width = originalImgage.getWidth();
		    
		    //Point point = element.getLocation();
		    //int elementWidth = element.getSize().getWidth(); 
		    //int elementHeight = element.getSize().getHeight();
		    
		    // Now no exception here
		    // BufferedImage elementScreenshot= fullImg.getSubimage(220, 170,elementWidth+150,elementHeight+100);

			
			
			BufferedImage subImgage = originalImgage.getSubimage(height, width, w, h);

			return subImgage;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static float getDiff(File f1, File f2, int width, int height) throws IOException {
		BufferedImage bi1 = null;
		BufferedImage bi2 = null;
		bi1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		bi2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		bi1 = ImageIO.read(f1);
		bi2 = ImageIO.read(f2);
		
		float diff = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int rgb1 = bi1.getRGB(i, j);
				int rgb2 = bi2.getRGB(i, j);

				int b1 = rgb1 & 0xff;
				int g1 = (rgb1 & 0xff00) >> 8;
				int r1 = (rgb1 & 0xff0000) >> 16;

				int b2 = rgb2 & 0xff;
				int g2 = (rgb2 & 0xff00) >> 8;
				int r2 = (rgb2 & 0xff0000) >> 16;

				diff += Math.abs(b1 - b2);
				diff += Math.abs(g1 - g2);
				diff += Math.abs(r1 - r2);
			}
		}
		return diff;

	}

	public void readImageLocal(File file) {
		ITesseract ITesseract = new Tesseract();
		ITesseract.setLanguage("eng");
		ITesseract.setDatapath("./tessdata");

		String doOCR = "";

		try {

			doOCR = ITesseract.doOCR(file);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(doOCR);
	}

}
