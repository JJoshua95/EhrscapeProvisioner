package ehrscapeProvisioner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.xml.sax.SAXException;

import com.google.gson.Gson;

import ehrscapeProvisioner.model.EhrscapeRequest;
import ehrscapeProvisioner.model.PatientDemographic;

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
    public Response doPost() throws ClientProtocolException, IOException, URISyntaxException {
    	//EhrscapeRequest req =  new EhrscapeRequest();
    	Response res = req.getSession("c4h_c4h_jarrod", "GeoSIGaI287");
    	System.out.println("Session id = " + EhrscapeRequest.config.getSessionId());
    	System.out.println(res.getStatus());
    	System.out.println("Response content: " + res.getEntity().toString());
    	//req.config.setSessionId(sessionId);
    	return res;
    }
    
    @POST
    @Path("createPatientDemographic")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPatientDemographic() throws ClientProtocolException, IOException, URISyntaxException {
    	Response res = req.createPatientDefault();
    	return res;
    }
    
    @POST
    @Path("createEhr")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createEhr() throws ClientProtocolException, IOException, URISyntaxException {
    	//System.out.println(req.config.getSessionId().replace("\"", "")); //details = details.replace("\"","\\\"");
    	// watch out for speech marks when getting strings from json objects
    	Response res = req.createEhr(EhrscapeRequest.config.getSessionId(), "JarrodEhrscapeProvisioner");
		return res;
    }
    
    @POST
    @Path("uploadTemplate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response showTemplate() throws ParserConfigurationException, SAXException, IOException, URISyntaxException {
    	Response res = req.uploadDefaultTemplate();
    	return res;
    }
    
    @POST
    @Path("uploadComposition")
    @Produces(MediaType.APPLICATION_JSON)
    public Response showComposition() throws ClientProtocolException, IOException, URISyntaxException {
    	Response str = req.uploadDefaultComposition(); 
    	return str;
    }
    
    @POST
    @Path("createFhirPatient")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createFhir() throws IOException, URISyntaxException {
    	Response str = req.createDefaultFhirPatientDemographic();
    	return str;
    }
    
    @GET
    @Path("getEhrWithSubjectId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEhrWithSubject() throws ClientProtocolException, URISyntaxException, IOException {
    	return req.getEhrWithSubjectId(EhrscapeRequest.config.getSubjectId(), EhrscapeRequest.config.getSubjectNamespace());
    }
    
    @GET
    @Path("getEhrWithEhrId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEhrWithEHRID() throws ClientProtocolException, URISyntaxException, IOException {
    	return req.getEhrWithEhrId("fa81f04e-27b1-4226-be66-67f9034c235d");
    }
    
    @PUT
    @Path("pingSession")
    @Produces(MediaType.APPLICATION_JSON)
    public Response pingEhrSession() throws ClientProtocolException, URISyntaxException, IOException {
    	return req.pingSession(EhrscapeRequest.config.getSessionId());
    }
    
    @PUT
    @Path("updateEhr")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateEhr(String body) throws ClientProtocolException, URISyntaxException, IOException {
    	return req.updateEhr(body, "fa81f04e-27b1-4226-be66-67f9034c235d");
    }
    
    @POST
    @Path("csvTest")
    @Produces(MediaType.APPLICATION_XML)
    public String readCsvPatient() throws IOException {
    	List<PatientDemographic> list = req.readPatientCsvToObjectlist(EhrscapeRequest.config.getPatientsFile());
    	return list.get(5).writeEhrStatusBody();//.encodeInFhirFormat(true);
    }
    
}
