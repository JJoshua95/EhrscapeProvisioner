package unit;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ehrscapeProvisioner.model.EhrscapeRequest;
import ehrscapeProvisioner.model.PatientDemographic;

public class CsvMappingTest {

	// use a patient record in a csv file and map it to patient object
	// check the formatting
	
	EhrscapeRequest req = new EhrscapeRequest();
	
	private PatientDemographic initialisePatient() {
		// create mock object
		PatientDemographic patient = new PatientDemographic();
		// set attributes
		patient.setAddress_1("6948 Et St.");
		patient.setAddress_2("Halesowen");
		patient.setAddress_3("Worcestershire");
		patient.setDateofBirth("06/06/1944");
		patient.setDepartment("1");
		patient.setForename("Ivor");
		patient.setGender("Male");
		patient.setGPNumber("1");
		patient.setKey("1");
		patient.setNHSNumber("9999999000");
		patient.setPasNumber("352541");
		patient.setPostcode("VX27 5DV");
		patient.setSurname("Cox");
		patient.setTelephone("011981 32362");
		patient.setPrefix("Mr");
		
		return patient;
	}
	
	@Test
	public void test() throws IOException {
		List<PatientDemographic> list = req.readPatientCsvToObjectlist("assets/data/singlePatient.csv");
		PatientDemographic patientFromCsv = list.get(0);
		PatientDemographic mockPatient = initialisePatient();
		String patientFromCsvFhirXml = patientFromCsv.encodeInFhirFormat(true);
		String mockPatientFhirXml = mockPatient.encodeInFhirFormat(true);
		assertEquals("Test if mock object and csv mapped object produce equivalent Fhir representations" ,
				patientFromCsvFhirXml , mockPatientFhirXml);
		
	}

}
