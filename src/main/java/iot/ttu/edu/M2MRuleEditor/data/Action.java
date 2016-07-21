package iot.ttu.edu.M2MRuleEditor.data;

public class Action {
	private Device device;
	private String resourceid;
	private String value;
	
	public Action(Device device, String resourceid, String value) {
		this.device = device;
		this.resourceid = resourceid;
		this.value = value;
	}
	
	public Device getDevice() {
		return device;
	}

	public String getResourceid() {
		return resourceid;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
