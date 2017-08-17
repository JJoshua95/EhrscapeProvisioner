package ehrscapeProvisioner.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MultiPatientProvisionerTicket {
	
	private String startTime;
	private String responseId;
	private String provisioningStatus;
	private JsonElement responseBody;
	private String completionTime;
	
	public MultiPatientProvisionerTicket(String responseId, String status, String startTime) {
		this.responseId = responseId;
		this.provisioningStatus = status;
		this.startTime = startTime;
	}

	public String getStartTime() {
		return startTime;
	}
	
	public String getResponseId() {
		return responseId;
	}
	
	public String getProvisioningStatus() {
		return provisioningStatus;
	}

	public void setProvisioningStatus(String provisioningStatus) {
		this.provisioningStatus = provisioningStatus;
	}

	public JsonElement getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(JsonElement responseBody) {
		this.responseBody = responseBody;
	}

	public String getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(String completionTime) {
		this.completionTime = completionTime;
	}

	public JsonObject toJsonObject() {
		JsonObject jsonFormat = new JsonObject();
		jsonFormat.addProperty("responseId", this.getResponseId());
		jsonFormat.addProperty("startTime", this.getStartTime());
		jsonFormat.addProperty("provisioningStatus", this.getProvisioningStatus());
		jsonFormat.addProperty("completionTime", this.getCompletionTime());
		jsonFormat.add("provisioningResponseBody", this.getResponseBody());
		return jsonFormat;
	}
}
