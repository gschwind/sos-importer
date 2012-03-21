/**
 * Copyright (C) 2012
 * by 52North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.sos.importer.model.measuredValue;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import org.n52.sos.importer.interfaces.Formatable;

public class NumericValue extends MeasuredValue implements Formatable {

	private String decimalSeparator;
	
	private String thousandsSeparator;

	public void setDecimalSeparator(String selectedDecimalSeparator) {
		this.decimalSeparator = selectedDecimalSeparator;
	}

	public String getDecimalSeparator() {
		return decimalSeparator;
	}

	public void setThousandsSeparator(String selectedThousandsSeparator) {
		this.thousandsSeparator = selectedThousandsSeparator;
	}

	public String getThousandsSeparator() {
		return thousandsSeparator;
	}
	
	@Override
	public Double parse(String s) {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator(decimalSeparator.charAt(0));
		symbols.setGroupingSeparator(thousandsSeparator.charAt(0));
		
		Number n;
		try {
			DecimalFormat formatter = new DecimalFormat();
			formatter.setDecimalFormatSymbols(symbols);
			n = formatter.parse(s);
        } catch (ParseException e) {
	        throw new NumberFormatException();
		}					
		
		return n.doubleValue();
	}

	@Override
	public String format(Object o) {
		double number = (Double)o;
		
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator(decimalSeparator.charAt(0));
		symbols.setGroupingSeparator(thousandsSeparator.charAt(0));
		
		DecimalFormat formatter = new DecimalFormat();
		formatter.setDecimalFormatSymbols(symbols);
		String n = formatter.format(number);
		return n;
	}
	
	@Override
	public String toString() {
		return "Numeric Value" + super.toString();
	}
}