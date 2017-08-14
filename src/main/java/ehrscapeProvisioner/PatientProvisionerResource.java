package ehrscapeProvisioner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.client.ClientProtocolException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ehrscapeProvisioner.model.EhrscapeRequest;
import ehrscapeProvisioner.model.PatientDemographic;

/**
 * Root resource (exposed at "provision" path)
 */
@Path("provision")
public class PatientProvisionerResource {

	// TODO change these strings to Response objects and use the constituent
	// responses to return relevant errors
	// return feedback if the requests fail
	// TODO make a new resource class with the invidual requests for the
	// frontend to access directly

	@POST
	@Path("single-provision-no-demographic")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String singleProvision(String inputBody) throws ClientProtocolException, IOException, URISyntaxException {
		EhrscapeRequest req = new EhrscapeRequest();

		Gson gson = new Gson();
		JsonObject jsonInput = (new JsonParser()).parse(inputBody.toString()).getAsJsonObject();
		//System.out.println(jsonInput.get("username").getAsString());
		//System.out.println(jsonInput.get("password").getAsString());

		// Check if user wants to overwrite the base url
		if (jsonInput.has("baseUrl")) {
			EhrscapeRequest.config.setBaseUrl(jsonInput.get("baseUrl").getAsString());
		}

		Response getSessionResponse = req.getSession(jsonInput.get("username").getAsString(),
				jsonInput.get("password").getAsString());
		//System.out.println(EhrscapeRequest.config.getSessionId());
		Response createEhrResponse = req.createEhr(EhrscapeRequest.config.getSessionId(), "JarrodEhrscapeProvisioner");
		Response uploadTemplateResponse = req.uploadDefaultTemplate();
		Response uploadCompResponse = req.uploadDefaultComposition();

		// put the final response stuff here

		// JsonObject jsonOutput = new JsonObject();
		// jsonOutput.addProperty("num", 123);
		// jsonOutput.addProperty("testKey", "testVal"); // for a custom
		// response later if needed

		String finalConfig = gson.toJson(EhrscapeRequest.config);
		// //System.out.println(jsonInput.toString());
		return finalConfig; // gson.toJson(jsonOutput);
	}

	@POST
	@Path("single-provision-marand")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String singleProvisionDemographic(String inputBody)
			throws ClientProtocolException, IOException, URISyntaxException {
		EhrscapeRequest req = new EhrscapeRequest();

		Gson gson = new Gson();
		JsonObject jsonInput = (new JsonParser()).parse(inputBody.toString()).getAsJsonObject();
		//System.out.println(jsonInput.get("username").getAsString());
		//System.out.println(jsonInput.get("password").getAsString());

		// Check if user wants to overwrite the base url
		if (jsonInput.has("baseUrl")) {
			EhrscapeRequest.config.setBaseUrl(jsonInput.get("baseUrl").getAsString());
		}

		Response getSessionResponse = req.getSession(jsonInput.get("username").getAsString(),
				jsonInput.get("password").getAsString());
		Response createPatientDemographicResponse = req.createPatientDefault();
		Response createEhrResponse = req.createEhr(EhrscapeRequest.config.getSubjectId(), "JarrodEhrscapeProvisioner");
		// replace uk.nhs.nhs_number , let that be a user input
		// make the default https://fhir.nhs.uk/Id/nhs-number but make this a
		// customisable input
		Response uploadTemplateResponse = req.uploadDefaultTemplate();
		Response uploadCompResponse = req.uploadDefaultComposition();

		// put the final response stuff here

		// JsonObject jsonOutput = new JsonObject();
		// jsonOutput.addProperty("num", 123);
		// jsonOutput.addProperty("testKey", "testVal"); // for a custom
		// response later if needed

		String finalConfig = gson.toJson(EhrscapeRequest.config);
		// System.out.println(jsonInput.toString());
		return finalConfig; // gson.toJson(jsonOutput);
	}

	@POST
	@Path("single-provision-fhir")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String singleProvisionFhirDemographic(String inputBody)
			throws ClientProtocolException, IOException, URISyntaxException {
		EhrscapeRequest req = new EhrscapeRequest();

		Gson gson = new Gson();
		JsonObject jsonInput = (new JsonParser()).parse(inputBody.toString()).getAsJsonObject();
		//System.out.println(jsonInput.get("username").getAsString());
		//System.out.println(jsonInput.get("password").getAsString());

		// Check if user wants to overwrite the base url
		if (jsonInput.has("baseUrl")) {
			EhrscapeRequest.config.setBaseUrl(jsonInput.get("baseUrl").getAsString());
		}

		Response getSessionResponse = req.getSession(jsonInput.get("username").getAsString(),
				jsonInput.get("password").getAsString());
		Response createPatientDemographicResponse = req.createDefaultFhirPatientDemographic();
		Response createEhrResponse = req.createEhr(EhrscapeRequest.config.getSubjectId(), "JarrodEhrscapeProvisioner");
		// replace uk.nhs.nhs_number , let that be a user input
		// make the default https://fhir.nhs.uk/Id/nhs-number but make this a
		// customisable input
		Response uploadTemplateResponse = req.uploadDefaultTemplate();
		Response uploadCompResponse = req.uploadDefaultComposition();

		// put the final response stuff here

		// JsonObject jsonOutput = new JsonObject();
		// jsonOutput.addProperty("num", 123);
		// jsonOutput.addProperty("testKey", "testVal"); // for a custom
		// response later if needed

		String finalConfig = gson.toJson(EhrscapeRequest.config);
		// System.out.println(jsonInput.toString());
		return finalConfig; // gson.toJson(jsonOutput);
	}

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
		//System.out.println(user);
		//System.out.println(pass);

		// Check if user wants to overwrite the base url
		if (jsonInput.has("baseUrl")) {
			EhrscapeRequest.config.setBaseUrl(jsonInput.get("baseUrl").getAsString());
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
		// finalJsonResponse.addProperty("Upload Allergies Template Response",
		// allergiesUploadTemplateRes.getEntity().toString());
		if (allergiesUploadTemplateRes.getStatus() == 400 || allergiesUploadTemplateRes.getStatus() == 403) {
			finalJsonResponse.addProperty("Error", "Failed to upload allergies template");
			return Response.status(allergiesUploadTemplateRes.getStatus()).entity(finalJsonResponse.toString())
					.type(MediaType.APPLICATION_JSON).build();
		}
		Response problemsUploadTemplateRes = req.uploadTemplate(problemsTemplateBody);
		JsonElement problemsElement = parser.parse(problemsUploadTemplateRes.getEntity().toString());
		finalJsonResponse.add("Upload Problems Template Response", problemsElement);
		// finalJsonResponse.addProperty("Upload Problems Template Response",
		// allergiesUploadTemplateRes.getEntity().toString());
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
		// finalJsonResponse.addProperty("Upload Procedures Template Response",
		// allergiesUploadTemplateRes.getEntity().toString());
		if (proceduresUploadTemplateRes.getStatus() == 400 || proceduresUploadTemplateRes.getStatus() == 403) {
			finalJsonResponse.addProperty("Error", "Failed to upload procedures template");
			return Response.status(proceduresUploadTemplateRes.getStatus()).entity(finalJsonResponse.toString())
					.type(MediaType.APPLICATION_JSON).build();
		}
		Response labResultsUploadTemplateRes = req.uploadTemplate(labResultsTemplateBody);
		JsonElement labResultsElement = parser.parse(labResultsUploadTemplateRes.getEntity().toString());
		finalJsonResponse.add("Upload Lab Results Template Response", labResultsElement);
		// finalJsonResponse.addProperty("Upload Lab Results Template Response",
		// allergiesUploadTemplateRes.getEntity().toString());
		if (labResultsUploadTemplateRes.getStatus() == 400 || labResultsUploadTemplateRes.getStatus() == 403) {
			finalJsonResponse.addProperty("Error", "Failed to upload lab results template");
			return Response.status(labResultsUploadTemplateRes.getStatus()).entity(finalJsonResponse.toString())
					.type(MediaType.APPLICATION_JSON).build();
		}

		// go through patients csv file
		List<PatientDemographic> patientList = req.readPatientCsvToObjectlist(EhrscapeRequest.config.getPatientsFile());
		// for each patient:
		int patientsSuccessfullyUploaded = 0;
		int numOfPatientUploadErrors = 0;
		StringBuilder patientUploadErrorsSb = new StringBuilder();
		for (PatientDemographic patient : patientList) {
			// demographics
			String marandPartyJson = patient.toMarandPartyJson();
			// System.out.println(patient.toMarandPartyJson());
			Response demographicResponse = req.createMarandPatientDemographic(marandPartyJson);
			// if creating the demographic fails move onto next patient
			if (demographicResponse.getStatus() == 400 || demographicResponse.getStatus() == 401
					|| demographicResponse.getStatus() == 403 || demographicResponse.getStatus() == 503) {
				patientUploadErrorsSb.append("Create Demographics Party Failed on Patient with Key: " + patient.getKey()
						+ ", Request Status: " + demographicResponse.getStatus() + "\n");
				numOfPatientUploadErrors++;
				continue;
			}
			// atm the subjectid is the marand party id
			// overwrite the subjectID and use the NHS number from the CSV file
			EhrscapeRequest.config.setSubjectId(patient.getNHSNumber());
			// EHR
			// create ehr
			Response createEhrResponse = req.createEhr(EhrscapeRequest.config.getSubjectId(),
					EhrscapeRequest.config.getCommiterName());
			if (createEhrResponse.getStatus() == 401 || createEhrResponse.getStatus() == 403) {
				patientUploadErrorsSb.append("Create EHR Failed on Patient with Key: " + patient.getKey()
						+ ", Request Status: " + createEhrResponse.getStatus() + "\n");
				numOfPatientUploadErrors++;
				continue;
			} else if (createEhrResponse.getStatus() == 400) {
				// but if it already exists get it
				Response getEhrResponse = req.getEhrWithSubjectId(EhrscapeRequest.config.getSubjectId(),
						EhrscapeRequest.config.getSubjectNamespace());
				if (getEhrResponse.getStatus() == 204 || getEhrResponse.getStatus() == 400
						|| getEhrResponse.getStatus() == 401 || getEhrResponse.getStatus() == 403) {
					patientUploadErrorsSb.append("Get EHR Failed on Patient with Key: " + patient.getKey()
							+ ", Request Status: " + getEhrResponse.getStatus() + "\n");
					numOfPatientUploadErrors++;
					continue;
				} else {
					//System.out.println("Got EhrId: " + EhrscapeRequest.config.getEhrId());
				}
			} else {
				//System.out.println("Created EHR: " + EhrscapeRequest.config.getEhrId());
			}

			// compositions
			Response multiCompositionRes = req.uploadMultipleCompositionsDefaultFolders(
					EhrscapeRequest.config.getEhrId(), true, true, true, true, true);
			patientsSuccessfullyUploaded++;
		}

		// vitals + import csv
		String vitalsTemplateBody = req.getFileAsString(assetsBaseFile + "vital-signs/vital-signs-template.xml");
		Response vitalsUploadTemplateRes = req.uploadTemplate(vitalsTemplateBody);
		JsonElement vitalsElement = parser.parse(vitalsUploadTemplateRes.getEntity().toString());
		finalJsonResponse.add("Upload Vitals Template Response", vitalsElement);
		// finalJsonResponse.addProperty("Upload Allergies Template Response",
		// allergiesUploadTemplateRes.getEntity().toString());
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

	@POST
	@Path("multi-patient-custom")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response multiplePatientProvisionCustom(String inputBody)
			throws ClientProtocolException, IOException, URISyntaxException {

		EhrscapeRequest req = new EhrscapeRequest();

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
		//System.out.println(user);
		//System.out.println(pass);

		// Check if user wants to overwrite the base url
		if (jsonInput.has("baseUrl")) {
			EhrscapeRequest.config.setBaseUrl(jsonInput.get("baseUrl").getAsString());
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
		
		//System.out.println("Vitals: " + doVitals);
		//System.out.println("Problems: " + doProblems);
		//System.out.println("Procedures: " + doProcedures);
		//System.out.println("Lab-Results: " + doLabResults);
		//System.out.println("Orders: " + doOrders);
		//System.out.println("Allergies: " + doAllergies);
		
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
			// finalJsonResponse.addProperty("Upload Allergies Template
			// Response", allergiesUploadTemplateRes.getEntity().toString());
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
			// finalJsonResponse.addProperty("Upload Problems Template
			// Response", allergiesUploadTemplateRes.getEntity().toString());
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
			// finalJsonResponse.addProperty("Upload Orders Template Response",
			// allergiesUploadTemplateRes.getEntity().toString());
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
			// finalJsonResponse.addProperty("Upload Procedures Template
			// Response", allergiesUploadTemplateRes.getEntity().toString());
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
			// finalJsonResponse.addProperty("Upload Lab Results Template
			// Response", allergiesUploadTemplateRes.getEntity().toString());
			if (labResultsUploadTemplateRes.getStatus() == 400 || labResultsUploadTemplateRes.getStatus() == 403) {
				finalJsonResponse.addProperty("Error", "Failed to upload lab results template");
				return Response.status(labResultsUploadTemplateRes.getStatus()).entity(finalJsonResponse.toString())
						.type(MediaType.APPLICATION_JSON).build();
			}
		}

		// go through patients csv file
		List<PatientDemographic> patientList = req.readPatientCsvToObjectlist(EhrscapeRequest.config.getPatientsFile());
		// for each patient:
		int patientsSuccessfullyUploaded = 0;
		int numOfPatientUploadErrors = 0;
		StringBuilder patientUploadErrorsSb = new StringBuilder();
		for (PatientDemographic patient : patientList) {
			// demographics
			String marandPartyJson = patient.toMarandPartyJson();
			// //System.out.println(patient.toMarandPartyJson());
			Response demographicResponse = req.createMarandPatientDemographic(marandPartyJson);
			// if creating the demographic fails move onto next patient
			if (demographicResponse.getStatus() == 400 || demographicResponse.getStatus() == 401
					|| demographicResponse.getStatus() == 403 || demographicResponse.getStatus() == 503) {
				patientUploadErrorsSb.append("Create Demographics Party Failed on Patient with Key: " + patient.getKey()
						+ ", Request Status: " + demographicResponse.getStatus() + "\n");
				numOfPatientUploadErrors++;
				continue;
			}
			// atm the subjectid is the marand party id
			// overwrite the subjectID and use the NHS number from the CSV file
			EhrscapeRequest.config.setSubjectId(patient.getNHSNumber());
			// EHR
			// create ehr
			Response createEhrResponse = req.createEhr(EhrscapeRequest.config.getSubjectId(),
					EhrscapeRequest.config.getCommiterName());
			if (createEhrResponse.getStatus() == 401 || createEhrResponse.getStatus() == 403) {
				patientUploadErrorsSb.append("Create EHR Failed on Patient with Key: " + patient.getKey()
						+ ", Request Status: " + createEhrResponse.getStatus() + "\n");
				numOfPatientUploadErrors++;
				continue;
			} else if (createEhrResponse.getStatus() == 400) {
				// but if it already exists get it
				Response getEhrResponse = req.getEhrWithSubjectId(EhrscapeRequest.config.getSubjectId(),
						EhrscapeRequest.config.getSubjectNamespace());
				if (getEhrResponse.getStatus() == 204 || getEhrResponse.getStatus() == 400
						|| getEhrResponse.getStatus() == 401 || getEhrResponse.getStatus() == 403) {
					patientUploadErrorsSb.append("Get EHR Failed on Patient with Key: " + patient.getKey()
							+ ", Request Status: " + getEhrResponse.getStatus() + "\n");
					numOfPatientUploadErrors++;
					continue;
				} else {
					//System.out.println("Got EhrId: " + EhrscapeRequest.config.getEhrId());
				}
			} else {
				//System.out.println("Created EHR: " + EhrscapeRequest.config.getEhrId());
			}

			// compositions
			Response multiCompositionRes = req.uploadMultipleCompositionsDefaultFolders(
					EhrscapeRequest.config.getEhrId(), doAllergies, doOrders, doProblems, doProcedures, doLabResults);
			patientsSuccessfullyUploaded++;
		}

		// vitals + import csv
		if (doVitals) {
			String vitalsTemplateBody = req.getFileAsString(assetsBaseFile + "vital-signs/vital-signs-template.xml");
			Response vitalsUploadTemplateRes = req.uploadTemplate(vitalsTemplateBody);
			JsonElement vitalsElement = parser.parse(vitalsUploadTemplateRes.getEntity().toString());
			finalJsonResponse.add("Upload Vitals Template Response", vitalsElement);
			// finalJsonResponse.addProperty("Upload Allergies Template
			// Response", allergiesUploadTemplateRes.getEntity().toString());
			if (vitalsUploadTemplateRes.getStatus() == 400 || vitalsUploadTemplateRes.getStatus() == 403) {
				finalJsonResponse.addProperty("Error", "Failed to upload allergies template");
				return Response.status(vitalsUploadTemplateRes.getStatus()).entity(finalJsonResponse.toString())
						.type(MediaType.APPLICATION_JSON).build();
			}
			Response importCsvResponse = req.importCsv("assets/data/nursing-obs.csv");
			finalJsonResponse.addProperty("importCSV", importCsvResponse.getEntity().toString());
		}

		finalJsonResponse.addProperty("Errors", numOfPatientUploadErrors);
		finalJsonResponse.addProperty("Number uploaded", patientsSuccessfullyUploaded);
		
		return Response.status(200).entity(finalJsonResponse.toString()).type(MediaType.APPLICATION_JSON).build();
	}

}
