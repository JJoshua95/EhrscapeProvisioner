package ehrscapeProvisioner.model;

public class MultiPatientProvisionerResponse {
	
	private String startTime;
	private String responseId;
	private String provisioningStatus;
	private String responseBody;
	
	public MultiPatientProvisionerResponse(String responseId, String status, String body, String startTime) {
		this.responseId = responseId;
		this.responseBody = body;
		this.provisioningStatus = status;
		this.startTime = startTime;
	}

	public String getStartTime() {
		return startTime;
	}
	/*
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	*/
	public String getResponseId() {
		return responseId;
	}
	/*
	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}
	*/
	public String getProvisioningStatus() {
		return provisioningStatus;
	}

	public void setProvisioningStatus(String provisioningStatus) {
		this.provisioningStatus = provisioningStatus;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}
	
}
