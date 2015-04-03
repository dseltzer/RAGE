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

import rage.RageTagLib
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

@TestFor(RageTagLib)
class RageTagLibTests {

	@Test
	void testRGBToBGR() {
		assertEquals("Unexpected BGR color", 
			"fd0000", applyTemplate('<g:rgbToBGR>0000fd</g:rgbToBGR>'))
	}

	@Test
	void testEncodeParam() {
		String encodedUrl = "http://localhost/site?this=aamp;that=b";
		assertEquals("Unexpected encoded parameter",
			encodedUrl, 
			applyTemplate('<g:encodeParam>http://localhost/site?this=a&that=b</g:encodeParam>'));
	}

	@Test
	void testRageServerURL() {
		//Set up mock configuration
		String rageServerURL = "localhost:8080/RAGE";
		def mockedConfig = new ConfigObject();
		mockedConfig.rage.server.url = rageServerURL;
		ConfigurationHolder.config = mockedConfig;

		assertEquals("Unexpected rage server URL",
			rageServerURL,
			applyTemplate('<g:rageServerURL />'));
	}
	
	@Test
	void testRgbToIcon() {
		assert applyTemplate('<g:rgbToIcon rgb="ff0000" />') == 'red-circle.png'
		assert applyTemplate('<g:rgbToIcon rgb="f3c1a9" />') == 'red-circle.png' //light red
		
		assert applyTemplate('<g:rgbToIcon rgb="cc00ff" />') == 'purple-circle.png'
		assert applyTemplate('<g:rgbToIcon rgb="a582f8" />') == 'purple-circle.png'
		assert applyTemplate('<g:rgbToIcon rgb="7d70a9" />') == 'purple-circle.png'
		
		assert applyTemplate('<g:rgbToIcon rgb="ff00ff" />') == 'pink-circle.png'
		assert applyTemplate('<g:rgbToIcon rgb="be387b" />') == 'pink-circle.png'
		
		assert applyTemplate('<g:rgbToIcon rgb="ffff00" />') == 'ylw-circle.png'
		assert applyTemplate('<g:rgbToIcon rgb="d4d466" />') == 'ylw-circle.png' //mustard yello
		
		assert applyTemplate('<g:rgbToIcon rgb="00ff00" />') == 'grn-circle.png'
		assert applyTemplate('<g:rgbToIcon rgb="0c7e68" />') == 'grn-circle.png' //forest green
		assert applyTemplate('<g:rgbToIcon rgb="b9f1be" />') == 'grn-circle.png' //light green
		assert applyTemplate('<g:rgbToIcon rgb="b3c86d" />') == 'grn-circle.png'
		
		assert applyTemplate('<g:rgbToIcon rgb="00ffff" />') == 'ltblu-circle.png'
		assert applyTemplate('<g:rgbToIcon rgb="a6f1e2" />') == 'ltblu-circle.png'
		
		assert applyTemplate('<g:rgbToIcon rgb="0000ff" />') == 'blu-circle.png'
		assert applyTemplate('<g:rgbToIcon rgb="0c8dcb" />') == 'blu-circle.png'
		assert applyTemplate('<g:rgbToIcon rgb="a5bff8" />') == 'blu-circle.png'
	}
	
	@Test
	void testRgbToTheme() {
		assert applyTemplate('<g:rgbToTheme rgb="ff0000" />') == 'rage_red'
		assert applyTemplate('<g:rgbToTheme rgb="cc00ff" />') == 'rage_purple'
		assert applyTemplate('<g:rgbToTheme rgb="ff00ff" />') == 'rage_pink'
		assert applyTemplate('<g:rgbToTheme rgb="ffff00" />') == 'rage_yellow'
		assert applyTemplate('<g:rgbToTheme rgb="00ff00" />') == 'rage_green'
		assert applyTemplate('<g:rgbToTheme rgb="00ffff" />') == 'rage_lightblue'
		assert applyTemplate('<g:rgbToTheme rgb="0000ff" />') == 'rage_blue'
	}
}
