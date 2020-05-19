package captcha;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * This class can be used for easy GET requests to a server I removed a lot of
 * things to make it as simple as possible and modified it for the use with the
 * 2captcha.com API
 * 
 * @author Pieter Hiele
 * @author modified and shrinked by Chillivanilli
 *
 */

public class HttpWrapper {

	private boolean printHeaders = false;
	private String html;
	private int responseCode = 0;
	private File image;
	public String key;
	public String method;
	public String coordinatescaptcha;
	public String body;
	public String textinstructions;
	public String imginstructions;
	public String phrase;
	public int numeric;
	public int calc;
	public int softId;

	/**
	 * Default constructor
	 */
	public HttpWrapper() {
		html = "";
	}

	/**
	 * A method to get the content and headers (if set) of a given page, with a
	 * given referer.
	 *
	 * @param url     The given URL.
	 * @param referer The given referer.
	 * @throws IllegalStateException Whenever an IO-related problem occurs.
	 * @post new.getHtml() will return the headers and content of the given URL.
	 */

	public void get(String url) {

		try {
			URL url_ = new URL(url);
			HttpURLConnection conn;

			conn = (HttpURLConnection) url_.openConnection();
			conn.setRequestMethod("GET");
			conn.setAllowUserInteraction(false);
			conn.setDoOutput(false);
			conn.setInstanceFollowRedirects(false);

			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0");
			conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			conn.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
			conn.setRequestProperty("Connection", "keep-alive");

			String headers = "";

			if (printHeaders) {
				for (String key : conn.getHeaderFields().keySet())
					headers += ((key != null) ? key + ": " : "") + conn.getHeaderField(key) + "\n";
			}

			responseCode = conn.getResponseCode();

			BufferedReader d = new BufferedReader(new InputStreamReader(new DataInputStream(conn.getInputStream())));
			String result = "";
			String line = null;
			while ((line = d.readLine()) != null) {
				line = new String(line.getBytes(), "UTF-8");
				result += line + "\n";
			}
			//result="OK|8888";
			d.close();

			if (printHeaders) {
				setHtml(headers + "\n" + result);
			} else {
				setHtml(result);
			}
		} catch (IOException e) {
			throw new IllegalStateException("An IOException occurred:" + "\n" + e.getMessage());
		}
	}

	public void post(String url) {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			try {
				HttpPost httppost = new HttpPost(url);

				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				builder.addBinaryBody("file", image, ContentType.DEFAULT_BINARY, "");
				builder.addTextBody("key", key);
				builder.addTextBody("method", method);
				builder.addTextBody("body", body);
				builder.addTextBody("textinstructions", textinstructions);
				//builder.addTextBody("imginstructions", imginstructions);
				builder.addTextBody("phrase", phrase);
				builder.addTextBody("numeric", String.valueOf(numeric)); //Numbers and Words
				builder.addTextBody("calc", String.valueOf(calc)); //Required Calculation
				builder.addTextBody("soft_id", String.valueOf(softId)); //Identification Software
				//builder.addTextBody("coordinatescaptcha", coordinatescaptcha);
				//builder.addTextBody("debug_dump", "1");
				
				HttpEntity entity = builder.build();
				httppost.setEntity(entity);
			    
				System.out.println("Executing request: " + httppost.getRequestLine());
				CloseableHttpResponse response = httpclient.execute(httppost);
				try {
					System.out.println("----------------------------------------");
					System.out.println(response.getStatusLine());

					String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
					setHtml(responseBody);
					EntityUtils.consume(response.getEntity());

				} finally {
					response.close();
				}
			} finally {
				httpclient.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Return the html content that this Wrapper has last retrieved from a request.
	 */
	public String getHtml() {
		return this.html;
	}

	/**
	 * Set the html content of this HttpWrapper.
	 *
	 * @param html The new html content.
	 */
	private void setHtml(String html) {
		this.html = html;
	}

	/**
	 * Set if headers should be print above the content or not
	 * 
	 * @param trueOrFalse
	 */
	public void setPrintHeaders(boolean trueOrFalse) {
		printHeaders = trueOrFalse;
	}

	/**
	 * Returns the response code of the request
	 * 
	 * @return
	 */
	public int getResponseCode() {
		return responseCode;
	}

	public File getImage() {
		return image;
	}

	public void setImage(File image) {
		this.image = image;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getTextinstructions() {
		return textinstructions;
	}

	public void setTextinstructions(String textinstructions) {
		this.textinstructions = textinstructions;
	}

	public String getImginstructions() {
		return imginstructions;
	}

	public void setImginstructions(String imginstructions) {
		this.imginstructions = imginstructions;
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public int getNumeric() {
		return numeric;
	}

	public void setNumeric(int numeric) {
		this.numeric = numeric;
	}

	public int getCalc() {
		return calc;
	}

	public void setCalc(int calc) {
		this.calc = calc;
	}

	public int getSoftId() {
		return softId;
	}

	public void setSoftId(int softId) {
		this.softId = softId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
}
