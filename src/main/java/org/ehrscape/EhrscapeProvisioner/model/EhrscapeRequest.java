package org.ehrscape.EhrscapeProvisioner.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class EhrscapeRequest {

	Gson gson = new Gson();
	private final static Logger logger = Logger.getLogger(EhrscapeRequest.class.getName());

	HttpClient client = HttpClientBuilder.create().build();

	// not sure if should be static just yet
	public static EhrscapeConfig config = new EhrscapeConfig();

	public String getSession(String username, String password) throws ClientProtocolException, IOException {

		String url = config.getBaseUrl() + "session?username=" + username + "&password=" + password + "";
		HttpPost request = new HttpPost(url);

		URIBuilder newBuilder = new URIBuilder(request.getURI());
		List<NameValuePair> params = newBuilder.getQueryParams();

		HttpResponse response = client.execute(request);
		String finalUrl = request.getRequestLine().toString();
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode() + "\n URL: " + finalUrl + " "
				+ params.toString());

		logger.info("Response status logged: " + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		// TODO set the session id attr of ehrscapeConfig to the returned value
		JsonObject jsonObject = (new JsonParser()).parse(result.toString()).getAsJsonObject();
		// jsonObject.get("sessionId");

		logger.info("" + jsonObject.get("sessionId"));

		config.setSessionId(jsonObject.get("sessionId").toString());
		return result.toString();// jsonResponse;

	}

	// TODO skip provisioning step by deciding how to handle the subjectIDs
	// maybe use the sessionID as the subjectID? too much of a hack perhaps
	// need some way of finding an unused subjectID from the server or perhaps
	// if we
	// are provisioning 500 patients simply increment each time
	// Could check the subjectIDs manually but is this overkill
	// for now use sessionID maybe and a random number unique id concatenated

	public String createEhr(String subjectID, String namespace, String commiter)
			throws ClientProtocolException, IOException {
		String url = config.getBaseUrl() + "ehr?subjectId=" + subjectID + "&subjectNamespace=" + namespace
				+ "&commiterName=" + commiter;
		HttpPost request = new HttpPost(url);
		request.addHeader("Ehr-Session", config.getSessionId().replace("\"", ""));
		logger.info("The current session is" + config.getSessionId());
		String finalUrl = request.getRequestLine().toString();
		System.out.println(finalUrl);
		HttpResponse response = client.execute(request);
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode() + "\n URL: " + finalUrl);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		JsonObject jsonObject = (new JsonParser()).parse(result.toString()).getAsJsonObject();
		return result.toString();
	}

	public String uploadDefaultTemplate(String filePath, String SessionId)
			throws ParserConfigurationException, SAXException, IOException {
		// get the template

		StringBuilder result = new StringBuilder("");

		// Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(filePath).getFile());
		System.out.println(file.getAbsolutePath());

		try (Scanner scanner = new Scanner(file)) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}

			scanner.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toString();

	}

	// HTTP GET request
	private final String USER_AGENT = "Mozilla/5.0";

	public int sendGet() throws Exception {

		String url = "http://www.google.com/search?q=developer";

		// HttpClient client = new DefaultHttpClient();
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);

		// add request header
		request.addHeader("User-Agent", USER_AGENT);

		HttpResponse response = client.execute(request);

		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		System.out.println(result.toString());

		return response.getStatusLine().getStatusCode();

	}

}