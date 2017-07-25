package org.ehrscape.EhrscapeProvisioner.model;

public class EhrscapeConfig {
	String patientsFile = "patients.csv";
	String baseUrl = "https://ehrscape.code4health.org/rest/v1/";
	String username = "";
	String password = "";
	String sessionId = "";
	String subjectId = "";
	String ehrId = "";
	String templateId = "Vital Signs Encounter (Composition)";
	String compositionId = "";
	String commiterName = "ehrscapeProvisioner";
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