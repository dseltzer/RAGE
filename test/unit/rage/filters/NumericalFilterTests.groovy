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

import static org.junit.Assert.*;
import org.junit.*;

import rage.Placemark
import rage.filters.NumericalFilter

class NumericalFilterTests {
	
	Placemark p = new Placemark()
	String title = "M 2.6, Central California"
	String content = "blah blah blah"
	
	Filter numericalFilter = null
	
	@Before
	public void setUp() {
		p.name = title
		p.description = content
	}
	
	@Test
	void testInstanceOfUrlString() {
		Filter filter = NumericalFilter.instanceOfUrlString("TITLE_x***id_LESSTHAN_5.0")
		assertNotNull("Filter not expected to be null", filter)
		assertNotNull(filter.toTheRightOfPattern)
		assertNotNull(filter.toTheLeftOfPattern)
		assertEquals("Unexpected value for the member field", 
			NumericalFilter.Field.TITLE, filter.field)
		assertEquals("Unexpected value for the member variable",
			"x_id", filter.variable)
		assertEquals("Unexpected value for the member operator",
			NumericalFilter.Operator.LESSTHAN, filter.operator)
		assertEquals("Unexpected value for the member operand",
			5.0, filter.operand, 0.0)
	}
	
	@Test
	void testInstanceOfUrlString_illegalArgumentException() {
		try {
			Filter filter = NumericalFilter.instanceOfUrlString("TITLE_x_EQUALS_5.0_hello")
			fail("exception expected")
		} catch (IllegalArgumentException i) {
		
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getClass())
		} 
	}
	
	@Test
	void testInstanceOf() {
		Filter filter = NumericalFilter.instanceOf("TITLE", "x", "EQUALS", 5.0)
		assertNotNull("Filter not expected to be null", filter)
		assertNotNull(filter.toTheRightOfPattern)
		assertNotNull(filter.toTheLeftOfPattern)
		assertEquals("Unexpected value for the member field",
			NumericalFilter.Field.TITLE, filter.field)
		assertEquals("Unexpected value for the member variable",
			"x", filter.variable)
		assertEquals("Unexpected value for the member operator",
			NumericalFilter.Operator.EQUALS, filter.operator)
		assertEquals("Unexpected value for the member operand",
			5.0, filter.operand, 0.0)
	}
	
	@Test
	void testInstanceOf_unexpectedField() {
		try {
			Filter filter = NumericalFilter.instanceOf("COTNENT", "x", "EQUALS", 5.0)
			fail("exception expected")
		} catch (IllegalArgumentException i) {
		
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getClass())
		} 
	}
	
	@Test
	void testInstanceOf_unexpectedOperator() {
		try {
			Filter filter = NumericalFilter.instanceOf("CONTENT", "x", "EQULAS", 5.0)
			fail("exception expected")
		} catch (IllegalArgumentException i) {
		
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getClass())
		}
	}
	
	@Test
	void testAccept_titleEquals() {
		Filter filter = new NumericalFilter(NumericalFilter.Field.TITLE, "variable", NumericalFilter.Operator.EQUALS, 5.0) {
			protected Double extractVariable(final String s) {
				assertEquals("extractVariable method received unexpected parameter", s, title)
				return 5.0
			}
		}
		assertTrue("filter should accept placemark", filter.accept(p))
	}
	
	@Test
	void testAccept_contentNotequals() {
		Filter filter = new NumericalFilter(NumericalFilter.Field.CONTENT, "variable", NumericalFilter.Operator.NOTEQUALS, 10.0) {
			protected Double extractVariable(final String s) {
				assertEquals("extractVariable method received unexpected parameter", s, content)
				return 5.0
			}
		}
		assertTrue("filter should accept placemark", filter.accept(p))
	}
	
	@Test
	void testAccept_greaterThan() {
		Filter filter = new NumericalFilter(NumericalFilter.Field.TITLE, "variable", NumericalFilter.Operator.GREATERTHAN, 3.0) {
			protected Double extractVariable(final String s) {
				return 5.0
			}
		}
		assertTrue("filter should accept placemark", filter.accept(p))
	}
	
	@Test
	void testAccept_lessThan() {
		Filter filter = new NumericalFilter(NumericalFilter.Field.TITLE, "variable", NumericalFilter.Operator.LESSTHAN, 6.0) {
			protected Double extractVariable(final String s) {
				return 5.0
			}
		}
		assertTrue("filter should accept placemark", filter.accept(p))
	}
	
	@Test
	void testAccept_variableNotFound() {
		Filter filter = new NumericalFilter(NumericalFilter.Field.TITLE, "variable", NumericalFilter.Operator.GREATERTHAN, 3.0) {
			protected Double extractVariable(final String s) {
				return null
			}
		}
		assertFalse("filter should not accept placemark", filter.accept(p))
	}

	@Test
	void testSerializeToUrlParam() {
		Filter filter = new NumericalFilter(NumericalFilter.Field.TITLE, "variable_id", NumericalFilter.Operator.EQUALS, 5.0)
		String urlParam = filter.serializeToUrlParam()
		assertNotNull("URL parameter not expected to be null", urlParam)
		assertEquals("Unexpected url param for filter", 
			"numerical=TITLE_variable***id_EQUALS_5.0", urlParam)
	}
	
	@Test
	void testExtractVariable_noDelimiter() {
		Filter filter = new NumericalFilter(null, "Testing", null, 0.0)
		Double value = filter.extractVariable("Testing123")
		assertNotNull("Extracted value not expected to be null", value)
		assertEquals("Unexpected variable value extected from string",
			123, value, 0.0)
	}
	
	@Test
	void testExtractVariable_spaceDelimiter() {
		Filter filter = new NumericalFilter(null, "Testing", null, 0.0)
		Double value = filter.extractVariable("Testing 123Testing")
		assertNotNull("Extracted value not expected to be null", value)
		assertEquals("Unexpected variable value extected from string",
			123, value, 0.0)
	}
	
	@Test
	void testExtractVariable_multipleSpaceDelimiter() {
		Filter filter = new NumericalFilter(null, "Testing", null, 0.0)
		Double value = filter.extractVariable("Testing    123Testing")
		assertNotNull("Extracted value not expected to be null", value)
		assertEquals("Unexpected variable value extected from string",
			123, value, 0.0)
	}
	
	@Test
	void testExtractVariable_equalDelimiter() {
		Filter filter = new NumericalFilter(null, "Testing", null, 0.0)
		Double value = filter.extractVariable("Testing=123Testing")
		assertNotNull("Extracted value not expected to be null", value)
		assertEquals("Unexpected variable value extected from string",
			123, value, 0.0)
	}
	
	@Test
	void testExtractVariable_colonDelimiter() {
		Filter filter = new NumericalFilter(null, "Testing", null, 0.0)
		Double value = filter.extractVariable("Testing:123Testing")
		assertNotNull("Extracted value not expected to be null", value)
		assertEquals("Unexpected variable value extected from string",
			123, value, 0.0)
	}
	
	@Test
	void testExtractVariable_withDecimal() {
		Filter filter = new NumericalFilter(null, "M", null, 0.0)
		Double value = filter.extractVariable("M 2.6, Central California")
		assertNotNull("Extracted value not expected to be null", value)
		assertEquals("Unexpected variable value extected from string",
			2.6, value, 0.0)
	}
	
	@Test
	void testExtractVariable_withCharactersInBetween() {
		Filter filter = new NumericalFilter(null, "num", null, 0.0)
		Double value = filter.extractVariable("number = 5 num = 10")
		assertNotNull("Extracted value not expected to be null", value)
		assertEquals("Unexpected variable value extected from string",
			10, value, 0.0)
	}
	
	@Test
	void testExtractVariable_withCharactersInBetweenNotFound() {
		Filter filter = new NumericalFilter(null, "num", null, 0.0)
		Double value = filter.extractVariable("num hello 10")
		assertNull("Extracted value expected to be null", value)
	}
	
	@Test
	void testExtractVariable_withCommaNotFound() {
		Filter filter = new NumericalFilter(null, "Magnitude", null, 0.0)
		Double value = filter.extractVariable("Magnitude, M 2.6, Central California")
		assertNull("Extracted value expected to be null", value)
	}
	
	@Test
	void testExtractVariable_leftOfVariable() {
		Filter filter = new NumericalFilter(null, "Magnitude", null, 0.0)
		Double value = filter.extractVariable("10.0 Magnitude, 20 Size")
		assertNotNull("Extracted value not expected to be null", value)
		assertEquals("Unexpected variable value extected from string",
			10.0, value, 0.0)
	}
	
	@Test
	void testExtractVariable_singleNumericChar() {
		Filter filter = new NumericalFilter(null, "interest_level", null, 0.0)
		Double value = filter.extractVariable("interest_level: 5")
		assertNotNull("Extracted value not expected to be null", value)
		assertEquals("Unexpected variable value extected from string",
			5.0, value, 0.0)
	}
}
