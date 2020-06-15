package poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadWordTemplate {

	private Logger logger = LoggerFactory.getLogger(ReadWordTemplate.class);
	
	public enum TEMPLATE_WORD_VARIABLES {
		RAZAO_SOCIAL, CNPJ, ENDERECO_CONDOMINIO, NOME_REPRESENTANTE, CPF_REPRESENTANTE, CNPJ_REPRESENTANTE, PARCELAS, UNIDADE,
		NOME_PROPRIETARIO, CPF_PROPRIETARIO
	}

	private Map<TEMPLATE_WORD_VARIABLES, String> values;
	private String documentTemplate;
	private String outPutDirectory;

	public static void main(String[] args) throws Exception {

		ReadWordTemplate readWordTemplate = new ReadWordTemplate();
		readWordTemplate.init();

	}

	public void init() throws Exception {

		try {

			/*
			values = new HashMap<TEMPLATE_WORD_VARIABLES, String>();
			values.put(TEMPLATE_WORD_VARIABLES.UNIDADE, "AOTPO");
			values.put(TEMPLATE_WORD_VARIABLES.RAZAO_SOCIAL, "AOTPO");
			values.put(TEMPLATE_WORD_VARIABLES.NOME_REPRESENTANTE, "AOTPO");
			values.put(TEMPLATE_WORD_VARIABLES.NOME_PROPRIETARIO, "NOME PROPRIED");
			values.put(TEMPLATE_WORD_VARIABLES.CPF_PROPRIETARIO, "11111111111");
			*/

			if (values == null && (documentTemplate == null || "".equals(documentTemplate)) && 
					(outPutDirectory == null || "".equals(outPutDirectory))) {
				throw new Exception("Valores das variáveis não foram preenchidos e/ou Template do documento não encontrado!");
			}

			SimpleDateFormat sdfTimer = new SimpleDateFormat("HH:mm:ss");
			System.out.println("Inicio - " + sdfTimer.format(new Date()));

			//File fileConfig = new File("./config.xml");

			///AppConfig appConfig = new AppConfig(new FileInputStream(fileConfig));
			// AppConfig appConfig = new
			// AppConfig(ReadWordTemplate.class.getResourceAsStream("config.xml"));
			//Config config = appConfig.getConfig();
			
			//File file = new File(config.getDirectoryPath() + config.getTemplateFisica());
			
			File file = new File(documentTemplate);
			XWPFDocument document = new XWPFDocument(new FileInputStream(file));
			List<XWPFParagraph> paragraphs = document.getParagraphs();

			Map<String, String> replacedElementsMap = new HashMap<>();
			replacedElementsMap.put("\\{RAZAO_SOCIAL_CONDOMINIO\\}", values.get(TEMPLATE_WORD_VARIABLES.RAZAO_SOCIAL));
			replacedElementsMap.put("\\{CNPJ_MF_CONDOMINIO\\}", values.get(TEMPLATE_WORD_VARIABLES.CNPJ));
			replacedElementsMap.put("\\{ENDERECO_CONDOMINIO\\}",
					values.get(TEMPLATE_WORD_VARIABLES.ENDERECO_CONDOMINIO));
			
			replacedElementsMap.put("\\{NOME_REPRESENTANTE\\}", values.get(TEMPLATE_WORD_VARIABLES.NOME_REPRESENTANTE));
			replacedElementsMap.put("\\{CPF_REPRESENTANTE\\}", values.get(TEMPLATE_WORD_VARIABLES.CPF_REPRESENTANTE));
			replacedElementsMap.put("\\{CNPJ_REPRESENTANTE\\}", values.get(TEMPLATE_WORD_VARIABLES.CNPJ_REPRESENTANTE));
			
			
			String parcelas = values.get(TEMPLATE_WORD_VARIABLES.PARCELAS);
			replacedElementsMap.put("\\{PARCELAS\\}", parcelas);
			if (parcelas != null) {
				replacedElementsMap.put("\\{PARCELAS_EXTENSO\\}", parcelasExtenso(Integer.parseInt(parcelas)));
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM yyyy");
			replacedElementsMap.put("\\{DATA_ATUAL\\}", sdf.format(new Date()));
			replacedElementsMap.put("\\{UNIDADE\\}", values.get(TEMPLATE_WORD_VARIABLES.UNIDADE));
			replacedElementsMap.put("\\{NOME_PROPRIETARIO\\}", values.get(TEMPLATE_WORD_VARIABLES.NOME_PROPRIETARIO));
			replacedElementsMap.put("\\{CPF_PROPRIETARIO\\}", values.get(TEMPLATE_WORD_VARIABLES.CPF_PROPRIETARIO));
			
			SimpleDateFormat sdfDDMMYYYY = new SimpleDateFormat("dd/MM/yyyy");
			replacedElementsMap.put("\\{DATA_ATUAL_DDMMYYYY\\}", sdfDDMMYYYY.format(new Date()));
			
			

			List<IBodyElement> bodyElements = document.getBodyElements();
			for (IBodyElement body : bodyElements) {

				switch (body.getElementType().name()) {
				case "PARAGRAPH":
					for (XWPFParagraph para : paragraphs) {
						List<XWPFRun> xwpfRuns = para.getRuns();
						for (XWPFRun xwpfRun : xwpfRuns) {
							String xwpfRunText = xwpfRun.getText(xwpfRun.getTextPosition());
							for (Map.Entry<String, String> entry : replacedElementsMap.entrySet()) {
								if (xwpfRunText != null && xwpfRunText.contains(entry.getKey().replace("\\", ""))) {
									xwpfRunText = xwpfRunText.replaceAll(entry.getKey(), entry.getValue());
								}
							}
							xwpfRun.setText(xwpfRunText, 0);
						}
					}
				break;
			/*
			 * case "TABLE": List<XWPFTable> tableList = body.getBody().getTables(); for
			 * (XWPFTable table : tableList) { if (table.getRows().size() > 1) { for
			 * (XWPFTableCell cell : table.getRow(1).getTableCells()) { for (XWPFParagraph p
			 * : cell.getParagraphs()) { for (XWPFRun r : p.getRuns()) { String text =
			 * r.getText(r.getTextPosition()); for (Map.Entry<String, String> entry :
			 * replacedElementsMap.entrySet()) { if (text != null &&
			 * text.contains(entry.getKey().replace("\\", ""))) { text =
			 * text.replaceAll(entry.getKey(), entry.getValue()); } } r.setText(text, 0); }
			 * } } } } break;
			 */
				}
			}

			SimpleDateFormat sdfDataFileName = new SimpleDateFormat("ddMMyyyy");
			String dataFileName = sdfDataFileName.format(new Date());

			StringBuilder newName = new StringBuilder(outPutDirectory).append(dataFileName).append("_")
					.append(replacedElementsMap.get("\\{RAZAO_SOCIAL_CONDOMINIO\\}")).append(".docx");

			
			/*FileOutputStream pdfOutput = new FileOutputStream(new File(newName.toString()));
			PdfOptions options = PdfOptions.create();
			PdfConverter.getInstance().convert(document, pdfOutput, options);*/
		
			/*PdfOptions options = PdfOptions.create();
			OutputStream out = new FileOutputStream(new File(newName.toString()));
			PdfConverter.getInstance().convert(document, out, options);
			*/
			
			
			//Exportando em arquivo doc
			File copied = new File(newName.toString());
			XWPFDocument documentTmp = document;
			FileOutputStream out = new FileOutputStream(copied);
			documentTmp.write(out);
			documentTmp.close();

			System.out.println("Fim - " + sdfTimer.format(new Date()));

		} catch (Exception e) {
			logger.error("Erro: " + e);
			e.printStackTrace();
		}
	}

	private static String parcelasExtenso(int parcelas) {

		switch (parcelas) {
		case 1:
			return "A VISTA";
		case 2:
			return "duas";
		case 3:
			return "três";
		case 4:
			return "quatro";
		case 5:
			return "cinco";
		case 6:
			return "seis";
		default:
			return "";
		}
	}

	public Map<TEMPLATE_WORD_VARIABLES, String> getValues() {
		return values;
	}

	public void setValues(Map<TEMPLATE_WORD_VARIABLES, String> values) {
		this.values = values;
	}

	public String getDocumentTemplate() {
		return documentTemplate;
	}

	public void setDocumentTemplate(String documentTemplate) {
		this.documentTemplate = documentTemplate;
	}

	public String getOutPutDirectory() {
		return outPutDirectory;
	}

	public void setOutPutDirectory(String outPutDirectory) {
		this.outPutDirectory = outPutDirectory;
	}
	
}
