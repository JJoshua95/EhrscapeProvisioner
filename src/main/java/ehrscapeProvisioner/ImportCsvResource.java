package ehrscapeProvisioner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

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
	public String csvToCompositions(String inputCsvBody) throws IOException, URISyntaxException {
		// parse the csv input and turn each row into JsonObject compositions
		//csvBodyToJsonCompositions(inputCsvBody);
		
		// loop through the json compositions and upload them to the ehrScape server
		
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
			return "EhrId csv file handled";
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
		// create the csv response
		StringBuilder csvResponseSb = new StringBuilder();
		csvResponseSb.append(csvInputHeader + ",compositionUid,errors\n");
		for (int i = 0; i < compositionArray.length; i++) {
			String subjectId = compositionArray[i].get("subjectId").getAsString();
			// remove the subject id from the composition json body before it is posted to the ehrScape server
			compositionArray[i].remove("subjectId");
			String compositionPostBody = compositionArray[i].toString();
			// System.out.println(compositionPostBody);
			
			try {
			// Do the post request to upload the composition
			String postResponse = req.uploadComposition(compositionPostBody);
			System.out.println(postResponse);
			} catch(Exception e) {
				e.getMessage();
			}
			// get all the values from the json object
			// https://stackoverflow.com/questions/31094305/java-gson-getting-the-list-of-all-keys-under-a-jsonobject
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(compositionPostBody);
			JsonObject obj = element.getAsJsonObject(); //since you know it's a JsonObject
			Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();//will return members of your object
			// put the subjectID in the response
			csvResponseSb.append(subjectId+",");
			for (Map.Entry<String, JsonElement> entry: entries) {
			    //System.out.println(entry.getKey());
				csvResponseSb.append(entry.getValue().getAsString() + ",");
			}
			// delete the final ","
			csvResponseSb.deleteCharAt(csvResponseSb.lastIndexOf(","));
			csvResponseSb.append("\n");
		}
	    //System.out.println(csvResponseSb.toString());  
		return csvResponseSb.toString();
	}

}
