package ehrscapeProvisioner;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonObject;

import au.com.bytecode.opencsv.CSVParser;

@Path("import")
public class ImportCsvResource {
	
	@POST
	@Path("csv")
	@Consumes(MediaType.TEXT_PLAIN)
	public String csvToCompositions(String inputCsvBody) throws IOException {
		// parse the csv input and turn each row into JsonObject compositions
		csvBodyToJsonCompositions(inputCsvBody);
		
		
		// loop through the json compositions and upload them to the ehrScape server
		
		return inputCsvBody;
	}
	
	private String csvBodyToJsonCompositions(String body) throws IOException {
		String[] split = body.split("\n");
		String header = split[0];
		if (header.contains("subjectId")) {
			System.out.println("subjectId column is present");
			JsonObject[] compositionArray = mapToJsonObjects(body);
			return "SubjectId csv file handled";
		} else if (header.contains("ehrId")) {
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
			System.out.println("Number of rows components: " + csvRowValues.length);
			// loop through the rows components with index j starting from the second element
			// the first element is the subject id - extract that separately to upload the data
			for (int j = 0; j < csvRowValues.length; j++) {
				jsonComposition.addProperty(compositionJsonKeys[j], csvRowValues[j]);;
			}
			jsonCompositionsArray[i-1] = jsonComposition;
		}
		return jsonCompositionsArray;
	}
	
	private String uploadJsonCompositionsArrayWithSubjectId(JsonObject[] compositionArray) {
		for (int i = 0; i < compositionArray.length; i++) {
			System.out.println(compositionArray[i].get("subjectId"));
		}
		return null;
	}

}
