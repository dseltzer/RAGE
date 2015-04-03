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

import groovy.util.slurpersupport.GPathResult

import rage.GeoParserService.GeoType
import rage.filters.Filter
import rage.filters.StringFilter
import rage.Feed.FeedType
import rage.utils.HttpUtils

import java.util.Map;

@TestFor(FeedController)
class FeedControllerTests {
	
	String feedTitle = "Example Feed"
	String feedDesc = "syndication feed description"
	String item1Title = "1"
	String item1Desc = "item 1 is about x"
	String item1Date = "2004-08-19T11:54:37-08:00"
	String item2Title = "2"
	String item2Desc = "item 2 is about y"
	String item2Date = "2004-08-19T11:54:37-08:00"
	
	@Before
	void setUp() {
		config.rage.discovery.enable = false
	}
	
	@Test
	void testKml() {
		def feedName = "feed";
		def feedDescription = "This Feed is the best!";

		def httpUtilsMock = mockFor(HttpUtils);
		httpUtilsMock.demand.static.getFeed(1..1) {String arg1, String arg2, String arg3 -> 
			return [responsecode: 200, content: "feed xml..."]
		};
		httpUtilsMock.createMock();

		def feedParserServiceMock = mockFor(FeedParserService);
		feedParserServiceMock.demand.parseFeed(1..1) {String arg1, List<Filter> arg2 -> 
			Feed feed = new Feed();
			feed.title = feedName;
			feed.description = feedDescription;
			return feed
		};
		controller.feedParserService = feedParserServiceMock.createMock();

		config.rage.discovery.enable = true
		def dataSourceServiceMock = mockFor(DataSourceService);
		dataSourceServiceMock.demand.createOrUpdateDataSource(1..1) {Feed arg1 ->
			if (null == arg1.getUrl()) {
				throw new Exception("Feed URL not provided!")
			}
		};
		controller.dataSourceService = dataSourceServiceMock.createMock()
		
		controller.params.url = "http://localhost:8080/feed.atom";
		controller.params.color = "ff0000";

		controller.kml()
		
		assertNotNull("Model not expected to be null", controller.modelAndView.model);
		Feed feed = controller.modelAndView.model.feedInstance;
		assertNotNull("Controller map does not contain expected model object: 'feedInstance'", feed);
		assertEquals(feedName, feed.title);
		assertEquals(feedDescription, feed.description);

		String color = controller.modelAndView.model.feedColor;
		assertNotNull("Controller map does not contain expected model object: 'feedColor'", color);
		assertEquals(controller.params.color, color);
		httpUtilsMock.verify();
		feedParserServiceMock.verify();
		dataSourceServiceMock.verify()
	}
	
	@Test
	void testKml_feedParseError() {
		def httpUtilsMock = mockFor(HttpUtils);
		httpUtilsMock.demand.static.getFeed(1..1) {String arg1, String arg2, String arg3 ->
			return [responsecode: 200, content: "feed xml..."]
		};
		httpUtilsMock.createMock();

		def feedParserServiceMock = mockFor(FeedParserService);
		feedParserServiceMock.demand.parseFeed(1..1) {String arg1, List<Filter> arg2 ->
			Feed feed = new Feed();
			feed.title = FeedParserService.PARSE_ERROR_TITLE;
			feed.description = "simulated error";
			return feed
		};
		controller.feedParserService = feedParserServiceMock.createMock();

		config.rage.discovery.enable = true
		def dataSourceServiceMock = mockFor(DataSourceService);
		dataSourceServiceMock.demand.createOrUpdateDataSource(1..1) {Feed arg1 ->
			if (null == arg1.getUrl()) {
				throw new Exception("Feed URL not provided!")
			}
		};
		controller.dataSourceService = dataSourceServiceMock.createMock()
		
		controller.params.url = "http://localhost:8080/feed.atom";
		controller.params.color = "ff0000";

		controller.kml()
		
		assertNotNull("Model not expected to be null", controller.modelAndView.model);
		Feed feed = controller.modelAndView.model.feedInstance;
		assertNotNull("Controller map does not contain expected model object: 'feedInstance'", feed);
		assertEquals(FeedParserService.PARSE_ERROR_TITLE, feed.title);
		assertEquals("simulated error", feed.description);

		String color = controller.modelAndView.model.feedColor;
		assertNotNull("Controller map does not contain expected model object: 'feedColor'", color);
		assertEquals(controller.params.color, color);
		httpUtilsMock.verify();
		feedParserServiceMock.verify();
		dataSourceServiceMock.verify()
	}

	@Test
	void testKml_no_url_param() {

		controller.params.color = "ff0000";
		controller.kml();

		assertNotNull(controller.flash);
		assertNotNull(controller.flash.message);
	}

	@Test
	void testKml_no_color_param() {

		def feedName = "feed";
		def feedDescription = "This Feed is the best!";

		String defaultColor = "00ff00";
		config.rage.default.marker.color = defaultColor;

		def httpUtilsMock = mockFor(HttpUtils);
		httpUtilsMock.demand.static.getFeed(1..1) {String arg1, String arg2, String arg3 -> 
			return [responsecode: 200, content: "feed xml..."]
		};
		httpUtilsMock.createMock();

		def feedParserServiceMock = mockFor(FeedParserService);
		feedParserServiceMock.demand.parseFeed(1..1) {String arg1, List<Filter> arg2 -> 
			Feed feed = new Feed();
			feed.title = feedName;
			feed.description = feedDescription;
			return feed
		};
		controller.feedParserService = feedParserServiceMock.createMock();

		controller.params.url = "http://localhost:8080/feed.atom";

		controller.kml();

		assertNotNull("Model not expected to be null", controller.modelAndView.model);
		Feed feed = controller.modelAndView.model.feedInstance;
		assertNotNull("Controller map does not contain expected model object: 'feedInstance'", feed);
		assertEquals(feedName, feed.title);
		assertEquals(feedDescription, feed.description);

		String color = controller.modelAndView.model.feedColor;
		assertNotNull("Controller map does not contain expected model object: 'feedColor'", color);
		assertEquals(defaultColor, color);
		httpUtilsMock.verify();
		feedParserServiceMock.verify();
	}

	@Test
	void testKml_titleFilter_param() {
		def feedName = "feed";
		def feedDescription = "This Feed is the best!";

		def httpUtilsMock = mockFor(HttpUtils);
		httpUtilsMock.demand.static.getFeed(1..1) {String arg1, String arg2, String arg3 ->
			return [responsecode: 200, content: "feed xml..."]
		};
		httpUtilsMock.createMock();

		def feedParserServiceMock = mockFor(FeedParserService);
		feedParserServiceMock.demand.parseFeed(1..1) {String arg1, List<Filter> arg2 ->
			assertNotNull("Filter list not expected to be null", arg2)
			assertEquals("Unexpected filter list size",
				1, arg2.size())
			StringFilter filter = arg2.get(0)
			assertNotNull("Filter not expected to be null", filter)
			assertEquals("Filter member field not set to expected value",
				StringFilter.Field.TITLE, filter.field)
			assertEquals("Filter member filterOperand not set to expected value",
				"hello", filter.filterOperand)
			assertEquals("Filter member operator not set to expected value",
				StringFilter.Operator.EQUALS, filter.operator)
		};
		controller.feedParserService = feedParserServiceMock.createMock();

		controller.params.url = "http://localhost:8080/feed.atom";
		controller.params.color = "ff0000";
		controller.params.title = "EQUALS_hello";

		controller.kml();
		httpUtilsMock.verify();
		feedParserServiceMock.verify();
	}
	
	@Test
	void testKml_contentFilter_param() {
		def feedName = "feed";
		def feedDescription = "This Feed is the best!";

		def httpUtilsMock = mockFor(HttpUtils);
		httpUtilsMock.demand.static.getFeed(1..1) {String arg1, String arg2, String arg3 ->
			return [responsecode: 200, content: "feed xml..."]
		};
		httpUtilsMock.createMock();

		def feedParserServiceMock = mockFor(FeedParserService);
		feedParserServiceMock.demand.parseFeed(1..1) {String arg1, List<Filter> arg2 ->
			assertNotNull("Filter list not expected to be null", arg2)
			assertEquals("Unexpected filter list size",
				1, arg2.size())
			StringFilter filter = arg2.get(0)
			assertNotNull("Filter not expected to be null", filter)
			assertEquals("Filter member field not set to expected value",
				StringFilter.Field.CONTENT, filter.field)
			assertEquals("Filter member filterOperand not set to expected value",
				"hello", filter.filterOperand)
			assertEquals("Filter member operator not set to expected value",
				StringFilter.Operator.EQUALS, filter.operator)
		};
		controller.feedParserService = feedParserServiceMock.createMock();

		controller.params.url = "http://localhost:8080/feed.atom";
		controller.params.color = "ff0000";
		controller.params.content = "EQUALS_hello";

		controller.kml();
		httpUtilsMock.verify();
		feedParserServiceMock.verify();
	}
	
	@Test
	void testKml_numericalFilter_param() {
		def feedName = "feed";
		def feedDescription = "This Feed is the best!";

		def httpUtilsMock = mockFor(HttpUtils);
		httpUtilsMock.demand.static.getFeed(1..1) {String arg1, String arg2, String arg3 ->
			return [responsecode: 200, content: "feed xml..."]
		};
		httpUtilsMock.createMock();

		def feedParserServiceMock = mockFor(FeedParserService);
		feedParserServiceMock.demand.parseFeed(1..1) {String arg1, List<Filter> arg2 ->
			assertNotNull("Filter list not expected to be null", arg2)
			assertEquals("Unexpected filter list size",
				1, arg2.size())
		};
		controller.feedParserService = feedParserServiceMock.createMock();

		controller.params.url = "http://localhost:8080/feed.atom";
		controller.params.color = "ff0000";
		controller.params.numerical = "TITLE_M_GREATERTHAN_3.0";

		controller.kml();
		httpUtilsMock.verify();
		feedParserServiceMock.verify();
	}
	
	@Test
	void testKml_boundingBoxFilter_param() {
		def feedName = "feed";
		def feedDescription = "This Feed is the best!";

		def httpUtilsMock = mockFor(HttpUtils);
		httpUtilsMock.demand.static.getFeed(1..1) {String arg1, String arg2, String arg3 ->
			return [responsecode: 200, content: "feed xml..."]
		};
		httpUtilsMock.createMock();

		def feedParserServiceMock = mockFor(FeedParserService);
		feedParserServiceMock.demand.parseFeed(1..1) {String arg1, List<Filter> arg2 ->
			assertNotNull("Filter list not expected to be null", arg2)
			assertEquals("Unexpected filter list size",
				1, arg2.size())
		};
		controller.feedParserService = feedParserServiceMock.createMock();

		controller.params.url = "http://localhost:8080/feed.atom";
		controller.params.color = "ff0000";
		controller.params.bbgeo = "-3.0,-2.0,-5.0,0.0";

		controller.kml();
		httpUtilsMock.verify();
		feedParserServiceMock.verify();
	}
	
	@Test
	void testKml_get_error() {
		def httpUtilsMock = mockFor(HttpUtils);
		httpUtilsMock.demand.static.getFeed(1..1) {String arg1, String arg2, String arg3 -> 
			return [responsecode: 404, responsemessage: "not found"]
		};
		httpUtilsMock.createMock();

		controller.params.url = "http://localhost:8080/notfound";
		controller.params.color = "ff0000";

		controller.kml();

		assertNotNull(controller.flash);
		assertEquals("GET response[404]: not found", controller.flash.message.toString());
		httpUtilsMock.verify();
	}
	
	@Test
	void testConvert_Raw_Atom_To_Kml() {
		def feedParserServiceMock = mockFor(FeedParserService);
		feedParserServiceMock.demand.parseFeed(1..1) {String arg1, List<Filter> filters ->
			Feed feed = new Feed()
			feed.placemarks = ["title":[new Placemark()]]
			return feed;
		}
		controller.feedParserService = feedParserServiceMock.createMock();

		controller.params.content = "I am atom but it doesn't matter since the converter is mocked";
		controller.params.color = "ff0000";

		controller.convert();
		assertNotNull("Model not expected to be null", controller.modelAndView.model);
		
		Feed feed = controller.modelAndView.model.feedInstance;
		assertNotNull("Controller map does not contain expected model object: 'feedInstance'", feed);

		String color = controller.modelAndView.model.feedColor;
		assertNotNull("Controller map does not contain expected model object: 'feedColor'", color);
		assertEquals("ff0000", color);
		feedParserServiceMock.verify();
	}
	
	@Test
	void testConvert_Invalid_Get_Request() {
		// Test that calling controller with GET fails
		controller.request.method = "GET"
		controller.params.color = "ff0000"
		controller.convert()
		assert 405 == controller.response.status
	}
	
	@Test
	void testConvert_POST_Content_To_Kml() {
		// Set up mock for feedParserService
		def feedName = "Example Feed"
		def feedDescription = "This Feed is the best!"
		def color = "ff0000"
		
		// Set up mock for feedParserService
		def feedParserServiceMock = mockFor(FeedParserService)
		Feed feed = new Feed()
		feed.title = feedName
		feed.description = feedDescription
		feed.type = FeedType.ATOM
		Placemark pmark = new Placemark(name: "sample place", description: "sample place description")
		Map<String, List<Placemark>> placemarks = ["mapkey":[pmark]]
		feed.placemarks = placemarks
		
		feedParserServiceMock.demand.parseFeed(1..1) { def arg1, def arg2 ->
			return feed
		}
		controller.feedParserService = feedParserServiceMock.createMock()
		
		controller.params.color = color
		controller.params.content = "<empty/>"
		
		controller.convert()
		assertNotNull("Model not expected to be null", controller.modelAndView.model);

		assertNotNull("Controller map does not contain expected model object: 'feedInstance'", controller.modelAndView.model.feedInstance);
		assertEquals(feed, controller.modelAndView.model.feedInstance);

		assertNotNull("Controller map does not contain expected model object: 'feedColor'", controller.modelAndView.model.feedColor);
		assertEquals(color, controller.modelAndView.model.feedColor);
		feedParserServiceMock.verify();
	}
	
	@Test
	void testConvert_Valid_POST_But_Invalid_GeoRSS_Content() {
		def feedName = "Example Feed"
		def feedDescription = "This Feed is the best!"
		def color = "ff00000"
		
		// Set up mock for feedParserService
		def feedParserServiceMock = mockFor(FeedParserService)
		Feed feed = new Feed()
		feed.title = feedName
		feed.description = feedDescription
		feed.type = FeedType.ATOM
		
		feedParserServiceMock.demand.parseFeed(1..1) { def arg1, def arg2 ->
			return feed
		}
		
		controller.feedParserService = feedParserServiceMock.createMock()
		
		controller.params.color = color
		controller.convert()
		assertEquals(415, controller.response.status)
		feedParserServiceMock.verify();
	}
	
}
