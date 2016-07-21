package iot.ttu.edu.M2MRuleEditor.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.json.JSONObject;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import iot.ttu.edu.M2MRuleEditor.coap.M2MCoapClient;
import iot.ttu.edu.M2MRuleEditor.data.DataTable;
import iot.ttu.edu.M2MRuleEditor.data.Device;
import iot.ttu.edu.M2MRuleEditor.data.RulesTableDataModel;

public class Editor extends EditorDesign {
	private final String device_regex = "[<]/[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}:[0-9]{1,5}/[0-9]{1,3}[>]";
	private final String resource_regex = "[<]/[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}:[0-9]{1,5}/[0-9]{1,3}/[0-9]{1,4}[>]";
	private final String name_regex = "[(].*[)]";
	private HashMap<String, Device> sensorMap = new HashMap<>();
	private HashMap<String, Device> actuatorMap = new HashMap<>();
	private M2MCoapClient.CheckRulesThread checkRulesThread = new M2MCoapClient.CheckRulesThread();
	
	public Editor() {
		super();
		addSensorItem();
		addActuatorItem();
	}

	public Editor(String[] array) {
		super();

		for (String element : array) {
			if (element.matches(device_regex)) {
				// System.out.println(element);
				String str = element.replace("</", "").replace(">", "");
				String[] strArray = str.split("/");
				if (strArray[1].equals("121") || strArray[1].equals("102")) {
					Device sensor = new Device(strArray[0], DataTable.getDeviceName(strArray[1]));
					sensor.setDeviceIp(strArray[0]);
					sensor.setDeviceId(strArray[1]);
					sensorMap.put(strArray[0] + "/" + strArray[1], sensor);
				} else {
					// System.out.println(strArray[0] + " = " + strArray[1]);
					Device actuator = new Device(strArray[0], DataTable.getDeviceName(strArray[1]));
					actuator.setDeviceIp(strArray[0]);
					actuator.setDeviceId(strArray[1]);
					actuatorMap.put(strArray[0] + "/" + strArray[1], actuator);
				}
			} else if (element.matches(resource_regex)) {
				String str = element.replace("</", "").replace(">", "");
				String[] strArray = str.split("/");
				if (strArray[1].equals("121") || strArray[1].equals("102")) {
					Device sensor = sensorMap.get(strArray[0] + "/" + strArray[1]);
					sensor.addResourceId(strArray[2]);
				} else {
					Device actuator = actuatorMap.get(strArray[0] + "/" + strArray[1]);
					actuator.addResourceId(strArray[2]);
				}
			}
		}

		addSensorItem();
		addActuatorItem();
		// M2MCoapClient.observeSensors(sensorMap);

		this.saveButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				RulesTableDataModel model = new RulesTableDataModel(Editor.this.ruleNameLabel.getValue());

				HasComponents ruleLayout = Editor.this.ruleLayout;
				Iterator it = ruleLayout.iterator();
				int i = 1;
				// add conditions
				while (it.hasNext()) {
					Object object = it.next();
					if (object.getClass().equals(RuleListItem.class)) {
						RuleListItem item = (RuleListItem) object;
						model.addRule(item.logicComboBox.getValue().toString().replaceAll(name_regex, ""),
								item.sensorComboBox.getValue().toString().replaceAll(name_regex, ""),
								item.sensorResourceComboBox.getValue().toString().replaceAll(name_regex, ""),
								item.operatorComboBox.getValue().toString().replaceAll(name_regex, ""),
								item.valueTextField.getValue().replaceAll(name_regex, ""));

						System.out.println(" i = " + (i++) + " = " + (String) item.logicComboBox.getValue() + " / "
								+ (String) item.sensorComboBox.getValue() + " / "
								+ (String) item.sensorResourceComboBox.getValue() + " / "
								+ (String) item.operatorComboBox.getValue() + " / " + item.valueTextField.getValue());
					}
				}
				// add actions
				HasComponents actionLayout = Editor.this.actionLayout;
				it = actionLayout.iterator();
				i = 1;
				while (it.hasNext()) {
					Object object = it.next();
					if (object.getClass().equals(ActionListItem.class)) {
						ActionListItem item = (ActionListItem) object;
						model.addAction(item.actuatorComboBox.getValue().toString().replaceAll(name_regex, ""),
								item.actuatorResourceComboBox.getValue().toString().replaceAll(name_regex, ""),
								item.valueTextField.getValue().replaceAll(name_regex, ""));

						System.out.println(" i = " + (i++) + " = " + (String) item.actuatorComboBox.getValue() + " / "
								+ (String) item.actuatorResourceComboBox.getValue() + " / "
								+ item.valueTextField.getValue());
					}
				}

				Editor.this.rulesTable.addItemToTable(model);
			}
		});

		this.renameButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				final Window window = new Window("Rename");
				window.setWidth(300.0f, Unit.PIXELS);
				window.setHeight(90.0f, Unit.PIXELS);
				window.setDraggable(false);

				VerticalLayout layout = new VerticalLayout();
				TextField textFields = new TextField();
				textFields.setSizeUndefined();
				textFields.setWidth("100%");
				textFields.setValue(Editor.this.ruleNameLabel.getValue());
				layout.addComponent(textFields);

				window.addCloseListener(new CloseListener() {
					@Override
					public void windowClose(CloseEvent e) {
						Editor.this.ruleNameLabel.setValue(textFields.getValue());
					}
				});

				window.setContent(layout);
				UI.getCurrent().addWindow(window);
				window.center();
				window.focus();
				window.setModal(true);
			}
		});

		this.applyButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				checkRulesThread.stopIt();
				checkRulesThread = new M2MCoapClient.CheckRulesThread();
				M2MCoapClient.setRuleTable(Editor.this.rulesTable.getTableMap());
				
				for (RulesTableDataModel model : Editor.this.rulesTable.getTableMap().values()) {
					for (RulesTableDataModel.Rule rule : model.getRules()) {						
						M2MCoapClient.observeSensor(sensorMap.get(rule.sensorId), rule.resourceId);
					}
				}
				
				checkRulesThread.start();
			}
		});
	}

	private void addSensorItem() {
		RuleListItem r1 = new RuleListItem(sensorMap);
		r1.removeButton.setEnabled(false);

		this.ruleLayout.setSizeUndefined();
		this.ruleLayout.setWidth("100%");
		this.ruleLayout.addComponent(r1);
		this.ruleLayout.setComponentAlignment(r1, Alignment.TOP_CENTER);
	}

	private void addActuatorItem() {
		ActionListItem a1 = new ActionListItem(actuatorMap);
		a1.removeButton.setEnabled(false);

		this.actionLayout.setSizeUndefined();
		this.actionLayout.setWidth("100%");
		this.actionLayout.addComponent(a1);
	}
}
