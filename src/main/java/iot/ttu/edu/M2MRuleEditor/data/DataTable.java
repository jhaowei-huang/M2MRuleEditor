package iot.ttu.edu.M2MRuleEditor.data;

import java.util.List;
import org.json.*;

public class DataTable {
	private static JSONObject table = null;
	private static IoTData data = new IoTData();

	public static JSONObject getTable() {
		List<TypeID> typeids = data.getTypeIDs();

		table = new JSONObject();
		JSONArray deviceArray = new JSONArray();

		for (TypeID t : typeids) {
			JSONArray array = new JSONArray();
			List<ResourceID> resourceids = data.getResourceData("" + t.getId());
			for (ResourceID r : resourceids) {
				JSONObject resourceObj = new JSONObject();
				resourceObj.put("id", r.getId());
				resourceObj.put("name", r.getName());
				array.put(resourceObj);
			}

			JSONObject obj = new JSONObject();
			obj.put("resource", array);
			obj.put("name", t.getName());
			obj.put("deviceid", "" + t.getId());

			deviceArray.put(obj);
		}

		table.put("device", deviceArray);
		// System.out.println("\n" + table);
		return table;
	}

	public static String getDeviceName(String deviceid) {
		try {
			return data.getTypeData(deviceid).getName();
		} catch (Exception e) {
			return "null";
		}
	}

	public static String getResourceName(String deviceid, String resourceid) {
		try {
			return data.getResourceData(deviceid, resourceid).getName();
		} catch (Exception e) {
			return "null";
		}
	}

	public static String getResourceName(int deviceid, String resourceid) {
		try {
			return data.getResourceData(deviceid + "", resourceid).getName();
		} catch (Exception e) {
			return "null";
		}
	}
}
