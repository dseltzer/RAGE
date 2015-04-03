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

class Geometry {

	private static final int MERIDIAN_NOT_SET = 0;
	
	enum Type {
		POINT,
		LINE,
		POLYGON
	}

	//Longitude is X-axis, Latitude is Y-axis
	List<Point2D> points = []
	Type type

	static constraints = {
	}
	
	boolean doesIntersectDateLine() {
		if (type == Type.POINT) {
			return false;
		} else {
			boolean rval = false;
			for (int i=0; i<points.size()-1; i++) {
				if (doesSegmentCrossDateLine(points.get(i).getX(), points.get(i+1).getX())) {
					rval = true;
					break;
				}
			}
			return rval;
		}
	}
	
	List<List<Point2D>> splitOnDateLine() {
		List<List<Point2D>> splitList = [];
		if (this.doesIntersectDateLine()) {
			List<Point2D> firstHalfPoints = [];
			List<Point2D> secondHalfPoints = [];
			
			int crossedDateLineToggle = 1;
			int firstHalfMeridian = MERIDIAN_NOT_SET;
			int secondHalfMeridian = MERIDIAN_NOT_SET;
			for (int i=0; i<points.size(); i++) {
				if (crossedDateLineToggle > 0) {
					firstHalfPoints.add(points.get(i));
				} else {
					secondHalfPoints.add(points.get(i));
				}
				
				if (!isLastElementInList(i, points.size())) {
					double lon1 = points.get(i).getX();
					double lat1 = points.get(i).getY();
					double lon2 = points.get(i+1).getX();
					double lat2 = points.get(i+1).getY();
					if (doesSegmentCrossDateLine(lon1, lon2)) {
						if (firstHalfMeridian == MERIDIAN_NOT_SET || secondHalfMeridian == MERIDIAN_NOT_SET) {
							if (lon1 > 0) {
								//geo crosses DateLine from East to West
								firstHalfMeridian = 180;
								secondHalfMeridian = -180;
							} else {
								//geo crosses DateLine from West to East
							firstHalfMeridian = -180;
							secondHalfMeridian = 180;
							}
						}

						double latAtMeridian = lat1;
						if (lat1 != lat2) {
							double rise = lat2 - lat1;
							double run = (180 - Math.abs(lon1)) + (180 - Math.abs(lon2));
							double slope = rise / run;
							latAtMeridian = slope * (180 - Math.abs(lon1)) + lat1;
						}
						firstHalfPoints.add(new Point2D.Double(firstHalfMeridian, latAtMeridian));
						secondHalfPoints.add(new Point2D.Double(secondHalfMeridian, latAtMeridian));
						crossedDateLineToggle = crossedDateLineToggle * -1;
					}
				}
			}
			
			//close split geometry
			if (type != Type.LINE) {
				secondHalfPoints.add(secondHalfPoints.get(0));
			}
			
			splitList.add(firstHalfPoints);
			splitList.add(secondHalfPoints);
		} else {
			splitList.add(this.points);
		}
		
		return splitList
	}
	
	private boolean isLastElementInList(int elementIndex, int listSize) {
		boolean rval = true;
		if (elementIndex < listSize-1) {
			rval = false;
		}
		return rval;
	}
	
	private boolean doesSegmentCrossDateLine(double longitude1, double longitude2) {
		//Test for longitudes that are seperated by more than 179 degrees, signals geometry has crossed the international dateline
		if (Math.abs(longitude1 - longitude2) > 179.0) {
			return true;
		} else {
			return false;
		}
	}
}
