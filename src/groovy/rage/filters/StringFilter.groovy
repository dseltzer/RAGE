/**************************************************************************
Copyright (C) 2008-2010 United States Government. All rights reserved. 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
****************************************************************************/
package rage.filters

import rage.Placemark

public class StringFilter implements Filter {

	public enum Field {
		TITLE,
		CONTENT
	}
	
	public enum Operator {
		EQUALS,
		NOTEQUALS,
		CONTAINS,
		NOTCONTAINS
	}
	
	private StringFilter(Field filterField, String filterValue, Operator op) {
		this.field = filterField
		this.filterOperand = filterValue
		this.operator = op
	}
	
	private final Field field
	private final String filterOperand
	private final Operator operator
	
	/**
	 * Static factory method used to generate a StringFilter instance that operates on the content field
	 */
	public static StringFilter instanceOfContentFilter(final String operatorString, final String filterValue) throws Exception {
		return new StringFilter(Field.CONTENT, filterValue, convertToOperator(operatorString))
	}
	
	/**
	* Static factory method used to generate a StringFilter instance that operates on the title field
	*/
   public static StringFilter instanceOfTitleFilter(final String operatorString, final String filterValue) throws Exception {
	   return new StringFilter(Field.TITLE, filterValue, convertToOperator(operatorString))
   }

   private static Operator convertToOperator(final String operatorString) throws Exception {
	   Operator op
	   if (Operator.EQUALS.name().equals(operatorString.toUpperCase())) {
		   op = Operator.EQUALS
	   } else if (Operator.NOTEQUALS.name().equals(operatorString.toUpperCase())) {
		   op = Operator.NOTEQUALS
	   } else if (Operator.CONTAINS.name().equals(operatorString.toUpperCase())) {
		   op = Operator.CONTAINS
	   } else if (Operator.NOTCONTAINS.name().equals(operatorString.toUpperCase())) {
		   op = Operator.NOTCONTAINS
	   }
	   
	   return op
   }
   
   public boolean accept(Placemark placemark) throws Exception {
		boolean rval = false
		
		String placemarkOperand = ""
		if (Field.TITLE == field) {
			placemarkOperand = placemark.name
		} else if (Field.CONTENT == field) {
			placemarkOperand = placemark.description
		}
		
		if (Operator.EQUALS == operator) {
			rval = placemarkOperand.equals(filterOperand)
		} else if (Operator.NOTEQUALS == operator) {
			rval = !placemarkOperand.equals(filterOperand)
		} else if (Operator.CONTAINS == operator) {
			rval = placemarkOperand.contains(filterOperand)
		} else if (Operator.NOTCONTAINS == operator) {
			rval = !placemarkOperand.contains(filterOperand)
		}
		
		return rval
	}

    public String serializeToUrlParam() {
		StringBuffer buf = new StringBuffer();
		buf.append(field.name().toLowerCase())
		buf.append("=")
		buf.append(operator.name())
		buf.append("_")
		buf.append(filterOperand)
	    return buf.toString();
    }
}
