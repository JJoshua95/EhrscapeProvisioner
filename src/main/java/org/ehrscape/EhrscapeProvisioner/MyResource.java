package org.ehrscape.EhrscapeProvisioner;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.ehrscape.EhrscapeProvisioner.model.EhrscapeRequest;
import org.xml.sax.SAXException;

import com.google.gson.Gson;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {
	EhrscapeRequest req =  new EhrscapeRequest();
	Gson gson = new Gson();
	
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */

    @POST
    @Path("getSession")
    @Produces(MediaType.APPLICATION_JSON)
    public String doPost() throws ClientProtocolException, IOException {
    	//EhrscapeRequest req =  new EhrscapeRequest();
    	String str = req.getSession("c4h_c4h_jarrod", "GeoSIGaI287");
    	System.out.println("Session id = " + req.config.getSessionId());
    	//req.config.setSessionId(sessionId);
    	return str;
    }
    
    @POST
    @Path("createEhr")
    @Produces(MediaType.APPLICATION_JSON)
    public String createEhr() throws ClientProtocolException, IOException {
    	//System.out.println(req.config.getSessionId().replace("\"", "")); //details = details.replace("\"","\\\"");
    	// watch out for speech marks when getting strings from json objects
    	String str = req.createEhr(req.config.getSessionId().replace("\"", ""), "uk.nhs.nhs_number", "JarrodEhrscapeProvisioner");
		return str;
    }
    
    @POST
    @Path("uploadTemplate")
    @Produces(MediaType.APPLICATION_JSON)
    public String showTemplate() throws ParserConfigurationException, SAXException, IOException {
    	String str = req.uploadDefaultTemplate("assets/sample_requests/vital-signs/vital-signs-template.xml", null);
    	return str;
    }
    
    @GET
    @Path("uploadComposition")
    @Produces(MediaType.APPLICATION_JSON)
    public String showComposition() {
    	String str = req.uploadDefaultComposition("assets/sample_requests/vital-signs/vital-signs-composition.json"); 
    	return str;
    }
    
}
