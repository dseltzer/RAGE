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

class ConverterUtilsTests {

	String decodedUrlParam = "http://localhost/site%20fart?this=a&that=b";
	String encodedUrlParam = "http://localhost/sitespa;fart?this=aamp;that=b";

	@Test
	void testEncodeParam() {
		String encoded = ConverterUtils.encodeParam(decodedUrlParam);
		assertEquals(encodedUrlParam, encoded);
	}

	@Test
	void testEncodeParam_with_plus() {
		String encoded = ConverterUtils.encodeParam("http://localhost/site+fart?this=a&that=b");
		assertEquals(encodedUrlParam, encoded);
	}

	@Test
	void testDecodeParam() {
		String decoded = ConverterUtils.decodeParam(encodedUrlParam);
		assertEquals(decodedUrlParam, decoded);
	}
}
