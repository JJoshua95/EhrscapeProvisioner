package unit;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ehrscapeProvisioner.ticketDao.MultiPatientProvisionerTicket;

public class TicketFormattingTest {

	// check the ticket formatting
	
	private String getResourceFileAsString(String fileName) {
		StringBuilder result = new StringBuilder("");
		File file = new File("src/test/resources/"+fileName);
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(file));

			String line;
			while ((line = bReader.readLine()) != null) {
				result.append(line).append("\n"); 
			}
			bReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	
	@Test
	public void ticketFormattingTest() {
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("TestJsonKey", "TestValue");
		JsonElement el = new JsonParser().parse(jsonObj.toString());
		MultiPatientProvisionerTicket ticket = new MultiPatientProvisionerTicket("testTicketId", "unitTestStatus",
				"testStartTime", el, "testFinishTime"); 
		String generatedTicket = ticket.toJsonObject().toString();
		String premadeTicket = getResourceFileAsString("ticketTestingFiles/exampleTicket.json").replaceAll("\n", "");
		
		assertEquals("Check that tickets are formatted correctly",generatedTicket,premadeTicket);
		
	}

}
