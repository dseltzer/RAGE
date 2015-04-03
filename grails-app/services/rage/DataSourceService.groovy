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

import rage.Feed
import rage.DataSource

class DataSourceService {

	public void createOrUpdateDataSource(Feed feed) {
		try {
			if (FeedParserService.PARSE_ERROR_TITLE.equals(feed.title)) {
				throw new Exception("Unable to store data source; Feed parse error: " + feed.getDescription() + " for url:" + feed.getUrl())
			}
			String url = removeUrlParams(feed.getUrl())
			DataSource source = DataSource.findByPrimaryLink(url)
			if (null == source) source = DataSource.findByAlternateLinks([url])
			if (null == source) { //create
				DataSource newSource = new DataSource()
				newSource.numTimesProcessed = 1
				newSource.title = feed.getTitle()
				newSource.subtitle = feed.getDescription()
				newSource.primaryLink = url
				newSource.save(failOnError:true, flush: true)
			} else { //update
				source.numTimesProcessed = source.numTimesProcessed + 1
				source.lastRequest = new Date()
				source.save(failOnError:true, flush: true)
			}
		} catch (Exception e) {
			//Data source tracking is an extract so if creating or updating throws an exception, do not interrupt rage's main goal of converting web feeds into KML!
			println("createOrUpdateDataSource Exception: " + e.getMessage()) 
		}
	}
	
	private String removeUrlParams(String url) {
		String cleanedUrl = url
		int paramsIndex = cleanedUrl.indexOf('?')
		if (paramsIndex != -1) {
			cleanedUrl = cleanedUrl.substring(0, paramsIndex)
		}
		return cleanedUrl
	}
}
