package estudo;

import java.awt.image.BufferedImage;
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

public class BotPrec {

	int WAIT_DEFAULT;
	WebDriver driver;
	WebDriverWait wait;
	ReadWordTemplate read;

	public static void main(String[] args) {
		BotPrec webScrapping = new BotPrec();
		
		webScrapping.readImageLocal();

		System.exit(0);
		
		List<Integer> pids = new ArrayList<Integer>();

		try {

			String out;
			Process p = Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq chromedriver.exe*\"");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

			while ((out = input.readLine()) != null) {
				if (out.length() > 36) {
					String processPID = out.substring(27, 35).trim();

					// String[] items = out.split(" ");
					// if (processPID.length > 1 && StringUtils.isNumeric(processPID)) {
					if (StringUtils.isNumeric(processPID)) {
						// pids.add(NumberUtils.toInt(processPID));
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
	
	public void readImageLocal() {
		ITesseract ITesseract = new Tesseract();
		ITesseract.setLanguage("eng");
		ITesseract.setDatapath("./tessdata");

		String doOCR = "";
		
		try {
			String path = "C:\\Users\\dell\\AppData\\Local\\Temp\\screenshot\\4.jpg";
			File file = new File(path);
			doOCR = ITesseract.doOCR(file);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println(doOCR);
	}

	public String readImage() {
		ITesseract ITesseract = new Tesseract();
		ITesseract.setLanguage("eng");
		ITesseract.setDatapath("./tessdata");

		String doOCR = "";
		
		try {
			String path = "C:\\Users\\dell\\AppData\\Local\\Temp\\screenshot\\ololo.jfif";
			File file = new File(path);

			File outputfile2 = null;
			File outputfile = null;

			try {
				BufferedImage read2 = ImageIO.read(file);
				outputfile = new File("C:\\Users\\dell\\AppData\\Local\\Temp\\screenshot\\saved.png");
				outputfile2 = new File("C:\\Users\\dell\\AppData\\Local\\Temp\\screenshot\\saved_x2.png");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			doOCR = ITesseract.doOCR(fileOriginCaptcha);
			//String doOCR = ITesseract.doOCR(file);

			System.out.println(doOCR);

		} catch (TesseractException e) {
			e.printStackTrace();
		}

		return doOCR;
	}

	public void init() {
		Map<ReadWordTemplate.TEMPLATE_WORD_VARIABLES, String> values = new HashMap<>();

		WAIT_DEFAULT = 100; // Em segundos;
		driver = new ChromeDriver();
		wait = new WebDriverWait(this.driver, Duration.ofSeconds(WAIT_DEFAULT));
		read = new ReadWordTemplate();

		String fieldCPF;
		String fieldDataNascimento;
		String fieldNomeMae;
		String fieldCaptcha;

		try {

			File fileOrigin = new File("./proc.txt");
			FileReader fr = new FileReader(fileOrigin); // reads the file
			BufferedReader br = new BufferedReader(fr); // creates a buffering character input stream
			StringBuffer sb = new StringBuffer(); // constructs a string buffer with no characters
			String line;

			driver.get("https://eproc.trf4.jus.br/eproc2trf4/externo_controlador.php?acao=consulta_processos_saldo_prec_rpv");
			
			WebElement optionFilterCPF = getWebElementOfXPath("//*[@id=\"optFiltroCPF\"]");
			optionFilterCPF.click();
			

			WebElement txtCpf = getWebElementOfId("txtCPF");
			WebElement txtNascimento = getWebElementOfId("txtDtaNascimento");
			WebElement txtNomeMae = getWebElementOfId("txtNomeMae");
			WebElement txtCaptcha = getWebElementOfId("txtCaptcha");
			WebElement imgCaptcha = getWebElementOfId("imgCaptcha");
			
			WebElement btnEnviarConsulta = getWebElementOfXPath("/html/body/div[1]/div[2]/div[2]/div/div/form/div[3]/button");

			while ((line = br.readLine()) != null) {
				String[] fields = line.split(";");

				fieldCPF = fields[0];
				fieldDataNascimento = fields[1];
				fieldNomeMae = fields[2];

				txtCpf.sendKeys(fieldCPF);
				txtNascimento.sendKeys(fieldDataNascimento);
				txtNomeMae.sendKeys(fieldNomeMae);

				String path = "C:\\Users\\dell\\AppData\\Local\\Temp\\screenshot\\" + fieldCPF + ".png";
				this.fileOriginCaptcha = new File(path);

				txtCaptcha.click();
				//Thread.sleep(1000);
				//((JavascriptExecutor) driver).executeScript("reloadCipCaptcha();");
				//((JavascriptExecutor) driver).executeScript("checkReloadCaptcha();");
				//Thread.sleep(3000);
				//this.instructionCaptcha = textCaptchaType.getText();
				imgCaptcha = getWebElementOfId("imgCaptcha");

				/*
				 * List<WebElement> elementosTabelasCaptcha =
				 * cipCaptchaImg.findElements(By.xpath("//*[@id=\"cipCaptchaImg\"]/tr"));
				 * 
				 * while (elementosTabelasCaptcha.size() < 1) { elementosTabelasCaptcha =
				 * cipCaptchaImg.findElements(By.xpath("//*[@id=\"cipCaptchaImg\"]/tr")); }
				 */

				File srcCaptcha = imgCaptcha.getScreenshotAs(OutputType.FILE);
				FileHandler.copy(srcCaptcha, this.fileOriginCaptcha);

				String resulCaptcha = readImage();
				//String resulCaptcha = solveCaptach();
				txtCaptcha.sendKeys(resulCaptcha);
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
			// driver.close();
			// driver.quit();

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
			service.setTextinstructions(instructionCaptcha);
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