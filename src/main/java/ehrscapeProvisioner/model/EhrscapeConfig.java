package ehrscapeProvisioner.model;

public class EhrscapeConfig {
	
	// defaults
	private String patientsFile = "assets/data/patientsAll.csv"; 
	private String baseUrl = "https://cdr.code4health.org/rest/v1/"; // Old server available at https://ehrscape.code4health.org/rest/v1/
	private String username = "";
	private String password = "";
	private String sessionId = "";
	private String subjectId = "";
	private String subjectNamespace = "http://fhir.nhs.net/Id/nhs-number";
	private String ehrId = "";
	private String templateId = "Vital Signs Encounter (Composition)";
	private String compositionId = "";
	private String commiterName = "EhrscapeProvisioner";
	private String fhirDemographicBaseUrl = "http://51.140.57.74:8090/fhir/";
	
	public String getPatientsFile() {
		return patientsFile;
	}
	
	public void setPatientsFile(String patientsFile) {
		this.patientsFile = patientsFile;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}
	
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public String getSubjectId() {
		return subjectId;
	}
	
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	
	public String getEhrId() {
		return ehrId;
	}
	
	public void setEhrId(String ehrId) {
		this.ehrId = ehrId;
	}
	
	public String getTemplateId() {
		return templateId;
	}
	
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	
	public String getCompositionId() {
		return compositionId;
	}
	
	public void setCompositionId(String compositionId) {
		this.compositionId = compositionId;
	}
	
	public String getCommiterName() {
		return commiterName;
	}
	
	public void setCommiterName(String commiterName) {
		this.commiterName = commiterName;
	}
	
	public String getSubjectNamespace() {
		return subjectNamespace;
	}

	public void setSubjectNamespace(String subjectNamespace) {
		this.subjectNamespace = subjectNamespace;
	}

	public String getFhirDemographicBaseUrl() {
		return fhirDemographicBaseUrl;
	}
	
	public void setFhirDemographicBaseUrl(String fhirDemographicBaseUrl) {
		this.fhirDemographicBaseUrl = fhirDemographicBaseUrl;
	}
	

	
}