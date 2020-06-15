package estudo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

public class PdfReader {

	private final static char DEFAULT_SEPARATOR = ',';

	public static void main(String[] args) throws IOException {

		try {
			PDDocument document = PDDocument.load(new File("F:\\lixo\\TRF 3a Região.pdf"));
			// PDDocument document = PDDocument.load(new File("F:\\lixo\\TRF 2a
			// Região.pdf"));

			document.getClass();

			if (!document.isEncrypted()) {

				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				stripper.setSortByPosition(true);

				PDFTextStripper tStripper = new PDFTextStripper();

				StringBuilder pdfFileInText = new StringBuilder(tStripper.getText(document));
				// System.out.println("Text:" + st);

				Files.write(Paths.get("F:\\lixo\\saida.txt"), pdfFileInText.toString().getBytes(StandardCharsets.UTF_8) ,StandardOpenOption.CREATE);

				// split by whitespace
				/*
				 * String lines[] = pdfFileInText.toString().split("\\r?\\n"); for (String line
				 * : lines) { System.out.println(line); }
				 */

				System.out.println("Finalizado!");

			}

			File filesToRead = new File("F:\\lixo\\");

			File[] listFiles = filesToRead.listFiles();
			FileWriter writer = new FileWriter(new File("F:\\lixo\\myFile.csv"));

			for (File file : listFiles) {

				if (file.getName().contains(".txt")) {
					// Stream<String> lines = Files.lines(file.toPath(), Charset.forName("UTF-8") );

					try (BufferedReader br = Files.newBufferedReader(file.toPath())) {

						String readLine = "";

						for (int i = 0; i < 9; i++) {
							readLine = br.readLine();
							System.out.println(readLine);
						}

						System.out.println(readLine);
						
						StringBuilder linhaFormatada = new StringBuilder();
						int tamanhoLinha = readLine.length();
							
						int posicaoAnoPrecatorio = tamanhoLinha-4;
						
						linhaFormatada.append(readLine.substring(posicaoAnoPrecatorio, tamanhoLinha))
							.append(DEFAULT_SEPARATOR);

						int index =  (posicaoAnoPrecatorio-1);
						boolean isFoundValue = false;
						for (int x=index; x > 0; --x) {
							char charFound = readLine.charAt(x);
							
							switch(charFound){
				            	case ' ': //espaço
				            		if (!isFoundValue) {
				            			linhaFormatada.append(readLine.substring(x, index).trim())
				            			.append(DEFAULT_SEPARATOR);
				            			
				            			isFoundValue = true;
				            			index = x;
				            		}
				            		break;
				            	default:
				            		if (!Character.isDigit(charFound) && !(new Character(charFound).equals(new Character('.')))
				            				) {
				            			linhaFormatada.append(readLine.substring(x+1, index).trim())
				            			.append(DEFAULT_SEPARATOR);
				            			x = -1;
				            		}
							}
							
						}
						
						writer.write(linhaFormatada.toString());
						writer.close();

					} catch (IOException e) {
						e.printStackTrace();
					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}	