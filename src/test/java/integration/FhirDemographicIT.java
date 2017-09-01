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
public class FhirDemographicIT {

	// connect to the fhir server
	
	EhrscapeRequest req = new EhrscapeRequest();
	
	@Test
	public void test() throws ClientProtocolException, URISyntaxException, IOException {
		EhrscapeRequest.config.setFhirDemographicBaseUrl("http://51.140.57.74:8090/fhir/");
		Response fhirRes = req.createDefaultFhirPatientDemographic();
		int statusCode = fhirRes.getStatus();
		assertEquals("Check status of marand demographic call", 201 , statusCode);
	}

}
