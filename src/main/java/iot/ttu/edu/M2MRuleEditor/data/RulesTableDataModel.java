package iot.ttu.edu.M2MRuleEditor.data;

import java.util.ArrayList;

public class RulesTableDataModel {
	private String ruleName = "";
	private ArrayList<Rule> rules;
	private ArrayList<Action> actions;
	private final String regex = "[(].*[)]";
	private final String[] logs = { "AND", "OR", "NOT" };
	private final String[] ops = { ">", "<", "=", "!=", ">=", "<=" };
	
	public RulesTableDataModel(String ruleName) {
		this.ruleName = ruleName;
		rules = new ArrayList<Rule>();
		actions = new ArrayList<Action>();
	}

	public void addRule(String logic, String sensorId, String resourceId, String op, String value) {
		rules.add(new Rule(logic, sensorId, resourceId, op, value));
	}
	
	public ArrayList<Rule> getRules() {
		return rules;
	}
	
	public void addAction(String actuatorId, String resourceId, String value) {
		actions.add(new Action(actuatorId, resourceId, value));
	}
	
	public ArrayList<Action> getActions() {
		return actions;
	}
	
	public String getRuleName() {
		return this.ruleName;
	}

	public class Rule {
		public String logic;
		public String sensorId;
		public String resourceId;
		public String op;
		public String value;
		public boolean enable = false;

		public Rule(String logic, String sensorId, String resourceId, String op, String value) {
			this.logic = logic;
			this.sensorId = sensorId;
			this.resourceId = resourceId;
			this.op = op;
			this.value = value;
		}
		
		public boolean run(String value) {
			switch(op) {
			case ">":
				if(Float.parseFloat(value) > Float.parseFloat(this.value)) {
					enable = true;
					return true;
				} else {
					enable = false;
				}
				break;
			case "<":
				if(Float.parseFloat(value) < Float.parseFloat(this.value)) {
					enable = true;
					return true;
				} else {
					enable = false;
				}
				break;
			default:
				System.out.println("unknown op");
				break;
			}
			
			return false;
		}
	}
	
	public class Action {
		public String actuatorId;
		public String resourceId;
		public String value;

		public Action(String actuatorId, String resourceId, String value) {
			this.actuatorId = actuatorId;
			this.resourceId = resourceId;
			this.value = value;
		}
	}
}
