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

class HttpUtils {

	private HttpUtils() {}
	
	/**
	 * Performs HTTP GET on provided URL
	 * Returns map containing HTTP reponse code: 'responsecode' key, 
	 * HTTP response message: 'responsemessage' key, and HTTP respons content: (feed XML): 'content' key
	 */
	public static getFeed(String feedUrl, String ifModifiedSince, String ifNoneMatch) {
		def url = new URL(feedUrl)

		Map returnMap = null
		try { 
			def connection = url.openConnection()

			connection.setConnectTimeout(5000);
			if (ifModifiedSince) {
				connection.setRequestProperty("If-Modified-Since", ifModifiedSince)
			}
			if (ifNoneMatch) {
				connection.setRequestProperty("If-None-Match", ifNoneMatch)
			}
			connection.connect()
			returnMap = [responsecode: connection.responseCode, 
					responsemessage: connection.responseMessage]

			if (200 == connection.responseCode) {
				returnMap.put("content", connection.content.text)
			}
		} catch (Exception e) {
			returnMap = [responsecode: 400, responsemessage: e.getMessage()]
		}
		return returnMap
	}
}
