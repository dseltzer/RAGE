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

import rage.Geometry.Type
import rage.GeoParserService.GeoType
import groovy.util.slurpersupport.GPathResult
import java.awt.geom.Point2D

@TestFor(GeoParserService)
class GeoParserServiceTests {

	@Test
	void testGetNamespacePrefix() {
		String RSS_2_0 = "<rss version='2.0' xmlns:geo='http://www.w3.org/2003/01/geo/wgs84_pos#'><channel>" +
					"<title>feed1</title><description>Feed containing georss-simple</description>" +
					"<item><geo:lat>45.256</geo:lat><geo:long>-71.92</geo:long></item>" +
					"<item><geo:lat>45.256</geo:lat><geo:long>-71.92</geo:long></item>" + 
					"</channel></rss>";
		String namespace = "http://www.w3.org/2003/01/geo/wgs84_pos#";

		assertEquals("geo", service.getNamespacePrefix(RSS_2_0, namespace));
	}

	@Test
	void testIsNamespaceUsed_used() {
		String RSS_2_0 = "<rss version='2.0' xmlns:geo='http://www.w3.org/2003/01/geo/wgs84_pos#'><channel>" +
					"<title>feed1</title><description>Feed containing georss-simple</description>" +
					"<item><geo:lat>45.256</geo:lat><geo:long>-71.92</geo:long></item>" +
					"<item><geo:lat>45.256</geo:lat><geo:long>-71.92</geo:long></item>" + 
					"</channel></rss>";
		String namespace = "http://www.w3.org/2003/01/geo/wgs84_pos#";

		assertTrue(service.isNamespaceUsed(RSS_2_0, namespace));
		
	}

	@Test
	void testIsNamespaceUsed_not_used() {
		String RSS_2_0 = "<rss version='2.0' xmlns:georss='http://www.georss.org/georss' xmlns:geo='http://www.w3.org/2003/01/geo/wgs84_pos#'><channel>" +
					"<title>feed1</title><description>Feed containing georss-simple</description>" +
					"<item><georss:point>45.256 -71.92</georss:point></item>" +
					"<item><georss:point>45.256 -71.92</georss:point></item>" + 
					"</channel></rss>";
		String namespace = "http://www.w3.org/2003/01/geo/wgs84_pos#";

		assertFalse(service.isNamespaceUsed(RSS_2_0, namespace));
		
	}

	@Test
	void testGetGeoType_Simple() {
		String RSS_2_0 = "<rss version='2.0' xmlns:georss='http://www.georss.org/georss'><channel>" +
					"<title>feed1</title><description>Feed containing georss-simple</description>" +
					"<item><georss:point>45.256 -71.92</georss:point></item>" +
					"<item><georss:point>45.256 -71.92</georss:point></item>" + 
					"</channel></rss>"

		GeoType type = service.getGeoType(RSS_2_0)
		assertEquals(GeoType.SIMPLE, type)
	}
	
	@Test
	void testGetGeoType_Simple_with_unused_W3C_namespace() {
		String RSS_2_0 = "<rss version='2.0' xmlns:georss='http://www.georss.org/georss' xmlns:geo='http://www.w3.org/2003/01/geo/wgs84_pos#'><channel>" +
					"<title>feed1</title><description>Feed containing georss-simple</description>" +
					"<item><georss:point>45.256 -71.92</georss:point></item>" +
					"<item><georss:point>45.256 -71.92</georss:point></item>" + 
					"</channel></rss>"

		GeoType type = service.getGeoType(RSS_2_0)
		assertEquals(GeoType.SIMPLE, type)
	}

	@Test
	void testGetGeoType_W3C() {
		String RSS_2_0 = "<rss version='2.0' xmlns:geo='http://www.w3.org/2003/01/geo/wgs84_pos#'><channel>" +
					"<title>feed1</title><description>Feed containing georss-simple</description>" +
					"<item><geo:lat>45.256</geo:lat><geo:long>-71.92</geo:long></item>" +
					"<item><geo:lat>45.256</geo:lat><geo:long>-71.92</geo:long></item>" + 
					"</channel></rss>"

		GeoType type = service.getGeoType(RSS_2_0)
		assertEquals(GeoType.W3C, type)
	}
	
	@Test
	void testParseDoubleList() {
		List<Point2D> points = service.parseDoubleList("45.256 -110.45 46.46 -109.48 43.84 -109.86 45.256 -110.45")
		assertNotNull("Points list generated from parsing doubleList is not expected to be null", points)
		assertEquals("Unexpected number of points in list",
			4, points.size())
		assertEquals("Unexpected point 1 lat",
			45.256, points.get(0).getY(), 0.0)
		assertEquals("Unexpected point 1 lat",
			-110.45, points.get(0).getX(), 0.0)
	}
	
	@Test
	void testCreatePoint() {
		Point2D.Double point = service.createPoint("1.0", "-1.0")
		assertNotNull("Point not expected to be null", point);
		assertEquals(1.0, point.getY(), 0.0);
		assertEquals(-1.0, point.getX(), 0.0);
	}
	
	@Test
	void testCreatePointWithCdataWrapper() {
		Point2D.Double point = service.createPoint("<![CDATA[1.0]]>", "<![CDATA[-1.0]]>")
		assertNotNull("Point not expected to be null", point);
		assertEquals(1.0, point.getY(), 0.0);
		assertEquals(-1.0, point.getX(), 0.0);
	}
	
	@Test
	void testParseGeometry() {
		def parser = new GeoParserService() {
			List<Geometry> parseGeometrySimple(GPathResult node) {
				List<Geometry> geometryList = []
				Point2D point1 = new Point2D.Double(-71.92, 45.256)
				Geometry geo = new Geometry(points: [point1], type: Geometry.Type.POINT)
				geometryList << geo
			}
		}
		
		def node = new XmlSlurper().parseText("<node>I am a node to parse</node>")
		
		List<Geometry> geometryList = parser.parseGeometry(GeoType.SIMPLE, node)
		assertNotNull("geometryList not expected to be null", geometryList)
		assertEquals("Unexpected number of items in geometry list",
			1, geometryList.size())
	}
	
	@Test
	void testParseGeometrySimple_no_simple_element() {
		def node = new XmlSlurper().parseText("<node xmlns:georss='http://www.georss.org/georss'><title>hello?</title></node>")
		try {
			service.parseGeometrySimple(node)
			fail("Exception expected!")
		} catch (Exception e) {
			println "Exception: ${e.getMessage()}"
		}
	}

	@Test
	void testParseGeometrySimple_point() {
		def node = new XmlSlurper().parseText("<node xmlns:georss='http://www.georss.org/georss'><title>hello?</title><georss:point>45.256 -71.92</georss:point></node>")
		
		List<Geometry> geometryList = service.parseGeometrySimple(node)
		assertNotNull("geometryList not expected to be null", geometryList)
		assertEquals("Unexpected number of items in geometry list",
			1, geometryList.size())
		Point2D point1 = new Point2D.Double(-71.92, 45.256)
		List<Point2D> points = [point1]
		assertGeometry(geometryList.get(0), points, Geometry.Type.POINT)
	}

	@Test
	void testParseGeometrySimple_multi_point() {

		String lat1 = "41.1963";
		String lon1 = "129.0622";
		String lat2 = "27.1691";
		String lon2 = "71.5866"
		String xmlFragment = "<node xmlns:georss='http://www.georss.org/georss'><title>hello?</title>" + 
				"<georss:point>${lat1} ${lon1}</georss:point>" + 
				"<georss:point>${lat2} ${lon2}</georss:point>" +
				"</node>";
				
		List<Geometry> geometryList = service.parseGeometrySimple(new XmlSlurper().parseText(xmlFragment))
		assertNotNull("geometryList not expected to be null", geometryList)
		assertEquals("Unexpected number of items in geometry list",
			2, geometryList.size())

		Point2D point1 = new Point2D.Double(new Double(lon1), new Double(lat1))
		assertGeometry(geometryList.get(0), [point1], Geometry.Type.POINT)
		Point2D point2 = new Point2D.Double(new Double(lon2), new Double(lat2))
		assertGeometry(geometryList.get(1), [point2], Geometry.Type.POINT)
	}

	@Test
	void testParseGeometrySimple_line() {
		def node = new XmlSlurper().parseText("<node xmlns:georss='http://www.georss.org/georss'><title>hello?</title><georss:line>45.256 -71.92 46 -72 45.256 -72.5</georss:line></node>")
		
		List<Geometry> geometryList = service.parseGeometrySimple(node)
		assertNotNull("geometryList not expected to be null", geometryList)
		assertEquals("Unexpected number of items in geometry list",
			1, geometryList.size())
		
		Point2D point1 = new Point2D.Double(-71.92, 45.256)
		Point2D point2 = new Point2D.Double(-72, 46)
		Point2D point3 = new Point2D.Double(-72.5, 45.256)
		List<Point2D> points = [point1, point2, point3]
		assertGeometry(geometryList.get(0), points, Geometry.Type.LINE)
	}

	@Test
	void testParseGeometrySimple_polygon() {
		def node = new XmlSlurper().parseText("<node xmlns:georss='http://www.georss.org/georss'><title>hello?</title><georss:polygon>45.256 -71.92 46 -72 45.256 -72.5 45.256 -71.92</georss:polygon></node>")
		
		List<Geometry> geometryList = service.parseGeometrySimple(node)
		assertNotNull("geometryList not expected to be null", geometryList)
		assertEquals("Unexpected number of items in geometry list",
			1, geometryList.size())
		
		Point2D point1 = new Point2D.Double(-71.92, 45.256)
		Point2D point2 = new Point2D.Double(-72, 46)
		Point2D point3 = new Point2D.Double(-72.5, 45.256)
		Point2D point4 = new Point2D.Double(-71.92, 45.256)
		List<Point2D> points = [point1, point2, point3, point4]
		assertGeometry(geometryList.get(0), points, Geometry.Type.POLYGON)
	}
	
	@Test
	void testParseGeometrySimple_multi_polygon() {
		def node = new XmlSlurper().parseText("<node xmlns:georss='http://www.georss.org/georss'><title>hello?</title><georss:polygon>45.256 -71.92 46 -72 45.256 -72.5 45.256 -71.92</georss:polygon><georss:polygon>-1.0 1.0 1.0 1.0 1.0 -1.0 -1.0 -1.0 -1.0 1.0</georss:polygon></node>")
		
		List<Geometry> geometryList = service.parseGeometrySimple(node)
		assertNotNull("geometryList not expected to be null", geometryList)
		assertEquals("Unexpected number of items in geometry list",
			2, geometryList.size())
		
		Point2D point1 = new Point2D.Double(-71.92, 45.256)
		Point2D point2 = new Point2D.Double(-72, 46)
		Point2D point3 = new Point2D.Double(-72.5, 45.256)
		Point2D point4 = new Point2D.Double(-71.92, 45.256)
		List<Point2D> points = [point1, point2, point3, point4]
		assertGeometry(geometryList.get(0), points, Geometry.Type.POLYGON)
	}
	
	@Test
	void testParseGeometrySimple_multi_geometry() {
		def node = new XmlSlurper().parseText("<node xmlns:georss='http://www.georss.org/georss'><title>hello?</title><georss:point>45.256 -71.92</georss:point><georss:polygon>45.256 -71.92 46 -72 45.256 -72.5 45.256 -71.92</georss:polygon></node>")
		
		List<Geometry> geometryList = service.parseGeometrySimple(node)
		assertNotNull("geometryList not expected to be null", geometryList)
		assertEquals("Unexpected number of items in geometry list",
			2, geometryList.size())
		
		Point2D point = new Point2D.Double(-71.92, 45.256)
		List<Point2D> pointList = [point]
		assertGeometry(geometryList.get(0), pointList, Geometry.Type.POINT)
		
		Point2D point1 = new Point2D.Double(-71.92, 45.256)
		Point2D point2 = new Point2D.Double(-72, 46)
		Point2D point3 = new Point2D.Double(-72.5, 45.256)
		Point2D point4 = new Point2D.Double(-71.92, 45.256)
		List<Point2D> polygonPointList = [point1, point2, point3, point4]
		assertGeometry(geometryList.get(1), polygonPointList, Geometry.Type.POLYGON)
	}

	@Test
	void testParseGeometryW3C_simple_point() {
		def node = new XmlSlurper().parseText("<node xmlns:geo='http://www.w3.org/2003/01/geo/wgs84_pos#'><title>hello?</title><geo:lat>45.256</geo:lat><geo:long>-71.92</geo:long></node>")
		
		List<Geometry> geometryList = service.parseGeometryW3C(node)
		assertNotNull("geometryList not expected to be null", geometryList)
		assertEquals("Unexpected number of items in geometry list",
			1, geometryList.size())

		Point2D point1 = new Point2D.Double(-71.92, 45.256)
		List<Point2D> points = [point1]
		assertGeometry(geometryList.get(0), points, Geometry.Type.POINT)
	}

	/*void testParseGeometryW3CSimplePointFromContent() {
		def node = new XmlSlurper().parseText("<node><title>hello?</title><content type='text/xml'><someOtherElment><geo:lat xmlns:geo='http://www.w3.org/2003/01/geo/wgs84_pos#'>45.256</geo:lat><geo:long xmlns:geo='http://www.w3.org/2003/01/geo/wgs84_pos#'>-71.92</geo:long></someOtherElment></content></node>")
		Geometry geometry = service.parseGeometryW3C(node)

		Point2D point1 = new Point2D.Double(-71.92, 45.256)
		List<Point2D> points = [point1]
		assertGeometry(geometry, points, Geometry.Type.POINT)
	}*/

	@Test
	void testParseGeometryW3C_point() {
		def node = new XmlSlurper().parseText("<node xmlns:geo='http://www.w3.org/2003/01/geo/wgs84_pos#'><title>hello?</title><geo:Point><geo:lat>45.256</geo:lat><geo:long>-71.92</geo:long></geo:Point></node>")
		
		List<Geometry> geometryList = service.parseGeometryW3C(node)
		assertNotNull("geometryList not expected to be null", geometryList)
		assertEquals("Unexpected number of items in geometry list",
			1, geometryList.size())
		
		Point2D point1 = new Point2D.Double(-71.92, 45.256)
		List<Point2D> points = [point1]
		assertGeometry(geometryList.get(0), points, Geometry.Type.POINT)
	}
	
	@Test
	void testParseGeometryW3C_multi_point() {

		String lat1 = "41.1963";
		String lon1 = "129.0622";
		String lat2 = "27.1691";
		String lon2 = "71.5866"

		String xmlFragment = "<node xmlns:geo='http://www.w3.org/2003/01/geo/wgs84_pos#'><title>hello?</title>" + 
				"<geo:Point><geo:lat>${lat1}</geo:lat><geo:long>${lon1}</geo:long></geo:Point>" + 
				"<geo:Point><geo:lat>${lat2}</geo:lat><geo:long>${lon2}</geo:long></geo:Point>" +
				"</node>";
		def node = new XmlSlurper().parseText(xmlFragment)
		
		List<Geometry> geometryList = service.parseGeometryW3C(node)
		assertNotNull("geometryList not expected to be null", geometryList)
		assertEquals("Unexpected number of items in geometry list",
			2, geometryList.size())

		Point2D point1 = new Point2D.Double(new Double(lon1), new Double(lat1))
		Point2D point2 = new Point2D.Double(new Double(lon2), new Double(lat2))
		assertGeometry(geometryList.get(0), [point1], Geometry.Type.POINT)
		assertGeometry(geometryList.get(1), [point2], Geometry.Type.POINT)
	}
	
	@Test
	void testParseGeometryW3C_multi_geometry() {
		def node = new XmlSlurper().parseText("<node xmlns:geo='http://www.w3.org/2003/01/geo/wgs84_pos#'><title>hello?</title><geo:lat>45.256</geo:lat><geo:long>-71.92</geo:long><geo:Point><geo:lat>45.256</geo:lat><geo:long>-71.92</geo:long></geo:Point></node>")
		
		List<Geometry> geometryList = service.parseGeometryW3C(node)
		assertNotNull("geometryList not expected to be null", geometryList)
		assertEquals("Unexpected number of items in geometry list",
			2, geometryList.size())

		Point2D point1 = new Point2D.Double(-71.92, 45.256)
		List<Point2D> point1List = [point1]
		assertGeometry(geometryList.get(0), point1List, Geometry.Type.POINT)
		
		Point2D point2 = new Point2D.Double(-71.92, 45.256)
		List<Point2D> point2List = [point2]
		assertGeometry(geometryList.get(1), point2List, Geometry.Type.POINT)
	}
	
	private assertGeometry(Geometry geometry, List<Point2D> expectedPoints, expectedType) {
		assertNotNull("geometry not expected to be null", geometry)
		assertEquals("Unexpected geometry type",
			expectedType, geometry.type)

		List<Point2D> points = geometry.points
		assertNotNull("geometry.points not expected to be null", points)
		assertEquals(expectedPoints.size, points.size)

		expectedPoints.eachWithIndex{ expectedPoint, i ->
			assertPoint(points.get(i), expectedPoint.getY(), expectedPoint.getX())
		}
	}

	private assertPoint(Point2D point, Double expectedLat, Double expectedLon) {
		assertEquals("Point lat does not contain expected value",
			expectedLat, point.getY(), 0.0)
		assertEquals("Point lon does not contain expected value",
			expectedLon, point.getX(), 0.0)
	}
}