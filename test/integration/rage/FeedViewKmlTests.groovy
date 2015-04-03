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

import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import java.awt.geom.Point2D

class FeedViewKmlTests extends GroovyPagesTestCase {

	def gspTemplate = null;
	String rgbColor = "ffff00";
	String bgrColor = "00ffff";
	String rageServerURL = "0.0.0.0:8080/RAGE";

	@Before
	public void setUp() {
		def targetGspFile = "grails-app/views/feed/kml.gsp";
		def kmlGsp = new File(targetGspFile);
		assertNotNull('Unable to load target gsp file: ${targetGspFile}', kmlGsp);
		def fileSize = kmlGsp.text.size();
		assertTrue(fileSize > 0);
		gspTemplate = kmlGsp.text;
		
		def mockedConfig = new ConfigObject();
		mockedConfig.rage.server.url = rageServerURL;
		ConfigurationHolder.config = mockedConfig;
	}

	@Test
	void testKml_noPoints() {
		assertNotNull("Unable to test GSP, template is null!", gspTemplate);

		def feed = createFeed("title", "test feed", "name", "test placemark", null, null, null);
		def result = applyTemplate(gspTemplate, [feedInstance: feed, feedColor: rgbColor]);
		assertNotNull("GSP provided no output", result);

		def kml = new XmlSlurper().parseText(result);

		assertDocument(kml);
		assertStyle(kml.Document, bgrColor, bgrColor);
		assertPlacemark(kml.Document, "test placemark", "#" + bgrColor, null);
		assertSnippet(kml.Document.Placemark);
	}

	@Test
	void testKml_singlePoint_with_timestamp() {
		assertNotNull("Unable to test GSP, template is null!", gspTemplate)
		
		String timestamp = "2001-01-05T00:00:00"
		Double lat = new Double(10.0)
		Double lon = new Double(15.0)
		Point2D point = new Point2D.Double(lon, lat)
		Geometry geo = new Geometry(points: [point], type: Geometry.Type.POINT)
		def feed = createFeed("title", "test feed", "name", "test placemark", timestamp, [geo], null)
		def result = applyTemplate(gspTemplate, [feedInstance: feed, feedColor: rgbColor])
		assertNotNull("GSP provided no output", result)

		def kml = new XmlSlurper().parseText(result)

		assertDocument(kml)
		assertStyle(kml.Document, bgrColor, bgrColor)
		assertPlacemark(kml.Document, "test placemark", "#" + bgrColor, timestamp)
		assertMultiGeometry_points(kml.Document.Placemark,
			["${lon},${lat},0"]) //point coords
	}

	@Test
	void testKmlSinglePlacemarkWithEnclosures() {
		assertNotNull("Unable to test GSP, template is null!", gspTemplate)
		
		Enclosure kmlEnclosure = new Enclosure(title: "sample.kml", url: "sample.kml", type: "application/vnd.google-earth.kml+xml")
		Enclosure atomEnclosure = new Enclosure(title: "atomEnclosure", url: "atom.xml", type: "application/atom+xml")
		def feed = createFeed("title", "test feed", "name", "test placemark", null, null, [kmlEnclosure, atomEnclosure])
		def result = applyTemplate(gspTemplate, [feedInstance: feed, feedColor: rgbColor])
		assertNotNull("GSP provided no output", result)

		def kml = new XmlSlurper().parseText(result)

		assertDocument(kml)
		assertStyle(kml.Document, bgrColor, bgrColor)
		assertPlacemark(kml.Document.Folder, "test placemark</br><a href=\"sample.kml\">sample.kml</a></br><a href=\"atom.xml\">atomEnclosure</a>", "#" + bgrColor, null)
		assertNetworkLinks(kml.Document.Folder, ["sample.kml": "sample.kml", "atomEnclosure": "http://0.0.0.0:8080/RAGE/feed/kml?url=atom.xml"]);
	}
	
	@Test
	void testKml_multi_points() {
		assertNotNull("Unable to test GSP, template is null!", gspTemplate)

		//Create box
		Double lat1 = new Double(10.0)
		Double lon1 = new Double(15.0)
		Double lat2 = new Double(10.0)
		Double lon2 = new Double(20.0)
		Double lat3 = new Double(5.0)
		Double lon3 = new Double(20.0)
		Double lat4 = new Double(5.0)
		Double lon4 = new Double(15.0)
		Point2D point1 = new Point2D.Double(lon1, lat1)
		Point2D point2 = new Point2D.Double(lon2, lat2)
		Point2D point3 = new Point2D.Double(lon3, lat3)
		Point2D point4 = new Point2D.Double(lon4, lat4)
		Geometry geo1 = new Geometry(points: [point1], type: Geometry.Type.POINT)
		Geometry geo2 = new Geometry(points: [point2], type: Geometry.Type.POINT)
		Geometry geo3 = new Geometry(points: [point3], type: Geometry.Type.POINT)
		Geometry geo4 = new Geometry(points: [point4], type: Geometry.Type.POINT)
		def feed = createFeed("title", "test feed", "name", "test placemark", null, [geo1, geo2, geo3, geo4], null)
		
		def result = applyTemplate(gspTemplate, [feedInstance: feed, feedColor: rgbColor])
		assertNotNull("GSP provided no output", result)
		println("${result}")

		def kml = new XmlSlurper().parseText(result)

		assertDocument(kml)
		assertStyle(kml.Document, bgrColor, bgrColor)
		assertPlacemark(kml.Document, "test placemark", "#" + bgrColor, null)
		List<String> coords = ["${lon1},${lat1},0", "${lon2},${lat2},0", "${lon3},${lat3},0", "${lon4},${lat4},0"]
		assertMultiGeometry_points(kml.Document.Placemark, coords)
	}

	@Test
	void testKml_Polygon() {
		assertNotNull("Unable to test GSP, template is null!", gspTemplate)

		//Create box
		Double lat1 = new Double(10.0)
		Double lon1 = new Double(15.0)
		Double lat2 = new Double(10.0)
		Double lon2 = new Double(20.0)
		Double lat3 = new Double(5.0)
		Double lon3 = new Double(20.0)
		Double lat4 = new Double(5.0)
		Double lon4 = new Double(15.0)
		Point2D point1 = new Point2D.Double(lon1, lat1)
		Point2D point2 = new Point2D.Double(lon2, lat2)
		Point2D point3 = new Point2D.Double(lon3, lat3)
		Point2D point4 = new Point2D.Double(lon4, lat4)
		Geometry geo = new Geometry(points: [point1, point2, point3, point4], type: Geometry.Type.POLYGON);
		def feed = createFeed("title", "test feed", "name", "test placemark", null, [geo], null)
		def result = applyTemplate(gspTemplate, [feedInstance: feed, feedColor: rgbColor])
		assertNotNull("GSP provided no output", result)
		//println("${result}")

		def kml = new XmlSlurper().parseText(result)

		assertDocument(kml)
		assertStyle(kml.Document, bgrColor, bgrColor)
		assertPlacemark(kml.Document, "test placemark", "#" + bgrColor, null)
		assertMultiGeometry_polygon(kml.Document.Placemark, 
				"${lon1},${lat1},0", //point coords
				["${lon1},${lat1},0${lon2},${lat2},0${lon3},${lat3},0${lon4},${lat4},0"]) //poly coords
	}
	
	@Test
	void testKml_PolygonThatCrossesDateline() {
		assertNotNull("Unable to test GSP, template is null!", gspTemplate)

		//Create box
		Double lat1 = new Double(1.0)
		Double lon1 = new Double(179.0)
		Double lat2 = new Double(1.0)
		Double lon2 = new Double(-179.0)
		Double lat3 = new Double(-1.0)
		Double lon3 = new Double(-179.0)
		Double lat4 = new Double(-1.0)
		Double lon4 = new Double(179.0)
		Point2D point1 = new Point2D.Double(lon1, lat1)
		Point2D point2 = new Point2D.Double(lon2, lat2)
		Point2D point3 = new Point2D.Double(lon3, lat3)
		Point2D point4 = new Point2D.Double(lon4, lat4)
		Geometry geo = new Geometry(points: [point1, point2, point3, point4, point1], type: Geometry.Type.POLYGON);
		def feed = createFeed("title", "test feed", "name", "test placemark", null, [geo], null)
		def result = applyTemplate(gspTemplate, [feedInstance: feed, feedColor: rgbColor])
		assertNotNull("GSP provided no output", result)
		//println("${result}")

		def kml = new XmlSlurper().parseText(result)

		assertDocument(kml)
		assertStyle(kml.Document, bgrColor, bgrColor)
		assertPlacemark(kml.Document, "test placemark", "#" + bgrColor, null)
		assertMultiGeometry_polygon(kml.Document.Placemark,
				"${lon1},${lat1},0", //point coords
				["${lon1},${lat1},0180.0,${lat2},0180.0,${lat3},0${lon4},${lat4},0${lon1},${lat1},0", "-180.0,${lat1},0${lon2},${lat2},0${lon3},${lat3},0-180.0,${lat4},0-180.0,${lat1},0"]) //poly coords
	}

	@Test
	void testKml_multi_geometry() {
		assertNotNull("Unable to test GSP, template is null!", gspTemplate)
		
		Double pointLat = new Double(0.0)
		Double pointLon = new Double(0.0)
		Point2D point = new Point2D.Double(pointLon, pointLat)
		Geometry pointGeo = new Geometry(points: [point], type: Geometry.Type.POINT)
		
		Double lat1 = new Double(10.0)
		Double lon1 = new Double(15.0)
		Double lat2 = new Double(10.0)
		Double lon2 = new Double(20.0)
		Double lat3 = new Double(5.0)
		Double lon3 = new Double(20.0)
		Double lat4 = new Double(5.0)
		Double lon4 = new Double(15.0)
		Point2D point1 = new Point2D.Double(lon1, lat1)
		Point2D point2 = new Point2D.Double(lon2, lat2)
		Point2D point3 = new Point2D.Double(lon3, lat3)
		Point2D point4 = new Point2D.Double(lon4, lat4)
		Geometry polygonGeo = new Geometry(points: [point1, point2, point3, point4], type: Geometry.Type.POLYGON);
		
		def feed = createFeed("title", "test feed", "name", "test placemark", null, [pointGeo, polygonGeo], null)
		def result = applyTemplate(gspTemplate, [feedInstance: feed, feedColor: rgbColor])
		assertNotNull("GSP provided no output", result)

		def kml = new XmlSlurper().parseText(result)

		assertDocument(kml)
		assertStyle(kml.Document, bgrColor, bgrColor)
		assertPlacemark(kml.Document, "test placemark", "#" + bgrColor, null)
		
		def mulitGeoNode = kml.Document.Placemark.MultiGeometry
		
		assertEquals("Node, ${mulitGeoNode.name()}, does not contain expected element 'Point'",
			2, mulitGeoNode.Point.size())

		//assert point
		def pointNode = mulitGeoNode.Point[0]
		assertEquals("node, ${pointNode.name()}, does not contain expected element 'coordinates'",
			1, pointNode.coordinates.size())
		String pointCoords = pointNode.coordinates
		assertCoords(pointCoords, "${pointLon},${pointLat},0")
		
		//assert point on corner of polygon
		def polygonPointNode = mulitGeoNode.Point[1]
		assertEquals("node, ${polygonPointNode.name()}, does not contain expected element 'coordinates'",
			1, polygonPointNode.coordinates.size())
		String polygonPointCoords = polygonPointNode.coordinates
		assertCoords(polygonPointCoords, "${lon1},${lat1},0")

		assertPolygon(mulitGeoNode, ["${lon1},${lat1},0${lon2},${lat2},0${lon3},${lat3},0${lon4},${lat4},0"])
	}
	
	@Test
	void testKml_grouped_placemarks() {
		assertNotNull("Unable to test GSP, template is null!", gspTemplate)
		
		String title = "item1"
		Double lat = new Double(10.0)
		Double lon = new Double(15.0)
		Point2D point = new Point2D.Double(lon, lat)
		Geometry geo = new Geometry(points: [point], type: Geometry.Type.POINT)
		
		def placemark1 = new Placemark()
		placemark1.name = title
		placemark1.description = "description"
		placemark1.geometryList = [geo]
		def placemark2 = new Placemark()
		placemark2.name = title
		placemark2.description = "description"
		placemark2.geometryList = [geo]
		Map<String, List<Placemark>> placemarks = ["item1":[placemark1, placemark2]]
		
		def feed = new Feed()
		feed.title = "feed1"
		feed.description = "test feed"
		feed.placemarks = placemarks
		
		def result = applyTemplate(gspTemplate, [feedInstance: feed, feedColor: rgbColor])
		assertNotNull("GSP provided no output", result)

		def kml = new XmlSlurper().parseText(result)

		assertDocument(kml)
		assertStyle(kml.Document, bgrColor, bgrColor)
		assertEquals("Document does not contain expected element(s) 'Folder'",
			1, kml.Document.Folder.size())
		assertEquals("Document does not contain expected element(s) 'Folder'",
			title + " [2]", (String) kml.Document.Folder.name.text())
		assertEquals("Folder does not contain expected element(s) 'Placemark'",
			2, kml.Document.Folder.Placemark.size())
	}
	
	private Feed createFeed(String feedTitle,
			String feedDescr,
			String placemarkName,
			String placemarkDesc,
			String placemarkTimestamp,
			List<Geometry> geometryList,
			List<Enclosure> enclosures) {

		def placemark = new Placemark()
		placemark.name = placemarkName
		placemark.description = placemarkDesc
		placemark.timestamp = placemarkTimestamp
		placemark.geometryList = geometryList
		placemark.enclosures = enclosures
		Map<String, List<Placemark>> placemarks = [placemarkName:[placemark]]
		
		def feed = new Feed()
		feed.title = feedTitle
		feed.description = feedDescr
		feed.placemarks = placemarks

		return feed		
	}

	private assertSnippet(GPathResult node) {
		assertEquals("Node, ${node.name()}, does not contain expected element 'Snippet'",
			1, node.Snippet.size())
	}

	private assertDocument(GPathResult node) {
		assertEquals("Node, ${node.name()}, does not contain expected element 'Document'",
			1, node.Document.size()) 
	}

	private assertPlacemark(GPathResult node, String expectedDescription, String expectedStyleUrl, String expectedTimestamp) {
		assertEquals("Node, ${node.name()}, does not contain expected element(s) 'Placemark'",
			1, node.Placemark.size())

		def placemarkNode = node.Placemark
		assertSnippet(placemarkNode);
		assertEquals("Node, ${placemarkNode.name()}, does not contain expected element(s) 'description'",
			1, placemarkNode.description.size())
		assertEquals("Unexpected description node value",
			expectedDescription, cleanWhiteSpace(placemarkNode.description.text().trim()))
		assertEquals("Node, ${placemarkNode.name()}, does not contain expected element(s) 'styleUrl'",
			1, placemarkNode.styleUrl.size())
		assertEquals("Unexpected value for element 'styleUrl'", 
			expectedStyleUrl, (String) placemarkNode.styleUrl.text())

		if (expectedTimestamp) {
			assertTimestamp(placemarkNode, expectedTimestamp)
		}
	}
	
	private assertNetworkLinks(GPathResult node, Map<String, String> enclosureMap) {
		assertEquals("Node, ${node.name()}, does not contain expected number of element(s) 'NetworkLink'",
			enclosureMap.size(), node.NetworkLink.size());

		node.NetworkLink.eachWithIndex {networkLinkNode, i ->
			assertEquals("Node, ${networkLinkNode.name()}, does not contain expected element 'Name'",
				1, networkLinkNode.name.size());
			assertTrue("Map does not contain expected key: " + networkLinkNode.name.text(),
				enclosureMap.containsKey(networkLinkNode.name.text()));

			assertEquals("Node, ${networkLinkNode.name()}, does not contain expected element 'Link'",
				1, networkLinkNode.Link.size());
			assertLink(networkLinkNode.Link, enclosureMap.get(networkLinkNode.name.text()));
		}
	}

	private assertLink(GPathResult node, String expectedUrl) {
		assertEquals("Node, ${node.name()}, does not contain expected element 'href'",
			1, node.href.size());
		assertEquals("Unexpected href",
			expectedUrl, node.href.text());
	}
	
	private assertTimestamp(GPathResult node, String expectedTimestamp) {
		assertEquals("Node, ${node.name()}, does not contain expected element 'TimeStamp'",
			1, node.TimeStamp.size())

		def timestampNode = node.TimeStamp
		assertEquals("Node, ${timestampNode.name()}, does not contain expected element 'when'",
			1, timestampNode.when.size())
		assertEquals("Unexpected value for element 'when'", 
			expectedTimestamp, (String) timestampNode.when.text())
	}

	private assertPoint(GPathResult node, String expectedCoords) {
		assertEquals("Node, ${node.name()}, does not contain expected element 'Point'",
			1, node.Point.size())

		def pointNode = node.Point
		assertEquals("node, ${pointNode.name()}, does not contain expected element 'coordinates'",
			1, pointNode.coordinates.size())
		String coords = node.Point.coordinates
		assertCoords(coords, expectedCoords)
	}

	private assertPolygon(GPathResult node, List<String> expectedPolyCoordsList) {
		assertEquals("node, ${node.name()}, does not contain expected element 'Polygon'",
			expectedPolyCoordsList.size(), node.Polygon.size())
		
		for (int i=0; i<expectedPolyCoordsList.size(); i++) {
			def polygonNode = node.Polygon[i]
			assertEquals("node, ${polygonNode.name()}, does not contain expected element 'coordinates'",
				1, polygonNode.outerBoundaryIs.LinearRing.coordinates.size())

			String coords = polygonNode.outerBoundaryIs.LinearRing.coordinates
			assertCoords(coords, expectedPolyCoordsList.get(i))
		}
	}

	private assertCoords(String coordsValue, String expectedCoords) {
		coordsValue = coordsValue.trim()
		coordsValue = coordsValue.replace("\n", "")
		coordsValue = coordsValue.replace(" ", "")
		coordsValue = coordsValue.replace("\t", "")
		assertEquals("Unexpected coordinates value",
			expectedCoords, coordsValue)
	}

	private assertMultiGeometry_polygon(GPathResult node, String expectedPointCoords, List<String> expectedPolyCoordsList) {
		assertEquals("Node, ${node.name()}, does not contain expected element 'MultiGeometry'",
			1, node.MultiGeometry.size())

		def mulitGeoNode = node.MultiGeometry
		assertPoint(mulitGeoNode, expectedPointCoords)
		assertPolygon(mulitGeoNode, expectedPolyCoordsList)
	}

	private assertMultiGeometry_points(GPathResult node, List<String> expectedCoordinates) {
		assertEquals("Node, ${node.name()}, does not contain expected element 'MultiGeometry'",
			1, node.MultiGeometry.size())

		def multiGeoNode = node.MultiGeometry
		assertEquals("Unexpected number of 'Point elements contained in MultiGeometry",
			expectedCoordinates.size(), multiGeoNode.Point.size())

		multiGeoNode.Point.eachWithIndex{pointNode, i->
			assertEquals("node, ${pointNode.name()}, does not contain expected element 'coordinates'",
				1, pointNode.coordinates.size())
			String coords = pointNode.coordinates
			println("coords: " + coords)
			assertCoords(coords, expectedCoordinates.get(i))
		}
	}

	private assertStyle(GPathResult node, String expectedStyleId, String expectedColor) {
		assertEquals("Node, ${node.name()}, does not contain expected element 'Style'",
			2, node.Style.size())
		assertStyleNode(node.Style[0], expectedStyleId + "_normal", "00ffffff", expectedColor)
		assertStyleNode(node.Style[1], expectedStyleId + "_highlighted", "ffffffff", expectedColor)
		
		assertEquals("Node, ${node.name()}, does not contain expected element 'StyleMap'",
			1, node.StyleMap.size())
		def styleMapNode = node.StyleMap
		assertEquals("Node, ${styleMapNode.name()}, does not contain expected value for attribute: 'id'",
			expectedStyleId, (String) styleMapNode.@id)
		assertEquals("Node, ${styleMapNode.name()}, does not contain expected element 'Pair'",
			2, styleMapNode.Pair.size())
	}
	
	private assertStyleNode(GPathResult styleNode, String id, String labelColor, String expectedColor) {
		assertEquals("Node, ${styleNode.name()}, does not contain expected value for attribute: 'id'",
					id, (String) styleNode.@id)
		
		assertEquals("Node, ${styleNode.name()}, does not contain expected element 'LableStyle'",
					1, styleNode.size())
		assertEquals("LabelStyle color does not contain expected value",
					labelColor,  (String) styleNode.LabelStyle.color.text())
		
		assertEquals("Node, ${styleNode.name()}, does not contain expected element 'IconStyle'",
					1, styleNode.IconStyle.size())
		assertTrue("IconStyle color does not contain expected color: ${expectedColor}",
					 ((String)styleNode.IconStyle.color.text()).contains(expectedColor))
		
		assertEquals("Node, ${styleNode.name()}, does not contain expected element 'LineStyle'",
					1, styleNode.LineStyle.size())
		assertTrue("LineStyle color does not contain expected color: ${expectedColor}",
					 ((String)styleNode.LineStyle.color.text()).contains(expectedColor))
		
		assertEquals("Node, ${styleNode.name()}, does not contain expected element 'PolyStyle'",
					1, styleNode.PolyStyle.size())
		assertTrue("PolyStyle color does not contain expected color: ${expectedColor}",
					 ((String)styleNode.PolyStyle.color.text()).contains(expectedColor))
	}
	
	private String cleanWhiteSpace(String val) {
		return val.replace("\r", "").replace("\t", "").replace("\n", "");
	}
}