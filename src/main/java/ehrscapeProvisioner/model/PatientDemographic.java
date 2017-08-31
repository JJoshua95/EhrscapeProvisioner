package ehrscapeProvisioner.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.StringType;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ca.uhn.fhir.context.FhirContext;

public class PatientDemographic {
	
	// patient.csv file header:
	// [Key, , Forename, Surname, Address_1, Address_2, Address_3, Postcode, Telephone, 
	// DateofBirth, Gender, NHSNumber, PasNumber, Department, GPNumber]
	
	// context - create this once, as it's an expensive operation
    // see http://hapifhir.io/doc_intro.html
	static FhirContext ctx = FhirContext.forDstu3();

	// patient member variables
	
	private String Key;
	private String Prefix;
	private String Forename;
	private String Surname;
	private String Address_1;
	private String Address_2;
	private String Address_3;
	private String Postcode ;
	private String Telephone ;
	private String DateofBirth;
	private String Gender;
	private String NHSNumber;
	private String PasNumber;
	private String Department;
	private String GPNumber;
	
	// Getters and Setters
	
	public void setKey(String key) {
		Key = key;
	}
	public void setForename(String forename) {
		Forename = forename;
	}
	public void setSurname(String surname) {
		Surname = surname;
	}
	public void setAddress_1(String address_1) {
		Address_1 = address_1;
	}
	public void setAddress_2(String address_2) {
		Address_2 = address_2;
	}
	public void setAddress_3(String address_3) {
		Address_3 = address_3;
	}
	public void setPostcode(String postcode) {
		Postcode = postcode;
	}
	public void setTelephone(String telephone) {
		Telephone = telephone;
	}
	public void setDateofBirth(String dateofBirth) {
		DateofBirth = dateofBirth;
	}
	public void setGender(String gender) {
		Gender = gender;
	}
	public void setNHSNumber(String nHSNumber) {
		NHSNumber = nHSNumber;
	}
	public void setPasNumber(String pasNumber) {
		PasNumber = pasNumber;
	}
	public void setDepartment(String department) {
		Department = department;
	}
	public void setGPNumber(String gPNumber) {
		GPNumber = gPNumber;
	}
	public void setPrefix(String prefix) {
		Prefix = prefix;
	}
	
	
	public String getKey() {
		return Key;
	}
	public String getForename() {
		return Forename;
	}
	public String getSurname() {
		return Surname;
	}
	public String getAddress_1() {
		return Address_1;
	}
	public String getAddress_2() {
		return Address_2;
	}
	public String getAddress_3() {
		return Address_3;
	}
	public String getPostcode() {
		return Postcode;
	}
	public String getTelephone() {
		return Telephone;
	}
	public String getDateofBirth() {
		return DateofBirth;
	}
	public String getGender() {
		return Gender;
	}
	public String getNHSNumber() {
		return NHSNumber;
	}
	public String getPasNumber() {
		return PasNumber;
	}
	public String getDepartment() {
		return Department;
	}
	public String getGPNumber() {
		return GPNumber;
	}
	public String getPrefix() {
		return Prefix;
	}
	
	// methods to create a marand party.json demographic body or fhir xml / json representation of this patient
	
	@Override
	public String toString() {
		return this.getKey() + " " + this.getForename() + " " + this.getSurname() + " " + this.getAddress_1() + " " + this.getAddress_2() + " "
				+ this.getAddress_3() + " " + this.getDateofBirth() + " " + this.getGender() + " " + this.getPostcode() + " " 
					+ this.getTelephone() + " " + this.getNHSNumber() + " ";
	}
	
	// TODO performance maybe just write directly as XML
	
	public String encodeInFhirFormat(boolean formatAsXML) {
		Patient patient = new Patient();
		// tutorial: https://fhir-drills.github.io/fhir-api.html
		// documentation - http://hapifhir.io/apidocs-dstu3/index.html
				
		// context - create this once, as it's an expensive operation
		// see http://hapifhir.io/doc_intro.html
		// FhirContext ctx = FhirContext.forDstu3();
		// now a class instance

        // you can use the Fluent API to chain calls
        // see http://hapifhir.io/doc_fhirobjects.html
        patient.addName().setUse(HumanName.NameUse.OFFICIAL)
                .addPrefix(this.getPrefix()).setFamily(this.getSurname()).addGiven(this.getForename());
        patient.addIdentifier()
                .setSystem("http://fhir.nhs.net/Id/nhs-number")
                .setValue(this.getNHSNumber());
        List<StringType> addressList = new ArrayList<StringType>();
        StringType st = new StringType(this.getAddress_1());
        addressList.add(st);
        patient.addAddress()
        		.setCity(this.getAddress_2()).setPostalCode(this.getPostcode()).setLine(addressList)
        		.setState(this.getAddress_3()).setText(this.getAddress_1() + ", " + this.getAddress_2() + ", " + this.getAddress_3() + ", " + this.getPostcode());
        String[] dateComponents = this.getDateofBirth().split("/");
        Calendar cal = Calendar.getInstance();
        int dayInt = Integer.parseInt(dateComponents[0]);
        int monthInt = Integer.parseInt(dateComponents[1]);
        int yearInt = Integer.parseInt(dateComponents[2]);
        cal.set(Calendar.DAY_OF_MONTH,dayInt);
        cal.set(Calendar.MONTH,monthInt-1); // month - 1 - months start at 0 , January is index 0 here
        cal.set(Calendar.YEAR,yearInt);

        Date d = cal.getTime();
        patient.setBirthDate(d);
        
        patient.addTelecom(new ContactPoint().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(this.getTelephone()));
        
        if (this.getGender().equals("Male")) {
        	patient.setGender(AdministrativeGender.MALE);
        } else if (this.getGender().equals("Female")) {
        	patient.setGender(AdministrativeGender.FEMALE);
        } else {
        	patient.setGender(AdministrativeGender.UNKNOWN);
        }

        // create a new XML parser and serialise our Patient object with it
        String encoded;
        if (formatAsXML == true) {
        	encoded = ctx.newXmlParser().setPrettyPrint(true)
        		.encodeResourceToString(patient);
        } else {
        	encoded = ctx.newJsonParser().setPrettyPrint(true)
                .encodeResourceToString(patient);
        }
        return encoded;

	}
	
	public String toFhirXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<Patient xmlns=\"http://hl7.org/fhir\">");
		sb.append("<identifier>");
		sb.append("<system value=\"http://fhir.nhs.net/Id/nhs-number\"/>");
		sb.append("<value value=\""+this.getNHSNumber()+"\"/>");
		sb.append("</identifier>");
		sb.append("<name>");
		sb.append("<use value=\"official\"/>");
		sb.append("<family value=\""+this.getSurname()+"\"/>");
		sb.append("<given value=\""+this.getForename()+"\"/>");
		sb.append("<prefix value=\""+this.getPrefix()+"\"/>");
		sb.append("</name>");
		sb.append("<telecom>");
		sb.append("<system value=\"phone\"/>");
		sb.append("<value value=\""+this.getTelephone()+"\"/>");
		sb.append("</telecom>");
		sb.append("<gender value=\""+this.getGender().toLowerCase()+"\"/>");
		String[] dateComponents = this.getDateofBirth().split("/");
		sb.append("<birthDate value=\""+dateComponents[2] + "-" + dateComponents[1] + "-" + dateComponents[0]+"\"/>");
		sb.append("<address>");
		sb.append("<text value=\""+this.getAddress_1() + ", " + this.getAddress_2() + ", " + this.getAddress_3()+ ", " + this.getPostcode() + "\"/>");
		sb.append("<line value=\""+this.getAddress_1() +"\"/>");
		sb.append("<city value=\""+ this.getAddress_2() +"\"/>");
		sb.append("<state value=\""+ this.getAddress_3()+"\"/>");
		sb.append("<postalCode value=\""+this.getPostcode()+"\"/>");
		sb.append("</address>");
		sb.append("</Patient>");
		return sb.toString();
	}
	
	public String toMarandPartyJson() {
		
		JsonObject jsonOutput = new JsonObject();
		
		JsonObject addressJson = new JsonObject();
		addressJson.addProperty("address", this.Address_1 + " , " + this.Address_2 + " , " + this.getPostcode());
		addressJson.addProperty("version", 1);
		
		jsonOutput.add("address", addressJson);
		
		String[] dateComponents = this.getDateofBirth().split("/");
		String formattedDate = dateComponents[2] + "-" + dateComponents[1] + "-" + dateComponents[0];
		jsonOutput.addProperty("dateOfBirth", formattedDate);
		
		jsonOutput.addProperty("firstNames", this.getForename());
		jsonOutput.addProperty("gender", this.getGender().toUpperCase());
		jsonOutput.addProperty("lastNames", this.getSurname());
		
		JsonObject prefixJson = new JsonObject();
		prefixJson.addProperty("key", "title");
		prefixJson.addProperty("value", this.getPrefix());
		prefixJson.addProperty("version", 0);
		JsonObject namespaceJson = new JsonObject();
		namespaceJson.addProperty("key", "http://fhir.nhs.net/Id/nhs-number");
		namespaceJson.addProperty("value", this.getNHSNumber());
		namespaceJson.addProperty("version", 1);
		
		JsonArray partyArrayJson = new JsonArray(); 
		partyArrayJson.add(prefixJson);
		partyArrayJson.add(namespaceJson);
		
		jsonOutput.add("partyAdditionalInfo", partyArrayJson);
		jsonOutput.addProperty("version", 1);
		
		return jsonOutput.toString();
	}
	
	public String writeEhrStatusBody() {
		// TODO generate the JSON below with a library 
		String[] dateComponents = this.getDateofBirth().split("/");
		String yearOfBirth = dateComponents[2];
		String jsonEhrStatusBody
				= " {"
				+ "    \"subjectId\": \"" + this.getNHSNumber() + "\","
				+ "    \"subjectNamespace\": \"" + EhrscapeRequest.config.getSubjectNamespace() + "\","
				+ "    \"queryable\": true,"
				+ "    \"modifiable\": true,"
				+ "    \"otherDetails\": {"
				+ "      \"@class\": \"ITEM_TREE\","
				+ "      \"items\": ["
				+ "        {"
				+ "          \"@class\": \"CLUSTER\","
				+ "          \"archetype_details\": {"
				+ "            \"@class\": \"ARCHETYPED\","
				+ "            \"archetype_id\": {"
				+ "              \"@class\": \"ARCHETYPE_ID\","
				+ "              \"value\": \"openEHR-EHR-CLUSTER.person_anonymised_parent.v1\""
				+ "            },"
				+ "            \"rm_version\": \"1.0.1\""
				+ "          },"
				+ "          \"archetype_node_id\": \"openEHR-EHR-CLUSTER.person_anonymised_parent.v1\","
				+ "          \"items\": ["
				+ "            {"
				+ "              \"@class\": \"ELEMENT\","
				+ "              \"archetype_node_id\": \"at0001\","
				+ "              \"name\": {"
				+ "                \"@class\": \"DV_TEXT\","
				+ "                \"value\": \"Administrative Gender\""
				+ "              },"
				+ "              \"value\": {"
				+ "                \"@class\": \"DV_CODED_TEXT\","
				+ "                \"defining_code\": {"
				+ "                  \"@class\": \"CODE_PHRASE\","
				+ "                  \"code_string\": \"at0009\","
				+ "                  \"terminology_id\": {"
				+ "                    \"@class\": \"TERMINOLOGY_ID\","
				+ "                    \"value\": \"local\""
				+ "                  }"
				+ "                },"
				+ "                \"value\": \"" + this.getGender() +"\""
				+ "              }"
				+ "            },"
				+ "            {"
				+ "              \"@class\": \"ELEMENT\","
				+ "              \"archetype_node_id\": \"at0002\","
				+ "              \"name\": {"
				+ "                \"@class\": \"DV_TEXT\","
				+ "                \"value\": \"Birth Sex\""
				+ "              },"
				+ "              \"value\": {"
				+ "                \"@class\": \"DV_CODED_TEXT\","
				+ "                \"defining_code\": {"
				+ "                  \"@class\": \"CODE_PHRASE\","
				+ "                  \"code_string\": \"at0009\","
				+ "                  \"terminology_id\": {"
				+ "                    \"@class\": \"TERMINOLOGY_ID\","
				+ "                    \"value\": \"local\""
				+ "                  }                },"
				+ "                \"value\": \"" + this.getGender() + "\""
				+ "              }"
				+ "            },"
				+ "            {"
				+ "              \"@class\": \"ELEMENT\","
				+ "              \"archetype_node_id\": \"at0003\","
				+ "              \"name\": {"
				+ "                \"@class\": \"DV_TEXT\","
				+ "                \"value\": \"Vital Status\""
				+ "              },"
				+ "              \"value\": {"
				+ "                \"@class\": \"DV_CODED_TEXT\","
				+ "                \"defining_code\": {"
				+ "                  \"@class\": \"CODE_PHRASE\","
				+ "                  \"code_string\": \"at0004\","
				+ "                  \"terminology_id\": {"
				+ "                    \"@class\": \"TERMINOLOGY_ID\","
				+ "                    \"value\": \"local\""
				+ "                  }"
				+ "                },"
				+ "                \"value\": \"Alive\""
				+ "              }"
				+ "            },"
				+ "            {"
				+ "              \"@class\": \"ELEMENT\","
				+ "              \"archetype_node_id\": \"at0014\","
				+ "              \"name\": {"
				+ "                \"@class\": \"DV_TEXT\","
				+ "                \"value\": \"Birth Year\""
				+ "              },"
				+ "              \"value\": {"
				+ "                \"@class\": \"DV_DATE\","
				+ "                \"value\": " + yearOfBirth
				+ "              }"
				+ "            }"
				+ "          ]"
				+ "        }"
				+ "      ]"
				+ "    }"
				+ "  } " ;
		
		// get rid of whitespace 
		return jsonEhrStatusBody.replaceAll("\\s+","");
		
	}

}
