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

import static org.junit.Assert.*;
import org.junit.*;
import grails.test.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder;
import groovy.util.slurpersupport.GPathResult
import java.awt.geom.Point2D
import rage.filters.Filter
import rage.filters.StringFilter

class GoogleEarthDashboardViewKmlTests extends GroovyPagesTestCase {

	def gspTemplate = null;
	String rageServerURL = "0.0.0.0:8080/RAGE";

	@Before
	public void setUp() {
		def targetGspFile = "grails-app/views/googleEarthDashboard/kml.gsp";
		def kmlGsp = new File(targetGspFile);
		assertNotNull('Unable to load target gsp file: ${targetGspFile}', kmlGsp);
		def fileSize = kmlGsp.text.size();
		assertTrue(fileSize > 0);
		gspTemplate = kmlGsp.text;

		
		def mockedConfig = new ConfigObject();
		mockedConfig.rage.server.url = rageServerURL;
		ConfigurationHolder.config = mockedConfig;

		assertNotNull("Unable to test GSP, template is null!", gspTemplate);
	}

	@Test
	void testKml() {
		String feedUrl  = "http://localhost:8080/feed";
		String feedName = "feed";
		String feedDescription  = "This feed rocks (especially in RAGE)";
		def testFeeds = [createFeed(feedUrl + "?this=a&that=b", feedName, feedDescription), createFeed(feedUrl, feedName, feedDescription)];

		def color1 = "ff8800";
		def color2 = "ff0000";
		def testColors = [color1, color2];
		
		def testFilters = [[], []];

		def result = applyTemplate(gspTemplate, [feeds:testFeeds, feedColors:testColors, feedFilters:testFilters]);
		assertNotNull("GSP provided no output", result);
		//println("${result}")

		def kml = new XmlSlurper().parseText(result);

		assertFolder(kml, null, null);
		assertNetworkLinks(kml.Folder, 
				2, 
				[feedName, feedName], 
				[feedDescription, feedDescription], 
				[feedUrl, feedUrl], 
				[color1, color2]);
	}
	
	@Test
	void testKml_withFilters() {
		String feedUrl  = "http://localhost:8080/feed";
		String feedName = "feed";
		String feedDescription  = "This feed rocks (especially in RAGE)";
		def testFeeds = [createFeed(feedUrl + "?this=a&that=b", feedName, feedDescription), createFeed(feedUrl, feedName, feedDescription)];

		def color1 = "ff8800";
		def color2 = "ff0000";
		def testColors = [color1, color2];
		
		def testFilters = [["title=EQUALS_hello"], ["title=EQUALS_hello"]];

		def result = applyTemplate(gspTemplate, [feeds:testFeeds, feedColors:testColors, feedFilters:testFilters]);
		assertNotNull("GSP provided no output", result);

		assertFalse("Generated kml should not contain feed description when filtered",
			result.contains(feedDescription));
		assertTrue("Generated kml should contain filter description when filtered",
			result.contains("This feed is being filtered by: "));
		assertTrue("Generated kml does not contain expected href with title parameter",
			result.contains("http://0.0.0.0:8080/RAGE/feed/kml?title=EQUALS_hello&color=ff0000&url=http://localhost:8080/feed"));
	}

	@Test
	void testKml_custom_folder_name_description() {
		String folderName = "Custom RAGE folder";
		String folderDescription = "This is a custom RAGE folder, with specified name and description";

		def result = applyTemplate(gspTemplate, [folderName:folderName, folderDescription:folderDescription]);
		assertNotNull("GSP provided no output", result);
		//println("${result}")

		def kml = new XmlSlurper().parseText(result);

		assertFolder(kml, folderName, folderDescription);
	}

	private createFeed(String url, String title, String description) {
		Feed feed = new Feed();
		feed.url = url;
		feed.title = title;
		feed.description = description;
		return feed;
	}

	private assertFolder(GPathResult node, String expectedName, String expectedDescription) {
		assertEquals("Node, ${node.name()}, does not contain expected element 'Folder'",
			1, node.Folder.size());


		assertEquals("Folder does not contain expected element 'name'",
			1, node.Folder.name.size());
		if (expectedName) {
			assertEquals("RAGE folder name does not contain expected value",
				expectedName, node.Folder.name.text().trim());
		}

		assertEquals("Folder does not contain expected element 'description'",
			1, node.Folder.description.size());
		if (expectedDescription) {
			assertEquals("RAGE folder description does not contain expected value",
				expectedDescription, node.Folder.description.text().trim());
		}
	}

	private assertNetworkLinks(GPathResult node, expectedNumNetworkLinks, List expectedNames, List expectedDescriptions, List expectedUrls, List expectedColors) {
		assertEquals("Node, ${node.name()}, does not contain ${expectedNumNetworkLinks} element(s) 'NetworkLink'",
			expectedNumNetworkLinks, node.NetworkLink.size());

		node.NetworkLink.eachWithIndex {networkLinkNode, i ->
			assertEquals("Node, ${networkLinkNode.name()}, does not contain expected element 'Name'",
				1, networkLinkNode.name.size());
			assertEquals("Node ${i} 'name' element does not contain expected value", 
				expectedNames.get(i), networkLinkNode.name.text());

			assertEquals("Node, ${networkLinkNode.name()}, does not contain expected element 'description'",
				1, networkLinkNode.description.size());
			assertTrue("Node ${i} 'description' element does not contain feed description: ${expectedDescriptions.get(i)}", 
				networkLinkNode.description.text().contains(expectedDescriptions.get(i)));

			assertEquals("Node, ${networkLinkNode.name()}, does not contain expected element 'Link'",
				1, networkLinkNode.Link.size());
			assertLink(networkLinkNode.Link, expectedUrls.get(i), expectedColors.get(i));
		}
	}

	private assertLink(GPathResult node, String expectedUrl, String expectedColor) {
		assertEquals("Node, ${node.name()}, does not contain expected element 'href'",
			1, node.href.size());
		assertTrue("Link does not contain RAGE server URL property",
			node.href.text().contains(rageServerURL));
		assertTrue("Link[${node.href.text().trim()}] does not contain expected RAGE parameter url=${expectedUrl}", 
			node.href.text().contains("url=" + expectedUrl));
		assertTrue("Link does not contain expected RAGE parameter color=${expectedColor}",
			node.href.text().contains("color=" + expectedColor));
		assertEquals("Node, ${node.name()}, does not contain expected element 'refreshInterval'",
			1, node.refreshInterval.size());
		assertEquals("Node, ${node.name()}, does not contain expected element 'refreshMode'",
			1, node.refreshMode.size());
		assertEquals("Unexpected value for element 'refreshMode'",
			"onInterval", node.refreshMode.text().trim());
		assertEquals("Unexpected value for element 'refreshInterval'",
			"300", node.refreshInterval.text().trim());
	}
}