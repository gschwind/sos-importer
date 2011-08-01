package org.n52.sos.importer.controller;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.n52.sos.importer.interfaces.StepController;
import org.n52.sos.importer.model.ModelStore;
import org.n52.sos.importer.model.Step4bModel;
import org.n52.sos.importer.model.measuredValue.MeasuredValue;
import org.n52.sos.importer.model.resources.FeatureOfInterest;
import org.n52.sos.importer.model.resources.Resource;
import org.n52.sos.importer.model.table.Column;
import org.n52.sos.importer.view.Step4Panel;

public class Step4bController extends StepController {

	private static final Logger logger = Logger.getLogger(Step4bController.class);
	
	private Step4bModel step4bModel;
	
	private TableController tableController = TableController.getInstance();;
	
	private Step4Panel step4Panel;
	
	public Step4bController() {
	}
	
	public Step4bController(Step4bModel step4bModel) {
		this.step4bModel = step4bModel;
	}

	@Override
	public void loadSettings() {
		Resource resource = step4bModel.getResource();
		int[] selectedRowsOrColumns = step4bModel.getSelectedRowsOrColumns();
		
		String text = step4bModel.getDescription();
		String orientation = tableController.getOrientationString();
		text = text.replaceAll("ORIENTATION", orientation);
		text = text.replaceAll("RESOURCE", resource.toString());
		
		step4Panel = new Step4Panel(text);
		
		tableController.setTableSelectionMode(TableController.COLUMNS);
		tableController.allowMultipleSelection();
		tableController.addMultipleSelectionListener(new SelectionChanged());
		
		for (int number: selectedRowsOrColumns) {
			Column column = new Column(number);
			MeasuredValue mv = ModelStore.getInstance().getMeasuredValueAt(column);
			resource.unassign(mv);
			tableController.selectColumn(number);
		}		
		
		tableController.clearMarkedTableElements();
		resource.getTableElement().mark();
	}
	
	@Override
	public void saveSettings() {
		Resource resource = step4bModel.getResource();	
		int[] selectedColumns = tableController.getSelectedColumns();		
		step4bModel.setSelectedRowsOrColumns(selectedColumns);
		
		for (int number: selectedColumns) {
			Column column = new Column(number);
			MeasuredValue mv = ModelStore.getInstance().getMeasuredValueAt(column);
			resource.assign(mv);
		}

		step4Panel = null;
	}
	
	private class SelectionChanged implements TableController.MultipleSelectionListener {

		@Override
		public void columnSelectionChanged(int[] selectedColumns) {
			for (int number: selectedColumns) {
				Column column = new Column(number);
				MeasuredValue mv = ModelStore.getInstance().getMeasuredValueAt(column);
				if (mv == null) {
					JOptionPane.showMessageDialog(null,
						    "This is not a measured value.",
						    "Info",
						    JOptionPane.INFORMATION_MESSAGE);
					tableController.deselectColumn(number);
					return;
				}
				
				Resource resource = step4bModel.getResource();
				if (resource.isAssigned(mv)) {
					JOptionPane.showMessageDialog(null,
						    resource + " already set for this measured value.",
						    "Info",
						    JOptionPane.INFORMATION_MESSAGE);
					tableController.deselectColumn(number);
					return;
				}
			}
		}

		@Override
		public void rowSelectionChanged(int[] selectedRows) {
			// TODO Auto-generated method stub
			
		}	
	}

	@Override
	public String getDescription() {
		return "Step 4b: Solve ambiguities";
	}


	@Override
	public JPanel getStepPanel() {
		return step4Panel;
	}


	@Override
	public StepController getNextStepController() {
		return new Step4cController();
	}


	@Override
	public boolean isNecessary() {	
		Resource resourceType = new FeatureOfInterest();
		Resource resource = null;
		
		//find how many Feature of Interests, Observed Properties, Units of 
		//Measurement or Sensors there are and handle the cases 0, 1 and n
		while (resourceType != null) {
			int number = resourceType.getList().size();
			// in case there is just one resource of this type:
			if (number == 1) {
				Resource oneResource = resourceType.getList().get(0);
				logger.info("Skip Step 4b for " + resourceType + "s" +
						" since there is just " + oneResource);
				
				for (MeasuredValue mv: ModelStore.getInstance().getMeasuredValues())
					oneResource.assign(mv);
			
			//in case there are more than two resources of this type:
			} else if (resource == null && number >= 2){
				resource = getNextUnassignedResource(resourceType);
			} else { //number == 0
				logger.info("Skip Step 4b for " + resourceType + "s" +
						" since there are not any " + resourceType + "s");
			}
			resourceType = resourceType.getNextResourceType();
		}
		
		step4bModel = new Step4bModel(resource);
		return resource != null;
	}
	
	@Override
	public StepController getNext() {
		Resource resourceType = step4bModel.getResource();
		
		Resource nextResource = null;
		while (resourceType != null) {
			nextResource = getNextUnassignedResource(resourceType);
			if (nextResource != null)
				return new Step4bController(new Step4bModel(nextResource));
			
			resourceType = resourceType.getNextResourceType();
		}
		return null;
	}	
	
	private Resource getNextUnassignedResource(Resource resourceType) {
		boolean unassignedMeasuredValues = areThereAnyUnassignedMeasuredValuesOf(resourceType);
		if (!unassignedMeasuredValues) return null;
		
		for (Resource resource: resourceType.getList())
			if (!isAssignedToMeasuredValues(resource))
				return resource;	
		return null;
	}
	
	private boolean areThereAnyUnassignedMeasuredValuesOf(Resource resourceType) {
		for (MeasuredValue mv: ModelStore.getInstance().getMeasuredValues()) 
			if (!resourceType.isAssigned(mv))
				return true;
		return false;
	}
	
	private boolean isAssignedToMeasuredValues(Resource resource) {
		for (MeasuredValue mv: ModelStore.getInstance().getMeasuredValues()) 
			if (resource.isAssignedTo(mv))
				return true;
		return false;
	}

	@Override
	public boolean isFinished() {
		return true;
	}
}
