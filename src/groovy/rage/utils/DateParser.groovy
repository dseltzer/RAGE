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

import java.text.SimpleDateFormat;
import java.util.TimeZone;

class DateParser {

	private static final SimpleDateFormat DATE_FORMAT_RFC8222 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
	private static final SimpleDateFormat DATE_FORMAT_RFC8222_2_DIGIT_YEAR = new SimpleDateFormat("EEE, dd MMM yy HH:mm:ss z");
	private static final SimpleDateFormat DATE_FORMAT_RFC8222_NO_DAY_PREFIX = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
	private static final SimpleDateFormat DATE_FORMAT_ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static final int RFC822_LENGTH_2_DIGIT_YEAR = 27;

	private DateParser() {}
	
	static {
		DATE_FORMAT_RFC8222.setTimeZone(TimeZone.getTimeZone("UTC"));
		DATE_FORMAT_RFC8222_2_DIGIT_YEAR.setTimeZone(TimeZone.getTimeZone("UTC"));
		DATE_FORMAT_ISO8601.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	public static String convertToISO8601(String dateTime) {
		Date date = null;
		try {
			date = DATE_FORMAT_RFC8222_2_DIGIT_YEAR.parse(dateTime);
		} catch (Exception e) {
			//ignore
		}
		
		if (null == date) {
			try {
				date = DATE_FORMAT_RFC8222.parse(dateTime);
			} catch (Exception e) {
				//ignore
			}
		}
		
		if (null == date) {
			try {
				date = DATE_FORMAT_RFC8222_NO_DAY_PREFIX.parse(dateTime);
			} catch (Exception e) {
				//ignore
			}
		}
		
		if (null == date) {
			return null;
		}
		return DATE_FORMAT_ISO8601.format(date) + "Z";
	}
}
