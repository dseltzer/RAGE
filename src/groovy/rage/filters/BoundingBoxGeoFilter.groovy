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

import java.awt.Shape
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D

import rage.Placemark
import rage.Geometry

public class BoundingBoxGeoFilter implements Filter {

	public static String PARAM_NAME = "bbgeo"
	
	private final Shape boundingBox
	private final String boundingBoxString
	
	private BoundingBoxGeoFilter(double upperLeftLat, 
								double upperLeftLon, 
								double lowerRightLat,
								double lowerRightLon) {
		
		//Latitude is Y-axis, Longitude is X-axis
		GeneralPath poly = new GeneralPath()
		poly.moveTo(upperLeftLon, upperLeftLat) //upperLeft corner
		poly.lineTo(lowerRightLon, upperLeftLat) //upperRight corner
		poly.lineTo(lowerRightLon, lowerRightLat) //lowerRight corner
		poly.lineTo(upperLeftLon, lowerRightLat) //lowerLeft corner
		poly.closePath()
		boundingBox = poly
		
		StringBuffer buf = new StringBuffer();
		buf.append(upperLeftLat)
		buf.append(",")
		buf.append(upperLeftLon)
		buf.append(",")
		buf.append(lowerRightLat)
		buf.append(",")
		buf.append(lowerRightLon)
		boundingBoxString = buf.toString()
	}
	
	public static BoundingBoxGeoFilter instanceOfUrlString(final String s) throws Exception {
		String[] decimalDegrees = s.split(",")
		if (decimalDegrees.length != 4) {
			throw new Exception("Unable to generate bounding box filter, upper left and lower right points were not provided")
		}
		return new BoundingBoxGeoFilter(new Double(decimalDegrees[0]), new Double(decimalDegrees[1]), new Double(decimalDegrees[2]), new Double(decimalDegrees[3]))
	}
	
	public static BoundingBoxGeoFilter instanceOfDecimalDegrees(final double upperLeftLat, 
																final double upperLeftLon,
																final double lowerRightLat,
																final double lowerRightLon) throws Exception {
		return new BoundingBoxGeoFilter(upperLeftLat, upperLeftLon, lowerRightLat, lowerRightLon)
	}
														
	public boolean accept(Placemark placemark) throws Exception {
		boolean rval = false
		Iterator<Geometry> geometryItr = placemark.geometryList.iterator()
		while (geometryItr.hasNext()) {
			Geometry geometry = geometryItr.next()
			Iterator<Point2D> pointItr = geometry.points.iterator()
			while(pointItr.hasNext()) {
				Point2D point = pointItr.next()
				if (boundingBox.contains(point)) {
					rval = true
					break
				}
			}
		}
		return rval
	}

    public String serializeToUrlParam() {
		StringBuffer buf = new StringBuffer();
		buf.append(PARAM_NAME)
		buf.append("=")
		buf.append(boundingBoxString)
	    return buf.toString();
    }
}
