package config.model;

import java.io.Serializable;

public class Config implements Serializable {

	private static final long serialVersionUID = 3013187935651944772L;
	
	private String directoryPath;
	private String fileName;
	private String outputDirectory;

	public String getDirectoryPath() {
		int lastIndexOf = directoryPath.lastIndexOf("\\")+1;
		if (lastIndexOf != directoryPath.length()) {
			directoryPath = directoryPath + "\\";
		}
		
		return directoryPath;
	}

	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getOutputDirectory() {
		int lastIndexOf = outputDirectory.lastIndexOf("\\")+1;
		if (lastIndexOf != outputDirectory.length()) {
			outputDirectory = outputDirectory + "\\";
		}
		
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

}
