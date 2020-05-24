package config.model;

import java.io.Serializable;

public class Config implements Serializable {
	
	private static final long serialVersionUID = 2279634712058136070L;
	private String chromeDrive;
	private String directoryPath;
	private String templateJuridica;
	private String templateFisica;
	private String outputDirectory;

	public String getDirectoryPath() {
		int lastIndexOf = directoryPath.lastIndexOf("\\") + 1;
		if (lastIndexOf != directoryPath.length()) {
			directoryPath = directoryPath + "\\";
		}

		return directoryPath;
	}

	public String getChromeDrive() {
		return chromeDrive;
	}

	public void setChromeDrive(String chromeDrive) {
		this.chromeDrive = chromeDrive;
	}

	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}

	public String getTemplateJuridica() {
		return templateJuridica;
	}

	public void setTemplateJuridica(String templateJuridica) {
		this.templateJuridica = templateJuridica;
	}

	public String getTemplateFisica() {
		return templateFisica;
	}

	public void setTemplateFisica(String templateFisica) {
		this.templateFisica = templateFisica;
	}

	public String getOutputDirectory() {
		int lastIndexOf = outputDirectory.lastIndexOf("\\") + 1;
		if (lastIndexOf != outputDirectory.length()) {
			outputDirectory = outputDirectory + "\\";
		}

		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

}
