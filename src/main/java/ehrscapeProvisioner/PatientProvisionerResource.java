package ehrscapeProvisioner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.http.client.ClientProtocolException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ehrscapeProvisioner.model.EhrscapeRequest;
import ehrscapeProvisioner.model.PatientDemographic;
import ehrscapeProvisioner.ticketDao.MultiPatientProvisionerTicket;
import ehrscapeProvisioner.ticketDao.MySqlTicketDao;

/**
 * This classes methods are accessible to users as RESTful resources using Jersey. It contains the provisioning scripts and ticket
 * implementation
 */
@Path("provision")
public class PatientProvisionerResource {

	/**
	 * This method carries out the provisioning of a single patient with no demographics created
	 * @param inputBody a JSON input the users enter to configure the script, with their domain credentials for instance
	 * @return HTTP Response object
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@POST
	@Path("single-provision-no-demographic")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response singleProvision(String inputBody) throws ClientProtocolException, IOException, URISyntaxException {
		EhrscapeRequest req = new EhrscapeRequest();

		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		JsonObject jsonInput = parser.parse(inputBody.toString()).getAsJsonObject();
		// put the final response stuff here
		JsonObject jsonOutput = new JsonObject();
		// Check if user wants to overwrite the base url
		if (jsonInput.has("baseUrl")) {
			req.config.setBaseUrl(jsonInput.get("baseUrl").getAsString());
		}
		Response getSessionResponse = req.getSession(jsonInput.get("username").getAsString(),
				jsonInput.get("password").getAsString());
		JsonElement sessionElement = parser.parse(getSessionResponse.getEntity().toString());
		jsonOutput.add("Get Session:", sessionElement);
		if (getSessionResponse.getStatus() == 400 || getSessionResponse.getStatus() == 401) {
			String finalConfig = gson.toJson(req.config);
			return Response.status(200).entity(jsonOutput.toString()).build();
		}
		Response createEhrResponse = req.createEhr(req.config.getSessionId(), req.config.getCommiterName());
		JsonElement ehrElement = parser.parse(createEhrResponse.getEntity().toString());
		jsonOutput.add("Create EHR:", ehrElement);
		Response uploadTemplateResponse = req.uploadDefaultTemplate();
		JsonElement templateElement = parser.parse(uploadTemplateResponse.getEntity().toString());
		jsonOutput.add("Upload Template:", templateElement);
		Response uploadCompResponse = req.uploadDefaultComposition();
		JsonElement compositionElement = parser.parse(uploadCompResponse.getEntity().toString());
		jsonOutput.add("Commit Composition:", compositionElement);

		String finalConfig = gson.toJson(req.config);
		JsonElement configElement = parser.parse(finalConfig);
		jsonOutput.add("Final Configuration", configElement);
		return Response.status(200).entity(jsonOutput.toString()).build();
	}

	/**
	 * This method carries out the provisioning of a single patient with a marand demographic resource created too
	 * @param inputBody a JSON input the users enter to configure the script, with their domain credentials for instance
	 * @return HTTP Response object
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@POST
	@Path("single-provision-marand")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response singleProvisionDemographic(String inputBody)
			throws ClientProtocolException, IOException, URISyntaxException {
		EhrscapeRequest req = new EhrscapeRequest();
		JsonParser parser = new JsonParser();
		Gson gson = new Gson();
		JsonObject jsonInput = parser.parse(inputBody.toString()).getAsJsonObject();
		// Check if user wants to overwrite the base url
		if (jsonInput.has("baseUrl")) {
			req.config.setBaseUrl(jsonInput.get("baseUrl").getAsString());
		}
		
		// put the final response stuff here
		JsonObject jsonOutput = new JsonObject();
		
		Response getSessionResponse = req.getSession(jsonInput.get("username").getAsString(),
				jsonInput.get("password").getAsString());
		JsonElement sessionElement = parser.parse(getSessionResponse.getEntity().toString());
		jsonOutput.add("Get Session:", sessionElement);
		if (getSessionResponse.getStatus() == 400 || getSessionResponse.getStatus() == 401) {
			String finalConfig = gson.toJson(req.config);
			return Response.status(200).entity(jsonOutput.toString()).build();
		}
		Response createPatientDemographicResponse = req.createPatientDefault();
		JsonElement demographicElement = parser.parse(createPatientDemographicResponse.getEntity().toString());
		jsonOutput.add("Marand Demographic:", demographicElement);
		Response createEhrResponse = req.createEhr(req.config.getSubjectId(), req.config.getCommiterName());
		JsonElement ehrElement = parser.parse(createEhrResponse.getEntity().toString());
		jsonOutput.add("Create EHR:", ehrElement);
		Response uploadTemplateResponse = req.uploadDefaultTemplate();
		JsonElement templateElement = parser.parse(uploadTemplateResponse.getEntity().toString());
		jsonOutput.add("Upload Template:", templateElement);
		Response uploadCompResponse = req.uploadDefaultComposition();
		JsonElement compositionElement = parser.parse(uploadCompResponse.getEntity().toString());
		jsonOutput.add("Commit Composition:", compositionElement);

		String finalConfig = gson.toJson(req.config);
		JsonElement configElement = parser.parse(finalConfig);
		jsonOutput.add("Final Configuration", configElement);
		// System.out.println(jsonInput.toString());
		return Response.status(200).entity(jsonOutput.toString()).build(); // gson.toJson(jsonOutput);
	}

	/**
	 * This method carries out the provisioning of a single patient with a FHIR Patient resource created too
	 * @param inputBody a JSON input the users enter to configure the script, with their domain credentials for instance
	 * @return HTTP Response object
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@POST
	@Path("single-provision-fhir")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response singleProvisionFhirDemographic(String inputBody)
			throws ClientProtocolException, IOException, URISyntaxException {
		EhrscapeRequest req = new EhrscapeRequest();

		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		JsonObject jsonInput = parser.parse(inputBody.toString()).getAsJsonObject();
		// put the final response stuff here

		JsonObject jsonOutput = new JsonObject();
		
		// Check if user wants to overwrite the base url
		if (jsonInput.has("baseUrl")) {
			req.config.setBaseUrl(jsonInput.get("baseUrl").getAsString());
		}
		// for when fhir server location changes 
		if (jsonInput.has("fhirDemographicBaseUrl")) {
			req.config.setFhirDemographicBaseUrl(jsonInput.get("fhirDemographicBaseUrl").getAsString());
		}

		Response getSessionResponse = req.getSession(jsonInput.get("username").getAsString(),
				jsonInput.get("password").getAsString());
		JsonElement sessionElement = parser.parse(getSessionResponse.getEntity().toString());
		jsonOutput.add("Get Session:", sessionElement);
		if (getSessionResponse.getStatus() == 400 || getSessionResponse.getStatus() == 401) {
			String finalConfig = gson.toJson(req.config);
			return Response.status(200).entity(jsonOutput.toString()).build();
		}
		Response createPatientDemographicResponse = req.createDefaultFhirPatientDemographic();
		JsonElement fhirElement = parser.parse(createPatientDemographicResponse.getEntity().toString());
		jsonOutput.add("FHIR demographic:", fhirElement);
		Response createEhrResponse = req.createEhr(req.config.getSubjectId(), req.config.getCommiterName());
		JsonElement ehrElement = parser.parse(createEhrResponse.getEntity().toString());
		jsonOutput.add("Create EHR:", ehrElement);
		Response uploadTemplateResponse = req.uploadDefaultTemplate();
		JsonElement templateElement = parser.parse(uploadTemplateResponse.getEntity().toString());
		jsonOutput.add("Upload Template:", templateElement);
		Response uploadCompResponse = req.uploadDefaultComposition();
		JsonElement compositionElement = parser.parse(uploadCompResponse.getEntity().toString());
		jsonOutput.add("Commit Composition:", compositionElement);

		String finalConfig = gson.toJson(req.config);
		JsonElement configElement = parser.parse(finalConfig);
		jsonOutput.add("Final Configuration", configElement);
		
		return Response.status(200).entity(jsonOutput.toString()).build(); 
	}
	
	/**
	 * This is a multi patient provisioning script, implemented with the exact same settings as the previous builds configuration,
	 * included as a starting point for the client
	 * @param inputBody a JSON input the users enter to configure the script, with their domain credentials for instance
	 * @return HTTP Response object
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@POST
	@Path("multi-patient-default")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response multiplePatientProvisionDefault(String inputBody)
			throws ClientProtocolException, IOException, URISyntaxException {

		EhrscapeRequest req = new EhrscapeRequest();
		// parse the request body
		JsonParser parser = new JsonParser();
		JsonObject jsonInput = (parser.parse(inputBody.toString()).getAsJsonObject());
		String user = jsonInput.get("username").getAsString();
		String pass = jsonInput.get("password").getAsString();

		// Check if user wants to overwrite the base url
		if (jsonInput.has("baseUrl")) {
			req.config.setBaseUrl(jsonInput.get("baseUrl").getAsString());
		}
		if (jsonInput.has("patientsFile")) {
			req.config.setPatientsFile(jsonInput.get("patientsFile").getAsString());
		}

		// prepare the response
		JsonObject finalJsonResponse = new JsonObject();

		// create Session
		Response createSessionRes = req.getSession(user, pass);
		JsonElement responseJsonElement = parser.parse(createSessionRes.getEntity().toString());
		finalJsonResponse.add("Get Session Response", responseJsonElement);
		if (createSessionRes.getStatus() == 400 || createSessionRes.getStatus() == 401) {
			finalJsonResponse.addProperty("Error", "Failed to create session");
			return Response.status(createSessionRes.getStatus()).entity(finalJsonResponse.toString())
					.type(MediaType.APPLICATION_JSON).build(); // createSessionRes;
		}
		// upload templates
		String assetsBaseFile = "assets/sample_requests/";
		String allergiesTemplateBody = req.getFileAsString(assetsBaseFile + "allergies/allergies-template.xml");
		String problemsTemplateBody = req.getFileAsString(assetsBaseFile + "problems/problems-template.xml");
		String ordersTemplateBody = req.getFileAsString(assetsBaseFile + "orders/orders-template.xml");
		String proceduresTemplateBody = req.getFileAsString(assetsBaseFile + "procedures/procedures-template.xml");
		String labResultsTemplateBody = req.getFileAsString(assetsBaseFile + "lab-results/lab-results-template.xml");

		Response allergiesUploadTemplateRes = req.uploadTemplate(allergiesTemplateBody);
		JsonElement allergyElement = parser.parse(allergiesUploadTemplateRes.getEntity().toString());
		finalJsonResponse.add("Upload Allergies Template Response", allergyElement);

		if (allergiesUploadTemplateRes.getStatus() == 400 || allergiesUploadTemplateRes.getStatus() == 403) {
			finalJsonResponse.addProperty("Error", "Failed to upload allergies template");
			return Response.status(allergiesUploadTemplateRes.getStatus()).entity(finalJsonResponse.toString())
					.type(MediaType.APPLICATION_JSON).build();
		}
		Response problemsUploadTemplateRes = req.uploadTemplate(problemsTemplateBody);
		JsonElement problemsElement = parser.parse(problemsUploadTemplateRes.getEntity().toString());
		finalJsonResponse.add("Upload Problems Template Response", problemsElement);
		if (problemsUploadTemplateRes.getStatus() == 400 || problemsUploadTemplateRes.getStatus() == 403) {
			finalJsonResponse.addProperty("Error", "Failed to upload problems template");
			return Response.status(problemsUploadTemplateRes.getStatus()).entity(finalJsonResponse.toString())
					.type(MediaType.APPLICATION_JSON).build();
		}
		Response ordersUploadTemplateRes = req.uploadTemplate(ordersTemplateBody);
		JsonElement ordersElement = parser.parse(ordersUploadTemplateRes.getEntity().toString());
		finalJsonResponse.add("Upload Orders Template Response", ordersElement);
		// finalJsonResponse.addProperty("Upload Orders Template Response",
		// allergiesUploadTemplateRes.getEntity().toString());
		if (ordersUploadTemplateRes.getStatus() == 400 || ordersUploadTemplateRes.getStatus() == 403) {
			finalJsonResponse.addProperty("Error", "Failed to upload orders template");
			return Response.status(ordersUploadTemplateRes.getStatus()).entity(finalJsonResponse.toString())
					.type(MediaType.APPLICATION_JSON).build();
		}
		Response proceduresUploadTemplateRes = req.uploadTemplate(proceduresTemplateBody);
		JsonElement proceduresElement = parser.parse(proceduresUploadTemplateRes.getEntity().toString());
		finalJsonResponse.add("Upload Procedures Template Response", proceduresElement);
		if (proceduresUploadTemplateRes.getStatus() == 400 || proceduresUploadTemplateRes.getStatus() == 403) {
			finalJsonResponse.addProperty("Error", "Failed to upload procedures template");
			return Response.status(proceduresUploadTemplateRes.getStatus()).entity(finalJsonResponse.toString())
					.type(MediaType.APPLICATION_JSON).build();
		}
		Response labResultsUploadTemplateRes = req.uploadTemplate(labResultsTemplateBody);
		JsonElement labResultsElement = parser.parse(labResultsUploadTemplateRes.getEntity().toString());
		finalJsonResponse.add("Upload Lab Results Template Response", labResultsElement);
		if (labResultsUploadTemplateRes.getStatus() == 400 || labResultsUploadTemplateRes.getStatus() == 403) {
			finalJsonResponse.addProperty("Error", "Failed to upload lab results template");
			return Response.status(labResultsUploadTemplateRes.getStatus()).entity(finalJsonResponse.toString())
					.type(MediaType.APPLICATION_JSON).build();
		}

		// go through patients csv file
		List<PatientDemographic> patientList = req.readPatientCsvToObjectlist(req.config.getPatientsFile());
		// for each patient:
		int patientsSuccessfullyUploaded = 0;
		int numOfPatientUploadErrors = 0;
		StringBuilder patientUploadErrorsSb = new StringBuilder();
		for (PatientDemographic patient : patientList) {
			// demographics
			String marandPartyJson = patient.toMarandPartyJson();
			Response demographicResponse = req.createMarandPatientDemographic(marandPartyJson);
			// if creating the demographic fails move onto next patient
			if (demographicResponse.getStatus() == 400 || demographicResponse.getStatus() == 401
					|| demographicResponse.getStatus() == 403 || demographicResponse.getStatus() == 503) {
				patientUploadErrorsSb.append("Create Demographics Party Failed on Patient with Key: " + patient.getKey()
						+ ", Request Status: " + demographicResponse.getStatus() + "\n");
				numOfPatientUploadErrors++;
				continue;
			}

			JsonElement demographicElement = parser.parse(demographicResponse.getEntity().toString());
			finalJsonResponse.add("Create Patient Demographic Response - Patient key: " + patient.getKey(), demographicElement);
			// as it stands the subjectid is the marand party id
			// overwrite the subjectID and use the NHS number from the CSV file
			// this is exactly how the previous version worked
			req.config.setSubjectId(patient.getNHSNumber());
			// EHR
			// create ehr
			Response createEhrResponse = req.createEhr(req.config.getSubjectId(),
					req.config.getCommiterName());
			if (createEhrResponse.getStatus() == 401 || createEhrResponse.getStatus() == 403) {
				patientUploadErrorsSb.append("Create EHR Failed on Patient with Key: " + patient.getKey()
						+ ", Request Status: " + createEhrResponse.getStatus() + "\n");
				numOfPatientUploadErrors++;
				continue;
			} else if (createEhrResponse.getStatus() == 400) {
				// but if it already exists get it
				Response getEhrResponse = req.getEhrWithSubjectId(req.config.getSubjectId(),
						req.config.getSubjectNamespace());
				if (getEhrResponse.getStatus() == 204 || getEhrResponse.getStatus() == 400
						|| getEhrResponse.getStatus() == 401 || getEhrResponse.getStatus() == 403) {
					patientUploadErrorsSb.append("Get EHR Failed on Patient with Key: " + patient.getKey()
							+ ", Request Status: " + getEhrResponse.getStatus() + "\n");
					numOfPatientUploadErrors++;
					continue;
				} else {
					JsonElement EhrElement = parser.parse(getEhrResponse.getEntity().toString());
					finalJsonResponse.add("Create Patient Demographic Response - Patient key: " + patient.getKey(), EhrElement);
				}
			} else {
				JsonElement EhrElement = parser.parse(createEhrResponse.getEntity().toString());
				finalJsonResponse.add("Create Patient Demographic Response - Patient key: " + patient.getKey(), EhrElement);
			}
			// compositions
			Response multiCompositionRes = req.uploadMultipleCompositionsDefaultFolders(
					req.config.getEhrId(), true, true, true, true, true);
			JsonElement compositionElement = parser.parse(multiCompositionRes.getEntity().toString());
			finalJsonResponse.add("Commit Composition Response Patient key: " + patient.getKey(), compositionElement);
			patientsSuccessfullyUploaded++;
		}

		// vitals + import csv
		String vitalsTemplateBody = req.getFileAsString(assetsBaseFile + "vital-signs/vital-signs-template.xml");
		Response vitalsUploadTemplateRes = req.uploadTemplate(vitalsTemplateBody);
		JsonElement vitalsElement = parser.parse(vitalsUploadTemplateRes.getEntity().toString());
		finalJsonResponse.add("Upload Vitals Template Response", vitalsElement);
		if (vitalsUploadTemplateRes.getStatus() == 400 || vitalsUploadTemplateRes.getStatus() == 403) {
			finalJsonResponse.addProperty("Error", "Failed to upload allergies template");
			return Response.status(vitalsUploadTemplateRes.getStatus()).entity(finalJsonResponse.toString())
					.type(MediaType.APPLICATION_JSON).build();
		}

		Response importCsvResponse = req.importCsv("assets/data/nursing-obs.csv");
		finalJsonResponse.addProperty("importCSV", importCsvResponse.getEntity().toString());

		finalJsonResponse.addProperty("Errors", numOfPatientUploadErrors);
		finalJsonResponse.addProperty("Number uploaded", patientsSuccessfullyUploaded);

		return Response.status(200).entity(finalJsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
	}
	
	/**
	 * This is a multi patient provisioning script, implemented with customisable dummy data
	 * @param inputBody a JSON input the users enter to configure the script, with their domain credentials for instance,
	 * along with customisation options like demographics type and what templates and compositions to upload
	 * @return HTTP Response object
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@POST
	@Path("multi-patient-custom")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response multiplePatientProvisionCustom(String inputBody)
			throws ClientProtocolException, IOException, URISyntaxException {

		EhrscapeRequest req = new EhrscapeRequest();
		
		// demographic configurations
		boolean marandDemographic = false;
		boolean fhirDemographic = false;
		// else no demographics 
		
		// compositions and vitals configuration settings
		boolean doVitals = false;
		boolean doLabResults = false;
		boolean doAllergies = false;
		boolean doOrders = false;
		boolean doProblems = false;
		boolean doProcedures = false;

		// parse the request body
		JsonParser parser = new JsonParser();
		JsonObject jsonInput = (parser.parse(inputBody.toString()).getAsJsonObject());
		String user = jsonInput.get("username").getAsString();
		String pass = jsonInput.get("password").getAsString();

		// Check if user wants to overwrite the base url
		if (jsonInput.has("baseUrl")) {
			req.config.setBaseUrl(jsonInput.get("baseUrl").getAsString());
		}

		if (jsonInput.has("doAllergies")) {
			doAllergies = jsonInput.get("doAllergies").getAsBoolean();
		}

		if (jsonInput.has("doProblems")) {
			doProblems = jsonInput.get("doProblems").getAsBoolean();
		}

		if (jsonInput.has("doProcedures")) {
			doProcedures = jsonInput.get("doProcedures").getAsBoolean();
		}

		if (jsonInput.has("doOrders")) {
			doOrders = jsonInput.get("doOrders").getAsBoolean();
		}

		if (jsonInput.has("doLabResults")) {
			doLabResults = jsonInput.get("doLabResults").getAsBoolean();
		}

		if (jsonInput.has("doVitals")) {
			doVitals = jsonInput.get("doVitals").getAsBoolean();
		}
		if (jsonInput.has("patientsFile")) {
			req.config.setPatientsFile(jsonInput.get("patientsFile").getAsString());
		}
		// for when fhir server location changes 
		if (jsonInput.has("fhirDemographicBaseUrl")) {
			req.config.setFhirDemographicBaseUrl(jsonInput.get("fhirDemographicBaseUrl").getAsString());
		}
		String userDemographicChoice = "";
		if (jsonInput.has("demographicType")) {
			userDemographicChoice = jsonInput.get("demographicType").getAsString();
			if ( (userDemographicChoice.equalsIgnoreCase("fhir")) ) {
				fhirDemographic = true;
				marandDemographic = false;
			} else if (userDemographicChoice.equalsIgnoreCase("marand")) {
				fhirDemographic = false;
				marandDemographic = true;
			} else {
				fhirDemographic = false;
				marandDemographic = false;
				// demographics will be skipped by default here
			}
		}
		
		// prepare the response
		JsonObject finalJsonResponse = new JsonObject();

		// create Session
		Response createSessionRes = req.getSession(user, pass);
		JsonElement responseJsonElement = parser.parse(createSessionRes.getEntity().toString());
		finalJsonResponse.add("Get Session Response", responseJsonElement);
		if (createSessionRes.getStatus() == 400 || createSessionRes.getStatus() == 401) {
			finalJsonResponse.addProperty("Error", "Failed to create session");
			return Response.status(createSessionRes.getStatus()).entity(finalJsonResponse.toString())
					.type(MediaType.APPLICATION_JSON).build(); // createSessionRes;
		}

		// upload templates
		String assetsBaseFile = "assets/sample_requests/";

		if (doAllergies) {
			String allergiesTemplateBody = req.getFileAsString(assetsBaseFile + "allergies/allergies-template.xml");
			Response allergiesUploadTemplateRes = req.uploadTemplate(allergiesTemplateBody);
			JsonElement allergyElement = parser.parse(allergiesUploadTemplateRes.getEntity().toString());
			finalJsonResponse.add("Upload Allergies Template Response", allergyElement);
			if (allergiesUploadTemplateRes.getStatus() == 400 || allergiesUploadTemplateRes.getStatus() == 403) {
				finalJsonResponse.addProperty("Error", "Failed to upload allergies template");
				return Response.status(allergiesUploadTemplateRes.getStatus()).entity(finalJsonResponse.toString())
						.type(MediaType.APPLICATION_JSON).build();
			}
		}

		if (doProblems) {
			String problemsTemplateBody = req.getFileAsString(assetsBaseFile + "problems/problems-template.xml");
			Response problemsUploadTemplateRes = req.uploadTemplate(problemsTemplateBody);
			JsonElement problemsElement = parser.parse(problemsUploadTemplateRes.getEntity().toString());
			finalJsonResponse.add("Upload Problems Template Response", problemsElement);
			if (problemsUploadTemplateRes.getStatus() == 400 || problemsUploadTemplateRes.getStatus() == 403) {
				finalJsonResponse.addProperty("Error", "Failed to upload problems template");
				return Response.status(problemsUploadTemplateRes.getStatus()).entity(finalJsonResponse.toString())
						.type(MediaType.APPLICATION_JSON).build();
			}
		}

		if (doOrders) {
			String ordersTemplateBody = req.getFileAsString(assetsBaseFile + "orders/orders-template.xml");
			Response ordersUploadTemplateRes = req.uploadTemplate(ordersTemplateBody);
			JsonElement ordersElement = parser.parse(ordersUploadTemplateRes.getEntity().toString());
			finalJsonResponse.add("Upload Orders Template Response", ordersElement);
			if (ordersUploadTemplateRes.getStatus() == 400 || ordersUploadTemplateRes.getStatus() == 403) {
				finalJsonResponse.addProperty("Error", "Failed to upload orders template");
				return Response.status(ordersUploadTemplateRes.getStatus()).entity(finalJsonResponse.toString())
						.type(MediaType.APPLICATION_JSON).build();
			}
		}

		if (doProcedures) {
			String proceduresTemplateBody = req.getFileAsString(assetsBaseFile + "procedures/procedures-template.xml");
			Response proceduresUploadTemplateRes = req.uploadTemplate(proceduresTemplateBody);
			JsonElement proceduresElement = parser.parse(proceduresUploadTemplateRes.getEntity().toString());
			finalJsonResponse.add("Upload Procedures Template Response", proceduresElement);
			if (proceduresUploadTemplateRes.getStatus() == 400 || proceduresUploadTemplateRes.getStatus() == 403) {
				finalJsonResponse.addProperty("Error", "Failed to upload procedures template");
				return Response.status(proceduresUploadTemplateRes.getStatus()).entity(finalJsonResponse.toString())
						.type(MediaType.APPLICATION_JSON).build();
			}
		}

		if (doLabResults) {
			String labResultsTemplateBody = req
					.getFileAsString(assetsBaseFile + "lab-results/lab-results-template.xml");
			Response labResultsUploadTemplateRes = req.uploadTemplate(labResultsTemplateBody);
			JsonElement labResultsElement = parser.parse(labResultsUploadTemplateRes.getEntity().toString());
			finalJsonResponse.add("Upload Lab Results Template Response", labResultsElement);
			if (labResultsUploadTemplateRes.getStatus() == 400 || labResultsUploadTemplateRes.getStatus() == 403) {
				finalJsonResponse.addProperty("Error", "Failed to upload lab results template");
				return Response.status(labResultsUploadTemplateRes.getStatus()).entity(finalJsonResponse.toString())
						.type(MediaType.APPLICATION_JSON).build();
			}
		}

		// go through patients csv file
		List<PatientDemographic> patientList = req.readPatientCsvToObjectlist(req.config.getPatientsFile());
		// for each patient:
		int patientsSuccessfullyUploaded = 0;
		int numOfPatientUploadErrors = 0;
		StringBuilder patientUploadErrorsSb = new StringBuilder();
		for (PatientDemographic patient : patientList) {
			// demographics
			if (fhirDemographic == true && marandDemographic == false) {
				// fhir demographic upload
				String fhirPatientBody = patient.encodeInFhirFormat(true); //or .toFhirXML();
				Response demographicResponse = req.createFhirPatientDemographic(req.config.getFhirDemographicBaseUrl()
						, fhirPatientBody);
				// if creating the FHIR demographic fails move onto next patient
				if (demographicResponse.getStatus() != 201) {
					patientUploadErrorsSb.append("Create FHIR Demographic Failed on Patient with Key: " + patient.getKey()
							+ ", Request Status: " + demographicResponse.getStatus() + "\n");
					numOfPatientUploadErrors++;
					continue;
				}
				JsonElement demographicElement = parser.parse(demographicResponse.getEntity().toString());
				finalJsonResponse.add("Create FHIR Demographic Response - Patient key: " + patient.getKey(), demographicElement);
			} else if (fhirDemographic == false && marandDemographic == true) {
				// marand demographic upload
				String marandPartyJson = patient.toMarandPartyJson();
				Response demographicResponse = req.createMarandPatientDemographic(marandPartyJson);
				// if creating the demographic fails move onto next patient
				if (demographicResponse.getStatus() == 400 || demographicResponse.getStatus() == 401
						|| demographicResponse.getStatus() == 403 || demographicResponse.getStatus() == 503) {
					patientUploadErrorsSb.append("Create Demographics Party Failed on Patient with Key: " + patient.getKey()
							+ ", Request Status: " + demographicResponse.getStatus() + "\n");
					numOfPatientUploadErrors++;
					continue;
				}
				JsonElement demographicElement = parser.parse(demographicResponse.getEntity().toString());
				finalJsonResponse.add("Create Patient Demographic Response - Patient key: " + patient.getKey(), demographicElement);
			} else if(fhirDemographic == false && marandDemographic == false) {
				// no demographic upload
				req.config.setSubjectId(patient.getNHSNumber());
			} else {
				// error
				req.config.setSubjectId(patient.getNHSNumber());
				numOfPatientUploadErrors++;
				patientUploadErrorsSb.append("Create Demographics Invalid Request Input, Patient with Key: " + patient.getKey() + "\n");
				continue;
			}
			// As it stands the subjectid is the marand party id
			// overwrite the subjectID and use the NHS number from the CSV file
			// EhrscapeRequest.config.setSubjectId(patient.getNHSNumber());
			// create ehr
			req.config.setSubjectId(patient.getNHSNumber());
			Response createEhrResponse = req.createEhr(req.config.getSubjectId(),
					req.config.getCommiterName());
			if (createEhrResponse.getStatus() == 401 || createEhrResponse.getStatus() == 403) {
				patientUploadErrorsSb.append("Create EHR Failed on Patient with Key: " + patient.getKey()
						+ ", Request Status: " + createEhrResponse.getStatus() + "\n");
				numOfPatientUploadErrors++;
				continue;
			} else if (createEhrResponse.getStatus() == 400) {
				// but if it already exists get it
				Response getEhrResponse = req.getEhrWithSubjectId(req.config.getSubjectId(),
						req.config.getSubjectNamespace());
				if (getEhrResponse.getStatus() == 204 || getEhrResponse.getStatus() == 400
						|| getEhrResponse.getStatus() == 401 || getEhrResponse.getStatus() == 403) {
					patientUploadErrorsSb.append("Get EHR Failed on Patient with Key: " + patient.getKey()
							+ ", Request Status: " + getEhrResponse.getStatus() + "\n");
					numOfPatientUploadErrors++;
					continue;
				} else {
					JsonElement ehrElement = parser.parse(getEhrResponse.getEntity().toString());
					finalJsonResponse.add("Create EHR Response - Patient key: " + patient.getKey(), ehrElement);
				}
			} else {
				JsonElement ehrElement = parser.parse(createEhrResponse.getEntity().toString());
				finalJsonResponse.add("Create EHR Response - Patient key: " + patient.getKey(), ehrElement);
				
			}

			// compositions
			Response multiCompositionRes = req.uploadMultipleCompositionsDefaultFolders(
					req.config.getEhrId(), doAllergies, doOrders, doProblems, doProcedures, doLabResults);
			patientsSuccessfullyUploaded++;
			JsonElement compositionElement = parser.parse(multiCompositionRes.getEntity().toString());
			finalJsonResponse.add("Commit Composition Response Patient key: " + patient.getKey(), compositionElement);
		}

		// vitals + import csv
		if (doVitals) {
			String vitalsTemplateBody = req.getFileAsString(assetsBaseFile + "vital-signs/vital-signs-template.xml");
			Response vitalsUploadTemplateRes = req.uploadTemplate(vitalsTemplateBody);
			JsonElement vitalsElement = parser.parse(vitalsUploadTemplateRes.getEntity().toString());
			finalJsonResponse.add("Upload Vitals Template Response", vitalsElement);
			if (vitalsUploadTemplateRes.getStatus() == 400 || vitalsUploadTemplateRes.getStatus() == 403) {
				finalJsonResponse.addProperty("Error", "Failed to upload allergies template");
				return Response.status(vitalsUploadTemplateRes.getStatus()).entity(finalJsonResponse.toString())
						.type(MediaType.APPLICATION_JSON).build();
			}
			Response importCsvResponse = req.importCsv("assets/data/nursing-obs.csv");
			finalJsonResponse.addProperty("importCSV", importCsvResponse.getEntity().toString());
		}

		finalJsonResponse.addProperty("Errors", numOfPatientUploadErrors);
		finalJsonResponse.addProperty("Error messages:", patientUploadErrorsSb.toString());
		finalJsonResponse.addProperty("Number uploaded", patientsSuccessfullyUploaded);

		return Response.status(200).entity(finalJsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
	}

	// Handling the multiProvisioner Requests in the background
	// otherwise on azure no response is returned as request is too long and its
	// switched off by
	// default after 2 minutes with no response

	// First request starts the script and returns an http 202
	// Subsequent requests from client check the work, and eventually return 200
	// when it's done.

	/**
	 * This method initiates the custom multi patient provisioner script in the background, and immediately returns
	 * an accepted response to the user with a location header directing them to a 'ticket' resource outlining their scripts progress.
	 * When the script completes the tivket resource is updated with the provisioner response
	 * @param inputBody inputBody a JSON input the users enter to configure the script, with their domain credentials for instance,
	 * along with customisation options like demographics type and what templates and compositions to upload
	 * @return HTTP Response object
	 */
	@POST
	@Path("cloud-multi-provisioner")
	public Response cloudMultiProvisioner(String inputBody) {
		MultiPatientProvisionerTicket responseTicket = createTicket();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					Response provisionResponse = multiplePatientProvisionCustom(inputBody);
					String jsonProvisionResBody = provisionResponse.getEntity().toString();
					JsonElement element = (new JsonParser()).parse(jsonProvisionResBody.toString());
					updateTicket(responseTicket.getTicketId(), element, "complete");
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		};

		Thread thread = new Thread(runnable);
		// run multi provisioner in background..
		thread.start(); // starts thread in background..
		return Response.status(Status.ACCEPTED)
				.header("location", "provision/ticket/" + responseTicket.getTicketId()).build();
	}
	
	/**
	 * This method initiates the 'default' multi patient provisioner script in the background, and immediately returns
	 * an accepted response to the user with a location header directing them to a 'ticket' resource outlining their scripts progress.
	 * When the script completes the tivket resource is updated with the provisioner response
	 * @param inputBody inputBody a JSON input the users enter to configure the script, with their domain credentials for instance,
	 * along with customisation options like demographics type and what templates and compositions to upload
	 * @return HTTP Response object
	 */
	@POST
	@Path("cloud-default-multi-provisioner")
	public Response cloudMultiProvisionerDefault(String inputBody) {
		MultiPatientProvisionerTicket responseTicket = createTicket();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					Response provisionResponse = multiplePatientProvisionDefault(inputBody);
					String jsonProvisionResBody = provisionResponse.getEntity().toString();
					JsonElement element = (new JsonParser()).parse(jsonProvisionResBody.toString());
					updateTicket(responseTicket.getTicketId(), element, "complete");
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		};

		Thread thread = new Thread(runnable);
		// run multi provisioner in background..
		thread.start(); // starts thread in background..
		return Response.status(Status.ACCEPTED)
				.header("location", "provision/ticket/" + responseTicket.getTicketId()).build();
	}

	/**
	 * This method finds the stored ticket resource and returns it to the user
	 * @param id - ticketID value
	 * @return HTTP Reponse - with the ticket object as its entity
	 */
	@GET
	@Path("ticket/{ticketId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTicket(@PathParam(value = "ticketId") String id) {
		//FileSystemTicketDao dao = new FileSystemTicketDao();
		MySqlTicketDao dao = new MySqlTicketDao();
		MultiPatientProvisionerTicket ticket;
		try {
			ticket = dao.getTicketRecord(id);
			return Response.status(200).entity(ticket.toJsonObject().toString()).build();
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(204).build();
		}
	}

	/**
	 * This method creates a new Ticket object resource
	 * @return MultiPatientProvisionerTicket resource
	 */
	@Produces(MediaType.APPLICATION_JSON)
	private MultiPatientProvisionerTicket createTicket() {
		//FileSystemTicketDao dao = new FileSystemTicketDao();
		MySqlTicketDao dao = new MySqlTicketDao();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date now = new Date();
		String date = formatter.format(now);
		String uniqueId = UUID.randomUUID().toString();
		MultiPatientProvisionerTicket ticket = new MultiPatientProvisionerTicket(uniqueId, "In Progress", date);
		dao.createTicketRecord(ticket);
		return ticket; 
	}
	
	
	/**
	 * This method updates an existing ticket resource - usually when a provisioing script finishes
	 * @param id ticketId value
	 * @param responseContent provisioner response to update the ticket with
	 * @param status new status of the script
	 * @return MultiPatientProvisionerTicket new ticket reource
	 */
	private MultiPatientProvisionerTicket updateTicket(String id, JsonElement responseContent, String status) {
		//FileSystemTicketDao dao = new FileSystemTicketDao();
		MySqlTicketDao dao = new MySqlTicketDao();
		MultiPatientProvisionerTicket ticket = dao.getTicketRecord(id);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date now = new Date();
		String date = formatter.format(now);
		ticket.setCompletionTime(date);
		ticket.setProvisioningStatus(status);
		ticket.setResponseBody(responseContent);
		//dao.createTicketRecord(ticket);
		dao.updateTicketRecord(ticket);
		return ticket;
	}
}
