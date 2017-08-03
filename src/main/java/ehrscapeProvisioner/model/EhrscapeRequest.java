package ehrscapeProvisioner.model;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.StringType;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ca.uhn.fhir.context.FhirContext;


public class EhrscapeRequest {

	Gson gson = new Gson();
	private final static Logger logger = Logger.getLogger(EhrscapeRequest.class.getName());

	HttpClient client = HttpClientBuilder.create().build();
	
	FhirContext ctx = FhirContext.forDstu3();

	public static EhrscapeConfig config = new EhrscapeConfig();

	private String getFile(String fileName) {

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

	public String getSession(String username, String password) throws ClientProtocolException, IOException {

		String url = config.getBaseUrl() + "session?username=" + username + "&password=" + password + "";
		HttpPost request = new HttpPost(url);
		
		config.setUsername(username);
		config.setPassword(password);
		
		URIBuilder newBuilder = new URIBuilder(request.getURI());
		List<NameValuePair> params = newBuilder.getQueryParams();

		HttpResponse response = client.execute(request);
		String finalUrl = request.getRequestLine().toString();
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode() + "\n URL: " + finalUrl + " "
				+ params.toString());

		logger.info("Response status logged: " + response.getStatusLine().getStatusCode());
		
		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity);
        System.out.println(result);

		JsonObject jsonObject = (new JsonParser()).parse(result.toString()).getAsJsonObject();
		// jsonObject.get("sessionId");

		logger.info("" + jsonObject.get("sessionId"));

		config.setSessionId(jsonObject.get("sessionId").getAsString());
		return result.toString();// jsonResponse;

	}
	
	// create patient demographic
	public String createPatientDefault() throws ClientProtocolException, IOException {
		String body = getFile("assets/sample_requests/party.json");
		String url = config.getBaseUrl()+"demographics/party";
		HttpPost request = new HttpPost(url);
		request.addHeader("Ehr-Session", config.getSessionId()); 
		request.addHeader("Content-Type", "application/json");
		request.setEntity(new StringEntity(body));
		HttpResponse response = client.execute(request);
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
		
		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity);
        //System.out.println(result);
		
		JsonObject jsonObject = (new JsonParser()).parse(result.toString()).getAsJsonObject();
		JsonObject jsonSubObject = jsonObject.getAsJsonObject("meta");
		String partyStringHref = jsonSubObject.get("href").getAsString();
		String partyID =  partyStringHref.substring(partyStringHref.lastIndexOf("/")+1);
		System.out.println(partyID);
		config.setSubjectId(partyID);
		return result;
	}

	// TODO skip provisioning step by deciding how to handle the subjectIDs
	// maybe use the sessionID as the subjectID? too much of a hack perhaps
	// need some way of finding an unused subjectID from the server or perhaps
	// if we are provisioning 500 patients simply increment each time
	// Could check the subjectIDs manually but is this overkill
	// for now use sessionID maybe and a random number unique id concatenated

	// Could create a uniqueID for now too, and test if an ehr exists for that if not try a new id
	// get the response code back and then take appropriate action
	// and then create the ehr
	
	public String createEhr(String subjectID, String namespace, String commiter)
			throws ClientProtocolException, IOException {
		String url = config.getBaseUrl() + "ehr?subjectId=" + subjectID + "&subjectNamespace=" + namespace
				+ "&commiterName=" + commiter;
		HttpPost request = new HttpPost(url);
		request.addHeader("Ehr-Session", config.getSessionId()); 
		logger.info("The current session is" + config.getSessionId());
		String finalUrl = request.getRequestLine().toString();
		System.out.println(finalUrl);
		HttpResponse response = client.execute(request);
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode() + "\n URL: " + finalUrl);
		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity);
        System.out.println(result);
        
		JsonObject jsonObject = (new JsonParser()).parse(result.toString()).getAsJsonObject();
		logger.info("" + jsonObject.get("ehrId"));

		config.setEhrId(jsonObject.get("ehrId").getAsString());
		config.setSubjectId(subjectID);
		
		return result.toString();
		
	}

	public String uploadDefaultTemplate() throws IOException {
		// get the template
		String body = getFile("assets/sample_requests/vital-signs/vital-signs-template.xml");
		System.out.println(body.length());
		String url = config.getBaseUrl() + "template/";
		HttpPost request = new HttpPost(url);
		request.addHeader("Ehr-Session", config.getSessionId()); 
		request.addHeader("Content-Type", "application/xml");
		request.setEntity(new StringEntity(body));

		logger.info("The current session is" + config.getSessionId());
		String finalUrl = request.getRequestLine().toString();
		System.out.println(finalUrl);

		HttpResponse response = client.execute(request);
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
		
		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity);
        System.out.println(result);
		
		// JsonObject jsonObject = (new
		// JsonParser()).parse(result.toString()).getAsJsonObject();
		return result.toString();
	}

	// Composition

	public String uploadDefaultComposition() throws ClientProtocolException, IOException, URISyntaxException {
		String body = getFile("assets/sample_requests/vital-signs/vital-signs-composition.json");
		System.out.println(body.length());
		System.out.println(config.getEhrId());
		System.out.println(config.getTemplateId());
		System.out.println(config.getCommiterName());
		String url = config.getBaseUrl() + "composition?ehrId=" + config.getEhrId() + "&templateId="
				+ config.getTemplateId() + "&committerName=" + config.getCommiterName();
		URIBuilder ub = new URIBuilder(config.getBaseUrl()+"composition");
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
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
		
		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity);
        System.out.println(result);
		
		JsonObject jsonObject = (new JsonParser()).parse(result.toString()).getAsJsonObject();
		logger.info("" + jsonObject.get("compositionUid"));

		config.setCompositionId(jsonObject.get("compositionUid").getAsString()); 
		
		return result.toString();
	}
	
	// FHIR Demographic call
	
	public String createFhirPatientDemographic() {
		// tutorial: https://fhir-drills.github.io/fhir-api.html
		// documentation - http://hapifhir.io/apidocs-dstu3/index.html
		
		// context - create this once, as it's an expensive operation
        // see http://hapifhir.io/doc_intro.html
        // FhirContext ctx = FhirContext.forDstu3();
		// now a class instance

        Patient patient = new Patient();

        // you can use the Fluent API to chain calls
        // see http://hapifhir.io/doc_fhirobjects.html
        patient.addName().setUse(HumanName.NameUse.OFFICIAL)
                .addPrefix("Mr").setFamily("Walford").addGiven("Steve");
        patient.addIdentifier()
                .setSystem("http://fhir.nhs.net/Id/nhs-number")
                .setValue("7430555");
        List<StringType> addressList = new ArrayList<StringType>();
        StringType st = new StringType("60 Florida Gardens");
        addressList.add(st);
        patient.addAddress()
        		.setCity("Cardiff").setPostalCode("LS23 4RT").setLine(addressList)
        		.setState("Glamorgan").setText("60 Florida Gardens, Cardiff, Glamorgan, LS23 4RT");
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH,12);
        cal.set(Calendar.MONTH,6); // month - 1
        cal.set(Calendar.YEAR,1965);

        Date d = cal.getTime();
        patient.setBirthDate(d);
        
        patient.addTelecom(new ContactPoint().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue("011981 32362"));
        
        patient.setGender(AdministrativeGender.MALE);

        // create a new XML parser and serialise our Patient object with it
        String encoded = ctx.newXmlParser().setPrettyPrint(true)
                .encodeResourceToString(patient);

        //System.out.println(encoded);
        
        return encoded;

	}
	
	// MULTIPLE PATIENT

}