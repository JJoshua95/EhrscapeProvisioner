package ehrscapeProvisioner;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.xml.sax.SAXException;

import com.google.gson.Gson;

import ehrscapeProvisioner.model.EhrscapeRequest;

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
     * @throws URISyntaxException 
     */

    @POST
    @Path("getSession")
    @Produces(MediaType.APPLICATION_JSON)
    public String doPost() throws ClientProtocolException, IOException, URISyntaxException {
    	//EhrscapeRequest req =  new EhrscapeRequest();
    	String str = req.getSession("c4h_c4h_jarrod", "GeoSIGaI287");
    	System.out.println("Session id = " + EhrscapeRequest.config.getSessionId());
    	//req.config.setSessionId(sessionId);
    	return str;
    }
    
    @POST
    @Path("createPatientDemographic")
    @Produces(MediaType.APPLICATION_JSON)
    public String createPatientDemographic() throws ClientProtocolException, IOException, URISyntaxException {
    	String str = req.createPatientDefault();
    	return str;
    }
    
    @POST
    @Path("createEhr")
    @Produces(MediaType.APPLICATION_JSON)
    public String createEhr() throws ClientProtocolException, IOException, URISyntaxException {
    	//System.out.println(req.config.getSessionId().replace("\"", "")); //details = details.replace("\"","\\\"");
    	// watch out for speech marks when getting strings from json objects
    	String str = req.createEhr(EhrscapeRequest.config.getSessionId(), "JarrodEhrscapeProvisioner");
		return str;
    }
    
    @POST
    @Path("uploadTemplate")
    @Produces(MediaType.APPLICATION_JSON)
    public String showTemplate() throws ParserConfigurationException, SAXException, IOException, URISyntaxException {
    	String str = req.uploadDefaultTemplate();
    	return str;
    }
    
    @POST
    @Path("uploadComposition")
    @Produces(MediaType.APPLICATION_JSON)
    public String showComposition() throws ClientProtocolException, IOException, URISyntaxException {
    	String str = req.uploadDefaultComposition(); 
    	return str;
    }
    
    @POST
    @Path("createFhirPatient")
    @Produces(MediaType.APPLICATION_XML)
    public String createFhir() throws IOException {
    	String str = "In progress";
    	return str;
    }
    
}
