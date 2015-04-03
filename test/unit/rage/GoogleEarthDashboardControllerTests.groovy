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

import rage.filters.Filter
import rage.filters.StringFilter
import rage.utils.HttpUtils

@TestFor(GoogleEarthDashboardController)
class GoogleEarthDashboardControllerTests {
	
	String rageServerURL = "localhost:8080/RAGE";

	@Test
	void testTimemap_noFeedUrlParameters() {
		controller.timemap();
		assertEquals("Unexpected view when not feeds provided", 
			"/googleEarthDashboard/create", view)
	}
	
	@Test
	void testTimemapnoFeedColorParameter() {
		controller.params.feedUrl1 = "http://localhost:8080/feed1"
		controller.timemap()
		assertEquals("Unexpected view when not feeds provided",
			"/googleEarthDashboard/timemap", view)
		assertEquals("Unexpected model.feedUrls",
			"[http://localhost:8080/feed1]", model.feedUrls.toString())
		assertEquals("Unexpected model.feedColors",
			"[ff0000]", model.feedColors.toString())
	}
	
	@Test
	void testTimemap() {
		controller.params.feedUrl1 = "http://localhost:8080/feed1"
		controller.params.feedColor1 = "#0000ff"
		controller.params.feedUrl2 = "http://localhost:8080/feed2"
		controller.params.feedColor2 = "00ff00";
		controller.timemap()
		assertEquals("Unexpected view when not feeds provided",
			"/googleEarthDashboard/timemap", view)
		assertEquals("Unexpected model.feedUrls",
			"[http://localhost:8080/feed1, http://localhost:8080/feed2]", model.feedUrls.toString())
		assertEquals("Unexpected model.feedColors",
			"[0000ff, 00ff00]", model.feedColors.toString())
	}
	
	@Test
	void testKml() {
		def feedName = "feed";
		def feedDescription = "This Feed is the best!";
		def color1 = "ff0000";
		def color2 = "ffff00";

		def httpUtilsMock = mockFor(HttpUtils);
		httpUtilsMock.demand.static.getFeed(2..2) {String arg1, String arg2, String arg3 -> 
			return [responsecode: 200, content: "feed xml"]
		};
		httpUtilsMock.createMock();

		def feedParserServiceMock = mockFor(FeedParserService);
		feedParserServiceMock.demand.parseFeed(2..2) {String arg1, List<Filter> arg2 -> 
			Feed feed = new Feed();
			feed.title = feedName;
			feed.description = feedDescription;
			return feed
		};
		controller.feedParserService = feedParserServiceMock.createMock();

		controller.params.feedUrl1 = "http://localhost:8080/feed1";
		controller.params.feedColor1 = "#" + color1;
		controller.params.feedUrl2 = "http://localhost:8080/feed2";
		controller.params.feedColor2 = "#" + color2;
		controller.params.titleOperator1 = "EQUALS";
		controller.params.titleFilter1 = "hello";

		controller.kml();

		assertNotNull("Model not expected to be null", controller.modelAndView.model);
		assertNotNull("Controller map does not contain expected model object: 'feeds'", controller.modelAndView.model.feeds);
		assertEquals(2, controller.modelAndView.model.feeds.size());
		assertFeed(controller.modelAndView.model.feeds.get(0), controller.params.feedUrl1, feedName, feedDescription);
		assertFeed(controller.modelAndView.model.feeds.get(1), controller.params.feedUrl2, feedName, feedDescription);

		assertNotNull("Controller map does not contain expected model object: 'feedColors'", controller.modelAndView.model.feedColors);
		assertEquals(2, controller.modelAndView.model.feedColors.size());
		assertEquals(color1, controller.modelAndView.model.feedColors.get(0));
		assertEquals(color2, controller.modelAndView.model.feedColors.get(1));
		
		assertNotNull("Controller map does not contain expected model object: 'feedFilters'", controller.modelAndView.model.feedFilters);
		assertEquals("feedFilters model object does not contain expected size", 2, controller.modelAndView.model.feedFilters.size());
		assertEquals("filters list(0) contains unexpected size", 0, controller.modelAndView.model.feedFilters.get(0).size());
		assertEquals("filters list(1) contains unexpected size", 0, controller.modelAndView.model.feedFilters.get(1).size());
		httpUtilsMock.verify();
		feedParserServiceMock.verify();
	}

	@Test
	void testKml_withTitleFilter() {
		def feedName = "feed";
		def feedDescription = "This Feed is the best!";
		def color1 = "ff0000";

		def httpUtilsMock = mockFor(HttpUtils);
		httpUtilsMock.demand.static.getFeed(1..1) {String arg1, String arg2, String arg3 ->
			return [responsecode: 200, content: "feed xml"]
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

		controller.params.feedUrl1 = "http://localhost:8080/feed1";
		controller.params.feedColor1 = "#" + color1;
		controller.params.titleFilterCheckbox1 = "on";
		controller.params.titleOperator1 = "EQUALS";
		controller.params.titleFilter1 = "hello";

		controller.kml();

		assertNotNull("Model not expected to be null", controller.modelAndView.model);
		assertNotNull("Controller map does not contain expected model object: 'feedFilters'", controller.modelAndView.model.feedFilters);
		assertEquals("feedFilters model object does not contain expected size", 1, controller.modelAndView.model.feedFilters.size());
		
		List<String> filters = controller.modelAndView.model.feedFilters.get(0);
		assertEquals(1, filters.size());
		assertEquals("Unexpeced filter value",
			"title=EQUALS_hello", filters.get(0))
		httpUtilsMock.verify();
		feedParserServiceMock.verify();
	}
	
	@Test
	void testKml_withContentFilter() {
		def feedName = "feed";
		def feedDescription = "This Feed is the best!";
		def color1 = "ff0000";

		def httpUtilsMock = mockFor(HttpUtils);
		httpUtilsMock.demand.static.getFeed(1..1) {String arg1, String arg2, String arg3 ->
			return [responsecode: 200, content: "feed xml"]
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

		controller.params.feedUrl1 = "http://localhost:8080/feed1";
		controller.params.feedColor1 = "#" + color1;
		controller.params.contentFilterCheckbox1 = "on";
		controller.params.contentOperator1 = "EQUALS";
		controller.params.contentFilter1 = "hello";

		controller.kml();

		assertNotNull("Model not expected to be null", controller.modelAndView.model);
		assertNotNull("Controller map does not contain expected model object: 'feedFilters'", controller.modelAndView.model.feedFilters);
		assertEquals("feedFilters model object does not contain expected size", 1, controller.modelAndView.model.feedFilters.size());
		
		List<Filter> filters = controller.modelAndView.model.feedFilters.get(0);
		assertEquals(1, filters.size());
		assertEquals("Unexpeced filter value",
			"content=EQUALS_hello", filters.get(0))
		httpUtilsMock.verify();
		feedParserServiceMock.verify();
	}
	
	@Test
	void testKml_withNumericalFilter() {
		def feedName = "feed";
		def feedDescription = "This Feed is the best!";
		def color1 = "ff0000";

		def httpUtilsMock = mockFor(HttpUtils);
		httpUtilsMock.demand.static.getFeed(1..1) {String arg1, String arg2, String arg3 ->
			return [responsecode: 200, content: "feed xml"]
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

		controller.params.feedUrl1 = "http://localhost:8080/feed1";
		controller.params.feedColor1 = "#" + color1;
		controller.params.numericalFilterCheckbox1 = "on";
		controller.params.numericalFilterVariable1 = "M";
		controller.params.numericalOperator1 = "GREATERTHAN";
		controller.params.numericalFilterOperand1 = "3.0";
		controller.params.numericalField1 = "TITLE";
		
		controller.kml();

		assertNotNull("Model not expected to be null", controller.modelAndView.model);
		assertNotNull("Controller map does not contain expected model object: 'feedFilters'", controller.modelAndView.model.feedFilters);
		assertEquals("feedFilters model object does not contain expected size", 1, controller.modelAndView.model.feedFilters.size());
		
		List<Filter> filters = controller.modelAndView.model.feedFilters.get(0);
		assertEquals(1, filters.size());
		assertEquals("Unexpeced filter value",
			"numerical=TITLE_M_GREATERTHAN_3.0", filters.get(0))
		httpUtilsMock.verify();
		feedParserServiceMock.verify();
	}
	
	@Test
	void testKml_withBoundingBoxFilter() {
		def feedName = "feed";
		def feedDescription = "This Feed is the best!";
		def color1 = "ff0000";

		def httpUtilsMock = mockFor(HttpUtils);
		httpUtilsMock.demand.static.getFeed(1..1) {String arg1, String arg2, String arg3 ->
			return [responsecode: 200, content: "feed xml"]
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

		controller.params.feedUrl1 = "http://localhost:8080/feed1";
		controller.params.feedColor1 = "#" + color1;
		controller.params.boundingBoxFilterCheckbox1 = "checked";
		controller.params.upperLeftLat1 = "-3.0";
		controller.params.upperLeftLon1 = "-2.0";
		controller.params.lowerRightLat1 = "-5.0";
		controller.params.lowerRightLon1 = "0.0";

		controller.kml();

		assertNotNull("Model not expected to be null", controller.modelAndView.model);
		assertNotNull("Controller map does not contain expected model object: 'feedFilters'", controller.modelAndView.model.feedFilters);
		assertEquals("feedFilters model object does not contain expected size", 1, controller.modelAndView.model.feedFilters.size());
		
		List<Filter> filters = controller.modelAndView.model.feedFilters.get(0);
		assertEquals(1, filters.size());
		assertEquals("Unexpeced filter value",
			"bbgeo=-3.0,-2.0,-5.0,0.0", filters.get(0))
		httpUtilsMock.verify();
		feedParserServiceMock.verify();
	}
	
	@Test
	void testKml_get_404() {

		def feedUrl = "http://localhost:8080/feed1";

		def httpUtilsMock = mockFor(HttpUtils);
		httpUtilsMock.demand.static.getFeed(1..1) {String arg1, String arg2, String arg3 -> 
			return [responsecode: 404, responsemessage: "Page not found"]
		};
		httpUtilsMock.createMock();

		controller.params.feedUrl1 = feedUrl;
		controller.params.feedColor1 = "ff0000";

		controller.kml();

		assertNotNull("Model not expected to be null", controller.modelAndView.model);
		assertNotNull("Controller map does not contain expected model object: 'feeds'", controller.modelAndView.model.feeds);
		assertEquals(1, controller.modelAndView.model.feeds.size());
		assertFeed(controller.modelAndView.model.feeds.get(0), feedUrl, feedUrl, "Unable to GET feed[404]: Page not found");
		httpUtilsMock.verify();
	}

	@Test
	void testKml_feed_parse_exception() {

		def feedUrl = "http://localhost:8080/feed1";
		def feedParseException = "Unable to parse feed because you suck";

		def httpUtilsMock = mockFor(HttpUtils);
		httpUtilsMock.demand.static.getFeed(1..1) {String arg1, String arg2, String arg3 -> 
			return [responsecode: 200, content: "feed xml"]
		};
		httpUtilsMock.createMock();

		def feedParserServiceMock = mockFor(FeedParserService);
		feedParserServiceMock.demand.parseFeed(1..1) {String arg1, List<Filter> arg2 -> 
			throw new Exception(feedParseException);
		};
		controller.feedParserService = feedParserServiceMock.createMock();

		controller.params.feedUrl1 = feedUrl;
		controller.params.feedColor1 = "ff0000";

		controller.kml();

		assertNotNull("Model not expected to be null", controller.modelAndView.model);
		assertNotNull("Controller map does not contain expected model object: 'feeds'", controller.modelAndView.model.feeds);
		assertEquals(1, controller.modelAndView.model.feeds.size());
		assertFeed(controller.modelAndView.model.feeds.get(0), feedUrl, feedUrl, "Parse Exception: " + feedParseException);
		httpUtilsMock.verify();
		feedParserServiceMock.verify();
	}

	private void assertFeed(Feed feed, String expectedUrl, String expectedTitle, String expectedDescription) {
		assertEquals("feed.url does not contain expected value",
			expectedUrl, feed.url);
		assertEquals("feed.title does not contain expected value",
			expectedTitle, feed.title);
		assertEquals("feed.description does not contain expected value",
			expectedDescription, feed.description);
	}
}
