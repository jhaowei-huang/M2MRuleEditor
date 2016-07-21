package iot.ttu.edu.M2MRuleEditor.ui;

import java.util.ArrayList;
import java.util.HashMap;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.ui.VerticalLayout;

import iot.ttu.edu.M2MRuleEditor.data.DataTable;
import iot.ttu.edu.M2MRuleEditor.data.Device;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class ActionListItem extends ActionListItemDesign {
	private static HashMap<String, Device> actuatorMap = null;
	private final String regex = "[(].*[)]";
	
	public ActionListItem(HashMap<String, Device> actuatorMap) {
		super();
		this.actuatorMap = actuatorMap;

		this.actuatorComboBox.setInputPrompt("actuator");
		this.actuatorResourceComboBox.setInputPrompt("resource");
		this.valueTextField.setInputPrompt("value");
		
		this.actuatorComboBox.removeAllItems();
		for (Device d : actuatorMap.values()) {
			this.actuatorComboBox.addItem(d.getIp() + "/" + d.getDeviceId() + "(" + d.getName() + ")");
		}
		this.actuatorComboBox.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().equals(ActionListItem.this.actuatorComboBox)) {
					String key = ActionListItem.this.actuatorComboBox.getValue().toString().replaceAll(regex, "");
					Device actuator = actuatorMap.get(key);
					ActionListItem.this.actuatorResourceComboBox.removeAllItems();
					String[] resourceArray = new String[actuator.getResourceId().size()];
					for (int i = 0; i < actuator.getResourceId().size(); i++) {
						resourceArray[i] = actuator.getResourceId().get(i) + "(" + 
								DataTable.getResourceName(actuator.getDeviceId(), actuator.getResourceId().get(i)) + ")";
					}
					
					ActionListItem.this.actuatorResourceComboBox.addItems(resourceArray);
				}
			}
		});
		
		this.actuatorResourceComboBox.removeAllItems();
		// this.actuatorResourceComboBox.addItems(itemIds);
		
		this.addButtun.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				VerticalLayout vLayout = (VerticalLayout) ActionListItem.this.getParent();
				ActionListItem r = new ActionListItem(actuatorMap);
				vLayout.addComponent(r);
			}
		});
		
		this.removeButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				VerticalLayout vLayout = (VerticalLayout) ActionListItem.this.getParent();
				vLayout.removeComponent(ActionListItem.this);
			}
		});
	}
}
