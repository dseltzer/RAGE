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

import grails.test.mixin.*

import rage.Feed

@TestFor(DataSourceService)
@Mock(DataSource)
class DataSourceServiceTests {
	
	@Test
	void testCreateOrUpdateDataSource_feedParseError() {
		Feed feed = new Feed([title: FeedParserService.PARSE_ERROR_TITLE, description: "simulated error", url: "http//:localhost/feed1?queryParamThatNeedsToBeRemoved=true"])
		service.createOrUpdateDataSource(feed)
		
		assert 0 == DataSource.count()
	}
	
	@Test
	void testCreateOrUpdateDataSource_create() {
		Feed feed = new Feed([title: "feed1", description: "descr1", url: "http//:localhost/feed1?queryParamThatNeedsToBeRemoved=true"])
		service.createOrUpdateDataSource(feed)
		
		assert 1 == DataSource.count()
		DataSource source = DataSource.findByTitle("feed1")
		assertNotNull("data source not expected to be null", source)
		assert "descr1" == source.subtitle
		assert 1 == source.numTimesProcessed
		assert "http//:localhost/feed1" == source.primaryLink
	}
	
	@Test
	void testCreateOrUpdateDataSource_update() {
		Date before = new Date(100000000)
		DataSource existingSource = new DataSource([title: "feed1", description: "descr1", 
			primaryLink: "http//:localhost/feed1", numTimesProcessed: 10, lastRequest: before])
		existingSource.save()
		
		Feed feed = new Feed([title: "feed1", description: "descr1", url: "http//:localhost/feed1?queryParamThatNeedsToBeRemoved=true"])
		service.createOrUpdateDataSource(feed)
		
		assert 1 == DataSource.count()
		DataSource source = DataSource.findByTitle("feed1")
		assertNotNull("data source not expected to be null", source)
		assert 11 == source.numTimesProcessed
		assert before.getTime() < source.lastRequest.getTime()
	}
	
	@Test
	void testCreateOrUpdateDataSource_update_alternate() {
		Date before = new Date(100000000)
		DataSource existingSource = new DataSource([title: "feed1", description: "descr1", 
			primaryLink: "http//:localhost/feed1", alternateLinks: ["otherLink"],  numTimesProcessed: 10, lastRequest: before])
		existingSource.save()
		
		Feed feed = new Feed([title: "feed1 that may have changed", description: "descr1", url: "otherLink"])
		service.createOrUpdateDataSource(feed)
		
		assert 1 == DataSource.count()
		DataSource source = DataSource.findByTitle("feed1")
		assertNotNull("data source not expected to be null", source)
		assert 11 == source.numTimesProcessed
		assert before.getTime() < source.lastRequest.getTime()
	}
}
