package integration;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ehrscapeProvisioner.ticketDao.MultiPatientProvisionerTicket;
import ehrscapeProvisioner.ticketDao.MySqlTicketDao;

@Category(IntegrationTest.class)
public class DatabaseIT {

	// test connection to database
	
	// maybe some CRUD examples
	private MySqlTicketDao dao = new MySqlTicketDao();
	private MultiPatientProvisionerTicket ticket;
	
	@Test
	public void connectionTest() {
		boolean isDbConnected = dao.isDbConnected();
		assertTrue(isDbConnected);
	}
	
	@Test
	public void createTicketTest() {
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("TestJsonKey", "TestValue");
		JsonElement el = new JsonParser().parse(jsonObj.toString());
		ticket = new MultiPatientProvisionerTicket("createTicketTestId", "unitTestStatus",
				"testStartTime", el, "testFinishTime");
		try {
			dao.createTicketRecord(ticket);
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void retrieveTicketTest() {
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("TestJsonKey", "TestValue");
		JsonElement el = new JsonParser().parse(jsonObj.toString());
		ticket = new MultiPatientProvisionerTicket("createTicketTestId", "unitTestStatus",
				"testStartTime", el, "testFinishTime");
		try {
			MultiPatientProvisionerTicket retrieval = dao.getTicketRecord("createTicketTestId");
			assertEquals("Check the ticket from the database is correct", retrieval, ticket);
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void updateTicketTest() {
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("TestJsonKey", "TestValue");
		JsonElement el = new JsonParser().parse(jsonObj.toString());
		JsonObject jsonUpObj = new JsonObject();
		jsonObj.addProperty("UpdateTestJsonKey", "UpdateTestValue");
		JsonElement updateEl = new JsonParser().parse(jsonObj.toString());
		MultiPatientProvisionerTicket ticketPreUpdate = new MultiPatientProvisionerTicket("updateTicketTestId", "preUpdateTestStatus",
				"preUpdateTestStartTime", el, "preUpdateTestFinishTime");
		MultiPatientProvisionerTicket expectedTicketPostUpdate = new MultiPatientProvisionerTicket("updateTicketTestId", "updateTestStatus",
				"updateTestStartTime", el, "updateTestFinishTime");
		try {
			dao.updateTicketInDb("updateTicketTestId", "updateTestStatus", 
					jsonUpObj.toString(), "updateTestFinishTime");
			MultiPatientProvisionerTicket ticketPostUpdate = dao.getTicketRecord("updateTicketTestId");
			assertEquals("Check the ticket from the database is correct", ticketPostUpdate, expectedTicketPostUpdate);
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
