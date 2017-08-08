package ehrscapeProvisioner.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

public class EhrscapeRequest {

	// TODO Error handling depending on response codes and providing an
	// appropriate response

	Gson gson = new Gson();

	private final static Logger logger = Logger.getLogger(EhrscapeRequest.class.getName());

	HttpClient client = HttpClientBuilder.create().build();

	public static EhrscapeConfig config = new EhrscapeConfig();

	private String getFileAsString(String fileName) {

		StringBuilder result = new StringBuilder("");

		// Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		// ClassLoader classLoader =
		// Thread.currentThread().getContextClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());

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

	// SINGLE PATIENT

	public Response getSession(String username, String password)
			throws ClientProtocolException, IOException, URISyntaxException {

		String url; // = config.getBaseUrl() + "session?username=" + username +
					// "&password=" + password + "";

		config.setUsername(username);
		config.setPassword(password);

		URIBuilder ub = new URIBuilder(config.getBaseUrl() + "session");
		ub.addParameter("username", username);
		ub.addParameter("password", password);
		url = ub.toString();

		HttpPost request = new HttpPost(url);

		List<NameValuePair> params = ub.getQueryParams();

		HttpResponse response = client.execute(request);
		String finalUrl = request.getRequestLine().toString();
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode() + "\n URL: " + finalUrl + " "
				+ params.toString());

		logger.info("Response status logged: " + response.getStatusLine().getStatusCode());

		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity);
		System.out.println(result);

		if (response.getStatusLine().getStatusCode() == 201 || response.getStatusLine().getStatusCode() == 200) {
			JsonObject jsonObject = (new JsonParser()).parse(result.toString()).getAsJsonObject();
			logger.info("" + jsonObject.get("sessionId"));
			config.setSessionId(jsonObject.get("sessionId").getAsString());
			return Response.ok(result, MediaType.APPLICATION_JSON).status(response.getStatusLine().getStatusCode())
					.build();

		} else {
			JsonObject jsonObject = (new JsonParser()).parse(result.toString()).getAsJsonObject();
			System.out.println(jsonObject.toString());
			return Response.ok(result, MediaType.APPLICATION_JSON).status(response.getStatusLine().getStatusCode())
					.build();
		}

	}

	// create patient demographic
	public Response createMarandPatientDemographic(String body)
			throws ClientProtocolException, IOException, URISyntaxException {
		// String body = getFileAsString(filename);
		URIBuilder ub = new URIBuilder(config.getBaseUrl() + "demographics/party");
		String url = ub.toString(); // = config.getBaseUrl() +
									// "demographics/party";
		HttpPost request = new HttpPost(url);
		request.addHeader("Ehr-Session", config.getSessionId());
		request.addHeader("Content-Type", "application/json");
		request.setEntity(new StringEntity(body));
		HttpResponse response = client.execute(request);
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity);
		// System.out.println(result);

		if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201) {
			JsonObject jsonObject = (new JsonParser()).parse(result.toString()).getAsJsonObject();
			JsonObject jsonSubObject = jsonObject.getAsJsonObject("meta");
			String partyStringHref = jsonSubObject.get("href").getAsString();
			String partyID = partyStringHref.substring(partyStringHref.lastIndexOf("/") + 1);
			System.out.println(partyID);
			config.setSubjectId(partyID);
			return Response.ok(result, MediaType.APPLICATION_JSON).status(response.getStatusLine().getStatusCode())
					.build();
		} else {
			JsonObject jsonResponse = new JsonObject();
			jsonResponse.addProperty("error-message", "demographic not created");
			jsonResponse.addProperty("response-code", response.getStatusLine().getStatusCode());
			jsonResponse.addProperty("message", response.getStatusLine().getReasonPhrase());
			result = jsonResponse.toString();
			return Response.ok(result, MediaType.APPLICATION_JSON).status(response.getStatusLine().getStatusCode())
					.build();
		}
	}

	// create patient demographic
	public Response createPatientDefault() throws ClientProtocolException, IOException, URISyntaxException {
		String body = getFileAsString("assets/sample_requests/party.json");
		Response response = createMarandPatientDemographic(body);
		return response;
	}

	// TODO skip provisioning step by deciding how to handle the subjectIDs
	// maybe use the sessionID as the subjectID? too much of a hack perhaps
	// need some way of finding an unused subjectID from the server or perhaps
	// if we are provisioning 500 patients simply increment each time
	// Could check the subjectIDs manually but is this overkill
	// for now use sessionID maybe and a random number unique id concatenated

	// Could create a uniqueID for now too, and test if an ehr exists for that
	// if not try a new id
	// get the response code back and then take appropriate action
	// and then create the ehr

	public Response createEhr(String subjectID, String commiter)
			throws ClientProtocolException, IOException, URISyntaxException {
		String url; // = config.getBaseUrl() + "ehr?subjectId=" + subjectID +
					// "&subjectNamespace=" + config.getSubjectNamespace() +
					// "&commiterName=" + commiter;

		URIBuilder ub = new URIBuilder(config.getBaseUrl() + "ehr");
		ub.addParameter("subjectId", subjectID);
		ub.addParameter("subjectNamespace", config.getSubjectNamespace());
		ub.addParameter("commiterName", commiter);
		url = ub.toString();
		System.out.println("Params === " + ub.getQueryParams().toString());

		HttpPost request = new HttpPost(url);
		request.addHeader("Ehr-Session", config.getSessionId());
		logger.info("The current session is" + config.getSessionId());
		String finalUrl = request.getRequestLine().toString();
		System.out.println(finalUrl);
		HttpResponse response = client.execute(request);
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode() + "\n URL: " + finalUrl);
		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode == 200 || responseCode == 201) {
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			System.out.println(result);

			JsonObject jsonObject = (new JsonParser()).parse(result.toString()).getAsJsonObject();
			logger.info("" + jsonObject.get("ehrId"));

			config.setEhrId(jsonObject.get("ehrId").getAsString());
			config.setSubjectId(subjectID);
			return Response.ok(result, MediaType.APPLICATION_JSON).build();
		} else {
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			System.out.println(result);
			return Response.ok(result, MediaType.APPLICATION_JSON).status(responseCode).build();
		}

	}

	public Response getEhrWithSubjectId(String subjectId, String subjectNamespace)
			throws URISyntaxException, ClientProtocolException, IOException {
		String url;
		URIBuilder ub = new URIBuilder(config.getBaseUrl() + "ehr");
		ub.addParameter("subjectId", subjectId);
		ub.addParameter("subjectNamespace", subjectNamespace);
		url = ub.toString();
		System.out.println(url);

		HttpGet request = new HttpGet(url);
		request.addHeader("Ehr-Session", config.getSessionId());
		HttpResponse response = client.execute(request);
		int responseCode = response.getStatusLine().getStatusCode();
		System.out.println("Status response code: " + responseCode);
		String result;

		if (responseCode == 200) {
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity);
			return Response.ok(result, MediaType.APPLICATION_JSON).status(responseCode).build();
		} else {
			JsonObject jsonResult = new JsonObject();
			jsonResult.addProperty("status", responseCode);
			jsonResult.addProperty("message", "No content - no EHR for the specified subject ID and namespace exists.");

			// result = jsonResult.toString();
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity);
			return Response.ok(result, MediaType.APPLICATION_JSON).status(responseCode).build();
		}

	}

	// templates

	public Response uploadTemplate(String body) throws IOException, URISyntaxException {
		// get the template
		// String body = getFileAsString(filename);
		System.out.println(body.length());
		String url; // = config.getBaseUrl() + "template/";

		URIBuilder ub = new URIBuilder(config.getBaseUrl() + "template/");
		url = ub.toString();

		HttpPost request = new HttpPost(url);
		request.addHeader("Ehr-Session", config.getSessionId());
		request.addHeader("Content-Type", "application/xml");
		request.setEntity(new StringEntity(body));

		logger.info("The current session is" + config.getSessionId());
		String finalUrl = request.getRequestLine().toString();
		System.out.println(finalUrl);

		HttpResponse response = client.execute(request);
		int responseCode = response.getStatusLine().getStatusCode();
		System.out.println("Response Code : " + responseCode);

		if (responseCode == 200 || responseCode == 201) {
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			System.out.println(result);
			return Response.ok(result, MediaType.APPLICATION_JSON).status(responseCode).build();
		} else {
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			return Response.ok(result, MediaType.APPLICATION_JSON).status(responseCode).build();
		}

		// JsonObject jsonObject = (new
		// JsonParser()).parse(result.toString()).getAsJsonObject();

	}

	public Response uploadDefaultTemplate() throws IOException, URISyntaxException {
		// get the template
		String body = getFileAsString("assets/sample_requests/vital-signs/vital-signs-template.xml");
		Response response = uploadTemplate(body);
		return response;
	}

	// Composition

	// for the import csv tool
	public Response uploadComposition(String body, String sessionId, String templateId, String commiterName,
			String ehrId) throws URISyntaxException, ClientProtocolException, IOException {
		String url;
		URIBuilder ub = new URIBuilder(config.getBaseUrl() + "composition");
		ub.addParameter("ehrId", ehrId);
		ub.addParameter("templateId", templateId);
		ub.addParameter("format", "FLAT");
		ub.addParameter("comitterId", commiterName);
		url = ub.toString();
		HttpPost request = new HttpPost(url);
		request.addHeader("Ehr-Session", sessionId);
		request.addHeader("Content-Type", "application/json");
		request.setEntity(new StringEntity(body));
		HttpResponse response = client.execute(request);
		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode == 201 || responseCode == 200) {
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			JsonObject jsonObject = (new JsonParser()).parse(result.toString()).getAsJsonObject();
			logger.info("" + jsonObject.get("compositionUid"));
			return Response.ok(result, MediaType.APPLICATION_JSON).status(responseCode).build();
		} else {
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			return Response.ok(result, MediaType.APPLICATION_JSON).status(responseCode).build();
		}
	}

	public Response uploadComposition(String body) throws ClientProtocolException, IOException, URISyntaxException {
		// get the composition body
		// String body = getFileAsString(filename);
		System.out.println(body.length());
		System.out.println(config.getEhrId());
		System.out.println(config.getTemplateId());
		System.out.println(config.getCommiterName());
		String url = config.getBaseUrl() + "composition?ehrId=" + config.getEhrId() + "&templateId="
				+ config.getTemplateId() + "&committerName=" + config.getCommiterName();
		URIBuilder ub = new URIBuilder(config.getBaseUrl() + "composition");
		ub.addParameter("ehrId", config.getEhrId());
		ub.addParameter("templateId", config.getTemplateId());
		ub.addParameter("format", "FLAT");
		ub.addParameter("comitterId", config.getCommiterName());
		url = ub.toString();

		HttpPost request = new HttpPost(url);
		request.addHeader("Ehr-Session", config.getSessionId());
		request.addHeader("Content-Type", "application/json");
		request.setEntity(new StringEntity(body));

		logger.info("The current session is" + config.getSessionId());
		String finalUrl = request.getRequestLine().toString();
		System.out.println(finalUrl);

		HttpResponse response = client.execute(request);
		int responseCode = response.getStatusLine().getStatusCode();
		System.out.println("Response Code : " + responseCode);

		if (responseCode == 201 || responseCode == 200) {
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			System.out.println(result);
			JsonObject jsonObject = (new JsonParser()).parse(result.toString()).getAsJsonObject();
			logger.info("" + jsonObject.get("compositionUid"));
			config.setCompositionId(jsonObject.get("compositionUid").getAsString());
			return Response.ok(result, MediaType.APPLICATION_JSON).status(responseCode).build();
		} else {
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			System.out.println(result);
			return Response.ok(result, MediaType.APPLICATION_JSON).status(responseCode).build();
		}
	}

	public Response uploadDefaultComposition() throws ClientProtocolException, IOException, URISyntaxException {
		// get the composition body
		String body = getFileAsString("assets/sample_requests/vital-signs/vital-signs-composition.json");
		Response response = uploadComposition(body);
		return response;
	}

	// FHIR Demographic call

	public Response createFhirPatientDemographic(String fhirBaseUrl, String body)
			throws URISyntaxException, ClientProtocolException, IOException {

		String url;
		URIBuilder ub = new URIBuilder(fhirBaseUrl + "Patient");
		url = ub.toString();

		HttpPost request = new HttpPost(url);
		request.addHeader("Content-Type", "application/xml");
		request.setEntity(new StringEntity(body));

		String finalUrl = request.getRequestLine().toString();
		logger.info("Post Request to : " + finalUrl);

		HttpResponse response = client.execute(request);
		int responseCode = response.getStatusLine().getStatusCode();

		if (responseCode == 200 || responseCode == 201) {

			/*
			 * Example response header set from this server:
			 * 
			 * Key : Date ,Value : Sat, 05 Aug 2017 14:24:36 GMT Key :
			 * X-Powered-By ,Value : HAPI FHIR 2.5 REST Server (FHIR Server;
			 * FHIR 1.0.2/DSTU2) Key : ETag ,Value : W/"1" Key :
			 * Content-Location ,Value :
			 * http://51.140.57.74:8090/fhir/Patient/453/_history/1 Key :
			 * Location ,Value :
			 * http://51.140.57.74:8090/fhir/Patient/453/_history/1 Key : Server
			 * ,Value : Jetty(9.2.22.v20170606)
			 * 
			 */

			String locationUrl = response.getFirstHeader("Location").getValue();
			System.out.println(locationUrl);
			// cut off all of the url so all that is left is: Patient/{id}
			String trimmedUrl = locationUrl.substring(locationUrl.lastIndexOf("Patient"),
					locationUrl.lastIndexOf("/_history/"));
			System.out.println(trimmedUrl);
			// then look at whats left after the forward slash
			String fhirPatientId = trimmedUrl.substring(trimmedUrl.lastIndexOf("/") + 1);
			System.out.println(fhirPatientId);

			config.setSubjectId(fhirPatientId);
			logger.info("SubjectId is now" + config.getSubjectId());

			JsonObject jsonResponse = new JsonObject();

			Header[] headers = response.getAllHeaders();
			for (Header header : headers) {
				// System.out.println("Key : " + header.getName() + " ,Value : "
				// + header.getValue());
				jsonResponse.addProperty(header.getName(), header.getValue());
			}

			// get location and set this as SubjectId
			return Response.ok(jsonResponse.toString(), MediaType.APPLICATION_JSON).status(responseCode).build();
			
		} else {
			JsonObject jsonResponse = new JsonObject();
			jsonResponse.addProperty("errorMessage", "Error creating this FHIR Resource");
			// TODO add more info to this error message
			
			return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).status(responseCode).build();
		}
	}

	public Response createDefaultFhirPatientDemographic()
			throws URISyntaxException, ClientProtocolException, IOException {
		String fhirPatientBody = getFileAsString("assets/sample_requests/defaultFhirPatient.xml");
		Response response = createFhirPatientDemographic(config.getFhirDemographicBaseUrl(), fhirPatientBody);
		return response;
	}

	// MULTIPLE PATIENT

	public List<PatientDemographic> readPatientCsvToObjectlist(String fileName) throws IOException {

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());

		CsvToBean<PatientDemographic> csvToBean = new CsvToBean<PatientDemographic>();
		// https://stackoverflow.com/questions/13505653/opencsv-how-to-map-selected-columns-to-java-bean-regardless-of-order/14976689#14976689
		// CSV Header:
		// [Key, , Forename, Surname, Address_1, Address_2, Address_3, Postcode,
		// Telephone,
		// DateofBirth, Gender, NHSNumber, PasNumber, Department, GPNumber]
		Map<String, String> columnMapping = new HashMap<String, String>();

		columnMapping.put("Key", "Key");
		columnMapping.put("Forename", "Forename");
		columnMapping.put("Surname", "Surname");
		columnMapping.put("Address_1", "Address_1");
		columnMapping.put("Address_2", "Address_2");
		columnMapping.put("Address_3", "Address_3");
		columnMapping.put("Postcode", "Postcode");
		columnMapping.put("Telephone", "Telephone");
		columnMapping.put("DateofBirth", "DateofBirth");
		columnMapping.put("Gender", "Gender");
		columnMapping.put("NHSNumber", "NHSNumber");
		columnMapping.put("PasNumber", "PasNumber");
		columnMapping.put("Department", "Department");
		columnMapping.put("GPNumber", "GPNumber");
		columnMapping.put("", "Prefix"); // the prefix columns in the dummy data
											// have no title atm.

		HeaderColumnNameTranslateMappingStrategy<PatientDemographic> strategy = new HeaderColumnNameTranslateMappingStrategy<PatientDemographic>();
		strategy.setType(PatientDemographic.class);
		strategy.setColumnMapping(columnMapping);

		List<PatientDemographic> list = null;
		CSVReader reader = new CSVReader(new FileReader(file), ',', '"', 0);
		list = csvToBean.parse(strategy, reader);

		// System.out.println(list.get(0).toString());
		// System.out.println(list.get(0).getPrefix());
		// System.out.println(list.get(1).encodeInFhirFormat(true));
		// System.out.println(list.get(0).toMarandPartyJson());

		return list;

	}

	public void uploadMultipleCompositions(String ehrId, boolean doAllergies, boolean doOrders, boolean doProblems,
			boolean doProcedures, boolean doLabResults, boolean doVitals) {

	}

	public String importCSV(String filename) {
		// replicate the marand import csv resource
		String fileString = getFileAsString(filename);
		return fileString;
	}

}