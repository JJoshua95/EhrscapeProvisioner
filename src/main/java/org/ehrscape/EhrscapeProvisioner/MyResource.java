package org.ehrscape.EhrscapeProvisioner;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.ClientProtocolException;
import org.ehrscape.EhrscapeProvisioner.model.EhrscapeRequest;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {
	
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    // @GET
    // @Produces(MediaType.TEXT_PLAIN)
    // public String getIt() {
    //     return "Got it!";
    // }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public TestObj getXml() {
    	TestObj t = new TestObj();
    	t.SetA("a");
    	t.SetB("b");
    	t.setSub("c", "d");
    	return t;
    }
    
    @GET
    @Path("getTest")
    @Produces(MediaType.TEXT_PLAIN)
    public int doGet() throws Exception {
    	EhrscapeRequest req =  new EhrscapeRequest();
    	int str = req.sendGet();
    	return str;
    }
    
    @POST
    @Path("getSession")
    @Produces(MediaType.APPLICATION_JSON)
    public String doPost() throws ClientProtocolException, IOException {
    	EhrscapeRequest req =  new EhrscapeRequest();
    	String str = req.getSession("c4h_c4h_jarrod", "GeoSIGaI287");
    	System.out.println("Session id = " + req.config.getSessionId());
    	return str;
    }
    
    
}
