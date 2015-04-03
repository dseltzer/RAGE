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
package rage.filters

import static org.junit.Assert.*;
import org.junit.*;

import rage.Placemark
import rage.Geometry
import rage.filters.BoundingBoxGeoFilter
import java.awt.geom.Point2D

class BoundingBoxGeoFilterTests {
	
	Placemark p = new Placemark()
	Point2D pointInsideBox = new Point2D.Double(0.0, 0.0)
	Point2D pointOutsideBox = new Point2D.Double(0.0, 1.5)
	Filter filter = new BoundingBoxGeoFilter(1.0, -1.0, -1.0, 1.0)
	
	@Test
	void testInstanceOfUrlString() {
		String params = "-3.0,-2.0,-5.0,0.0"
		BoundingBoxGeoFilter boxFilter = BoundingBoxGeoFilter.instanceOfUrlString(params)
		assertNotNull("Filter not expected to be null", boxFilter)
		assertNotNull("Bounding box shape not expected to be null", boxFilter.boundingBox)
		assertEquals(params, boxFilter.boundingBoxString)
	}
	
	@Test
	void testInstanceOfUrlString_unexpectedNumberOfPoints() {
		String params = "-3.0,-2.0,-5.0"
		try {
			BoundingBoxGeoFilter boxFilter = BoundingBoxGeoFilter.instanceOfUrlString(params)
			fail("Exception expected, params value does not contain expected four decimal degree points");
		} catch (Exception e) {
			//expected
		}
	}
	
	@Test
	void testInstanceOfDecimalDegrees() {
		BoundingBoxGeoFilter boxFilter = BoundingBoxGeoFilter.instanceOfDecimalDegrees(-3.0, -2.0, -5.0, 0.0)
		assertNotNull("Filter not expected to be null", boxFilter)
		assertNotNull("Bounding box shape not expected to be null", boxFilter.boundingBox)
		assertEquals("-3.0,-2.0,-5.0,0.0", boxFilter.boundingBoxString)
	}
	
	@Test
	void testAccept_insideBox() {
		Geometry geo = new Geometry()
		geo.type = Geometry.Type.POINT
		geo.points = [pointInsideBox]
		p.geometryList = [geo]
		assertTrue("filter should accept placemark", filter.accept(p))
	}
	
	@Test
	void testAccept_outsideBox() {
		Geometry geo = new Geometry()
		geo.type = Geometry.Type.POINT
		geo.points = [pointOutsideBox]
		p.geometryList = [geo]
		assertFalse("filter should not accept placemark", filter.accept(p))
	}

	@Test
	void testSerializeToUrlParam_title() {
		assertEquals("Unexpected url param for filter", 
			"bbgeo=1.0,-1.0,-1.0,1.0", filter.serializeToUrlParam())
	}
}
