package captcha;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import captcha.api.TwoCaptchaService;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import poi.ReadWordTemplate;

public class ReadCaptcha {
	int WAIT_DEFAULT;
	WebDriver driver;
	WebDriverWait wait;
	ReadWordTemplate read;

	public static void main(String[] args) {
		ReadCaptcha webScrapping = new ReadCaptcha();
		// webScrapping.readImage();
		
		List<Integer> pids = new ArrayList<Integer>();

		try {

			String out;
			Process p = Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq chromedriver.exe*\"");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

			while ((out = input.readLine()) != null) {
				if (out.length() > 36) {
					String processPID = out.substring(27, 35).trim();
					
					//String[] items = out.split(" ");
					//if (processPID.length > 1 && StringUtils.isNumeric(processPID)) {
					if (StringUtils.isNumeric(processPID)) {
						//pids.add(NumberUtils.toInt(processPID));
						Runtime.getRuntime().exec("taskkill /F /PID " + processPID);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.setProperty("webdriver.chrome.driver", "c:\\rb_process\\chromedriver.exe");
		ChromeOptions opt = new ChromeOptions();
		opt.setPageLoadStrategy(PageLoadStrategy.NORMAL);
		webScrapping.init();

		// Initialize browser webScrapping.readImage();
	}

	public void readImage() {
		ITesseract ITesseract = new Tesseract();
		ITesseract.setLanguage("eng");
		ITesseract.setDatapath("./tessdata");

		try {
			String path = "C:\\Users\\dell\\AppData\\Local\\Temp\\screenshot\\nova.png";
			File file = new File(path);

			File outputfile2 = null;
			File outputfile = null;

			try {
				BufferedImage read2 = ImageIO.read(file);
				// makeGray(read2);
				BufferedImage xx = getGrayscaledImage(read2);

				outputfile = new File("C:\\Users\\dell\\AppData\\Local\\Temp\\screenshot\\saved.png");
				ImageIO.write(xx, "png", outputfile);

				BufferedImage xx2 = applyAlphaChannel(read2, Color.WHITE);

				outputfile2 = new File("C:\\Users\\dell\\AppData\\Local\\Temp\\screenshot\\saved_x2.png");
				ImageIO.write(xx2, "png", outputfile2);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String doOCR = ITesseract.doOCR(file);

			System.out.println(doOCR);

		} catch (TesseractException e) {
			e.printStackTrace();
		}

	}

	public static BufferedImage getGrayscaledImage(BufferedImage img) {

		ImageFilter filter = new ImageFilter() {
			public final int filterRGB(int x, int y, int rgb) {
				// TODO - optimization? Bit shifts, not this shits
				Color currentColor = new Color(rgb);
				if (currentColor.getRed() < 2 && currentColor.getGreen() < 2 && currentColor.getBlue() < 2) {
					return new Color(rgb).darker().getRGB();
				}

				return Color.WHITE.getRGB();
			}
		};

		ImageProducer producer = new FilteredImageSource(img.getSource(), filter);
		Image image = Toolkit.getDefaultToolkit().createImage(producer);
		return toBufferedImage(image);

	}

	private static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_BYTE_GRAY);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

	/**
	 * All pixels that have the specified color are rendered transparent.
	 *
	 * @param img   the img
	 * @param color the color
	 * @return the image
	 */
	public static BufferedImage applyAlphaChannel(final BufferedImage img, final Color color) {
		if (color == null || img == null) {
			return img;
		}

		final ImageFilter filter = new RGBImageFilter() {

			// the color we are looking for... Alpha bits are set to opaque
			public final int markerRGB = color.getRGB() | 0xFF000000;

			@Override
			public final int filterRGB(final int x, final int y, final int rgb) {
				if ((rgb | 0xFF000000) == this.markerRGB) {
					// Mark the alpha bits as zero - transparent
					return 0x00FFFFFF & rgb;
				} else {
					// nothing to do
					return rgb;
				}
			}
		};

		final ImageProducer ip = new FilteredImageSource(img.getSource(), filter);
		Image createImage = Toolkit.getDefaultToolkit().createImage(ip);
		return toBufferedImage(createImage);
	}

	public static void makeGray(BufferedImage img) {
		for (int x = 0; x < img.getWidth(); ++x)
			for (int y = 0; y < img.getHeight(); ++y) {
				int rgb = img.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb & 0xFF);

				// Normalize and gamma correct:
				float rr = (float) Math.pow(r / 255.0, 2.2);
				float gg = (float) Math.pow(g / 255.0, 2.2);
				float bb = (float) Math.pow(b / 255.0, 2.2);

				// Calculate luminance:
				float lum = (float) (0.2126 * rr + 0.7152 * gg + 0.0722 * bb);

				// Gamma compand and rescale to byte range:
				int grayLevel = (int) (255.0 * Math.pow(lum, 1.0 / 2.2));
				int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel;
				img.setRGB(x, y, gray);
			}
	}

	public void init() {
		Map<ReadWordTemplate.TEMPLATE_WORD_VARIABLES, String> values = new HashMap<>();

		WAIT_DEFAULT = 100; // Em segundos;
		driver = new ChromeDriver();
		wait = new WebDriverWait(this.driver, Duration.ofSeconds(WAIT_DEFAULT));
		read = new ReadWordTemplate();

		String fieldCpfCnpjEmitente;
		String fieldPrimeiroCampoCmc7;
		String fieldSegundoCampoCmc7;
		String fieldTerceiroCampoCmc7;
		String fieldCpfCnpjInteressado;

		try {

			File fileOrigin = new File("./cheques.txt");
			FileReader fr = new FileReader(fileOrigin); // reads the file
			BufferedReader br = new BufferedReader(fr); // creates a buffering character input stream
			StringBuffer sb = new StringBuffer(); // constructs a string buffer with no characters
			String line;

			driver.get("https://www.chequelegal.com.br/");
			WebElement linkButtonConsultar = getWebElementOfXPath("//*[@id='Table_01']/tbody/tr[1]/td[4]/a/img");
			linkButtonConsultar.click();
			// driver.navigate().to("https://www.chequelegal.com.br/");

			WebElement cpfCnpjEmitente = getWebElementOfId("cpfCnpjEmitente");
			WebElement primeiroCampoCmc7 = getWebElementOfId("primeiroCampoCmc7");
			WebElement segundoCampoCmc7 = getWebElementOfId("segundoCampoCmc7");
			WebElement terceiroCampoCmc7 = getWebElementOfId("terceiroCampoCmc7");
			WebElement cpfCnpjInteressado = getWebElementOfId("cpfCnpjInteressado");
			WebElement aceiteTermoUso = getWebElementOfId("aceiteTermoUso");
			WebElement captcha = getWebElementOfId("captcha"); // Campo para preencher com o Captcha
			WebElement textCaptchaType = getWebElementOfId("textCaptchaType"); // Campo onde indica o que precisa fazer
																				// com o Captcha
			WebElement btnEnviarConsulta = getWebElementOfId("btEnviar");

			while ((line = br.readLine()) != null) {
				String[] fields = line.split(";");

				fieldCpfCnpjEmitente = fields[0];
				fieldPrimeiroCampoCmc7 = fields[1];
				fieldSegundoCampoCmc7 = fields[2];
				fieldTerceiroCampoCmc7 = fields[3];
				fieldCpfCnpjInteressado = fields[4];

				/*
				 * cpfCnpjEmitente.sendKeys("35081907000107");
				 * primeiroCampoCmc7.sendKeys("03336073");
				 * segundoCampoCmc7.sendKeys("0180000895");
				 * terceiroCampoCmc7.sendKeys("091130325532");
				 * cpfCnpjInteressado.sendKeys("21715898842");
				 */

				cpfCnpjEmitente.sendKeys(fieldCpfCnpjEmitente);
				primeiroCampoCmc7.sendKeys(fieldPrimeiroCampoCmc7);
				segundoCampoCmc7.sendKeys(fieldSegundoCampoCmc7);
				terceiroCampoCmc7.sendKeys(fieldTerceiroCampoCmc7);
				cpfCnpjInteressado.sendKeys(fieldCpfCnpjInteressado);

				String path = "C:\\Users\\dell\\AppData\\Local\\Temp\\screenshot\\" + fieldCpfCnpjEmitente + ".png";
				this.fileOriginCaptcha = new File(path);
				
				aceiteTermoUso.click();
				captcha.click();
				Thread.sleep(1000);
				((JavascriptExecutor) driver).executeScript("reloadCipCaptcha();");
				((JavascriptExecutor) driver).executeScript("checkReloadCaptcha();");
				Thread.sleep(3000);
				this.instructionCaptcha = textCaptchaType.getText();
				WebElement cipCaptchaImg = getWebElementOfId("cipCaptchaImg");
				
				/*
				 * List<WebElement> elementosTabelasCaptcha =
				 * cipCaptchaImg.findElements(By.xpath("//*[@id=\"cipCaptchaImg\"]/tr"));
				 * 
				 * while (elementosTabelasCaptcha.size() < 1) { elementosTabelasCaptcha =
				 * cipCaptchaImg.findElements(By.xpath("//*[@id=\"cipCaptchaImg\"]/tr")); }
				 */

				File srcCaptcha = cipCaptchaImg.getScreenshotAs(OutputType.FILE);
				FileHandler.copy(srcCaptcha, this.fileOriginCaptcha);

				String resulCaptcha = solveCaptach();
				//String resulCaptcha ="";
				captcha.sendKeys(resulCaptcha);
				btnEnviarConsulta.click();

				System.out.println("resulCaptcha" + resulCaptcha);
				
				WebElement errors = getWebElementOfId("errors"); // Campo onde indica o que precisa fazer
				List<WebElement> erros = errors.findElements(By.xpath("//*[@id=\\\"errors\\\"]/ul"));
				
				for (WebElement erro : erros) {
					
					System.out.println(erro.getText());
					
				}

				System.out.println("opa");
			}

			JOptionPane optionPane = new JOptionPane("Processamento concluído!", JOptionPane.INFORMATION_MESSAGE);
			JDialog dialog = optionPane.createDialog("Informação!");
			dialog.setAlwaysOnTop(true);
			dialog.setVisible(true);

			Thread.sleep(3000);
			//driver.close();
			//driver.quit();

			// System.exit(0);

		} catch (Exception e) {
			driver.close();
			driver.quit();
			e.printStackTrace();
			System.exit(1);
		}

	}

	private WebElement getWebElementOfId(String id) {

		return this.wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
	}

	private List<WebElement> getWebElementsOfId(String id) {

		return this.wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(id)));
	}

	private static String getStringOfXPath(WebElement element, String xPath) {
		return element.findElement(By.xpath(xPath)).getText();
	}

	private WebElement getWebElementOfXPath(String xPath) {
		return this.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xPath)));

	}

	private static String getStringOfId(WebElement element, String id) {
		return element.findElement(By.id(id)).getText();
	}

	private void fillDocument(Map<ReadWordTemplate.TEMPLATE_WORD_VARIABLES, String> values) throws Exception {
		read.setValues(values);
		read.init();
	}

	public static void takeSnapShot(WebDriver webdriver, String fileWithPath) throws Exception {

		// Convert web driver object to TakeScreenshot

		TakesScreenshot scrShot = ((TakesScreenshot) webdriver);

		// Call getScreenshotAs method to create image file

		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);

		// Move image file to new destination

		File DestFile = new File(fileWithPath);

		// Copy file at destination

		// FileUtils.copyFile(SrcFile, DestFile);

	}

	/**
	 * Pela descricao do campo onde informa as instrucoes para preencher o Captcha
	 * <p/>
	 * eh possivel identificar se precisa realizar calculo ou simplesmente
	 * "digitar".
	 * 
	 * @param description
	 * @return 0-Digitar o valor do campo.\n1-Resolver Conta.
	 */
	private int typeOfCaptcha(String description) {

		if (description.toLowerCase().contains("resolva a conta")) {
			return 1;
		}

		return 0;

	}

	private File fileOriginCaptcha;
	private String instructionCaptcha;

	private String solveCaptach() {
		String result = "";
		String apiKey = "65e6bab2c89058ace4091fb491231334";
		String methodBase64 = "base64";

		try {
			TwoCaptchaService service = new TwoCaptchaService();
			service.setImage(fileOriginCaptcha);
			service.setApiKey(apiKey);
			service.setMethod(methodBase64);
			service.setTextinstructions("Enter the code from above");
			service.setPhrase("1");

			int tipoNumerico = typeOfCaptcha(instructionCaptcha);
			service.setCalc(tipoNumerico);

			if (tipoNumerico == 1) {
				service.setNumeric(4);
			}

			Random random = new Random();
			int randomInteger = (random.nextInt() < 0 ? (-1 * random.nextInt()) : random.nextInt());
			service.setSoftId(randomInteger);

			// hw.coordinatescaptcha = "1"; //Somente para canvas
			// hw.imginstructions = instructionCaptcha; //Somente para ReCaptcha

			result = service.solveCaptcha();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

}