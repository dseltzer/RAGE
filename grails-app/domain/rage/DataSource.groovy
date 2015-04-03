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

import org.bson.types.ObjectId

class DataSource {

	ObjectId id
	
	static hasMany = [alternateLinks: String, tags: String]
	
	String primaryLink
	String title
	String subtitle
	Date lastRequest = new Date()
	int numTimesProcessed = 0
	
	static constraints = {
		subtitle nullable:true
	}
	
	static mapping = {
		primaryLink index:true
		alternateLinks index:true
	}
}
