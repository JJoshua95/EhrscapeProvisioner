package org.ehrscape.EhrscapeProvisioner;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.ClientProtocolException;
import org.ehrscape.EhrscapeProvisioner.model.EhrscapeRequest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Root resource (exposed at "provision" path)
 */
@Path("provision")
public class SinglePatient {
	
	@GET
	@Path("test")
	@Produces(MediaType.TEXT_PLAIN)
	public String tester() {
		String str = "Provision";
		return str;
	}
	
	@POST
	@Path("single-provision")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String singleProvision(String inputBody) throws ClientProtocolException, IOException, URISyntaxException {
		EhrscapeRequest req =  new EhrscapeRequest();
		
		Gson gson = new Gson();
		JsonObject jsonInput = (new JsonParser()).parse(inputBody.toString()).getAsJsonObject();
		System.out.println(jsonInput.get("username").getAsString());
		System.out.println(jsonInput.get("password").getAsString());
		JsonObject jsonOutput = new JsonObject();
		String getSessionResponse = req.getSession(jsonInput.get("username").getAsString(),jsonInput.get("password").getAsString()); 
		String createEhrResponse = req.createEhr(req.config.getSessionId().replace("\"", ""), "uk.nhs.nhs_number", "JarrodEhrscapeProvisioner");
		String uploadTemplateResponse = req.uploadDefaultTemplate();
		String uploadCompResponse = req.uploadDefaultComposition();
		
		// put the final response stuff here
		
		jsonOutput.addProperty("num", 123);
		jsonOutput.addProperty("testKey", "testVal");
		String finalConfig = gson.toJson(req.config);
		//System.out.println(jsonInput.toString());
		return finalConfig; //gson.toJson(jsonOutput);
	}
	
}
