package iot.ttu.edu.M2MRuleEditor.ui;

import java.util.HashMap;

import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Notification;
import com.vaadin.addon.contextmenu.MenuItem;

import iot.ttu.edu.M2MRuleEditor.data.RulesTableDataModel;

public class RulesTable extends RulesTableDesign {
	private static final String[] columnHeaders = { "Enable", "Rule Name" };
	private HashMap<String, RulesTableDataModel> tableMap = new HashMap<String, RulesTableDataModel>();

	public RulesTable() {
		super();

		this.table.setSelectable(true);
		this.table.setMultiSelect(true);
		this.table.setImmediate(true);
		this.table.setColumnReorderingAllowed(true);
		this.table.setColumnCollapsingAllowed(true);

		this.table.addContainerProperty("Enable", String.class, null);
		this.table.addContainerProperty("Rule Name", String.class, null);

		this.table.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				Item item = RulesTable.this.table.getItem(event.getItemId());
				String key = item.getItemProperty("Rule Name").getValue().toString();
				tableMap.get(key);
			}
		});
		// Create a context menu for 'someComponent'
		ContextMenu contextMenu = new ContextMenu(table, true);
		// Checkable item
		final MenuItem item = contextMenu.addItem("Checkable", e -> {
		            Notification.show("checked: " + e.isChecked());
		        });
		item.setCheckable(true);
		item.setChecked(true);

		// Disabled item
		MenuItem item2 = contextMenu.addItem("Disabled", e -> {
		            Notification.show("disabled");
		});
		item2.setEnabled(false);
	}

	public void addItemToTable(RulesTableDataModel model) {
		if (tableMap.get(model.getRuleName()) != null) {
			// alert
			System.out.println("overwrite " + model.getRuleName());
		}

		tableMap.put(model.getRuleName(), model);
		Object newItemId = this.table.addItem();
		Item row = this.table.getItem(newItemId);
		row.getItemProperty("Enable").setValue("OK");
		row.getItemProperty("Rule Name").setValue(model.getRuleName());	
	}

	public HashMap<String, RulesTableDataModel> getTableMap() {
		return this.tableMap;
	}
}
