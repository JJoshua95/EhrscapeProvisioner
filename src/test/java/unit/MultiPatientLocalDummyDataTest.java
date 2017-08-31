package unit;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import ehrscapeProvisioner.model.EhrscapeRequest;

@RunWith(Theories.class)
public class MultiPatientLocalDummyDataTest {

	// check that relevant files containing 
	// dummy data elements can be found in the system
	
	// checks files needed for multi patient provisioning
		
	private File getFile(String fileName) {
		ClassLoader classLoader = EhrscapeRequest.class.getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());
		return file;
	}
	
	@Theory
    public void fileExists(String filename) {
		try{
			File file = getFile(filename);
			assertTrue("File Exists", file.exists());
		} catch(Exception e) {
			fail("File Not Found Error");
		}
    }
	
	public static @DataPoints String[] filenames = {
	    "assets/data/patientsAll.csv",
	    "assets/data/nursing-obs.csv",
	    "assets/sample_requests/allergies/allergies-template.xml",
	    "assets/sample_requests/allergies/AllergiesList_1FLAT.json",
	    "assets/sample_requests/allergies/AllergiesList_2FLAT.json",
	    "assets/sample_requests/allergies/AllergiesList_3FLAT.json",
	    "assets/sample_requests/allergies/AllergiesList_4FLAT.json",
	    "assets/sample_requests/allergies/AllergiesList_5FLAT.json",
	    "assets/sample_requests/allergies/AllergiesList_6FLAT.json",
	    "assets/sample_requests/lab-results/IDCR-LabReportFLATINPUT1.json",
	    "assets/sample_requests/lab-results/IDCR-LabReportFLATINPUT2.json",
	    "assets/sample_requests/lab-results/IDCR-LabReportFLATINPUT3.json",
	    "assets/sample_requests/lab-results/IDCR-LabReportFLATINPUT4.json",
	    "assets/sample_requests/lab-results/IDCR-LabReportFLATINPUT5.json",
	    "assets/sample_requests/lab-results/IDCR-LabReportFLATINPUT6.json",
	    "assets/sample_requests/lab-results/lab-results-template.xml",
	    "assets/sample_requests/orders/IDCRLabOrderFLAT1.json",
	    "assets/sample_requests/orders/IDCRLabOrderFLAT2.json",
	    "assets/sample_requests/orders/IDCRLabOrderFLAT3.json",
	    "assets/sample_requests/orders/IDCRLabOrderFLAT4.json",
	    "assets/sample_requests/orders/IDCRLabOrderFLAT5.json",
	    "assets/sample_requests/orders/IDCRLabOrderFLAT6.json",
	    "assets/sample_requests/orders/orders-template.xml",
	    "assets/sample_requests/problems/1_1_IDCRProblemList.v1.json",
	    "assets/sample_requests/problems/1_2_IDCRProblemList.v1.json",
	    "assets/sample_requests/problems/1_3_IDCRProblemList.v1.json",
	    "assets/sample_requests/problems/1_4_IDCRProblemList.v1.json",
	    "assets/sample_requests/problems/1_5_IDCRProblemList.v1.json",
	    "assets/sample_requests/problems/1_6_IDCRProblemList.v1.json",
	    "assets/sample_requests/problems/problems-template.xml",
	    "assets/sample_requests/procedures/IDCRProceduresList_1FLAT.json",
	    "assets/sample_requests/procedures/IDCRProceduresList_2FLAT.json",
	    "assets/sample_requests/procedures/IDCRProceduresList_3FLAT.json",
	    "assets/sample_requests/procedures/IDCRProceduresList_4FLAT.json",
	    "assets/sample_requests/procedures/IDCRProceduresList_5FLAT.json",
	    "assets/sample_requests/procedures/IDCRProceduresList_6FLAT.json",
	    "assets/sample_requests/procedures/procedures-template.xml"
	};

}
