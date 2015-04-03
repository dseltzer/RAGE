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
package rage

import static org.junit.Assert.*;
import org.junit.*;
import rage.utils.HttpUtils;

class HttpUtilsTest {
	
	@Test
	void testGetFeed() {
		Map feedMap = HttpUtils.getFeed("http://earthquake.usgs.gov/earthquakes/catalogs/7day-M5.xml", null,  null)
		assertNotNull(feedMap)
		assert feedMap.containsKey('responsecode')
		assertEquals("Unexpected response code for GET", 200, feedMap.get('responsecode'))
		assert feedMap.containsKey('responsemessage')
		assertEquals('OK', feedMap.get('responsemessage'))
		assert feedMap.containsKey('content')
		assertNotNull(feedMap.get('content'))
	}
}
