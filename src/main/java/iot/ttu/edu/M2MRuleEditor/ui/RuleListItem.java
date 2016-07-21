package iot.ttu.edu.M2MRuleEditor.ui;

import java.util.ArrayList;
import java.util.HashMap;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;

import iot.ttu.edu.M2MRuleEditor.data.DataTable;
import iot.ttu.edu.M2MRuleEditor.data.Device;

public class RuleListItem extends RuleListItemDesign {
	private static HashMap<String, Device> sensorMap = null;
	private final static String[] logs = { "AND", "OR", "NOT" };
	private final static String[] ops = { ">", "<", "=", "!=", ">=", "<=" };
	private final String regex = "[(].*[)]";

	public RuleListItem(HashMap<String, Device> sensorMap) {
		super();
		this.sensorMap = sensorMap;
		this.logicComboBox.setVisible(false);

		this.sensorComboBox.setInputPrompt("sensor");
		this.sensorResourceComboBox.setInputPrompt("resource");
		this.operatorComboBox.setInputPrompt("op");
		this.valueTextField.setInputPrompt("value");

		this.logicComboBox.removeAllItems();
		this.logicComboBox.addItems(logs);
		this.logicComboBox.setValue(logs[0]);

		this.sensorComboBox.removeAllItems();
		for (Device d : sensorMap.values()) {
			this.sensorComboBox.addItem(d.getIp() + "/" + d.getDeviceId() + "(" + d.getName() + ")");
		}
		this.sensorComboBox.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().equals(RuleListItem.this.sensorComboBox)) {
					String sensorString = RuleListItem.this.sensorComboBox.getValue().toString();
					String key = RuleListItem.this.sensorComboBox.getValue().toString().replaceAll(regex, "");
					Device sensor = sensorMap.get(key);
					RuleListItem.this.sensorResourceComboBox.removeAllItems();
					String[] resourceArray = new String[sensor.getResourceId().size()];
					for (int i = 0; i < sensor.getResourceId().size(); i++) {
						resourceArray[i] = sensor.getResourceId().get(i) + "(" + 
								DataTable.getResourceName(sensor.getDeviceId(), sensor.getResourceId().get(i)) +
								")";
					}
					
					RuleListItem.this.sensorResourceComboBox.addItems(resourceArray);
				}
			}
		});

		this.sensorResourceComboBox.removeAllItems();
		// this.resourceComboBox.addItems(itemIds);

		this.operatorComboBox.removeAllItems();
		this.operatorComboBox.addItems(ops);

		this.addButtun.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				VerticalLayout vLayout = (VerticalLayout) RuleListItem.this.getParent();
				RuleListItem r = new RuleListItem(sensorMap);
				r.textLabel.setVisible(false);
				r.logicComboBox.setVisible(true);
				vLayout.addComponent(r);
			}
		});

		this.removeButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				VerticalLayout vLayout = (VerticalLayout) RuleListItem.this.getParent();
				vLayout.removeComponent(RuleListItem.this);
			}
		});
	}

	public RuleListItem(int i) {
		super();
	}
}
