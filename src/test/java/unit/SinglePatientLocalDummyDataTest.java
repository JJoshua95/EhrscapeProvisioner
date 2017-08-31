package unit;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import ehrscapeProvisioner.model.EhrscapeRequest;

public class SinglePatientLocalDummyDataTest {

	// check that relevant files containing
	// dummy data elements can be found in the system
	
	// checks files needed for single patient provision

	private File getFile(String fileName) {
		ClassLoader classLoader = EhrscapeRequest.class.getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());
		return file;
	}

	@Test
	public void defaultTemplatefileExists() {
		try{
			File file = getFile("assets/sample_requests/vital-signs/vital-signs-template.xml");
			assertTrue("File Exists", file.exists());
		} catch(Exception e) {
			fail("File Not Found Error");
		}
    }
	
	@Test
	public void defaultCompositionfileExists() {
		try{
			File file = getFile("assets/sample_requests/vital-signs/vital-signs-composition.json");
			assertTrue("File Exists", file.exists());
		} catch(Exception e) {
			fail("File Not Found Error");
		}
    }

}
