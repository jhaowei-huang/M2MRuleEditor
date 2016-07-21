package iot.ttu.edu.M2MRuleEditor.coap;

import java.util.HashMap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

import iot.ttu.edu.M2MRuleEditor.data.Device;
import iot.ttu.edu.M2MRuleEditor.data.Event;
import iot.ttu.edu.M2MRuleEditor.data.RulesTableDataModel;
import iot.ttu.edu.M2MRuleEditor.data.RulesTableDataModel.Action;
import iot.ttu.edu.M2MRuleEditor.data.RulesTableDataModel.Rule;

public class M2MCoapClient {
	public final static CoapClient client = new CoapClient();
	private static HashMap<String, Device> sensorMap;
	private static String IP = "coap://192.168.6.254:5683"; // "coap://140.129.33.157:5683";
	private static String COMMAND_DISCOVER = "/.well-known/core";
	private static HashMap<String, ObserveSensorsHandler> observeSensorsHandlerMap = new HashMap<String, ObserveSensorsHandler>();
	private static HashMap<String, Event> eventMap = new HashMap<String, Event>();
	private static HashMap<String, RulesTableDataModel> ruleTable = null;
	// private static Queue<Event> eventQueue = new LinkedList<Event>();

	public static void connect() {
		client.setURI(IP + COMMAND_DISCOVER);
		client.setTimeout(15000);
	}

	public static void observeSensors(HashMap<String, Device> map) {
		sensorMap = map;

		for (Device device : sensorMap.values()) {
			for (String resourceid : device.getResourceId()) {
				client.setURI(IP + "/" + device.getIp() + "/" + device.getDeviceId() + "/" + resourceid);
				System.out.println(client.getURI());
				client.observe(new ObserveSensorsHandler(device, resourceid));
			}
		}
	}

	public static void setRuleTable(HashMap<String, RulesTableDataModel> table) {
		ruleTable = table;
	}

	public static void observeSensor(Device device, String resourceid) {
		if (observeSensorsHandlerMap.get(device.getDeviceId() + "/" + resourceid) == null) {
			ObserveSensorsHandler handler = new ObserveSensorsHandler(device, resourceid);
			observeSensorsHandlerMap.put(device.getDeviceId() + "/" + resourceid, handler);
			client.setURI(IP + "/" + device.getIp() + "/" + device.getDeviceId() + "/" + resourceid);
			client.observe(handler);
			System.out.print("observe = " + device.getDeviceId() + " / " + resourceid);
		}
	}

	public static class ObserveSensorsHandler implements CoapHandler {
		private Device device = null;
		private String resourceid = null;

		public ObserveSensorsHandler(Device device, String resourceid) {
			this.device = device;
			this.resourceid = resourceid;
		}

		@Override
		public void onError() {

		}

		@Override
		public void onLoad(CoapResponse response) {
			String key = device.getDeviceIp() + "/" + device.getDeviceId() + "/" + resourceid;
			// System.out.println("event key = " + key);
			Event e = eventMap.get(key);
			if (e == null) {
				eventMap.put(key, new Event(device, resourceid, response.getResponseText()));
			} else {
				e.setValue(response.getResponseText());
			}

			// eventQueue.add(new Event(device, resourceid,
			// response.getResponseText()));
			// System.out.println(device.getName() + " = " +
			// response.getResponseText());
		}
	}

	public static class CheckRulesThread extends Thread {
		private volatile boolean isContinue = true;
		
		public void stopIt() {
			isContinue = false;
		}
		
		private void loop() {
			for (RulesTableDataModel item : ruleTable.values()) {
				boolean flag = false;
				int i = 0;
				for (Rule rule : item.getRules()) {
					// System.out.println("rule key = " + rule.sensorId + "/" + rule.resourceId);
					Event event = eventMap.get(rule.sensorId + "/" + rule.resourceId);
					if (event == null)
						return;

					String value = event.getValue();
					// System.out.println("current = " + value);
					if (rule.run(value)) {
						if (i == 0) {
							flag = true;
						} else if (rule.logic.equals("AND"))
							flag = flag & rule.enable;
						else if (rule.logic.equals("OR"))
							flag = flag | rule.enable;

						// System.out.println(rule.sensorId + "/" + rule.resourceId + "is true");
					}
				}

				if (flag) {
					for (Action action : item.getActions()) {
						// System.out.println("do action : " + IP + "/" + action.actuatorId + "/" + action.resourceId);
						String uri = IP + "/" + action.actuatorId + "/" + action.resourceId;
						client.setURI(uri);
						client.put(action.value, 0);
					}
				}
			}
		}

		@Override
		public void run() {
			while (isContinue) {
				try {
					loop();
					// System.out.println(Thread.currentThread().getId() + " end loop");
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
