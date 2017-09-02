package integration;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.core.Response;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import ehrscapeProvisioner.model.EhrscapeRequest;

@Category(IntegrationTest.class)
public class EhrscapeApi_IT {

	// test that the api can be accessed
	EhrscapeRequest req = new EhrscapeRequest();
	
	@Test
	public void sessionTest() throws ClientProtocolException, IOException, URISyntaxException {
		req.config.setBaseUrl("https://cdr.code4health.org/rest/v1/");
		Response sessionResponse = req.getSession("jjoshua_0d7701c4-dad1-44de-82b3-a79964d92ab6", "$2a$10$ojvYB");
		int statusCode = sessionResponse.getStatus();
		assertEquals("Check status of create session call", 201 , statusCode);
	}
	
	@Test
	public void marandDemographicTest() throws ClientProtocolException, IOException, URISyntaxException {
		req.config.setBaseUrl("https://cdr.code4health.org/rest/v1/");
		Response sessionResponse = req.getSession("jjoshua_0d7701c4-dad1-44de-82b3-a79964d92ab6", "$2a$10$ojvYB");
		assertEquals("Check status of Composition call", 201 , sessionResponse.getStatus());
		Response marandDemoRes = req.createPatientDefault();
		int statusCode = marandDemoRes.getStatus();
		assertEquals("Check status of marand demographic call", 201 , statusCode);
	}
	
	@Test
	public void ehrTest() throws ClientProtocolException, IOException, URISyntaxException {
		req.config.setBaseUrl("https://cdr.code4health.org/rest/v1/");
		Response sessionResponse = req.getSession("jjoshua_0d7701c4-dad1-44de-82b3-a79964d92ab6", "$2a$10$ojvYB");
		assertEquals("Check status of Composition call", 201 , sessionResponse.getStatus());
		Response ehrRes = req.createEhr("IntegrationTesting-"+ req.config.getSessionId(), "ehrscapeProvsionerTesting");
		
		int statusCode = ehrRes.getStatus();
		assertEquals("Check status of ehr call", 201 , statusCode);
		
	}
	
	@Test
	public void templateAndCompositionTest() throws ClientProtocolException, IOException, URISyntaxException {
		req.config.setBaseUrl("https://cdr.code4health.org/rest/v1/");
		Response sessionResponse = req.getSession("jjoshua_0d7701c4-dad1-44de-82b3-a79964d92ab6", "$2a$10$ojvYB");
		assertEquals("Check status of Composition call", 201 , sessionResponse.getStatus());
		Response ehrRes = req.createEhr("IntegrationTesting-"+req.config.getSessionId(), "ehrscapeProvsionerTesting");
		assertEquals("Check status of ehr call", 201 , ehrRes.getStatus());
		Response templateRes = req.uploadDefaultTemplate();
		int templateResStatus = templateRes.getStatus();
		/*
		if (templateRes.getStatus() == 400 || templateRes.getStatus() == 401 
				|| templateRes.getStatus() == 403) {
			fail("Error with template");
		}
		*/
		assertEquals("Check status of template call", 201 , templateResStatus);
		Response compRes = req.uploadDefaultComposition();
		int compstatusCode = compRes.getStatus();
		assertEquals("Check status of Composition call", 201 , compstatusCode);
	}

}
