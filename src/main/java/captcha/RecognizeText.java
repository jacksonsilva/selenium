package captcha;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;
import static org.opencv.imgproc.Imgproc.Canny;
import static org.opencv.imgproc.Imgproc.blur;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.erode;
import static org.opencv.imgproc.Imgproc.getStructuringElement;

import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class RecognizeText {

	// Source path content images
		static String SRC_PATH = "F:\\bender\\tmp\\";
		static String TESS_DATA = "C:\\Program Files\\Tesseract-OCR\\tessdata";
		
		// Create tess obj
		static Tesseract tesseract = new Tesseract();
		
		// Load OPENCV
		static {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			tesseract.setDatapath(TESS_DATA);
		}
		
		
		String extractTextFromImage(Mat inputMat) {
			String result = "";
			Mat gray = new Mat();
			
			// Convert to gray scale
			cvtColor(inputMat, gray, COLOR_BGR2GRAY);
			imwrite(SRC_PATH + "gray.png", gray);
			
			//  Apply closing, opening
			Mat element = getStructuringElement(MORPH_RECT, new Size(2, 2), new Point(1, 1));
			dilate(gray, gray, element);
			erode(gray, gray, element);

			imwrite(SRC_PATH + "closeopen.png", gray);
			
			
			Mat canny = new Mat();
			Mat detectedEdges = new Mat();
			cvtColor(inputMat, canny, COLOR_BGR2GRAY);
			blur(canny, detectedEdges, new Size(3, 3));
			
			imwrite(SRC_PATH + "canny.png", canny);
			imwrite(SRC_PATH + "detectedEdges.png", detectedEdges);
			
			int threshold = 0;
			
			Canny(detectedEdges, detectedEdges, threshold, threshold * 3, 3, false);
			
			//dst = new Mat(src.size(), CvType.CV_8UC3, Scalar.all(0));
			

			try {
				// Recognize text with OCR
				result = tesseract.doOCR(new File(SRC_PATH + "gray.png"));
				
				String result2 = tesseract.doOCR(new File(SRC_PATH + "closeopen.png"));
				String replace = result2.trim().replace("\n", "");
				System.out.println("[" + replace + "]");
				
				
				result2 = tesseract.doOCR(new File(SRC_PATH + "canny.png"));
				replace = result2.trim().replace("\n", "");
				System.out.println("[" + replace + "]");
				
				result2 = tesseract.doOCR(new File(SRC_PATH + "detectedEdges.png"));
				replace = result2.trim().replace("\n", "");
				System.out.println("[" + replace + "]");
				
			} catch (TesseractException e) {
				e.printStackTrace();
			}

			return result;
		}
		
		
		public static void main(String[] args) {
			System.out.println("Start recognize text from image");
			long start = System.currentTimeMillis();
		
			// Read image
			Mat origin = imread(SRC_PATH + "20100087342.jpg");
			
			String result = new RecognizeText().extractTextFromImage(origin);
			System.out.println(result);
			
			System.out.println("Time");
			System.out.println(System.currentTimeMillis() - start);
			System.out.println("Done");

		}
		
		
	}