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
package rage.utils

import static org.junit.Assert.*;
import org.junit.*;

class DateParserTests {

	@Test
	void testConvertToISO8601_RFC822() {
		String dateTimeRFC822 = "Wed, 27 Jan 2010 00:00:00 EST";
		String convertedDateTime = DateParser.convertToISO8601(dateTimeRFC822);
		assertEquals("2010-01-27T05:00:00Z", convertedDateTime);
	}

	@Test
	void testConvertToISO8601_bad_date_format() {
		String dateTimeRFC822 = "Wed, 2010 Jan 27 EST";
		String convertedDateTime = DateParser.convertToISO8601(dateTimeRFC822);
		assertEquals(null, convertedDateTime);
	}

	@Test
	void testConvertToISO8601_RFC822_two_digit_year() {
		String dateTimeRFC822 = "Wed, 27 Jan 10 00:00:00 EST";
		String convertedDateTime = DateParser.convertToISO8601(dateTimeRFC822);
		assertEquals("2010-01-27T05:00:00Z", convertedDateTime);
	}
	
	@Test
	void testConvertToISO8601_RFC822_with_no_day() {
		String dateTime = "07 Apr 2009 04:35:51 -0000";
		String convertedDateTime = DateParser.convertToISO8601(dateTime);
		assertNotNull("Converted date time should not be null", convertedDateTime);
		assertEquals("2009-04-07T04:35:51Z", convertedDateTime);
	}
}
