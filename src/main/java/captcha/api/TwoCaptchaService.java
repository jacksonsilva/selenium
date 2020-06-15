package captcha.api;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.asynchttpclient.proxy.ProxyType;

import captcha.HttpWrapper;

public class TwoCaptchaService {

	/**
	 * This class is used to establish a connection to 2captcha.com and receive the
	 * token for solving google recaptcha v2
	 * 
	 * @author Chillivanilli
	 * @version 1.0
	 * 
	 *          If you have a custom software requests, please contact me via forum:
	 *          http://thebot.net/members/chillivanilli.174861/ via eMail:
	 *          chillivanilli@chillibots.com via skype: ktlotzek
	 */

	/**
	 * Your 2captcha.com captcha KEY
	 */
	private String apiKey;

	/**
	 * The google site key from the page you want to solve the recaptcha at
	 */
	private String googleKey;

	/**
	 * The URL where the recaptcha is placed. For example:
	 * https://www.google.com/recaptcha/api2/demo
	 */
	private String pageUrl;

	/**
	 * The proxy ip if you want a worker to solve the recaptcha through your proxy
	 */
	private String proxyIp;

	/**
	 * The proxy port
	 */
	private String proxyPort;

	/**
	 * Your proxy username, if your proxy uses user authentication
	 */
	private String proxyUser;

	/**
	 * Your proxy password, if your proxy uses user authentication
	 */
	private String proxyPw;

	/**
	 * Your proxy type, for example ProxyType.HTTP
	 */
	private ProxyType proxyType;

	/**
	 * The HttpWrapper which the requests are made with
	 */
	private HttpWrapper hw;

	private File image;
	private String method;
	private String textinstructions;
	private String phrase;
	private int numeric;
	private int regSense;
	private int calc;
	private int softId;

	public TwoCaptchaService() {
		hw = new HttpWrapper();
	}

	/**
	 * Constructor if you don't use any proxy
	 * 
	 * @param apiKey
	 * @param googleKey
	 * @param pageUrl
	 */
	public TwoCaptchaService(String apiKey, String googleKey, String pageUrl) {
		this.apiKey = apiKey;
		this.googleKey = googleKey;
		this.pageUrl = pageUrl;
		hw = new HttpWrapper();
	}

	/**
	 * Constructor if you are using a proxy without user authentication
	 * 
	 * @param apiKey
	 * @param googleKey
	 * @param pageUrl
	 * @param proxyIp
	 * @param proxyPw
	 * @param proxyType
	 */
	public TwoCaptchaService(String apiKey, String googleKey, String pageUrl, String proxyIp, String proxyPort,
			ProxyType proxyType) {
		this(apiKey, googleKey, pageUrl);
		this.proxyIp = proxyIp;
		this.proxyPort = proxyPort;
		this.proxyType = proxyType;
	}

	/**
	 * Constructor if you are using a proxy with user authentication
	 * 
	 * @param apiKey
	 * @param googleKey
	 * @param pageUrl
	 * @param proxyIp
	 * @param proxyPort
	 * @param proxyUser
	 * @param proxyPw
	 * @param proxyType
	 */
	public TwoCaptchaService(String apiKey, String googleKey, String pageUrl, String proxyIp, String proxyPort,
			String proxyUser, String proxyPw, ProxyType proxyType) {
		this(apiKey, googleKey, pageUrl);
		this.proxyIp = proxyIp;
		this.proxyPort = proxyPort;
		this.proxyUser = proxyUser;
		this.proxyPw = proxyPw;
		this.proxyType = proxyType;
	}

	/**
	 * Sends the recaptcha challenge to 2captcha.com and checks every second if a
	 * worker has solved it
	 * 
	 * @return The response-token which is needed to solve and submit the recaptcha
	 * @throws InterruptedException, when thread.sleep is interrupted
	 * @throws IOException,          when there is any server issue and the request
	 *                               cannot be completed
	 */
	public String solveCaptcha() throws InterruptedException, IOException {
		System.out.println("Sending recaptcha challenge to 2captcha.com");

		// File file = new
		// File("C:\\Users\\dell\\AppData\\Local\\Temp\\screenshot\\image.png");
		// setImage(file);
		hw.setImage(getImage());
		byte[] fileContent = FileUtils.readFileToByteArray(getImage());
		hw.body = Base64.getEncoder().encodeToString(fileContent);
		hw.key = getApiKey();
		hw.method = getMethod();
		// hw.coordinatescaptcha = "1";
		hw.textinstructions = getTextinstructions();
		// hw.imginstructions = "";
		hw.phrase = getPhrase();
		hw.calc = getCalc();
		hw.numeric = getNumeric();
		hw.softId = getSoftId();

		hw.post("http://2captcha.com/in.php");

		String captchaId = hw.getHtml().replaceAll("\\D", "");
		int timeCounter = 0;

		do {

			hw.get("http://2captcha.com/res.php?key=" + apiKey + "&action=get" + "&id=" + captchaId);
			Thread.sleep(2000);
			timeCounter++;
			System.out.println("Waiting for captcha to be solved");
			
		} while (hw.getHtml().contains("NOT_READY"));

		System.out.println("It took " + timeCounter + " seconds to solve the captcha");
		String gRecaptchaResponse = hw.getHtml().replaceAll("OK\\|", "").replaceAll("\\n", "");
		return gRecaptchaResponse;
	}

	/**
	 * 
	 * @return The 2captcha.com captcha key
	 */
	public String getApiKey() {
		return apiKey;
	}

	/**
	 * Sets the 2captcha.com captcha key
	 * 
	 * @param apiKey
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * 
	 * @return The google site key
	 */
	public String getGoogleKey() {
		return googleKey;
	}

	/**
	 * Sets the google site key
	 * 
	 * @param googleKey
	 */
	public void setGoogleKey(String googleKey) {
		this.googleKey = googleKey;
	}

	/**
	 *
	 * @return The page url
	 */
	public String getPageUrl() {
		return pageUrl;
	}

	/**
	 * Sets the page url
	 * 
	 * @param pageUrl
	 */
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	/**
	 *
	 * @return The proxy ip
	 */
	public String getProxyIp() {
		return proxyIp;
	}

	/**
	 * Sets the proxy ip
	 * 
	 * @param proxyIp
	 */
	public void setProxyIp(String proxyIp) {
		this.proxyIp = proxyIp;
	}

	/**
	 * 
	 * @return The proxy port
	 */
	public String getProxyPort() {
		return proxyPort;
	}

	/**
	 * Sets the proxy port
	 * 
	 * @param proxyPort
	 */
	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * 
	 * @return The proxy authentication user
	 */
	public String getProxyUser() {
		return proxyUser;
	}

	/**
	 * Sets the proxy authentication user
	 * 
	 * @param proxyUser
	 */
	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	/**
	 * 
	 * @return The proxy authentication password
	 */
	public String getProxyPw() {
		return proxyPw;
	}

	/**
	 * Sets the proxy authentication password
	 * 
	 * @param proxyPw
	 */
	public void setProxyPw(String proxyPw) {
		this.proxyPw = proxyPw;
	}

	/**
	 * 
	 * @return The proxy type
	 */
	public ProxyType getProxyType() {
		return proxyType;
	}

	/**
	 * Sets the proxy type
	 * 
	 * @param proxyType
	 */
	public void setProxyType(ProxyType proxyType) {
		this.proxyType = proxyType;
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

	public String getTextinstructions() {
		return textinstructions;
	}

	public void setTextinstructions(String textinstructions) {
		this.textinstructions = textinstructions;
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

	public int getRegSense() {
		return regSense;
	}

	public void setRegSense(int regSense) {
		this.regSense = regSense;
	}
	
}
