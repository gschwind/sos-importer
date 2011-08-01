package org.n52.sos.importer.model.position;

import org.apache.log4j.Logger;
import org.n52.sos.importer.interfaces.Component;
import org.n52.sos.importer.model.table.TableElement;

public abstract class PositionComponent extends Component {

	private static final Logger logger = Logger.getLogger(PositionComponent.class);
	
	private TableElement tableElement;
	
	private String pattern;
	
	private double value = -1;
	
	private String unit = null;

	public PositionComponent(TableElement tableElement, String pattern) {
		this.tableElement = tableElement;
		this.pattern = pattern;
	}
	
	public PositionComponent(double value, String unit) {
		this.value = value;
		this.unit = unit;
	}

	public void setValue(double value) {
		logger.info("Assign Value to " + this.getClass().getName());
		this.value = value;
	}

	public double getValue() {
		return value;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUnit() {
		return unit;
	}

	public void setTableElement(TableElement tableElement) {
		logger.info("Assign Column to " + this.getClass().getName());
		this.tableElement = tableElement;
	}

	public TableElement getTableElement() {
		return tableElement;
	}
	
	public void mark() {
		if (tableElement != null)
			tableElement.mark();
	}

	public String getParsedUnit() {
		if (unit == null || unit.equals(""))
			return "n/a";
		else if (unit.equals("m") || unit.equals("meters")) 
			return "m";
		else if (unit.equals("ft") || unit.equals("feet"))
			return "ft";
		else if (unit.equals("degree") || unit.equals("°"))
			return "degree";
		return "n/a";
	}
	
	@Override 
	public String toString() {
		if (getTableElement() == null)
			return " " + getValue() + getUnit();
		else 
			return " " + getTableElement();
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getPattern() {
		return pattern;
	}
}
