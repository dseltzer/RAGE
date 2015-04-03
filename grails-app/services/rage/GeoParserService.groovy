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

import groovy.xml.StreamingMarkupBuilder
import groovy.util.slurpersupport.GPathResult
import rage.Geometry.Type
import java.awt.geom.Point2D

class GeoParserService {

	boolean transactional = false

	final static String NS_W3C = 'http://www.w3.org/2003/01/geo/wgs84_pos#'
	final static String NS_GEORSS_SIMPLE = 'http://www.georss.org/georss'

	enum GeoType {
		W3C,
		SIMPLE,
		GML
	}

	String getNamespacePrefix(String xml, String namespace) {
		String prefix = null;
		int nsIndex = xml.indexOf(namespace);
		if (nsIndex != -1) {
			String xmlUpToNamespace = xml.substring(0, nsIndex-2);
			int lastColonIndex = xmlUpToNamespace.lastIndexOf(":");
			if (lastColonIndex != -1) {
				prefix = xmlUpToNamespace.substring(lastColonIndex + 1);
			}
		}
		return prefix;
	}

	boolean isNamespaceUsed(String xml, String namespace) {
		def rval = false;

		String prefix = getNamespacePrefix(xml, namespace);
		if (xml.contains(prefix + ":")) {
			rval = true;
		}
		
		return rval;
	}

	GeoType getGeoType(String xml) {
		if (xml.contains(NS_W3C) && isNamespaceUsed(xml, NS_W3C)) {
			return GeoType.W3C
		} else if (xml.contains(NS_GEORSS_SIMPLE) && isNamespaceUsed(xml, NS_GEORSS_SIMPLE)) {
			return GeoType.SIMPLE
		}

		//Unable to determine geo type
		return null
	}

	List<Geometry> parseGeometry(GeoType type, GPathResult node) {
		List<Geometry> geometryList = null
		try {
			switch(type) {
				case [GeoType.SIMPLE]:
					geometryList = parseGeometrySimple(node)
					break
				case [GeoType.W3C]:
					geometryList = parseGeometryW3C(node)
					break
			}
		} catch (Exception e) {
			log.error("Unable to process geometry from entry, Exception: ${e}.getMessage()")
		}
		return geometryList
	}

	//georss-simple parser
	List<Geometry> parseGeometrySimple(GPathResult node) {
		List<Geometry> geometryList = []

		if (node.point.size() >= 1) {
			node.point.each{point->
				String doublelist = point
				List<Point2D> points = parseDoubleList(doublelist.trim())
				Geometry geoPoint = new Geometry(points: points, type: Geometry.Type.POINT)
				geometryList << geoPoint
			}
		}
		if (node.line.size() >= 1) {
			node.line.each{line->
				String doublelist = line
				List<Point2D> points = parseDoubleList(doublelist.trim())
				Geometry geoLine = new Geometry(points: points, type: Geometry.Type.LINE)
				geometryList << geoLine
			}
		}
		if (node.polygon.size() >= 1) {
			node.polygon.each{polygon->
				String doublelist = polygon
				List<Point2D> points = parseDoubleList(doublelist.trim())
				Geometry geoPolygon = new Geometry(points: points, type: Geometry.Type.POLYGON)
				geometryList << geoPolygon
			}
		}
		
		if (geometryList.isEmpty()) {
			throw new Exception ("Unable to locate supported georss-simple in node: " + new StreamingMarkupBuilder().bindNode(node).toString())
		}

		return geometryList
	}

	//W3C geo parser
	List<Geometry> parseGeometryW3C(GPathResult node) {
		List<Geometry> geometryList = []

		if (node.lat.size() >= 1 && node.long.size() >= 1) {
			List<String> lats = []
			List<String> lons = []
			node.lat.each{lat->
				lats << (String) lat
			}
			node.long.each{lon->
				lons << (String) lon
			}
			for (int i = 0; i < lats.size(); i++) {
				List<Point2D> points = [createPoint(lats.get(i), lons.get(i))]
				Geometry geoPolygon = new Geometry(points: points, type: Geometry.Type.POINT)
				geometryList << geoPolygon
			}
		}
		if (node.Point.size() >= 1) {
			node.Point.each{pointNode->
				String doublelist = pointNode
				List<Point2D> points = [createPoint((String) pointNode.lat, (String) pointNode.long)]
				Geometry geoPolygon = new Geometry(points: points, type: Geometry.Type.POINT)
				geometryList << geoPolygon
			}
		}
		
		if (geometryList.isEmpty()) {
			throw new Exception ("Unable to locate supported W3C geo in node: " + new StreamingMarkupBuilder().bindNode(node).toString())
		}
		
		return geometryList
	}

	/**
	 * Parse georss and gml 'doubleList', an element that contains space-separated list of double (coordinate) values.
	 */
	private List<Point2D> parseDoubleList(String doubleList) {
		List<Point2D> points = []
		List<String> coordinates = doubleList.trim().split(' ')
		if (coordinates && coordinates.size >= 2) {
			for (def i = 0; i < coordinates.size; i = i + 2) {
				//Latitude is Y-axis, Longitude is X-axis
				points << createPoint((String) coordinates.get(i), (String) coordinates.get(i + 1))
			}
		}
		
		return points;
	}
	
	private Point2D.Double createPoint(String lat, String lon) {
		Double latDouble = new Double(removeCDATAWrapper(lat).trim())
		Double lonDouble = new Double(removeCDATAWrapper(lon).trim())
		return new Point2D.Double(lonDouble.doubleValue(), latDouble.doubleValue())
	}
	
	private String removeCDATAWrapper(String value) {
		return value.replaceAll('<!\\[CDATA\\[', "").replaceAll('\\]\\]>', "")
	}
	
}
