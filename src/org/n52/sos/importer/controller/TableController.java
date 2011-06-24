package org.n52.sos.importer.controller;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.n52.sos.importer.model.table.Cell;
import org.n52.sos.importer.view.TablePanel;

public class TableController {
	
	private static TableController instance = null;
	
	public static final int COLUMNS = 1;
	public static final int ROWS = 2;
	public static final int CELLS = 3;
	
	private TablePanel tableView;
	
	private JTable table;
	
	private SingleSelectionListener singleSelectionListener;
	
	private MultipleSelectionListener multipleSelectionListener;
	
	private int tableSelectionMode;
	
	private int orientation = COLUMNS;

	private TableController() {
		tableView = TablePanel.getInstance();
		table = tableView.getTable();
		table.getSelectionModel().addListSelectionListener(new RowSelectionListener());
		table.getColumnModel().getSelectionModel()
		    .addListSelectionListener(new ColumnSelectionListener());
	}

	public static TableController getInstance() {
		if (instance == null)
			instance = new TableController();
		return instance;
	}	
	
	public void setContent(Object[][] content) {
		DefaultTableModel dtm = new EditableTableModel(false);

		int columns = content[0].length;
		dtm.setColumnCount(columns);
		//Object[] columnIdentifiers = new Object[columns];
		//dtm.setColumnIdentifiers(columnIdentifiers);
		int rows = content.length;

		for (int i = 0; i < rows; i++) {
			dtm.addRow(content[i]);
		}
		table.setModel(dtm);
	}
	
	public void setColumnHeading(int column, String heading) {
		table.getColumnModel().getColumn(column).setHeaderValue(heading);
	}
	
	public void allowSingleSelection() {
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	public void allowMultipleSelection() {
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}
	
	public void setTableSelectionMode(int tableSelectionMode) {
		this.tableSelectionMode = tableSelectionMode;
		
		switch(tableSelectionMode) {
		case ROWS:
			table.setColumnSelectionAllowed(false);
			table.setRowSelectionAllowed(true);
			table.setShowVerticalLines(false);
			table.setShowHorizontalLines(true);
			break;
		case COLUMNS:
			table.setColumnSelectionAllowed(true);
			table.setRowSelectionAllowed(false);
			table.setShowVerticalLines(true);
			table.setShowHorizontalLines(false);
			break;
		case CELLS:
			table.setColumnSelectionAllowed(true);
			table.setRowSelectionAllowed(true);
			table.setShowVerticalLines(true);
			table.setShowHorizontalLines(true);
			break;
		}
	}
	
	public int getTableSelectionMode() {
		return tableSelectionMode;
	}

	public void selectColumn(int number) {
		table.addColumnSelectionInterval(number, number);
	}
	
	public void selectRow(int number) {
		table.addRowSelectionInterval(number, number);
	}
	
	public void deselectColumn(int number) {
		table.removeColumnSelectionInterval(number, number);
	}
	
	public void deselectRow(int number) {
		table.removeRowSelectionInterval(number, number);
	}
	
	public void deselectAllColumns() {
		int columns = table.getColumnCount() - 1;
		table.removeColumnSelectionInterval(0, columns);
	}
	
	public void deselectAllRows() {
		int rows = table.getRowCount() - 1;
		table.removeColumnSelectionInterval(0, rows);
	}
	
	public void turnSelectionOff() {
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.setCellSelectionEnabled(false);
		table.setFocusable(false);
	}
	
	public int[] getSelectedColumns() {
		return table.getSelectedColumns();
	}
	
	public int getSelectedColumn() {
		return table.getSelectedColumn();
	}
	
	public int[] getSelectedRows() {
		return table.getSelectedRows();
	}
	
	public int getSelectedRow() {
		return table.getSelectedRow();
	}
	
	public List<String> getSelectedValues() {
		ArrayList<String> values = new ArrayList<String>();
		
		switch(tableSelectionMode) {
		case COLUMNS:
			int column = table.getSelectedColumn();		
			int rowCount = table.getRowCount();
		
			for (int i = 0; i < rowCount; i++)
				values.add((String)table.getValueAt(i, column));
		
			break;
		case ROWS:		
			int row = table.getSelectedRow();
			int columnCount = table.getColumnCount();
			
			for (int i = 0; i < columnCount; i++)
				values.add((String)table.getValueAt(row, i));

			break;
		case CELLS:
			values.add((String)table.getValueAt(getSelectedRow(), getSelectedColumn()));
			break;
		}
		return values;
	}
	
	public String getSelectedCellValue() {
		int column = table.getSelectedColumn();
		int row = table.getSelectedRow();
		return (String)table.getValueAt(row, column);
	}
	
	public String getValueAt(Cell c) {
		return (String) table.getValueAt(c.getRow(), c.getColumn());
	}
	
	public String getValueAt(int row, int column) {
		return (String) table.getValueAt(row, column);
	}
	
	public int getRowCount() {
		return table.getRowCount();
	}
	
	public void colorColumn(Color color, int number) {
		table.setDefaultRenderer(Object.class, new ColoredTableCellRenderer(color, number, -1));
	}
	
	public void colorRow(Color color, int number) {
		table.setDefaultRenderer(Object.class, new ColoredTableCellRenderer(color, -1, number));
	}
	
	public void colorCell(Color color, Cell cell) {
		table.setDefaultRenderer(Object.class, new ColoredTableCellRenderer(color, cell));
	}

	public void addSingleSelectionListener(SingleSelectionListener singleSelectionListener) {
		this.singleSelectionListener = singleSelectionListener;
	}	
	
	public void addMultipleSelectionListener(MultipleSelectionListener multipleSelectionListener) {
		this.multipleSelectionListener = multipleSelectionListener;
	}
	
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public int getOrientation() {
		return orientation;
	}

	private class ColoredTableCellRenderer extends DefaultTableCellRenderer {
    
		private static final long serialVersionUID = 1L;

		private Color color;
		
		private int columnToColor;
		
		private int rowToColor;
		
		private Cell cellToColor;
		
		public ColoredTableCellRenderer(Color color, int columnToColor, int rowToColor) {
			this.color = color;
			this.columnToColor = columnToColor;
			this.rowToColor = rowToColor;
		}
		
		public ColoredTableCellRenderer(Color color, Cell cellToColor) {
			this.color = color;
			this.cellToColor = cellToColor;
		}
		
		@Override
		public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column) {
			setEnabled(table == null || table.isEnabled());

	        if (row == rowToColor) setBackground(color);
	        else if (column == columnToColor) setBackground(color);
	        else if (cellToColor != null && column == cellToColor.getColumn() && row == cellToColor.getRow()) setBackground(color);
	        else setBackground(null);
	        
	        super.getTableCellRendererComponent(table, value, selected, focused, row, column);
	        return this;
		}
	}
	
	private class EditableTableModel extends DefaultTableModel {
		
		private static final long serialVersionUID = 1L;

		private boolean editable;
		
		public EditableTableModel(boolean editable) {
			super();
			this.editable = editable;
		}
	
		@Override
		public boolean isCellEditable(int row, int column) {
	        return editable;
	    }
	}
	
	private class ColumnSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			if (table.getColumnSelectionAllowed() && arg0.getValueIsAdjusting()) {
				if (table.getSelectionModel().getSelectionMode() == ListSelectionModel.SINGLE_SELECTION &&
						singleSelectionListener != null) 
					singleSelectionListener.columnSelectionChanged(table.getSelectedColumn());
				else if (table.getSelectionModel().getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION &&
						multipleSelectionListener != null) 
					multipleSelectionListener.columnSelectionChanged(table.getSelectedColumns());
			}
		}
	}
	
	private class RowSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			if (table.getRowSelectionAllowed() && arg0.getValueIsAdjusting()) {
				if (table.getSelectionModel().getSelectionMode() == ListSelectionModel.SINGLE_SELECTION &&
						singleSelectionListener != null) 
					singleSelectionListener.rowSelectionChanged(table.getSelectedRow());
				else if (table.getSelectionModel().getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION &&
						multipleSelectionListener != null) 
					multipleSelectionListener.rowSelectionChanged(table.getSelectedRows());
			}
		}
	}
	
	public interface SingleSelectionListener {
		public void columnSelectionChanged(int selectedColumn);

		public void rowSelectionChanged(int selectedRow);
	}
	
	public interface MultipleSelectionListener {
		public void columnSelectionChanged(int[] selectedColumns);

		public void rowSelectionChanged(int[] selectedRows);
	}
}
