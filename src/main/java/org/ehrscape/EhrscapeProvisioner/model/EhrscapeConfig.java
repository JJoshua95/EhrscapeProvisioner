package org.ehrscape.EhrscapeProvisioner.model;

public class EhrscapeConfig {
	
	private String patientsFile = "patients.csv";
	private String baseUrl = "https://ehrscape.code4health.org/rest/v1/"; // "https://cdr.code4health.org/rest/v1/"; //
	private String username = "";
	private String password = "";
	private String sessionId = "";
	private String subjectId = "";
	private String ehrId = "";
	private String templateId = "Vital Signs Encounter (Composition)";
	private String compositionId = "";
	private String commiterName = "ehrscapeProvisioner";
	
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
	
}

/*

var EhrscapeConfig = {
  patientsFile: 'patients1.csv',
  baseUrl: 'https://ehrscape.code-4-health.org/rest/v1/',
  username: '',
  password: '',
  sessionId: '',
  subjectNamespace: 'uk.nhs.hospital_number',
  subjectId: '',
  ehrId: '',
  templateId: 'Vital Signs Encounter (Composition)',
  compositionId: '',
  commiterName: 'ehrscapeProvisioner'
}

module.exports = EhrscapeConfig;

*/