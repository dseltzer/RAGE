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

import java.util.regex.Matcher
import java.util.regex.Pattern

import rage.Placemark

public class NumericalFilter implements Filter {

	public static String PARAM_NAME = "numerical"
	public static String DELIMITER = "_"
	public static String DELIMITER_SWAP = "***"
	
	public enum Field {
		TITLE,
		CONTENT
	}
	
	public enum Operator {
		EQUALS,
		NOTEQUALS,
		LESSTHAN,
		GREATERTHAN
	}
	
	protected NumericalFilter(Field field, String variable, Operator operator, double operand) {
		/*
		* RightOfPattern REGEX:
		* 		variable string followed by
		* 			zero-to-many-possive(*+): non-digit-characher(\\D) that is-not(^) a comma or word-character(\\w)
		* 			grouped subpattern containing a number(\\d) which may contain a decimal place
		*/
		this.toTheRightOfPattern = Pattern.compile(variable + "[\\D&&[^,\\w]]*+(\\d+.?\\d*)") 
		this.toTheLeftOfPattern = Pattern.compile("(\\d+.?\\d+)+[\\D&&[^,\\w]]*" + variable)
		this.field = field
		this.variable = variable
		this.operator = operator
		this.operand = operand
	}
	
	private final Pattern toTheRightOfPattern
	private final Pattern toTheLeftOfPattern
	private final Field field
	private final String variable
	private final double operand
	private final Operator operator
	
	public static NumericalFilter instanceOfUrlString(final String s) throws Exception {
		String[] parameters = s.split(DELIMITER)
		if (parameters.length != 4) {
			throw new IllegalArgumentException("Input, " + s + ", contains unexpected number of tokens.  Expecting 4, received " + parameters.length)
		}
		
		return NumericalFilter.instanceOf(parameters[0], parameters[1].replace(DELIMITER_SWAP, DELIMITER), parameters[2], new Double(parameters[3]))
	}
	
	public static NumericalFilter instanceOf(final String stringField, final String var, final String stringOperator, final double operand) throws Exception {
		Field f
		if (Field.TITLE.name().equals(stringField)) {
			f = Field.TITLE
		} else if (Field.CONTENT.name().equals(stringField)) {
			f = Field.CONTENT
		} else {
			throw new IllegalArgumentException("Unable to build NumericalFilter, unexpected field: " + stringField);
		}
		
		Operator op
		if (Operator.EQUALS.name().equals(stringOperator.toUpperCase())) {
			op = Operator.EQUALS
		} else if (Operator.NOTEQUALS.name().equals(stringOperator.toUpperCase())) {
			op = Operator.NOTEQUALS
		} else if (Operator.LESSTHAN.name().equals(stringOperator.toUpperCase())) {
			op = Operator.LESSTHAN
		} else if (Operator.GREATERTHAN.name().equals(stringOperator.toUpperCase())) {
			op = Operator.GREATERTHAN
		} else {
			throw new IllegalArgumentException("Unable to build NumericalFilter, unexpected operator: " + stringField);
		}
		
		return new NumericalFilter(f, var, op, operand)
	}
	
	public boolean accept(Placemark p) throws Exception {
		String s = null
		if (Field.TITLE == field) {
			s = p.name
		} else if (Field.CONTENT == field) {
			s = p.description
		}
		Double variable = extractVariable(s)
		
		boolean rval = false
		if (null != variable) {
			if (Operator.EQUALS == operator) {
				rval = variable == operand
			} else if (Operator.NOTEQUALS == operator) {
				rval = variable != operand
			} else if (Operator.LESSTHAN == operator) {
				rval = variable < operand
			} else if (Operator.GREATERTHAN == operator) {
				rval = variable > operand
			}
		}
		
		return rval
	}

    public String serializeToUrlParam() {
		StringBuffer buf = new StringBuffer();
		buf.append(PARAM_NAME)
		buf.append("=")
		buf.append(field.name())
		buf.append(DELIMITER)
		buf.append(variable.replace(DELIMITER, DELIMITER_SWAP))
		buf.append(DELIMITER)
		buf.append(operator.name())
		buf.append(DELIMITER)
		buf.append(operand)
	    return buf.toString();
    }
	
	protected Double extractVariable(final String s) {
		Double rval = null
		Matcher matcher = toTheRightOfPattern.matcher(s)
		if (matcher.find()) {
			String match = matcher.group(1);
			int lastCharIndex = match.length()  - 1
			if (!Character.isDigit(match.charAt(lastCharIndex))) {
				match = match.substring(0, lastCharIndex)
			}
			rval = new Double(match)
		} else {
			matcher = toTheLeftOfPattern.matcher(s)
			if (matcher.find()) {
				rval = new Double(matcher.group(1))
			}
		}

		return rval
	}
}
