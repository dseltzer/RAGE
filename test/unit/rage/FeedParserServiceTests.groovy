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
import rage.Feed.FeedType
import rage.GeoParserService.GeoType
import rage.filters.Filter
import rage.Placemark

import java.awt.geom.Point2D

@TestFor(FeedParserService)
class FeedParserServiceTests {
	
	public static final boolean FEED_HAS_DATE = true;
	public static final boolean FEED_DOES_NOT_HAVE_DATE = false;
	
	String feedTitle = "feed1"
	String feedDesc = "syndication feed description"
	String item1Title = "1"
	String item1Desc = "item 1 is about x"
	String item1Date = "2004-08-19T11:54:37Z"
	String item2Title = "2"
	String item2Desc = "item 2 is about y"
	String item2Date = "2004-08-19T11:54:37Z"
	
	@Before
	public void setUp() {
		service.geoParserService = new GeoParserService() {
			GeoType getGeoType(String xml) {
				return GeoType.SIMPLE;
			}
			
			List<Geometry> parseGeometry(GeoType type, GPathResult node) {
				List<Geometry> geoList = []
				return geoList;
			}
		};
	}

	@Test
	void testAddPlacemark() {
		Placemark p = new Placemark()
		p.name = "title1"
		
		List<Placemark> placemarkList = [p]
		Map<String, List<Placemark>> placemarkMap = ["title1":placemarkList]
		
		service.addPlacemark(p, placemarkMap, null)
		
		assertEquals("Unexpected map size", 1, placemarkMap.size())
		assertTrue("Map does not contain expected key", placemarkMap.containsKey("title1"))
		List<Placemark> list = placemarkMap.get("title1")
		assertNotNull("map value not expected to be null", list)
		assertEquals("Unexpected list size", 2, list.size())
	}
	
	@Test
	void testAddPlacemark_emptyList() {
		Map<String, List<Placemark>> placemarkMap = [:]
		Placemark p = new Placemark()
		p.name = "title1"
		
		service.addPlacemark(p, placemarkMap, null)
		
		assertEquals("Unexpected map size", 1, placemarkMap.size())
		assertTrue("Map does not contain expected key", placemarkMap.containsKey("title1"))
		List<Placemark> list = placemarkMap.get("title1")
		assertNotNull("map value not expected to be null", list)
		assertEquals("Unexpected list size", 1, list.size())
	}
	
	@Test
	void testAddPlacemarkWithFilters_reject() {
		Map<String, List<Placemark>> placemarkMap = [:]
		Placemark p = new Placemark()
		p.name = "title1"
		
		Filter filter = new Filter() {
			boolean accept(Placemark placemark) throws Exception {
				return false
			}
			
			public String serializeToUrlParam() {
				return null;
			}
		}

		service.addPlacemark(p, placemarkMap, [filter])
		
		assertEquals("Unexpected map size", 0, placemarkMap.size())
		assertFalse("Map should not contain key", placemarkMap.containsKey("title1"))
	}
	
	@Test
	void testAddPlacemarkWithFilters_accept() {
		Map<String, List<Placemark>> placemarkMap = [:]
		Placemark p = new Placemark()
		p.name = "title1"
		
		Filter filter = new Filter() {
			boolean accept(Placemark placemark) throws Exception {
				return true
			}
			
			public String serializeToUrlParam() {
				return null;
			}
		}

		service.addPlacemark(p, placemarkMap, [filter])
		
		assertEquals("Unexpected map size", 1, placemarkMap.size())
		assertTrue("Map does not contain expected key", placemarkMap.containsKey("title1"))
		List<Placemark> list = placemarkMap.get("title1")
		assertNotNull("map value not expected to be null", list)
		assertEquals("Unexpected list size", 1, list.size())
	}
	
	@Test
	void testParseFeed_html() {
		String html = "<html><head></head><body>This is not a syndication feed!</body></html>";
		Feed feed = service.parseFeed(html, null);
		
		assertNotNull("Feed cannot be null", feed);
		assertEquals("Feed field 'title' does not contain expected value",
			FeedParserService.PARSE_ERROR_TITLE, feed.title)
		assertEquals("Feed field 'description' does not contain expected value",
			FeedParserService.PARSE_ERROR_NO_TYPE_MSG, feed.description)
	}

	@Test
	void testParseFeed_malformed_xml() {
		String badXml = "<entry1><entry2></entry1></entry2>";
		Feed feed = service.parseFeed(badXml, null);
		
		assertNotNull("Feed cannot be null", feed);
		assertEquals("Feed field 'title' does not contain expected value",
			FeedParserService.PARSE_ERROR_TITLE, feed.title)
		assertEquals("Feed field 'description' does not contain expected value",
			FeedParserService.PARSE_ERROR_MALFORMED_XML_MSG, feed.description)
	}

	@Test
	void testParseFeed_Atom_with_noEntries() {
		String atom = "<feed xmlns='http://www.w3.org/2005/Atom'>" +
				"<title>" + feedTitle + "</title><subtitle>" + feedDesc + "</subtitle>" +
				"</feed>"

		Feed feed = service.parseFeed(atom, null)

		assertNotNull("Feed cannot be null", feed)
		assertEquals("Feed field 'title' does not contain expected value",
			feedTitle, feed.title)
		assertEquals("Feed field 'description' does not contain expected value",
			feedDesc, feed.description)
		assertEquals("Feed field 'type' does not contain expected value",
			FeedType.ATOM, feed.type)
	}

	@Test
	void testParseFeed_Atom_with_updated_field() {
		String updated = "2005-04-06T20:25:05"
		String atom = "<feed xmlns='http://www.w3.org/2005/Atom'>" +
				"<title>" + feedTitle + "</title><subtitle>" + feedDesc + "</subtitle>" +
				"<entry><title>" + item1Title + "</title><content type='text'>" + item1Desc + "</content><updated>" + item1Date + "</updated></entry>" +
				"<entry><title>" + item2Title + "</title><content type='text'>" + item2Desc + "</content><updated>" + item2Date + "</updated></entry>" +
				"</feed>"

		Feed feed = service.parseFeed(atom, null)
		assertFeed(feed, FeedType.ATOM, FEED_HAS_DATE, null)
	}

	@Test
	void testParseFeed_Atom_with_published_field_and_kml_enclosures() {
		String updated = "2005-04-06T20:25:05"
		String kmlEnclosureUrl = "http://localhost/enclosure.kml"
		String kmlEnclosure  = "<link rel='enclosure' type='application/vnd.google-earth.kml+xml' title='myKml' href='" + kmlEnclosureUrl + "' />"
		String otherEnclosure  = "<link rel='enclosure' type='text/html' href='http:whatever' />"

		String atom = "<feed xmlns='http://www.w3.org/2005/Atom'>" +
				"<title>" + feedTitle + "</title><subtitle>" + feedDesc + "</subtitle>" +
				"<entry><title>" + item1Title + "</title><content type='text'>" + item1Desc + "</content><updated>" + updated + "</updated><published>" + item1Date + "</published>" + kmlEnclosure + otherEnclosure + "</entry>" +
				"<entry><title>" + item2Title + "</title><content type='text'>" + item2Desc + "</content><updated>" + updated + "</updated><published>" + item2Date + "</published>" + kmlEnclosure + otherEnclosure + "</entry>" +
				"</feed>"

		Feed feed = service.parseFeed(atom, null)
		Enclosure kml = new Enclosure(title: "myKml", url: kmlEnclosureUrl, type: "application/vnd.google-earth.kml+xml")
		Enclosure other = new Enclosure(title: "http:whatever", url: "http:whatever", type: "text/html")
		assertFeed(feed, FeedType.ATOM, FEED_HAS_DATE, [kml, other])
	}

	@Test
	void testParseFeed_Atom_with_content_field() {
		String atom = "<feed xmlns='http://www.w3.org/2005/Atom' xmlns:georss='http://www.georss.org/georss'>" +
				"<title>" + feedTitle + "</title>" + "<subtitle>" + feedDesc + "</subtitle>" +
				"<entry><title>" + item1Title + "</title><content type='text'>" + item1Desc + "</content><georss:point>45.256 -71.92</georss:point></entry>" +
				"<entry><title>" + item2Title + "</title><content type='text'>" + item2Desc + "</content><georss:point>45.256 -71.92</georss:point></entry>" + 
				"</feed>"

		Feed feed = service.parseFeed(atom, null)
		assertFeed(feed, FeedType.ATOM, FEED_DOES_NOT_HAVE_DATE, null)
	}
	
	@Test
	void testParseFeed_Atom_with_xml_content_field() {
		String atom = "<feed xmlns='http://www.w3.org/2005/Atom' xmlns:georss='http://www.georss.org/georss'>" +
				"<title>" + feedTitle + "</title>" + "<subtitle>" + feedDesc + "</subtitle>" +
				"<entry><title>" + item1Title + "</title><content type='application/xml'><entity><field>value</field></entity></content><georss:point>45.256 -71.92</georss:point></entry>" +
				"</feed>"

		Feed feed = service.parseFeed(atom, null)
		Map<String, List<Placemark>> placemarks = feed.placemarks
		assertNotNull("Feed field 'placemarks' should not be null", placemarks)
		assertEquals("Feed field 'placemarks' contains unexpected value",
			1, placemarks.size())
		Placemark placemark = placemarks.get(item1Title).get(0)
		assertEquals("Placemark field 'description' does not contain expected value",
			"content: <br />&nbsp;&nbsp;&nbsp;entity: <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;field: value", placemark.description)
	}
	
	@Test
	void testParseFeed_Atom_with_summary_field() {
		String atom = "<feed xmlns='http://www.w3.org/2005/Atom'>" +
				"<title>" + feedTitle + "</title>" + "<subtitle>" + feedDesc + "</subtitle>" +
				"<entry><title>" + item1Title + "</title><summary>" + item1Desc + "</summary></entry>" +
				"<entry><title>" + item2Title + "</title><summary>" + item2Desc + "</summary></entry>" + 
				"</feed>"

		Feed feed = service.parseFeed(atom, null)
		assertFeed(feed, FeedType.ATOM, FEED_DOES_NOT_HAVE_DATE, null)
	}

	@Test
	void testParseFeed_RSS_1_0_with_noEntries() {
		String RSS_1_0 = "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' " +
						"xmlns:dc='http://purl.org/dc/elements/1.1/' " +
						"xmlns='http://purl.org/rss/1.0'>" +
					"<channel rdf:about='http://xml.com/xml/news.rss'>" +
					"<title>" + feedTitle + "</title><description>" + feedDesc + "</description>" +
					"</channel>" +
					"</rdf:RDF>"

		Feed feed = service.parseFeed(RSS_1_0, null)

		assertNotNull("Feed cannot be null", feed)
		assertEquals("Feed field 'title' does not contain expected value",
			feedTitle, feed.title)
		assertEquals("Feed field 'description' does not contain expected value",
			feedDesc, feed.description)
		assertEquals("Feed field 'type' does not contain expected value",
			FeedType.RSS_1_0, feed.type)
	}

	@Test
	void testParseFeed_RSS_1_0_with_dc_date() {
		String RSS_1_0 = "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' " +
						"xmlns:dc='http://purl.org/dc/elements/1.1/' " +
						"xmlns='http://purl.org/rss/1.0'>" +
					"<channel rdf:about='http://xml.com/xml/news.rss'>" +
					"<title>" + feedTitle + "</title><description>" + feedDesc + "</description>" +
					"</channel>" +
					"<item><title>" + item1Title + "</title><description>" + item1Desc + "</description><dc:date>" + item1Date + "</dc:date></item>" +
					"<item><title>" + item2Title + "</title><description>" + item2Desc + "</description><dc:date>" + item2Date + "</dc:date></item>" +
					"</rdf:RDF>"

		Feed feed = service.parseFeed(RSS_1_0, null)
		assertFeed(feed, FeedType.RSS_1_0, FEED_HAS_DATE, null)
	}

	@Test
	void testParseFeed_RSS_1_0_with_modules_content() {
		String RSS_1_0 = "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' " +
						"xmlns:content='http://purl.org/rss/1.0/modules/content/' " +
						"xmlns='http://purl.org/rss/1.0'>" +
					"<channel rdf:about='http://xml.com/xml/news.rss'>" +
					"<title>" + feedTitle + "</title><description>" + feedDesc + "</description>" +
					"</channel>" +
					"<item><title>" + item1Title + "</title><content:encoded>" + item1Desc + "</content:encoded></item>" +
					"<item><title>" + item2Title + "</title><content:encoded>" + item2Desc + "</content:encoded></item>" +
					"</rdf:RDF>"

		Feed feed = service.parseFeed(RSS_1_0, null)
		assertFeed(feed, FeedType.RSS_1_0, FEED_DOES_NOT_HAVE_DATE, null)
	}

	@Test
	void testParseFeed_RSS_2_0_with_noEntries() {
		String kmlEnclosureUrl = "http://localhost/enclosure.kml"
		String kmlEnclosure  = "<enclosure url='" + kmlEnclosureUrl + "' type='application/vnd.google-earth.kml+xml' />"
		
		String RSS_2_0 = "<rss version='2.0' xmlns:content='http://purl.org/rss/1.0/modules/content/' " +
					"xmlns:georss='http://www.georss.org/georss'><channel>" +
					"<title>" + feedTitle + "</title>" + "<description>" + feedDesc + "</description>" +
					"</channel></rss>"

		Feed feed = service.parseFeed(RSS_2_0, null)

		assertNotNull("Feed cannot be null", feed)
		assertEquals("Feed field 'title' does not contain expected value",
			feedTitle, feed.title)
		assertEquals("Feed field 'description' does not contain expected value",
			feedDesc, feed.description)
		assertEquals("Feed field 'type' does not contain expected value",
			FeedType.RSS_2_0, feed.type)
	}

	@Test
	void testParseFeed_RSS_2_0_with_modules_content_and_kml_enclosures() {
		String kmlEnclosureUrl = "http://localhost/enclosure.kml"
		String kmlEnclosure  = "<enclosure url='" + kmlEnclosureUrl + "' type='application/vnd.google-earth.kml+xml' />"
		
		String RSS_2_0 = "<rss version='2.0' xmlns:content='http://purl.org/rss/1.0/modules/content/' " +
					"xmlns:georss='http://www.georss.org/georss'><channel>" +
					"<title>" + feedTitle + "</title>" + "<description>" + feedDesc + "</description>" +
					"<item><title>" + item1Title + "</title><content:encoded>" + item1Desc + "</content:encoded>" + kmlEnclosure + "</item>" +
					"<item><title>" + item2Title + "</title><content:encoded>" + item2Desc + "</content:encoded>" + kmlEnclosure + "</item>" +
					"</channel></rss>"

		Feed feed = service.parseFeed(RSS_2_0, null)
		Enclosure kml = new Enclosure(title: kmlEnclosureUrl, url: kmlEnclosureUrl, type: "application/vnd.google-earth.kml+xml")
		assertFeed(feed, FeedType.RSS_2_0, FEED_DOES_NOT_HAVE_DATE, [kml])
	}

	@Test
	void testParseFeed_RSS_2_0_with_link() {
		String linkUrl = "http://localhost/link";
		String RSS_2_0 = "<rss version='2.0' xmlns:georss='http://www.georss.org/georss' xmlns:dc='http://purl.org/dc/elements/1.1/'><channel>" +
					"<title>" + feedTitle + "</title>" + "<description>" + feedDesc + "</description>" +
					"<item><title>" + item1Title + "</title><description>" + item1Desc + "</description><link>" + linkUrl + "</link></item>" +
					"<item><title>" + item2Title + "</title><description>" + item2Desc + "</description><link>" + linkUrl + "</link></item>" +
					"</channel></rss>"

		Feed feed = service.parseFeed(RSS_2_0, null)
		Enclosure link = new Enclosure(title: "link", url: linkUrl, type: "text/html")
		assertFeed(feed, FeedType.RSS_2_0, FEED_DOES_NOT_HAVE_DATE, [link])
	}
	
	@Test
	void testParseFeed_RSS_2_0_with_dc_date() {
		String RSS_2_0 = "<rss version='2.0' xmlns:georss='http://www.georss.org/georss' xmlns:dc='http://purl.org/dc/elements/1.1/'><channel>" +
					"<title>" + feedTitle + "</title>" + "<description>" + feedDesc + "</description>" +
					"<item><title>" + item1Title + "</title><description>" + item1Desc + "</description><dc:date>" + item1Date + "</dc:date></item>" +
					"<item><title>" + item2Title + "</title><description>" + item2Desc + "</description><dc:date>" + item2Date + "</dc:date></item>" +
					"</channel></rss>"

		Feed feed = service.parseFeed(RSS_2_0, null)
		assertFeed(feed, FeedType.RSS_2_0, FEED_HAS_DATE, null)
	}

	@Test
	void testParseFeed_RSS_2_0_with_pubdate() {
		String RSS_2_0 = "<rss version='2.0' xmlns:georss='http://www.georss.org/georss'><channel>" +
					"<title>" + feedTitle + "</title>" + "<description>" + feedDesc + "</description>" +
					"<item><title>" + item1Title + "</title><description>" + item1Desc + "</description><pubDate>Thu, 19 Aug 2004 11:54:37 UTC</pubDate></item>" +
					"<item><title>" + item2Title + "</title><description>" + item2Desc + "</description><pubDate>Thu, 19 Aug 2004 11:54:37 UTC</pubDate></item>" +
					"</channel></rss>"

		Feed feed = service.parseFeed(RSS_2_0, null)
		assertFeed(feed, FeedType.RSS_2_0, FEED_HAS_DATE, null)
	}

	@Test
	void testRemoveCDATAWrapper() {
		String cleanValue = service.removeCDATAWrapper("<![CDATA[Hello World]]>");
		assertEquals("Hello World", cleanValue)
	}

	@Test
	void testConvertXmlToHtml() {
		def xml = new XmlSlurper().parseText("<entity><field>value</field></entity>");
		//def xml = new XmlSlurper().parseText("<address><street><number>123</number><name>Market</name></street><zip>80015</zip><state>co</state></address>");
		String html = service.convertXmlToHtml("", xml)
		
		assertNotNull("Html generated from XML is not expected to be null", html)
		assertEquals("entity: <br />&nbsp;&nbsp;&nbsp;field: value", html)
	}
	
	private assertFeed(Feed feed, FeedType type, boolean hasDate, List<Enclosure> enclosures) {
		
		String expectedItem1Timestamp = null
		String expectedItem2Timestamp = null
		if (hasDate) {
			expectedItem1Timestamp = item1Date
			expectedItem2Timestamp = item2Date
		}

		assertNotNull("Feed cannot be null", feed)
		assertEquals("Feed field 'title' does not contain expected value",
			feedTitle, feed.title)
		assertEquals("Feed field 'description' does not contain expected value",
			feedDesc, feed.description)
		assertEquals("Feed field 'type' does not contain expected value",
			type, feed.type)

		Map<String, List<Placemark>> placemarks = feed.placemarks
		assertNotNull("Feed field 'placemarks' should not be null", placemarks)
		assertEquals("Feed field 'placemarks' contains unexpected value",
			2, placemarks.size())
		
		assertPlacemark(placemarks.get(item1Title).get(0), item1Title, item1Desc, expectedItem1Timestamp, enclosures);
		assertPlacemark(placemarks.get(item2Title).get(0), item2Title, item2Desc, expectedItem2Timestamp, enclosures);		
	}

	private assertPlacemark(Placemark placemark, String name, String desc, String timestamp, List<Enclosure> enclosures) {
		assertNotNull(placemark);
		assertEquals("Placemark field 'name' does not contain expected value",
			name, placemark.name)
		assertEquals("Placemark field 'description' does not contain expected value",
			desc, placemark.description)
		assertEquals("Placemark field 'timestamp' does not contain expected value",
			timestamp, placemark.timestamp)

		List<Geometry> geometryList = placemark.geometryList
		assertNotNull("placemark.geometryList is not expected to be null", geometryList)
		
		if (null != enclosures) {
			assertNotNull("Placemark enclosures not expected to be null", placemark.enclosures)
			assertEquals("Unexpected number of enclosures",
				enclosures.size(), placemark.enclosures.size())
			
			for (int i=0; i<enclosures.size(); i++) {
				assertEquals("Unexpected enclosure title",
					enclosures.get(i).getTitle(), placemark.enclosures.get(i).getTitle())
				assertEquals("Unexpected enclosure url",
					enclosures.get(i).getUrl(), placemark.enclosures.get(i).getUrl())
				assertEquals("Unexpected enclosure title",
					enclosures.get(i).getType(), placemark.enclosures.get(i).getType())
			}
		}

	}
}
