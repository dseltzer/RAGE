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

class Feed {

	enum FeedType {
		ATOM,
		RSS_2_0,
		RSS_1_0
	}

	String url
	FeedType type
	String title
	String description
	String lastModified //Support conditional GET
	String eTag //Support conditional GET
	Map<String, List<Placemark>> placemarks

	static constraints = {
		url()
	}

	String toString() {
		"$type: $title"
	}
}
