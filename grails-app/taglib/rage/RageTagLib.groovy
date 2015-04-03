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

import org.codehaus.groovy.grails.commons.ConfigurationHolder;
import rage.utils.ConverterUtils;

class RageTagLib {

	public static final int RGB_HEX_STRING_LENGTH = 6;
	public static final int RGB_HEX_STRING_R_START_INDEX = 0;
	public static final int RGB_HEX_STRING_G_START_INDEX = 2;
	public static final int RGB_HEX_STRING_B_START_INDEX = 4;

	/**
	 * KML specifies color as hex a string in the format bbggrr
	 * Html specifies color as hex a string in the format rrggbb
	 * Need a custom tag to translate from rgb colors to bgr colors
	 */
	def rgbToBGR = {attrs, body ->
		String rgb = attrs.body ?: body();
		rgb = rgb.trim();
		String bgr;
		if (RGB_HEX_STRING_LENGTH == rgb.size()) {
			def r = rgb.substring(RGB_HEX_STRING_R_START_INDEX, RGB_HEX_STRING_G_START_INDEX);
			def g = rgb.substring(RGB_HEX_STRING_G_START_INDEX, RGB_HEX_STRING_B_START_INDEX);
			def b = rgb.substring(RGB_HEX_STRING_B_START_INDEX);
			bgr = b + g + r;
		} else {
			//Badly formed RGB input, just echo out
			bgr = rgb;
		}
		out << bgr;
	}

	/**
	 * Encode URL string for placement in KML file
	 */ 
	def encodeParam = {attrs, body ->
		String param = attrs.body ?: body();
		out << ConverterUtils.encodeParam(param);
	}

	/**
	 * Returns value of configuration property 'rage.server.url'
	 */
	def rageServerURL = {attrs, body ->
		out << ConfigurationHolder.config.rage.server.url;
	}
	
	
	def rageSupportUrl = {attrs, body ->
		out << ConfigurationHolder.config.rage.support.url;
	}
	
	def rgbToIcon = {attrs, body ->
		String icon = "wht-circle.png"
		String color = rgbToColor(attrs.rgb)
		if ("red".equals(color)) {
			icon = "red-circle.png"
		} else if ("yellow".equals(color)) {
			icon = "ylw-circle.png"
		} else if ("green".equals(color)) {
			icon = "grn-circle.png"
		} else if ("lightblue".equals(color)) {
			icon = "ltblu-circle.png"
		} else if ("blue".equals(color)) {
			icon = "blu-circle.png"
		} else if ("purple".equals(color)) {
			icon = "purple-circle.png"
		} else if ("pink".equals(color)) {
			icon = "pink-circle.png"
		}
		out << icon
	}
	
	def rgbToTheme = {attrs, body ->
		out << "rage_" + rgbToColor(attrs.rgb)
	}
	
	private String rgbToColor(String rgb) {
		Long r = Long.decode("0x" + rgb.substring(RGB_HEX_STRING_R_START_INDEX, RGB_HEX_STRING_G_START_INDEX))
		Long g = Long.decode("0x" + rgb.substring(RGB_HEX_STRING_G_START_INDEX, RGB_HEX_STRING_B_START_INDEX))
		Long b = Long.decode("0x" + rgb.substring(RGB_HEX_STRING_B_START_INDEX))
		
		String color = "white"
		if (r >= 102 && g < 102 && b < 102) {
			color = "red"
		} else if (r >= 191 && g > 191 && b < 127) {
			color = "yellow"
		} else if (r < 102 && g >= 102 && b <= 191) {
			color = "green"
		} else if (r < 191 && g > 191 && b > 191) {
			color = "lightblue"
		} else if (r < 102 && g <= 191 && b >= 102) {
			color = "blue"
		} else if (r <= 204 && g < 191 && b >= 127) {
			color = "purple"
		} else if (r > 127 && g < 127 && b >= 102) {
			color = "pink"
		} else if (r >= 204 && g < 204 && b < 204) {
			color = "red"
		} else if (r < 204 && g > 191 && b < 204) {
			color = "green" //light green
		} else if (r < 204 && g < 204 && b >= 204) {
			color = "blue"
		}
		//println(rgb + " = rgb(" + r + "," + g + "," + b + ") turned into " + color)
		return color
	}
}
