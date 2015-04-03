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
import rage.filters.StringFilter

class StringFilterTests {
	
	Placemark p = new Placemark()
	String title = "hello"
	String content = "How are you doing today?"
	
	@Before
	public void setUp() {
		p.name = title
		p.description = content
	}

	@Test
	void testConvertToOperator_equals() {
		assertEquals("Filter member operator not set to expected value",
			StringFilter.Operator.EQUALS, StringFilter.convertToOperator("equals"))
	}
	
	@Test
	void testConvertToOperator_notequals() {
		assertEquals("Filter member operator not set to expected value",
			StringFilter.Operator.NOTEQUALS, StringFilter.convertToOperator("notequals"))
	}
	
	@Test
	void testConvertToOperator_contains() {
		assertEquals("Filter member operator not set to expected value",
			StringFilter.Operator.CONTAINS, StringFilter.convertToOperator("contains"))
	}
	
	@Test
	void testConvertToOperator_notcontains() {
		assertEquals("Filter member operator not set to expected value",
			StringFilter.Operator.NOTCONTAINS, StringFilter.convertToOperator("notcontains"))
	}
	
	@Test
	void testInstanceOfTitleFilter() {
		String filterValue = "hello"
		StringFilter titleFilter = StringFilter.instanceOfTitleFilter("equals", filterValue)
		assertNotNull("Title filter not expected to be null", titleFilter)
		assertEquals("Filter member field not set to expected value",
			StringFilter.Field.TITLE, titleFilter.field)
		assertEquals("Filter member filterOperand not set to expected value",
			filterValue, titleFilter.filterOperand)
		assertEquals("Filter member operator not set to expected value",
			StringFilter.Operator.EQUALS, titleFilter.operator)
	}

	@Test
	void testInstanceOfContentFilter() {
		String filterValue = "hello"
		StringFilter contentFilter = StringFilter.instanceOfContentFilter("equals", filterValue)
		assertNotNull("Content filter not expected to be null", contentFilter)
		assertEquals("Filter member field not set to expected value",
			StringFilter.Field.CONTENT, contentFilter.field)
		assertEquals("Filter member filterOperand not set to expected value",
			filterValue, contentFilter.filterOperand)
		assertEquals("Filter member operator not set to expected value",
			StringFilter.Operator.EQUALS, contentFilter.operator)
	}

	@Test
	void testAcceptContentEqualsValue() {
		StringFilter filter = new StringFilter(StringFilter.Field.CONTENT, content, StringFilter.Operator.EQUALS)
		assertTrue("filter should accept placemark", filter.accept(p))
	}
	
	@Test
	void testAcceptTitleEqualsValue_true() {
		StringFilter filter = new StringFilter(StringFilter.Field.TITLE, title, StringFilter.Operator.EQUALS)
		assertTrue("filter should accept placemark", filter.accept(p))
	}
	
	@Test
	void testAcceptTitleEqualsValue_false() {
		StringFilter filter = new StringFilter(StringFilter.Field.TITLE, "goodbye", StringFilter.Operator.EQUALS)
		assertFalse("filter should not accept placemark", filter.accept(p))
	}
	
	@Test
	void testAcceptTitleNotEqualsValue_true() {
		StringFilter filter = new StringFilter(StringFilter.Field.TITLE, "goodbye", StringFilter.Operator.NOTEQUALS)
		assertTrue("filter should accept placemark", filter.accept(p))
	}
	
	@Test
	void testAcceptTitleNotEqualsValue_false() {
		StringFilter filter = new StringFilter(StringFilter.Field.TITLE, title, StringFilter.Operator.NOTEQUALS)
		assertFalse("filter should not accept placemark", filter.accept(p))
	}
	
	@Test
	void testAcceptTitleContainsValue_true() {
		StringFilter filter = new StringFilter(StringFilter.Field.TITLE, "ello", StringFilter.Operator.CONTAINS)
		assertTrue("filter should accept placemark", filter.accept(p))
	}
	
	@Test
	void testAcceptTitleContainValue_false() {
		StringFilter filter = new StringFilter(StringFilter.Field.TITLE, "bye", StringFilter.Operator.CONTAINS)
		assertFalse("filter should not accept placemark", filter.accept(p))
	}
	
	@Test
	void testAcceptTitleNotContainsValue_true() {
		StringFilter filter = new StringFilter(StringFilter.Field.TITLE, "bye", StringFilter.Operator.NOTCONTAINS)
		assertTrue("filter should accept placemark", filter.accept(p))
	}
	
	@Test
	void testAcceptTitleNotContainValue_false() {
		StringFilter filter = new StringFilter(StringFilter.Field.TITLE, "ello", StringFilter.Operator.NOTCONTAINS)
		assertFalse("filter should not accept placemark", filter.accept(p))
	}
	
	@Test
	void testSerializeToUrlParam_title() {
		StringFilter filter = new StringFilter(StringFilter.Field.TITLE, "ello", StringFilter.Operator.NOTCONTAINS)
		assertEquals("Unexpected url param for filter", 
			"title=NOTCONTAINS_ello", filter.serializeToUrlParam())
	}
}
