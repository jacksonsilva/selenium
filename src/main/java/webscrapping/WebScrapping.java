package webscrapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.text.MaskFormatter;
import javax.xml.stream.XMLStreamException;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import config.AppConfig;
import config.model.Config;
import poi.ReadWordTemplate;

public class WebScrapping {

	private Logger logger = LoggerFactory.getLogger(WebScrapping.class);

	int WAIT_DEFAULT = 30; // Em segundos;
	WebDriver driver = new ChromeDriver();

	WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(WAIT_DEFAULT));
	ReadWordTemplate read = new ReadWordTemplate();

	static Config config;

	public static void main(String[] args) {

		try {
			// opt.addArguments("--disable-gpu");

			config = readConfigFile();
			// Initialize browser
			WebScrapping webScrapping = new WebScrapping();
			webScrapping.init();
		} catch (Exception e) {
			JOptionPane optionPane = new JOptionPane(e.getMessage(), JOptionPane.ERROR_MESSAGE);
			JDialog dialog = optionPane.createDialog("Erro!");
			dialog.setAlwaysOnTop(true);
			dialog.setVisible(true);
		}

	}

	private static Config readConfigFile() throws FileNotFoundException, XMLStreamException {

		File fileConfig = new File("./config.xml");

		AppConfig appConfig = new AppConfig(new FileInputStream(fileConfig));
		// AppConfig appConfig = new
		// AppConfig(ReadWordTemplate.class.getResourceAsStream("config.xml"));

		Config config = appConfig.getConfig();

		System.setProperty("webdriver.chrome.driver", config.getChromeDrive());
		ChromeOptions opt = new ChromeOptions();
		opt.setPageLoadStrategy(PageLoadStrategy.NORMAL);

		return config;

	}

	public void init() {

		String outputDirectory;
		StringBuilder fullPathDocumentTemplate;

		try {

			MaskFormatter maskCpf = new MaskFormatter("###.###.###-##");
			MaskFormatter maskCnpj = new MaskFormatter("###.###.###/####-##");

			// Config config = readConfigFile();
			outputDirectory = config.getOutputDirectory();

			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy hhmmss");
			logger.info("Iniciando processo: [" + sdf.format(new Date()) + "]");

			Map<ReadWordTemplate.TEMPLATE_WORD_VARIABLES, String> values = new HashMap<>();

			// driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			driver.get("http://linkservidor.com/credg5.aspx");

			WebElement username = getWebElementOfId("ctl00_PageContent_UserName");
			WebElement password = getWebElementOfId("ctl00_PageContent_Password");
			WebElement buttonOkToLogin = getWebElementOfId("ctl00_PageContent_OKButton__Button");

			username.sendKeys("usuario");
			password.sendKeys("password");

			buttonOkToLogin.click();

			String url = driver.getCurrentUrl();

			String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\:([0-9]{2,5})";

			Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
			Matcher matcher = pattern.matcher(url);
			String ipFinal = "0.0.0.0";
			if (matcher.find()) {
				ipFinal = matcher.group();
			}

			String urlPesquisaContato = "http://" + ipFinal + "/Comercial/PesquisaContato.aspx";
			driver.navigate().to(urlPesquisaContato);
			this.wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_DEFAULT));

			WebElement buttonIr = getWebElementOfId("ctl00_PageContent_SearchButton1__Button");
			buttonIr.click();

			Thread.sleep(5000);
			WebElement tabelaCondominio = getWebElementOfId("PesquisaContatoTableControlGrid");

			List<WebElement> elementosTabelasContatos = tabelaCondominio
					.findElements(By.xpath("//*[@id=\"PesquisaContatoTableControlGrid\"]/tbody/tr"));

			int xContatos = elementosTabelasContatos.size();

			for (int i = 1; i <= xContatos; i++) {
				if (i > 1) {
					driver.navigate().to(urlPesquisaContato);
					buttonIr = getWebElementOfId("ctl00_PageContent_SearchButton1__Button");
					buttonIr.click();
				}

				WebElement buttonVisualizarComercial;
				if (xContatos > 1) {
					buttonVisualizarComercial = getWebElementOfXPath(
							"//*[@id=\"PesquisaContatoTableControlGrid\"]/tbody/tr[" + i
									+ "]/td[1]/input[@type='image']");
				} else {
					buttonVisualizarComercial = getWebElementOfXPath(
							"//*[@id=\"PesquisaContatoTableControlGrid\"]/tbody/tr/td/input[@type='image']");
				}

				buttonVisualizarComercial.click();

				WebElement abaPropostas = getWebElementOfId(
						"__tab_ctl00_PageContent_ComercialRecordControlTabContainer_TabPanel2");
				abaPropostas.click();

				WebElement abaPropostasButtonVisualizar = getWebElementOfId(
						"ctl00_PageContent_ComercialRecordControlTabContainer_TabPanel2_PropostasTableControlRepeater_ctl00_ViewRowButton1");
				abaPropostasButtonVisualizar.click();

				WebElement tableOfCondominio = getWebElementOfId("ctl00_PageContent_PropostasRecordControlPanel");

				String cnpj = getStringOfXPath(tableOfCondominio,
						"/html/body/form/table/tbody/tr[2]/td[2]/table/tbody/tr[3]/td[3]/div[1]/div[2]/table/tbody/tr[1]/td/div/div/table/tbody/tr[3]/td[2]/div/table/tbody/tr/td/div/table/tbody/tr[4]/td[2]");

				maskCnpj.setValueContainsLiteralCharacters(false);
				values.put(ReadWordTemplate.TEMPLATE_WORD_VARIABLES.CNPJ, maskCnpj.valueToString(cnpj));

				String razaoSocial = getStringOfXPath(tableOfCondominio,
						"/html/body/form/table/tbody/tr[2]/td[2]/table/tbody/tr[3]/td[3]/div[1]/div[2]/table/tbody/tr[1]/td/div/div/table/tbody/tr[3]/td[2]/div/table/tbody/tr/td/div/table/tbody/tr[4]/td[5]");
				values.put(ReadWordTemplate.TEMPLATE_WORD_VARIABLES.RAZAO_SOCIAL, razaoSocial);

				String enderecoCondominio = getStringOfXPath(tableOfCondominio,
						"/html/body/form/table/tbody/tr[2]/td[2]/table/tbody/tr[3]/td[3]/div[1]/div[2]/table/tbody/tr[1]/td/div/div/table/tbody/tr[3]/td[2]/div/table/tbody/tr/td/div/table/tbody/tr[5]/td[5]");
				String bairroCondominio = getStringOfXPath(tableOfCondominio,
						"/html/body/form/table/tbody/tr[2]/td[2]/table/tbody/tr[3]/td[3]/div[1]/div[2]/table/tbody/tr[1]/td/div/div/table/tbody/tr[3]/td[2]/div/table/tbody/tr/td/div/table/tbody/tr[6]/td[2]");
				String ufCondominio = getStringOfXPath(tableOfCondominio,
						"/html/body/form/table/tbody/tr[2]/td[2]/table/tbody/tr[3]/td[3]/div[1]/div[2]/table/tbody/tr[1]/td/div/div/table/tbody/tr[3]/td[2]/div/table/tbody/tr/td/div/table/tbody/tr[6]/td[8]");

				String fullAddress = new StringBuilder().append(enderecoCondominio).append(" ").append(bairroCondominio)
						.append(" ").append(ufCondominio).toString();
				values.put(ReadWordTemplate.TEMPLATE_WORD_VARIABLES.ENDERECO_CONDOMINIO, fullAddress);

				String documentoRepresentante = getStringOfXPath(tableOfCondominio,
						"/html/body/form/table/tbody/tr[2]/td[2]/table/tbody/tr[3]/td[3]/div[1]/div[2]/table/tbody/tr[1]/td/div/div/table/tbody/tr[3]/td[2]/div/table/tbody/tr/td/div/table/tbody/tr[10]/td[2]");

				fullPathDocumentTemplate = new StringBuilder(config.getDirectoryPath());

				if (documentoRepresentante != null && !"".equals(documentoRepresentante)) {

					String documentv1 = documentoRepresentante.replace(".", "").replace("/", "").replace("-", "")
							.trim();

					if (documentv1.length() > 11) {

						maskCnpj.setValueContainsLiteralCharacters(false);
						values.put(ReadWordTemplate.TEMPLATE_WORD_VARIABLES.CNPJ_REPRESENTANTE,
								maskCnpj.valueToString(documentv1));

						fullPathDocumentTemplate.append(config.getTemplateJuridica());

					} else {

						maskCpf.setValueContainsLiteralCharacters(false);
						values.put(ReadWordTemplate.TEMPLATE_WORD_VARIABLES.CPF_REPRESENTANTE,
								maskCpf.valueToString(documentv1));

						fullPathDocumentTemplate.append(config.getTemplateFisica());

					}

				}

				String nomeRepresentante = getStringOfXPath(tableOfCondominio,
						"/html/body/form/table/tbody/tr[2]/td[2]/table/tbody/tr[3]/td[3]/div[1]/div[2]/table/tbody/tr[1]/td/div/div/table/tbody/tr[3]/td[2]/div/table/tbody/tr/td/div/table/tbody/tr[10]/td[5]");

				String nomeRepresentantev1 = nomeRepresentante.replace("/", "").trim();

				values.put(ReadWordTemplate.TEMPLATE_WORD_VARIABLES.NOME_REPRESENTANTE, nomeRepresentantev1);

				Select select = new Select(driver.findElement(By.id("ctl00_PageContent_TxAdesao")));
				String parcelas = select.getFirstSelectedOption().getText();
				if (parcelas != null && parcelas.trim().equalsIgnoreCase("A VISTA")) {
					parcelas = "1";
				} else {
					parcelas = select.getFirstSelectedOption().getText().substring(0, 1);
				}
				values.put(ReadWordTemplate.TEMPLATE_WORD_VARIABLES.PARCELAS, parcelas);

				String cpfProprietario = getStringOfXPath(tableOfCondominio,
						"/html/body/form/table/tbody/tr[2]/td[2]/table/tbody/tr[3]/td[3]/div[1]/div[2]/table/tbody/tr[1]/td/div/div/table/tbody/tr[3]/td[2]/div/table/tbody/tr/td/div/table/tbody/tr[15]/td[2]");
				values.put(ReadWordTemplate.TEMPLATE_WORD_VARIABLES.CPF_PROPRIETARIO,
						maskCpf.valueToString(cpfProprietario));

				String nomeProprietario = getStringOfXPath(tableOfCondominio,
						"/html/body/form/table/tbody/tr[2]/td[2]/table/tbody/tr[3]/td[3]/div[1]/div[2]/table/tbody/tr[1]/td/div/div/table/tbody/tr[3]/td[2]/div/table/tbody/tr/td/div/table/tbody/tr[15]/td[5]");
				values.put(ReadWordTemplate.TEMPLATE_WORD_VARIABLES.NOME_PROPRIETARIO, nomeProprietario);

				String unidade = getStringOfXPath(tableOfCondominio,
						"/html/body/form/table/tbody/tr[2]/td[2]/table/tbody/tr[3]/td[3]/div[1]/div[2]/table/tbody/tr[1]/td/div/div/table/tbody/tr[3]/td[2]/div/table/tbody/tr/td/div/table/tbody/tr[17]/td[2]");
				values.put(ReadWordTemplate.TEMPLATE_WORD_VARIABLES.UNIDADE, unidade);

				fillDocument(values, outputDirectory, fullPathDocumentTemplate.toString());
			}
			Thread.sleep(1000);

			driver.close();
			driver.quit();

			JOptionPane optionPane = new JOptionPane("Processamento concluído!", JOptionPane.INFORMATION_MESSAGE);
			JDialog dialog = optionPane.createDialog("Informação!");
			dialog.setAlwaysOnTop(true);
			dialog.setVisible(true);

			System.exit(0);

		} catch (Exception e) {

			logger.error("Erro: [" + e + "]");

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

	private void fillDocument(Map<ReadWordTemplate.TEMPLATE_WORD_VARIABLES, String> values, String outputDirectory,
			String template) throws Exception {
		read.setValues(values);
		read.setOutPutDirectory(outputDirectory);
		read.setDocumentTemplate(template);

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

}
