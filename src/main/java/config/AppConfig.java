package config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import config.model.Config;

public class AppConfig {

	protected Config config;

	public AppConfig() {

	}

	public AppConfig(InputStream pathConfigFile) throws FileNotFoundException, XMLStreamException {

		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		XMLEventReader reader = xmlInputFactory.createXMLEventReader(pathConfigFile);

		config = new Config();

		while (reader.hasNext()) {
			XMLEvent nextEvent = reader.nextEvent();
			if (nextEvent.isStartElement()) {
				StartElement startElement = nextEvent.asStartElement();
				switch (startElement.getName().getLocalPart()) {
				case "chrome_drive":
					nextEvent = reader.nextEvent();
					config.setChromeDrive(nextEvent.asCharacters().getData());
					break;
				case "directory_path":
					nextEvent = reader.nextEvent();
					config.setDirectoryPath(nextEvent.asCharacters().getData());
					break;
				case "templateJuridica":
					nextEvent = reader.nextEvent();
					config.setTemplateJuridica(nextEvent.asCharacters().getData());
					break;
				case "templateFisica":
					nextEvent = reader.nextEvent();
					config.setTemplateFisica(nextEvent.asCharacters().getData());
					break;
				case "output_directory":
					nextEvent = reader.nextEvent();
					config.setOutputDirectory(nextEvent.asCharacters().getData());
					
					File f = new File(config.getOutputDirectory());
					if (!f.exists()) {
						f.mkdir();
					}
					
					break;
				}
			}

			/*
			 * if (nextEvent.isEndElement()) { EndElement endElement =
			 * nextEvent.asEndElement(); if
			 * (endElement.getName().getLocalPart().equals("website")) {
			 * websites.add(website); }
			 */
		}
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

}
