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

import java.awt.geom.Point2D

@TestFor(Geometry)
class GeometryTests {
    @Test
    void testDoesIntersectDateLine_point() {
		Geometry geoPoint = new Geometry(type: Geometry.Type.POINT, points: [buildPoint(0.0, 0.0)])
		assertFalse("points can never cross the international dateline", geoPoint.doesIntersectDateLine());
    }
	
	@Test
	void testDoesIntersectDateLine_polygon() {
		Geometry geoPoly1 = new Geometry(type: Geometry.Type.POLYGON, points: [buildPoint(1.0, 179.0), buildPoint(1.0, -179.0), buildPoint(-1.0, -179.0), buildPoint(-1.0, 179.0), buildPoint(1.0, 179.0)])
		assertTrue("Polygon should intersect the international dateline", geoPoly1.doesIntersectDateLine());
		
		Geometry geoPoly2 = new Geometry(type: Geometry.Type.POLYGON, points: [buildPoint(1.0, 1.0), buildPoint(1.0, -1.0), buildPoint(-1.0, -1.0), buildPoint(-1.0, 1.0), buildPoint(1.0, 1.0)])
		assertFalse("Polygon should not intersect the international dateline", geoPoly2.doesIntersectDateLine());
		
		Geometry geoPoly3 = new Geometry(type: Geometry.Type.POLYGON, points: [buildPoint(1.0, 178.0), buildPoint(1.0, 179.0), buildPoint(-1.0, 179.0), buildPoint(-1.0, 178.0), buildPoint(1.0, 178.0)])
		assertFalse("Polygon should not intersect the international dateline", geoPoly3.doesIntersectDateLine());
		
		Geometry geoPoly4 = new Geometry(type: Geometry.Type.POLYGON, points: [buildPoint(1.0, -179.0), buildPoint(1.0, -178.0), buildPoint(-1.0, -178.0), buildPoint(-1.0, -179.0), buildPoint(1.0, -179.0)])
		assertFalse("Polygon should not intersect the international dateline", geoPoly4.doesIntersectDateLine());
	}
	
	@Test
	void testSplitOnDateLine_polygonNotIntersectsDateLine() {
		List<Point2D> orginalGeoPointList = [buildPoint(1.0, 1.0), buildPoint(1.0, -1.0), buildPoint(-1.0, -1.0), buildPoint(-1.0, 1.0), buildPoint(1.0, 1.0)];
		Geometry geoPoly = new Geometry(type: Geometry.Type.POLYGON, points: orginalGeoPointList)
		List<List<Point2D>> splitGeoList = geoPoly.splitOnDateLine();
		assertNotNull("list not expected to be null", splitGeoList);
		assertEquals("Unexpected list size", 
			1, splitGeoList.size());
		
		List<Point2D> splitGeoPointList = splitGeoList.get(0);
		assertEquals("points list should be the same size",
			orginalGeoPointList.size(), splitGeoPointList.size());
		for (int i=0; i<splitGeoPointList.size(); i++) {
			assertEquals("longitude values not the same",
				orginalGeoPointList.get(i).getX(), splitGeoPointList.get(i).getX(), 0.0);
			assertEquals("latitude values are not the same",
				orginalGeoPointList.get(i).getY(), splitGeoPointList.get(i).getY(), 0.0);
		}
	}
	
	@Test
	void testSplitOnDateLine_polygonIntersectsDateLine() {
		Geometry geoPoly = new Geometry(type: Geometry.Type.POLYGON, points: [buildPoint(1.0, 179.0), buildPoint(1.0, -179.0), buildPoint(-1.0, -179.0), buildPoint(-1.0, 179.0), buildPoint(1.0, 179.0)])
		List<List<Point2D>> splitGeoList = geoPoly.splitOnDateLine();
		assertNotNull("list not expected to be null", splitGeoList);
		assertEquals("Unexpected list size",
			2, splitGeoList.size());
		
		List<Point2D> geo1 = splitGeoList.get(0);
		assertEquals("geo1 points list should be the same size",
			5, geo1.size());
		assertEquals("Unexpected geo1 point1 latitude value",
			1.0, geo1.get(0).getY(), 0.0);
		assertEquals("Unexpected geo1 point1 longitude value",
			179.0, geo1.get(0).getX(), 0.0);
		assertEquals("Unexpected geo1 point2 latitude value",
			1.0, geo1.get(1).getY(), 0.0);
		assertEquals("Unexpected geo1 point2 longitude value",
			180.0, geo1.get(1).getX(), 0.0);
		assertEquals("Unexpected geo1 point3 latitude value",
			-1.0, geo1.get(2).getY(), 0.0);
		assertEquals("Unexpected geo1 point3 longitude value",
			180.0, geo1.get(2).getX(), 0.0);
		assertEquals("Unexpected geo1 point4 latitude value",
			-1.0, geo1.get(3).getY(), 0.0);
		assertEquals("Unexpected geo1 point4 longitude value",
			179.0, geo1.get(3).getX(), 0.0);
		assertEquals("Unexpected geo1 point5 latitude value",
			1.0, geo1.get(4).getY(), 0.0);
		assertEquals("Unexpected geo1 point5 longitude value",
			179.0, geo1.get(4).getX(), 0.0);
		
		List<Point2D> geo2 = splitGeoList.get(1);
		assertEquals("geo2 points list should be the same size",
			5, geo2.size());
		assertEquals("Unexpected geo2 point1 latitude value",
			1.0, geo2.get(0).getY(), 0.0);
		assertEquals("Unexpected geo2 point1 longitude value",
			-180.0, geo2.get(0).getX(), 0.0);
		assertEquals("Unexpected geo2 point2 latitude value",
			1.0, geo2.get(1).getY(), 0.0);
		assertEquals("Unexpected geo2 point2 longitude value",
			-179.0, geo2.get(1).getX(), 0.0);
		assertEquals("Unexpected geo2 point3 latitude value",
			-1.0, geo2.get(2).getY(), 0.0);
		assertEquals("Unexpected geo2 point3 longitude value",
			-179.0, geo2.get(2).getX(), 0.0);
		assertEquals("Unexpected geo2 point4 latitude value",
			-1.0, geo2.get(3).getY(), 0.0);
		assertEquals("Unexpected geo2 point4 longitude value",
			-180.0, geo2.get(3).getX(), 0.0);
		assertEquals("Unexpected geo2 point5 latitude value",
			1.0, geo2.get(4).getY(), 0.0);
		assertEquals("Unexpected geo2 point5 longitude value",
			-180.0, geo2.get(4).getX(), 0.0);
	}
	
	@Test
	void testSplitOnDateLine_polygonIntersectsDateLineAtAngle() {
		Geometry geoPoly = new Geometry(type: Geometry.Type.POLYGON, points: [buildPoint(4.0, 179.0), buildPoint(8.0, -179.0), buildPoint(4.0, -178.0), buildPoint(2.0, -179.0), buildPoint(4.0, 179.0)])
		List<List<Point2D>> splitGeoList = geoPoly.splitOnDateLine();
		assertNotNull("list not expected to be null", splitGeoList);
		assertEquals("Unexpected list size",
			2, splitGeoList.size());
		
		List<Point2D> geo1 = splitGeoList.get(0);
		assertEquals("geo1 points list should be the same size",
			4, geo1.size());
		assertEquals("Unexpected geo1 point1 latitude value",
			4.0, geo1.get(0).getY(), 0.0);
		assertEquals("Unexpected geo1 point1 longitude value",
			179.0, geo1.get(0).getX(), 0.0);
		assertEquals("Unexpected geo1 point2 latitude value",
			6.0, geo1.get(1).getY(), 0.0);
		assertEquals("Unexpected geo1 point2 longitude value",
			180.0, geo1.get(1).getX(), 0.0);
		assertEquals("Unexpected geo1 point3 latitude value",
			3.0, geo1.get(2).getY(), 0.0);
		assertEquals("Unexpected geo1 point3 longitude value",
			180.0, geo1.get(2).getX(), 0.0);
		assertEquals("Unexpected geo1 point4 latitude value",
			4.0, geo1.get(3).getY(), 0.0);
		assertEquals("Unexpected geo1 point4 longitude value",
			179.0, geo1.get(3).getX(), 0.0);

		List<Point2D> geo2 = splitGeoList.get(1);
		assertEquals("geo2 points list should be the same size",
			6, geo2.size());
		assertEquals("Unexpected geo2 point1 latitude value",
			6.0, geo2.get(0).getY(), 0.0);
		assertEquals("Unexpected geo2 point1 longitude value",
			-180.0, geo2.get(0).getX(), 0.0);
		assertEquals("Unexpected geo2 point2 latitude value",
			8.0, geo2.get(1).getY(), 0.0);
		assertEquals("Unexpected geo2 point2 longitude value",
			-179.0, geo2.get(1).getX(), 0.0);
		assertEquals("Unexpected geo2 point3 latitude value",
			4.0, geo2.get(2).getY(), 0.0);
		assertEquals("Unexpected geo2 point3 longitude value",
			-178.0, geo2.get(2).getX(), 0.0);
		assertEquals("Unexpected geo2 point4 latitude value",
			2.0, geo2.get(3).getY(), 0.0);
		assertEquals("Unexpected geo2 point4 longitude value",
			-179.0, geo2.get(3).getX(), 0.0);
		assertEquals("Unexpected geo2 point5 latitude value",
			3.0, geo2.get(4).getY(), 0.0);
		assertEquals("Unexpected geo2 point5 longitude value",
			-180.0, geo2.get(4).getX(), 0.0);
		assertEquals("Unexpected geo2 point6 latitude value",
			6.0, geo2.get(5).getY(), 0.0);
		assertEquals("Unexpected geo2 point6 longitude value",
			-180.0, geo2.get(5).getX(), 0.0);
	}
	
	@Test
	void testSplitOnDateLine_polygonIntersectsDateLineAtAngle_crossesEquator() {
		//-1.0 179.0 1.0 -179.0 -1.0 -179.0 -3.0 179.0 -1.0 179.0
		Geometry geoPoly = new Geometry(type: Geometry.Type.POLYGON, points: [buildPoint(-1.0, 179.0), buildPoint(1.0, -179.0), buildPoint(-1.0, -179.0), buildPoint(-3.0, 179.0), buildPoint(-1.0, 179.0)])
		List<List<Point2D>> splitGeoList = geoPoly.splitOnDateLine();
		assertNotNull("list not expected to be null", splitGeoList);
		assertEquals("Unexpected list size",
			2, splitGeoList.size());
		
		List<Point2D> geo1 = splitGeoList.get(0);
		assertEquals("geo1 points list should be the same size",
			5, geo1.size());
		assertEquals("Unexpected geo1 point1 latitude value",
			-1.0, geo1.get(0).getY(), 0.0);
		assertEquals("Unexpected geo1 point1 longitude value",
			179.0, geo1.get(0).getX(), 0.0);
		assertEquals("Unexpected geo1 point2 latitude value",
			0.0, geo1.get(1).getY(), 0.0);
		assertEquals("Unexpected geo1 point2 longitude value",
			180.0, geo1.get(1).getX(), 0.0);
		assertEquals("Unexpected geo1 point3 latitude value",
			-2.0, geo1.get(2).getY(), 0.0);
		assertEquals("Unexpected geo1 point3 longitude value",
			180.0, geo1.get(2).getX(), 0.0);
		assertEquals("Unexpected geo1 point4 latitude value",
			-3.0, geo1.get(3).getY(), 0.0);
		assertEquals("Unexpected geo1 point4 longitude value",
			179.0, geo1.get(3).getX(), 0.0);
		assertEquals("Unexpected geo1 point5 latitude value",
			-1.0, geo1.get(4).getY(), 0.0);
		assertEquals("Unexpected geo1 point5 longitude value",
			179.0, geo1.get(4).getX(), 0.0);

		List<Point2D> geo2 = splitGeoList.get(1);
		assertEquals("geo2 points list should be the same size",
			5, geo2.size());
		assertEquals("Unexpected geo2 point1 latitude value",
			0.0, geo2.get(0).getY(), 0.0);
		assertEquals("Unexpected geo2 point1 longitude value",
			-180.0, geo2.get(0).getX(), 0.0);
		assertEquals("Unexpected geo2 point2 latitude value",
			1.0, geo2.get(1).getY(), 0.0);
		assertEquals("Unexpected geo2 point2 longitude value",
			-179.0, geo2.get(1).getX(), 0.0);
		assertEquals("Unexpected geo2 point3 latitude value",
			-1.0, geo2.get(2).getY(), 0.0);
		assertEquals("Unexpected geo2 point3 longitude value",
			-179.0, geo2.get(2).getX(), 0.0);
		assertEquals("Unexpected geo2 point4 latitude value",
			-2, geo2.get(3).getY(), 0.0);
		assertEquals("Unexpected geo2 point4 longitude value",
			-180.0, geo2.get(3).getX(), 0.0);
		assertEquals("Unexpected geo2 point5 latitude value",
			0.0, geo2.get(4).getY(), 0.0);
		assertEquals("Unexpected geo2 point5 longitude value",
			-180.0, geo2.get(4).getX(), 0.0);
	}
	
	@Test
	void testSplitOnDateLine_polygonIntersectsDateLineAtAngle_negativeSlope() {
		Geometry geoPoly = new Geometry(type: Geometry.Type.POLYGON, points: [buildPoint(-4.0, 179.0), buildPoint(-8.0, -179.0), buildPoint(-4.0, -178.0), buildPoint(-2.0, -179.0), buildPoint(-4.0, 179.0)])
		List<List<Point2D>> splitGeoList = geoPoly.splitOnDateLine();
		assertNotNull("list not expected to be null", splitGeoList);
		assertEquals("Unexpected list size",
			2, splitGeoList.size());
		
		List<Point2D> geo1 = splitGeoList.get(0);
		assertEquals("geo1 points list should be the same size",
			4, geo1.size());
		assertEquals("Unexpected geo1 point1 latitude value",
			-4.0, geo1.get(0).getY(), 0.0);
		assertEquals("Unexpected geo1 point1 longitude value",
			179.0, geo1.get(0).getX(), 0.0);
		assertEquals("Unexpected geo1 point2 latitude value",
			-6.0, geo1.get(1).getY(), 0.0);
		assertEquals("Unexpected geo1 point2 longitude value",
			180.0, geo1.get(1).getX(), 0.0);
		assertEquals("Unexpected geo1 point3 latitude value",
			-3.0, geo1.get(2).getY(), 0.0);
		assertEquals("Unexpected geo1 point3 longitude value",
			180.0, geo1.get(2).getX(), 0.0);
		assertEquals("Unexpected geo1 point4 latitude value",
			-4.0, geo1.get(3).getY(), 0.0);
		assertEquals("Unexpected geo1 point4 longitude value",
			179.0, geo1.get(3).getX(), 0.0);

		List<Point2D> geo2 = splitGeoList.get(1);
		assertEquals("geo2 points list should be the same size",
			6, geo2.size());
		assertEquals("Unexpected geo2 point1 latitude value",
			-6.0, geo2.get(0).getY(), 0.0);
		assertEquals("Unexpected geo2 point1 longitude value",
			-180.0, geo2.get(0).getX(), 0.0);
		assertEquals("Unexpected geo2 point2 latitude value",
			-8.0, geo2.get(1).getY(), 0.0);
		assertEquals("Unexpected geo2 point2 longitude value",
			-179.0, geo2.get(1).getX(), 0.0);
		assertEquals("Unexpected geo2 point3 latitude value",
			-4.0, geo2.get(2).getY(), 0.0);
		assertEquals("Unexpected geo2 point3 longitude value",
			-178.0, geo2.get(2).getX(), 0.0);
		assertEquals("Unexpected geo2 point4 latitude value",
			-2.0, geo2.get(3).getY(), 0.0);
		assertEquals("Unexpected geo2 point4 longitude value",
			-179.0, geo2.get(3).getX(), 0.0);
		assertEquals("Unexpected geo2 point5 latitude value",
			-3.0, geo2.get(4).getY(), 0.0);
		assertEquals("Unexpected geo2 point5 longitude value",
			-180.0, geo2.get(4).getX(), 0.0);
		assertEquals("Unexpected geo2 point6 latitude value",
			-6.0, geo2.get(5).getY(), 0.0);
		assertEquals("Unexpected geo2 point6 longitude value",
			-180.0, geo2.get(5).getX(), 0.0);
	}
	
	private Point2D buildPoint(double lat, double lon) {
		return new Point2D.Double(lon, lat)
	}
}
