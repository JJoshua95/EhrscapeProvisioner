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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import au.com.bytecode.opencsv.CSVParser;

import ehrscapeProvisioner.model.EhrscapeRequest;

@Path("import")
public class ImportCsvResource {
	
	// TODO allow a json input for the username password and namespace config options here
	
	private String csvInputHeader;
	private EhrscapeRequest req = new EhrscapeRequest();
	
	@POST
	@Path("csv")
	@Consumes(MediaType.TEXT_PLAIN)
	public String csvToCompositions(@HeaderParam("sessionId") String sessionId, String inputCsvBody) throws IOException, URISyntaxException {
		// parse the CSV input and turn each row into JsonObject compositions
		//csvBodyToJsonCompositions(inputCsvBody);
		// loop through the JSON compositions and upload them to the ehrScape server
		return csvBodyToJsonCompositions(inputCsvBody);
	}
	
	private String csvBodyToJsonCompositions(String body) throws IOException, URISyntaxException {
		String[] split = body.split("\n");
		csvInputHeader = split[0];
		if (csvInputHeader.contains("subjectId")) {
			System.out.println("subjectId column is present");
			JsonObject[] compositionArray = mapToJsonObjects(body);
			String csvResponse = uploadJsonCompositionsArrayWithSubjectId(compositionArray);
			return  csvResponse;
		} else if (csvInputHeader.contains("ehrId")) {
			System.out.println("ehrId column is present");
			mapToJsonObjects(body);
			return "EhrId csv file in progress";
		} else {
			// return an error message 
			System.out.println("ERROR PARSING");
			return "ERROR PARSING";
		}
	}
	
	private JsonObject[] mapToJsonObjects(String body) throws IOException {
		CSVParser csvParser = new CSVParser();
		//String[] lines = csvParser.parseLineMulti(body);
		String csvRows[] = body.split("\n");
		String header = csvRows[0];
		String[] compositionJsonKeys = csvParser.parseLine(header);
		// go through remaining rows 
		System.out.println("Number of rows " + csvRows.length);
		JsonObject[] jsonCompositionsArray = new JsonObject[csvRows.length-1];
		for (int i = 1; i < csvRows.length; i++) {
			JsonObject jsonComposition = new JsonObject();
			// look at the individual row
			String csvRow = csvRows[i];
			String csvRowValues[] = csvParser.parseLine(csvRow);
			// System.out.println("Number of rows components: " + csvRowValues.length);
			// loop through the rows components with index j starting from the second element
			// the first element is the subject id - extract that separately to upload the data
			for (int j = 0; j < csvRowValues.length; j++) {
				jsonComposition.addProperty(compositionJsonKeys[j], csvRowValues[j]);;
			}
			jsonCompositionsArray[i-1] = jsonComposition;
		}
		return jsonCompositionsArray;
	}
	
	private String uploadJsonCompositionsArrayWithSubjectId(JsonObject[] compositionArray) throws IOException, URISyntaxException {
		// TODO get the error messages to see why some compositions dont upload
		// create the CSV response
		StringBuilder csvResponseSb = new StringBuilder();
		csvResponseSb.append(csvInputHeader + ",compositionUid,errors\n");
		for (int i = 0; i < compositionArray.length; i++) {
			String subjectId = compositionArray[i].get("subjectId").getAsString();
			// remove the subject id from the composition json body before it is posted to the ehrScape server
			compositionArray[i].remove("subjectId");
			String compositionPostBody = compositionArray[i].toString();
			// System.out.println(compositionPostBody);
			
			// Do the post request to upload the composition
			// use subjectId to create Ehr or if it exists retrieve one 
			Response ehrCreateResponse = req.createEhr(subjectId, "ImportCsvTool");
			int responseCode = ehrCreateResponse.getStatus();
			System.out.println("Create EHR Response Code: " + responseCode);
			System.out.println("Create EHR Response Content:" + ehrCreateResponse.getEntity());
			String compositionUidCsvString = "";
			String errorCsvValue = "";
			// if the ehr exists get the ehrId
			// then use the ehrId to upload the composition
			if (responseCode == 400) {
				// get ehr
				Response getEhrResponse = req.getEhrWithSubjectId(subjectId, EhrscapeRequest.config.getSubjectNamespace());
				String getEhrResponseBody = getEhrResponse.getEntity().toString();
				System.out.println("Get EHR response body \n" + getEhrResponseBody);
				// parse the response to get the ehrId
				JsonObject jsonObject = (new JsonParser()).parse(getEhrResponseBody.toString()).getAsJsonObject();
				String ehrId = jsonObject.get("ehrId").getAsString();
				Response uploadCompositionResponse = req.uploadComposition(compositionPostBody, EhrscapeRequest.config.getSessionId(), 
						"Vital Signs Encounter (Composition)", "importCsvResource", ehrId);
				String uploadCompositionResponseBody = uploadCompositionResponse.getEntity().toString();
				int uploadCompositionResponseCode = uploadCompositionResponse.getStatus();
				if (uploadCompositionResponseCode == 201 || uploadCompositionResponseCode == 200) {
					// if successfully created
					JsonObject jsonCompositionResponseObject 
					= (new JsonParser()).parse(uploadCompositionResponseBody.toString()).getAsJsonObject();
					String compUid = jsonCompositionResponseObject.get("compositionUid").getAsString();
					compositionUidCsvString = compUid;
				} else {
					// failed to upload
					errorCsvValue = "Failed to upload this composition";
				}
			} else if (responseCode == 201) {
				// or just upload composition for the newly created EHR
				String ehrCreateResponseBody = ehrCreateResponse.getEntity().toString();
				JsonObject jsonNewEhrResponseObject 
				= (new JsonParser()).parse(ehrCreateResponseBody.toString()).getAsJsonObject();
				String newlyCreatedEhrId = jsonNewEhrResponseObject.get("ehrId").getAsString();
				Response uploadCompositionResponse = req.uploadComposition(compositionPostBody, EhrscapeRequest.config.getSessionId(), 
						"Vital Signs Encounter (Composition)", "importCsvResource", newlyCreatedEhrId);
				String uploadCompositionResponseBody = uploadCompositionResponse.getEntity().toString();
				int uploadCompositionResponseCode = uploadCompositionResponse.getStatus();
				if (uploadCompositionResponseCode == 201 || uploadCompositionResponseCode == 200) {
					// if successfully created
					JsonObject jsonCompositionResponseObject 
					= (new JsonParser()).parse(uploadCompositionResponseBody.toString()).getAsJsonObject();
					String compUid = jsonCompositionResponseObject.get("compositionUid").getAsString();
					compositionUidCsvString = compUid;
				} else {
					// failed to upload
					errorCsvValue = "Failed to upload this composition";
				}
			} else {
				// error
				errorCsvValue = "Failed to upload this composition";
			}
			
			// get all the values from the JSON object
			// https://stackoverflow.com/questions/31094305/java-gson-getting-the-list-of-all-keys-under-a-jsonobject
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(compositionPostBody);
			JsonObject obj = element.getAsJsonObject(); 
			Set<Map.Entry<String, JsonElement>> entries = obj.entrySet(); 
			// put the subjectID in the response
			csvResponseSb.append(subjectId+",");
			for (Map.Entry<String, JsonElement> entry: entries) {
			    //System.out.println(entry.getKey());
				csvResponseSb.append(entry.getValue().getAsString() + ",");
			}
			// delete the final ","
			//csvResponseSb.deleteCharAt(csvResponseSb.lastIndexOf(","));
			csvResponseSb.append(compositionUidCsvString + ",");
			csvResponseSb.append(errorCsvValue);
			csvResponseSb.append("\n");
		}
		return csvResponseSb.toString();
	}

}
