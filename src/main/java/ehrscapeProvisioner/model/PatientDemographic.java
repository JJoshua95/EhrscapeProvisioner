package ehrscapeProvisioner.model;

public class PatientDemographic {
	
	// patient.csv file header:
	// [Key, , Forename, Surname, Address_1, Address_2, Address_3, Postcode, Telephone, 
	// DateofBirth, Gender, NHSNumber, PasNumber, Department, GPNumber]

	// patient member variables
	
	private String Key;
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
	
	// indices if manually mapping 
	/*
	private int KeyIndex;
	private int ForenameIndex;
	private int SurnameIndex;
	private int Address1Index;
	private int Address2Index;
	private int Address3Index;
	private int PostcodeIndex;
	private int TelephoneIndex;
	private int DateofBirthIndex;
	private int GenderIndex;
	private int NHSNumberIndex;
	private int PasNumberIndex;
	private int DepartmentIndex;
	private int GPNumberIndex;
	*/
	
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
	
	// methods to create a marand party.json demographic body or fhir xml / json representation of this patient
	@Override
	public String toString() {
		return this.getKey() + " " + this.getForename() + " " + this.getSurname() + " " + this.getAddress_1() + " " + this.getAddress_2() + " "
				+ this.getAddress_3() + " " + this.getDateofBirth() + " " + this.getGender() + " " + this.getPostcode() + " " 
					+ this.getTelephone() + " " + this.getNHSNumber() + " ";
	}

}
