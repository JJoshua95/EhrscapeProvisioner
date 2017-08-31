package unit;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import ehrscapeProvisioner.model.EhrscapeRequest;
import ehrscapeProvisioner.model.PatientDemographic;

public class PatientFormattingTest {
	
	// check that the formatting methods create files that are correct
	EhrscapeRequest req = new EhrscapeRequest();
	
	private String getResourceFileAsString(String fileName) {
		StringBuilder result = new StringBuilder("");
		File file = new File("src/test/resources/"+fileName);
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(file));

			String line;
			while ((line = bReader.readLine()) != null) {
				result.append(line).append("\n"); 
			}
			bReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	
	private PatientDemographic initialisePatient() {
		// create mock object
		PatientDemographic patient = new PatientDemographic();
		// set attributes
		patient.setAddress_1("60 Florida Gardens");
		patient.setAddress_2("Cardiff");
		patient.setAddress_3("Glamorgan");
		patient.setDateofBirth("12/07/1965");
		patient.setDepartment("1");
		patient.setForename("Steve");
		patient.setGender("Male");
		patient.setGPNumber("1");
		patient.setKey("1");
		patient.setNHSNumber("7430555");
		patient.setPasNumber("352541");
		patient.setPostcode("LS23 4RT");
		patient.setSurname("Walford");
		patient.setTelephone("011981 32362");
		patient.setPrefix("Mr");
		
		return patient;
	}
	
	@Test
	public void fhirXmlGenerationConsistencyTest() {
		PatientDemographic patient = initialisePatient();
		// remove whitespace for comparisons, they are formated differently but not an issue
		String hapiFhirLibraryString = patient.encodeInFhirFormat(true).replaceAll("\\s","");
		String manualFhirString = patient.toFhirXML().replaceAll("\\s","");
		assertEquals("Test if the PatientDemographic methods generating FHIR xml representations are equivalent, "
				+ "one uses HapiFHIR library, other manually creates the xml",
				hapiFhirLibraryString, manualFhirString);
	}
	
	// test both methods against a pre-prepared fhir resource
	
	@Test
	public void manualFhirXmlTest() {
		PatientDemographic patient = initialisePatient();
		// remove whitespace for comparisons, they are formated differently but not an issue
		String premadeFhirXML = getResourceFileAsString("patientTestingFiles/defaultFhirPatient.xml").replaceAll("\\s","");
		String manualFhirString = patient.toFhirXML().replaceAll("\\s","");
		assertEquals("Test if the PatientDemographic method generating FHIR xml representations manually",
				premadeFhirXML, manualFhirString);
	}
	
	
	@Test
	public void hapiFhirLibraryXmlTest() {
		PatientDemographic patient = initialisePatient();
		// remove whitespace for comparisons, they are formated differently but not an issue
		String premadeFhirXML = getResourceFileAsString("patientTestingFiles/defaultFhirPatient.xml").replaceAll("\\s","");
		String hapiFhirFhirString = patient.toFhirXML().replaceAll("\\s","");
		assertEquals("Test if the PatientDemographic method generating FHIR xml representations using HapiFhir libraries",
				premadeFhirXML, hapiFhirFhirString);
	}
	
	@Test
	public void marandFormatTest() {
		PatientDemographic patient = initialisePatient();
		String generatedMarandJson = patient.toMarandPartyJson().replaceAll("\\s","");
		String premadeMarandJson = getResourceFileAsString("patientTestingFiles/party.json").replaceAll("\\s","");
		assertEquals("Test Marand Json Generation", generatedMarandJson, premadeMarandJson);
	}
	
}
