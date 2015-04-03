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

class ConverterUtils {

	private final static String PARAM_AMPERSTAND_DECODED = "&"
	private final static String PARAM_AMPERSTAND_ENCODED = "amp;"
	private final static String PARAM_SPACE_DECODED = "%20"
	private final static String PARAM_SPACE_ENCODED = "spa;"

	private ConverterUtils() {}

	public static String encodeParam(String value) {
		String encoded = value;
		encoded = encoded.replaceAll(PARAM_AMPERSTAND_DECODED, PARAM_AMPERSTAND_ENCODED);
		encoded = encoded.replaceAll(PARAM_SPACE_DECODED, PARAM_SPACE_ENCODED);
		encoded = encoded.replaceAll('\\+', PARAM_SPACE_ENCODED);
		return encoded;
	}

	public static String decodeParam(String value) {
		String decoded = value;
		decoded = decoded.replaceAll(PARAM_AMPERSTAND_ENCODED, PARAM_AMPERSTAND_DECODED);
		decoded = decoded.replaceAll(PARAM_SPACE_ENCODED, "%20");
		return decoded
	}
}
