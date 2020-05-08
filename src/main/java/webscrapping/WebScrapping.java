package webscrapping;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import poi.ReadWordTemplate;

public class WebScrapping {

	private int WAIT_DEFAULT = 10; //Em segundos;
	private WebDriver driver = new ChromeDriver();
	private WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(WAIT_DEFAULT));
	private ReadWordTemplate read = new ReadWordTemplate();
	

	public static void main(String[] args) {

		System.setProperty("webdriver.chrome.driver", "D:\\projetos\\chromedriver.exe");
		ChromeOptions opt = new ChromeOptions();
		opt.setPageLoadStrategy(PageLoadStrategy.NORMAL);

		
		
		// Initialize browser
		WebScrapping webScrapping = new WebScrapping();
		webScrapping.init();
		
	}
	
	public void init() {
		Map<ReadWordTemplate.TEMPLATE_WORD_VARIABLES, String> values = new HashMap<>();
		
		try {
			
			driver.get("endereco1");

			// Maximize browser
			// driver.manage().window().maximize();

			// *[@id="ctl00_PageContent_UserName"]

			// Get search box element from webElement 'q' using Find Element
			// WebElement searchBox = driver.findElement(By.name("q"));

			//WebElement username = driver.findElement(By.id("ctl00_PageContent_UserName"));
			WebElement username = getWebElementOfId("ctl00_PageContent_UserName");
			WebElement password = getWebElementOfId("ctl00_PageContent_Password");
			WebElement buttonOkToLogin = getWebElementOfId("ctl00_PageContent_OKButton__Button");

			username.sendKeys("usuario");
			password.sendKeys("senha");

			buttonOkToLogin.click();

			// WebElement menuPropostas =
			// driver.findElement(By.xpath("//*[@id=\"ctl00__Menu_MultiLevelMenun0\"]/table/tbody/tr/td/a"));
			// WebElement menuPesquisar =
			// driver.findElement(By.xpath("/html/body/form/table/tbody/tr[2]/td[2]/table/tbody/tr[2]/td[3]/table/tbody/tr/td[1]/table/tbody/tr/td[2]/div/table/tbody/tr[1]/td/table/tbody/tr/td/a"));
			// menuPesquisar.click();

			driver.navigate().to("endereco12");

			WebElement buttonIr = getWebElementOfId("ctl00_PageContent_SearchButton1__Button");
			buttonIr.click();

			List<WebElement> elementosTabelasContatos =  getWebElementsOfId("PesquisaContatoTableControlGrid");
			int xContatos = elementosTabelasContatos.size();
			
			for (int i=1; i <= xContatos; i ++ ) {
				
				WebElement buttonVisualizarComercial;
				if (xContatos > 1) {
					buttonVisualizarComercial  = getWebElementOfXPath("//*[@id=\"PesquisaContatoTableControlGrid\"]/tbody/tr["+ i +"]/td[1]/input[@type='image']");
				} else {
					buttonVisualizarComercial  = getWebElementOfXPath("//*[@id=\"PesquisaContatoTableControlGrid\"]/tbody/tr/td/input[@type='image']");
				}
				
				//WebElement buttonVisualizarComercial = getWebElementOfId("ctl00_PageContent_PesquisaContatoTableControlRepeater_ctl00_btnView");
				buttonVisualizarComercial.click();

				//TODO: Como identificar se existe mais de uma proposta
				WebElement abaPropostas = getWebElementOfId("__tab_ctl00_PageContent_ComercialRecordControlTabContainer_TabPanel2");
				abaPropostas.click();

				WebElement abaPropostasButtonVisualizar = getWebElementOfId("ctl00_PageContent_ComercialRecordControlTabContainer_TabPanel2_PropostasTableControlRepeater_ctl00_ViewRowButton1");
				abaPropostasButtonVisualizar.click();

				
				WebElement tableOfCondominio = getWebElementOfId("ctl00_PageContent_PropostasRecordControlPanel");
				
				String cnpj = getStringOfXPath(tableOfCondominio, "/html/body/form/table/tbody/tr[2]/td[2]/table/tbody/tr[3]/td[3]/div[1]/div[2]/table/tbody/tr[1]/td/div/div/table/tbody/tr[3]/td[2]/div/table/tbody/tr/td/div/table/tbody/tr[4]/td[2]");

				values.put(ReadWordTemplate.TEMPLATE_WORD_VARIABLES.CNPJ, cnpj);
				
				String razaoSocial = getStringOfXPath(tableOfCondominio, "/html/body/form/table/tbody/tr[2]/td[2]/table/tbody/tr[3]/td[3]/div[1]/div[2]/table/tbody/tr[1]/td/div/div/table/tbody/tr[3]/td[2]/div/table/tbody/tr/td/div/table/tbody/tr[4]/td[5]");
				values.put(ReadWordTemplate.TEMPLATE_WORD_VARIABLES.RAZAO_SOCIAL, razaoSocial);
				
				String enderecoCondominio = getStringOfXPath(tableOfCondominio, "/html/body/form/table/tbody/tr[2]/td[2]/table/tbody/tr[3]/td[3]/div[1]/div[2]/table/tbody/tr[1]/td/div/div/table/tbody/tr[3]/td[2]/div/table/tbody/tr/td/div/table/tbody/tr[5]/td[5]");
				String bairroCondominio = getStringOfXPath(tableOfCondominio, "/html/body/form/table/tbody/tr[2]/td[2]/table/tbody/tr[3]/td[3]/div[1]/div[2]/table/tbody/tr[1]/td/div/div/table/tbody/tr[3]/td[2]/div/table/tbody/tr/td/div/table/tbody/tr[6]/td[2]");
				String ufCondominio = getStringOfXPath(tableOfCondominio, "/html/body/form/table/tbody/tr[2]/td[2]/table/tbody/tr[3]/td[3]/div[1]/div[2]/table/tbody/tr[1]/td/div/div/table/tbody/tr[3]/td[2]/div/table/tbody/tr/td/div/table/tbody/tr[6]/td[8]");
				
				String fullAddress = new StringBuilder().append(enderecoCondominio).append(" ").append(bairroCondominio).append(" ").append(ufCondominio).toString();
				values.put(ReadWordTemplate.TEMPLATE_WORD_VARIABLES.ENDERECO_CONDOMINIO, fullAddress);
				
				String cpfRepresentante = getStringOfXPath(tableOfCondominio, "/html/body/form/table/tbody/tr[2]/td[2]/table/tbody/tr[3]/td[3]/div[1]/div[2]/table/tbody/tr[1]/td/div/div/table/tbody/tr[3]/td[2]/div/table/tbody/tr/td/div/table/tbody/tr[10]/td[2]"); 
				values.put(ReadWordTemplate.TEMPLATE_WORD_VARIABLES.CPF_REPRESENTANTE, cpfRepresentante);
				
				String nomeRepresentante = getStringOfXPath(tableOfCondominio, "/html/body/form/table/tbody/tr[2]/td[2]/table/tbody/tr[3]/td[3]/div[1]/div[2]/table/tbody/tr[1]/td/div/div/table/tbody/tr[3]/td[2]/div/table/tbody/tr/td/div/table/tbody/tr[10]/td[5]"); 
				values.put(ReadWordTemplate.TEMPLATE_WORD_VARIABLES.NOME_REPRESENTANTE, nomeRepresentante);
				
				Select select = new Select(driver.findElement(By.id("ctl00_PageContent_TxAdesao")));
				String parcelas = select.getFirstSelectedOption().getText().substring(0, 1);
				
				values.put(ReadWordTemplate.TEMPLATE_WORD_VARIABLES.PARCELAS, parcelas);
				
				fillDocument(values);
			}
			
		
			driver.close();
			driver.quit();
			
			
			System.exit(0);
			
		} catch (Exception e) {
			driver.close();
			driver.quit();
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	private WebElement getWebElementOfId(String id) {
		
		return this.wait.until(
				ExpectedConditions.presenceOfElementLocated(By.id(id)));
	}
	
	private List<WebElement> getWebElementsOfId(String id) {
		
		return this.wait.until(
				ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(id)));
	}
	
	private static String getStringOfXPath(WebElement element, String xPath) {
		return element.findElement(By.xpath(xPath)).getText();
	}
	
	private WebElement getWebElementOfXPath(String xPath) {
		return this.wait.until(
				ExpectedConditions.presenceOfElementLocated(By.xpath(xPath)));
		
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

}
