package poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import config.AppConfig;
import config.model.Config;

public class ReadWordTemplate {

	public enum TEMPLATE_WORD_VARIABLES {
		RAZAO_SOCIAL, CNPJ, ENDERECO_CONDOMINIO, NOME_REPRESENTANTE, CPF_REPRESENTANTE, PARCELAS
	}

	private Map<TEMPLATE_WORD_VARIABLES, String> values;

	public static void main(String[] args) throws Exception {

		ReadWordTemplate readWordTemplate = new ReadWordTemplate();
		readWordTemplate.init();

	}

	public void init() throws Exception {

		try {
			if (values == null) {
				throw new Exception("Valores das variáveis não foram preenchidos");
			}
			
			SimpleDateFormat sdfTimer = new SimpleDateFormat("HH:mm:ss");
			System.out.println("Inicio - " + sdfTimer.format(new Date()));

			File fileConfig = new File("D:\\projetos\\doc\\config.xml");

			AppConfig appConfig = new AppConfig(new FileInputStream(fileConfig));
			// AppConfig appConfig = new
			// AppConfig(ReadWordTemplate.class.getResourceAsStream("config.xml"));
			Config config = appConfig.getConfig();

			File file = new File(config.getDirectoryPath() + config.getFileName());
			XWPFDocument document = new XWPFDocument(new FileInputStream(file));
			List<XWPFParagraph> paragraphs = document.getParagraphs();

			Map<String, String> replacedElementsMap = new HashMap<>();
			replacedElementsMap.put("\\{RAZAO_SOCIAL_CONDOMINIO\\}", values.get(TEMPLATE_WORD_VARIABLES.RAZAO_SOCIAL));
			replacedElementsMap.put("\\{CNPJ_MF_CONDOMINIO\\}", values.get(TEMPLATE_WORD_VARIABLES.CNPJ));
			replacedElementsMap.put("\\{ENDERECO_CONDOMINIO\\}", values.get(TEMPLATE_WORD_VARIABLES.ENDERECO_CONDOMINIO));
			replacedElementsMap.put("\\{NOME_REPRESENTANTE\\}", values.get(TEMPLATE_WORD_VARIABLES.NOME_REPRESENTANTE));
			replacedElementsMap.put("\\{RG_REPRESENTANTE\\}", "RG REPRESENTANTE");
			replacedElementsMap.put("\\{CPF_REPRESENTANTE\\}", values.get(TEMPLATE_WORD_VARIABLES.CPF_REPRESENTANTE));
			replacedElementsMap.put("\\{ENDERECO_REPRESENTANTE\\}", "ENDERECO REPRESENTANTE");
			String parcelas = values.get(TEMPLATE_WORD_VARIABLES.PARCELAS);
			replacedElementsMap.put("\\{PARCELAS\\}", parcelas);
			replacedElementsMap.put("\\{PARCELAS_EXTENSO\\}", parcelasExtenso(Integer.parseInt(parcelas)));

			SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM yyyy");
			replacedElementsMap.put("\\{DATA_ATUAL\\}", sdf.format(new Date()));

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

			SimpleDateFormat sdfDataFileName = new SimpleDateFormat("ddMMyyyy");
			String dataFileName = sdfDataFileName.format(new Date());

			StringBuilder newName = new StringBuilder(config.getOutputDirectory()).append(dataFileName).append("_")
					.append(replacedElementsMap.get("\\{RAZAO_SOCIAL_CONDOMINIO\\}")).append(".docx");

			File copied = new File(newName.toString());
			XWPFDocument documentTmp = document;
			FileOutputStream out = new FileOutputStream(copied);
			documentTmp.write(out);
			documentTmp.close();

			System.out.println("Fim - " + sdfTimer.format(new Date()));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String parcelasExtenso(int parcelas) {
		
		switch (parcelas) {
		case 1:
			return "uma";
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

}
