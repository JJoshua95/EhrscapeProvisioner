package ehrscapeProvisioner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.client.ClientProtocolException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import au.com.bytecode.opencsv.CSVParser;
import ehrscapeProvisioner.model.EhrscapeRequest;

@Path("import")
public class ImportCsvResource {

	private String csvInputHeader;
	private EhrscapeRequest req = new EhrscapeRequest();
	
	@POST
	@Path("csv")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response csvToCompositions(@HeaderParam("Ehr-Session") String sessionId, String inputCsvBody, 
			@HeaderParam("BaseURL") String baseUrl)
			throws IOException, URISyntaxException {
		// parse the CSV input and turn each row into JsonObject compositions
		// loop through the JSON compositions and upload them to the C4H
		// server
		
		// Check if baseUrl set
		if (baseUrl != null) {
			req.config.setBaseUrl(baseUrl);
		}

		// check if the user has provided a Ehr-Session header 
		if (sessionId != null) {
			// check if it is valid 
			Response PingSessionResponse = req.pingSession(sessionId);
			if (PingSessionResponse.getStatus() == 204) {
				// do the upload
				// set the provided session
				req.config.setSessionId(sessionId);
				String csvResponse = csvBodyToJsonCompositions(inputCsvBody);
				if (csvResponse.equals("ERROR PARSING")) {
					return Response.status(400).entity("Bad request - unreadable CSV data.").type(MediaType.TEXT_PLAIN).build();
				} else {
					return Response.status(200).entity(csvResponse).type(MediaType.TEXT_PLAIN).build();
				}
			} else {
				// return unauthorised response
				return Response.status(401).entity("Unauthenticated - could not authenticate the user").type(MediaType.TEXT_PLAIN).build();
			}
		} else {
			// return an unauthorised response
			return Response.status(401).entity("Unauthenticated - could not authenticate the user").type(MediaType.TEXT_PLAIN).build();
			
		}
		
	}
	
	private String csvBodyToJsonCompositions(String body) throws IOException, URISyntaxException {
		String[] split = body.split("\n");
		csvInputHeader = split[0];
		if (csvInputHeader.contains("subjectId")) {
			JsonObject[] compositionArray = mapToJsonObjects(body);
			String csvResponse = uploadJsonCompositionsArrayWithSubjectId(compositionArray);
			return csvResponse;
		} else if (csvInputHeader.contains("ehrId")) {
			JsonObject[] compositionArray = mapToJsonObjects(body);
			String csvResponse = uploadJsonCompositionsArrayWithEhrId(compositionArray);
			return csvResponse;
		} else {
			// return an error message
			return "ERROR PARSING";
		}
	}

	private JsonObject[] mapToJsonObjects(String body) throws IOException {
		CSVParser csvParser = new CSVParser();
		String csvRows[] = body.split("\n");
		String header = csvRows[0];
		String[] compositionJsonKeys = csvParser.parseLine(header);
		// go through remaining rows
		JsonObject[] jsonCompositionsArray = new JsonObject[csvRows.length - 1];
		for (int i = 1; i < csvRows.length; i++) {
			JsonObject jsonComposition = new JsonObject();
			// look at the individual row
			String csvRow = csvRows[i];
			String csvRowValues[] = csvParser.parseLine(csvRow);
			// loop through the rows components with index j starting from the
			// second element
			// the first element is the subject id - extract that separately to
			// upload the data
			for (int j = 0; j < csvRowValues.length; j++) {
				jsonComposition.addProperty(compositionJsonKeys[j], csvRowValues[j]);
				;
			}
			jsonCompositionsArray[i - 1] = jsonComposition;
		}
		return jsonCompositionsArray;
	}

	private String uploadJsonCompositionsArrayWithSubjectId(JsonObject[] compositionArray)
			throws IOException, URISyntaxException {
		// create the CSV response
		StringBuilder csvResponseSb = new StringBuilder();
		csvResponseSb.append(csvInputHeader + ",compositionUid,errors\n");
		for (int i = 0; i < compositionArray.length; i++) {
			String subjectId = compositionArray[i].get("subjectId").getAsString();
			// remove the subject id from the composition json body before it is
			// posted to the C4H server
			compositionArray[i].remove("subjectId");
			String compositionPostBody = compositionArray[i].toString();
			// //System.out.println(compositionPostBody);

			// Do the post request to upload the composition
			// use subjectId to create Ehr or if it exists retrieve one
			Response ehrCreateResponse = req.createEhr(subjectId, "ImportCsvTool");
			int responseCode = ehrCreateResponse.getStatus();
			String compositionUidCsvString = "";
			String errorCsvValue = "";
			// if the ehr exists get the ehrId
			// then use the ehrId to upload the composition
			if (responseCode == 400) {
				// get ehr
				Response getEhrResponse = req.getEhrWithSubjectId(subjectId,
						req.config.getSubjectNamespace());
				String getEhrResponseBody = getEhrResponse.getEntity().toString();
				// parse the response to get the ehrId
				JsonObject jsonObject = (new JsonParser()).parse(getEhrResponseBody.toString()).getAsJsonObject();
				String ehrId = jsonObject.get("ehrId").getAsString();
				Response uploadCompositionResponse = req.uploadComposition(compositionPostBody,
						req.config.getSessionId(), "Vital Signs Encounter (Composition)",
						"importCsvResource", ehrId);
				String uploadCompositionResponseBody = uploadCompositionResponse.getEntity().toString();
				int uploadCompositionResponseCode = uploadCompositionResponse.getStatus();
				if (uploadCompositionResponseCode == 201 || uploadCompositionResponseCode == 200) {
					// if successfully created
					JsonObject jsonCompositionResponseObject = (new JsonParser())
							.parse(uploadCompositionResponseBody.toString()).getAsJsonObject();
					String compUid = jsonCompositionResponseObject.get("compositionUid").getAsString();
					compositionUidCsvString = compUid;
				} else {
					// failed to upload
					errorCsvValue = "Error with Commit Composition Call: " + uploadCompositionResponseCode + " "
							+ uploadCompositionResponseBody.replaceAll(",", ";");
				}
			} else if (responseCode == 201) {
				// or just upload composition for the newly created EHR
				String ehrCreateResponseBody = ehrCreateResponse.getEntity().toString();
				JsonObject jsonNewEhrResponseObject = (new JsonParser()).parse(ehrCreateResponseBody.toString())
						.getAsJsonObject();
				String newlyCreatedEhrId = jsonNewEhrResponseObject.get("ehrId").getAsString();
				Response uploadCompositionResponse = req.uploadComposition(compositionPostBody,
						req.config.getSessionId(), "Vital Signs Encounter (Composition)",
						"importCsvResource", newlyCreatedEhrId);
				String uploadCompositionResponseBody = uploadCompositionResponse.getEntity().toString();
				int uploadCompositionResponseCode = uploadCompositionResponse.getStatus();
				if (uploadCompositionResponseCode == 201 || uploadCompositionResponseCode == 200) {
					// if successfully created
					JsonObject jsonCompositionResponseObject = (new JsonParser())
							.parse(uploadCompositionResponseBody.toString()).getAsJsonObject();
					String compUid = jsonCompositionResponseObject.get("compositionUid").getAsString();
					compositionUidCsvString = compUid;
				} else {
					// failed to upload
					errorCsvValue = "Error with Commit Composition Call: " + uploadCompositionResponseCode + " "
							+ uploadCompositionResponseBody.replaceAll(",", ";");
				}
			} else {
				// error
				errorCsvValue = "Error with Create EHR call: " + ehrCreateResponse.getStatus() + " "
						+ ehrCreateResponse.getEntity().toString().replaceAll(",", ";");
			}
			// get all the values from the JSON object
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(compositionPostBody);
			JsonObject obj = element.getAsJsonObject();
			Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
			// put the subjectID in the response
			csvResponseSb.append(subjectId + ",");
			for (Map.Entry<String, JsonElement> entry : entries) {
				// System.out.println(entry.getKey());
				csvResponseSb.append(entry.getValue().getAsString() + ",");
			}
			csvResponseSb.append(compositionUidCsvString + ",");
			csvResponseSb.append(errorCsvValue);
			csvResponseSb.append("\n");
		}
		return csvResponseSb.toString();
	}

	private String uploadJsonCompositionsArrayWithEhrId(JsonObject[] compositionArray) throws ClientProtocolException, 
	URISyntaxException, IOException {
		// create the CSV response
		StringBuilder csvResponseSb = new StringBuilder();
		csvResponseSb.append(csvInputHeader + ",compositionUid,errors\n");
		for (int i = 0; i < compositionArray.length; i++) {
			String ehrId = compositionArray[i].get("ehrId").getAsString();
			// remove the subject id from the composition json body before it is
			// posted to the ehrScape server
			compositionArray[i].remove("ehrId");
			String compositionPostBody = compositionArray[i].toString();

			// Do the post request to upload the composition
			// use ehrId to get the Ehr if it exists, if it does exist upload composition
			Response ehrGetResponse = req.getEhrWithEhrId(ehrId);
			int responseCode = ehrGetResponse.getStatus();
			String compositionUidCsvString = "";
			String errorCsvValue = "";
			// if the ehr exists get the ehrId
			// then use the ehrId to upload the composition
			if (responseCode == 200) {
				Response uploadCompositionResponse = req.uploadComposition(compositionPostBody, req.config.getSessionId(), 
						req.config.getTemplateId(), "ImportCSV-Tool", ehrId);
				String uploadCompositionResponseBody = uploadCompositionResponse.getEntity().toString();
				int uploadCompositionResponseCode = uploadCompositionResponse.getStatus();
				if (uploadCompositionResponseCode == 201 || uploadCompositionResponseCode == 200) {
					// if successfully created
					JsonObject jsonCompositionResponseObject = (new JsonParser())
							.parse(uploadCompositionResponseBody.toString()).getAsJsonObject();
					String compUid = jsonCompositionResponseObject.get("compositionUid").getAsString();
					compositionUidCsvString = compUid;
				} else {
					// failed to upload
					errorCsvValue = "Error with Commit Composition Call: " + uploadCompositionResponseCode + " "
							+ uploadCompositionResponseBody.replaceAll(",", ";");
				}
			} else {
				errorCsvValue = "Error with Get Ehr Call: " + ehrGetResponse.getStatus() + " "
						+ ehrGetResponse.getEntity().toString().replaceAll(",", ";");
			}
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(compositionPostBody);
			JsonObject obj = element.getAsJsonObject();
			Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
			// put the ehrID in the response
			csvResponseSb.append(ehrId + ",");
			for (Map.Entry<String, JsonElement> entry : entries) {
				csvResponseSb.append(entry.getValue().getAsString() + ",");
			}
			csvResponseSb.append(compositionUidCsvString + ",");
			csvResponseSb.append(errorCsvValue);
			csvResponseSb.append("\n");
		}
		return csvResponseSb.toString();
	}
}
